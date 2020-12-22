package pl.pburcon.phoneSpam.main.modules

import cats.effect.{ConcurrentEffect, ContextShift, Resource, Timer}
import com.softwaremill.macwire.wire
import pl.pburcon.phoneSpam.main.config.ConfigLoader.load
import pl.pburcon.phoneSpam.report.produce.kafka.{KafkaReportPhoneProducer, KafkaReportPhoneProducerBuilder}
import pl.pburcon.phoneSpam.util.kafka.{KafkaAvroSettingsBuilder, KafkaConfig}
import pureconfig.generic.auto._

class KafkaProducerModule[F[_]: ConcurrentEffect: ContextShift: Timer] {

  //
  // public module components
  //

  def buildReportProducer(): Resource[F, KafkaReportPhoneProducer[F]] =
    reportPhoneProducerBuilder.buildReportPhoneProducer()

  //
  // private module components
  //

  protected lazy val kafkaConfig: KafkaConfig = load[KafkaConfig]("kafka")

  // TODO
  // note that this service is duplicated between kafka producer/consumer modules,
  // it should be re-configured so that we only maintain a single schema registry client
  protected lazy val avroSettings: KafkaAvroSettingsBuilder[F] =
    wire[KafkaAvroSettingsBuilder[F]]

  protected lazy val reportPhoneProducerBuilder: KafkaReportPhoneProducerBuilder[F] =
    wire[KafkaReportPhoneProducerBuilder[F]]

}
