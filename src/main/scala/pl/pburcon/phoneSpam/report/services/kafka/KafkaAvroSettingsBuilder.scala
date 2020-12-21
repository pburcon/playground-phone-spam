package pl.pburcon.phoneSpam.report.services.kafka

import cats.effect.{Resource, Sync}
import fs2.kafka.vulcan.{Auth, AvroSettings, SchemaRegistryClientSettings}
import pl.pburcon.phoneSpam.app.config.KafkaConfig
import pl.pburcon.phoneSpam.util.effect.ResourceSync

class KafkaAvroSettingsBuilder[F[_]: Sync](kafkaConfig: KafkaConfig) {

  def buildSettings(): Resource[F, AvroSettings[F]] =
    ResourceSync.delay {
      AvroSettings {
        SchemaRegistryClientSettings(kafkaConfig.schemaRegistryUrl).withAuth(Auth.None)
      }
    }

}
