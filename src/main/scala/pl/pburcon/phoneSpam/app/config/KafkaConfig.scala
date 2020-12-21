package pl.pburcon.phoneSpam.app.config

final case class KafkaConfig(
    consumerGroupId: String,
    bootstrapServers: String,
    schemaRegistryUrl: String
)
