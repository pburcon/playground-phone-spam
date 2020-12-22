package pl.pburcon.phoneSpam.main.modules

import cats.Parallel
import cats.effect.{ConcurrentEffect, ContextShift, Resource, Timer}
import com.softwaremill.macwire.wire
import pl.pburcon.phoneSpam.main.config.ConfigLoader.load
import pl.pburcon.phoneSpam.report.consume.kafka._
import pl.pburcon.phoneSpam.report.entries.cassandra.PhoneNumberEntriesRepository
import pl.pburcon.phoneSpam.report.entries.services.{ReportPhoneEntryAddService, ReportPhoneEntryAddServiceImpl}
import pl.pburcon.phoneSpam.report.latest.config.ReportPhoneLatestConfig
import pl.pburcon.phoneSpam.report.latest.redis.ReportPhoneLatestRedisClient
import pl.pburcon.phoneSpam.report.latest.services.{ReportPhoneLatestUpdateService, ReportPhoneLatestUpdateServiceImpl}
import pl.pburcon.phoneSpam.report.summary.config.ReportPhoneSummaryConfig
import pl.pburcon.phoneSpam.report.summary.redis.ReportPhoneSummaryRedisClient
import pl.pburcon.phoneSpam.report.summary.services._
import pl.pburcon.phoneSpam.report.top.redis.{ReportPhoneTopRedisClient, ReportPhoneTopRedisClientImpl}
import pl.pburcon.phoneSpam.report.top.services.{ReportPhoneTopUpdateService, ReportPhoneTopUpdateServiceImpl}
import pl.pburcon.phoneSpam.util.kafka.{KafkaAvroSettingsBuilder, KafkaConfig}
import pl.pburcon.phoneSpam.util.redis.RedisClient
import pureconfig.generic.auto._

class KafkaConsumerModule[F[_]: ConcurrentEffect: ContextShift: Parallel: Timer](
    val entriesRepository: PhoneNumberEntriesRepository[F],
    val redisClient: RedisClient[F],
) {

  //
  // public module components
  //

  def buildReportConsumer(): Resource[F, KafkaReportPhoneConsumer[F]] =
    reportPhoneConsumerBuilder.buildReportPhoneConsumer(krpp)

  //
  // private module components
  //

  protected lazy val kafkaConfig: KafkaConfig = load[KafkaConfig]("kafka")

  // TODO
  // note that this service is duplicated between kafka producer/consumer modules,
  // it should be re-configured so that we only maintain a single schema registry client
  protected lazy val avroSettings: KafkaAvroSettingsBuilder[F] =
    wire[KafkaAvroSettingsBuilder[F]]

  protected lazy val reportPhoneConsumerBuilder: KafkaReportPhoneConsumerBuilder[F] =
    wire[KafkaReportPhoneConsumerBuilder[F]]

  protected lazy val krpp: KafkaReportPhoneProcessor[F] = wire[KafkaReportPhoneProcessorImpl[F]]

  // TODO
  // note that those services are duplicated in http module, reorganize
  protected lazy val rpSummaryRedisClient: ReportPhoneSummaryRedisClient[F] = wire[ReportPhoneSummaryRedisClient[F]]
  protected lazy val rpTopRedisClient: ReportPhoneTopRedisClient[F]         = wire[ReportPhoneTopRedisClientImpl[F]]
  protected lazy val rpLatestRedisClient: ReportPhoneLatestRedisClient[F]   = wire[ReportPhoneLatestRedisClient[F]]

  protected lazy val rpLatestConfig: ReportPhoneLatestConfig   = load[ReportPhoneLatestConfig]("report-phone.latest")
  protected lazy val rpSummaryConfig: ReportPhoneSummaryConfig = load[ReportPhoneSummaryConfig]("report-phone.summary")

  protected lazy val rpeas: ReportPhoneEntryAddService[F]      = wire[ReportPhoneEntryAddServiceImpl[F]]
  protected lazy val rplus: ReportPhoneLatestUpdateService[F]  = wire[ReportPhoneLatestUpdateServiceImpl[F]]
  protected lazy val rpsus: ReportPhoneSummaryUpdateService[F] = wire[ReportPhoneSummaryUpdateServiceImpl[F]]
  protected lazy val rptus: ReportPhoneTopUpdateService[F]     = wire[ReportPhoneTopUpdateServiceImpl[F]]

}
