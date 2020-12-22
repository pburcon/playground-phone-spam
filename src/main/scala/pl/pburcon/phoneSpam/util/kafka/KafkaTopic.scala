package pl.pburcon.phoneSpam.util.kafka

sealed trait KafkaTopic {
  def topic: String
}

object KafkaTopics {
  object ReportPhone extends KafkaTopic { override val topic: String = "report-phone" }
}
