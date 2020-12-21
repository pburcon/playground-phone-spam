package pl.pburcon.phoneSpam.app.modules

import cats.effect.{ConcurrentEffect, ContextShift, Resource}
import com.softwaremill.macwire.wire
import fs2.kafka.KafkaProducer
import pl.pburcon.phoneSpam.app.config.ConfigLoader.load
import pl.pburcon.phoneSpam.app.config.KafkaConfig
import pl.pburcon.phoneSpam.report.domain.dto.ReportPhoneAddRequest
import pl.pburcon.phoneSpam.report.services.kafka.{KafkaAvroSettingsBuilder, ReportPhoneProducer}
import pureconfig.generic.auto._

final class KafkaModule[F[_]: ConcurrentEffect: ContextShift] {

  //
  // public module components
  //

  def buildReportProducer(): Resource[F, KafkaProducer[F, String, ReportPhoneAddRequest]] =
    reportPhoneProducer.buildProducer()

  //
  // private module components
  //

  protected lazy val kafkaConfig: KafkaConfig = load[KafkaConfig]("kafka")

  protected lazy val avroSettings: KafkaAvroSettingsBuilder[F]   = wire[KafkaAvroSettingsBuilder[F]]
  protected lazy val reportPhoneProducer: ReportPhoneProducer[F] = wire[ReportPhoneProducer[F]]

}
