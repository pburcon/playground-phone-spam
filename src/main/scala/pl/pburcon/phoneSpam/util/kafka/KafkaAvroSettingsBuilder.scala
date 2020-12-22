package pl.pburcon.phoneSpam.util.kafka

import cats.effect.{Resource, Sync}
import fs2.kafka.vulcan.{Auth, AvroSettings, SchemaRegistryClientSettings}
import pl.pburcon.phoneSpam.util.cats.effect.ResourceSync

class KafkaAvroSettingsBuilder[F[_]: Sync](kafkaConfig: KafkaConfig) {

  /**
    * Build an Avro Schema Registry client resource.
    */
  def buildSettings(): Resource[F, AvroSettings[F]] =
    // TODO
    // note that there's no way to actually release the underlying schema registry client
    // which is very strange
    // or maybe I just failed to find the proper way
    // TODO
    // despite being able to run SR in HA mode it does not seem to be possible to provide multiple SR urls to the SR client
    ResourceSync.delay {
      AvroSettings {
        SchemaRegistryClientSettings(kafkaConfig.schemaRegistryUrl).withAuth(Auth.None)
      }
    }

}
