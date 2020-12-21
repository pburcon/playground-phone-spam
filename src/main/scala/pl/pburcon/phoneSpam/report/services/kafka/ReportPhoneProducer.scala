package pl.pburcon.phoneSpam.report.services.kafka

import cats.effect.{ConcurrentEffect, ContextShift, Resource}
import fs2.kafka.vulcan._
import vulcan.Codec
import vulcan.generic._
import fs2.kafka._
import pl.pburcon.phoneSpam.app.config.KafkaConfig
import pl.pburcon.phoneSpam.report.domain.dto.ReportPhoneAddRequest
import pl.pburcon.phoneSpam.util.effect.ResourceSync
import pl.pburcon.phoneSpam.util.tagged.AvroCodecs

class ReportPhoneProducer[F[_]: ContextShift: ConcurrentEffect](
    kafkaConfig: KafkaConfig,
    avroSettingsBuilder: KafkaAvroSettingsBuilder[F]
) extends AvroCodecs {

  def buildProducer(): Resource[F, KafkaProducer.Metrics[F, String, ReportPhoneAddRequest]] =
    for {
      avroSettings     <- avroSettingsBuilder.buildSettings()
      producerSettings <- buildProducerSettings(avroSettings)
      producer         <- producerResource[F].using(producerSettings)
    } yield producer

  private def buildProducerSettings(avroSettings: AvroSettings[F]) =
    ResourceSync.delay {

      implicit val codec: Codec[ReportPhoneAddRequest] =
        Codec.derive[ReportPhoneAddRequest]

      implicit val serializer: RecordSerializer[F, ReportPhoneAddRequest] =
        avroSerializer[ReportPhoneAddRequest].using(avroSettings)

      ProducerSettings[F, String, ReportPhoneAddRequest]
        .withBootstrapServers(kafkaConfig.bootstrapServers)
    }

}
