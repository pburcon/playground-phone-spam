package pl.pburcon.phoneSpam.util.kafka

final case class KafkaConfig(
    consumerGroupId: String,
    bootstrapServers: String,
    schemaRegistryUrl: String
)
