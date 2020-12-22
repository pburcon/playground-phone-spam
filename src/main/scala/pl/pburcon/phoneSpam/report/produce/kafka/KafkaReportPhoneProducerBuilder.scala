package pl.pburcon.phoneSpam.report.produce.kafka

import cats.effect.{ConcurrentEffect, ContextShift, Resource, Timer}
import cats.implicits._
import fs2.concurrent.Queue
import vulcan.Codec
import vulcan.generic._
import fs2.kafka._
import fs2.kafka.vulcan.{AvroSettings, avroSerializer}
import fs2.{Pipe, Stream}
import pl.pburcon.phoneSpam.report.add.domain.dto.ReportPhoneAddRequest
import pl.pburcon.phoneSpam.util.cats.effect.ResourceSync
import pl.pburcon.phoneSpam.util.kafka.{KafkaAvroSettingsBuilder, KafkaConfig, KafkaTopics}
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.vulcan.AvroCodecs

import scala.concurrent.duration._

// TODO
//  managing the http4s -> fs2 flow via queue is surprisingly complicated, there must be a better way
// TODO
//  refactor, split
// TODO
//  the architecture here is pretty strange, maybe it's by library design but who knows:
//  you specify the record type very early, during avro settings creation, but you specify the topic very late,
//  when creating ProducerRecords, what's the logic here?
//  it makes refactoring this code in a more generic way pretty difficult as, for example, deriving codecs would have to be extracted a level higher
class KafkaReportPhoneProducerBuilder[F[_]: ConcurrentEffect: ContextShift: Timer](
    kafkaConfig: KafkaConfig,
    avroSettingsBuilder: KafkaAvroSettingsBuilder[F]
) extends AvroCodecs
    with BaseLogging[F] {

  def buildReportPhoneProducer(): Resource[F, KafkaReportPhoneProducerImpl[F]] =
    for {
      avroSettings          <- avroSettingsBuilder.buildSettings()
      kafkaProducerSettings <- buildKafkaProducerSettings(avroSettings)
      kafkaProducer         <- buildKafkaProducer(kafkaProducerSettings)
      queue                 <- buildQueue()
      producer              <- buildProducer(queue, reportPhoneProducerPipe(kafkaProducer))
    } yield producer

  private def buildKafkaProducerSettings(avroSettings: AvroSettings[F]) =
    ResourceSync.delay {
      implicit val codec: Codec[ReportPhoneAddRequest] = Codec.derive[ReportPhoneAddRequest]
      implicit val serializer: RecordSerializer[F, ReportPhoneAddRequest] =
        avroSerializer[ReportPhoneAddRequest].using(avroSettings)

      ProducerSettings[F, String, ReportPhoneAddRequest]
        .withBootstrapServers(kafkaConfig.bootstrapServers)
    }

  private def buildKafkaProducer(producerSettings: ProducerSettings[F, String, ReportPhoneAddRequest]) =
    producerResource[F].using(producerSettings)

  private def buildQueue() =
    Resource.liftF {
      Queue.bounded[F, ReportPhoneAddRequest](10000)
    }

  private def reportPhoneProducerPipe(
      producer: KafkaProducer[F, String, ReportPhoneAddRequest]
  ): Pipe[F, ReportPhoneAddRequest, _] =
    (stream: Stream[F, ReportPhoneAddRequest]) =>
      stream
        .map(toRecords)
        .evalTap(rds => log(logger.debug(s"Producing records $rds")))
        .evalMap(producer.produce)
        .groupWithin(100, 1.second)
        .evalMap(_.sequence)
        .evalTap(rds => log(logger.debug(s"Produced records chunk $rds")))

  private def toRecords(request: ReportPhoneAddRequest) = {
    val record  = ProducerRecord(KafkaTopics.ReportPhone.topic, request.phoneNumber, request)
    val records = ProducerRecords.one(record)
    records
  }

  private def buildProducer(
      queue: Queue[F, ReportPhoneAddRequest],
      pipe: Pipe[F, ReportPhoneAddRequest, _]
  ) =
    ResourceSync.delay {
      new KafkaReportPhoneProducerImpl[F](queue, pipe)
    }

}
