package pl.pburcon.phoneSpam.report.consume.kafka

import cats.effect.{ConcurrentEffect, ContextShift, Resource, Timer}
import cats.implicits._
import fs2.{Pipe, Stream}
import vulcan.Codec
import vulcan.generic._
import fs2.kafka._
import fs2.kafka.vulcan.{AvroSettings, avroDeserializer}
import pl.pburcon.phoneSpam.report.add.domain.dto.ReportPhoneAddRequest
import pl.pburcon.phoneSpam.util.cats.effect.ResourceSync
import pl.pburcon.phoneSpam.util.kafka.{KafkaAvroSettingsBuilder, KafkaConfig, KafkaTopics}
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.vulcan.AvroCodecs

import scala.concurrent.duration._
import scala.util.control.NonFatal

class KafkaReportPhoneConsumer[F[_]: ConcurrentEffect: Timer](
    consumer: KafkaConsumer[F, String, ReportPhoneAddRequest],
    processor: KafkaReportPhoneProcessor[F]
) extends BaseLogging[F] {

  def startConsumer(): Stream[F, Unit] =
    (for {
      _ <- logStream(logger.info(s"Starting consumer $loggingClassName"))
      _ <- subscribeConsumer()
      _ <- consumer.stream.through(processRecords())
    } yield ()).handleError({
      case NonFatal(t) => logger.error(t)(s"Unexpected exception - starting consumer $loggingClassName")
    })
    
  private def subscribeConsumer() =
    Stream.eval(consumer.subscribeTo(KafkaTopics.ReportPhone.topic))

  // TODO handling processing failures
  // TODO throughput would be better if consumer processed the stream in chunks
  private def processRecords(): Pipe[F, CommittableConsumerRecord[F, String, ReportPhoneAddRequest], Unit] =
    (stream: Stream[F, CommittableConsumerRecord[F, String, ReportPhoneAddRequest]]) =>
      stream
        .mapAsync(16) { committable =>
          processor
            .process(committable.record.value)
            .map(_ => committable.offset)
            .flatTap(_ => log(logger.trace(s"Processed one $committable")))
        }
        .through(commitBatchWithin(100, 1.second))

}

class KafkaReportPhoneConsumerBuilder[F[_]: ConcurrentEffect: ContextShift: Timer](
    avroSettingsBuilder: KafkaAvroSettingsBuilder[F],
    kafkaConfig: KafkaConfig,
) extends AvroCodecs {

  def buildReportPhoneConsumer(processor: KafkaReportPhoneProcessor[F]): Resource[F, KafkaReportPhoneConsumer[F]] =
    for {
      avroSettings          <- avroSettingsBuilder.buildSettings()
      kafkaConsumerSettings <- buildKafkaConsumerSettings(avroSettings)
      kafkaConsumer         <- consumerResource(kafkaConsumerSettings)
      consumer              <- buildConsumer(kafkaConsumer, processor)
    } yield consumer

  private def buildKafkaConsumerSettings(avroSettings: AvroSettings[F]) =
    ResourceSync.delay {
      implicit val codec: Codec[ReportPhoneAddRequest] = Codec.derive[ReportPhoneAddRequest]
      implicit val serializer: RecordDeserializer[F, ReportPhoneAddRequest] =
        avroDeserializer[ReportPhoneAddRequest].using(avroSettings)

      ConsumerSettings[F, String, ReportPhoneAddRequest]
        .withAutoOffsetReset(AutoOffsetReset.Earliest)
        .withBootstrapServers(kafkaConfig.bootstrapServers)
        .withEnableAutoCommit(false) // manual offset management
        .withGroupId(kafkaConfig.consumerGroupId)
    }

  private def buildConsumer(
      kafkaConsumer: KafkaConsumer[F, String, ReportPhoneAddRequest],
      processor: KafkaReportPhoneProcessor[F]
  ): Resource[F, KafkaReportPhoneConsumer[F]] =
    ResourceSync.delay {
      new KafkaReportPhoneConsumer[F](kafkaConsumer, processor)
    }
}
