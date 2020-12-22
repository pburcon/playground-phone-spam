package pl.pburcon.phoneSpam.main.modules

import cats.Parallel
import cats.effect.{ConcurrentEffect, Resource, Timer}
import cats.implicits.toSemigroupKOps
import com.softwaremill.macwire.wire
import org.http4s.HttpApp
import org.http4s.syntax.kleisli.http4sKleisliResponseSyntaxOptionT
import pl.pburcon.phoneSpam.healthCheck.routes.{FailCheckRoute, HealthCheckRoute}
import pl.pburcon.phoneSpam.main.config.ConfigLoader.load
import pl.pburcon.phoneSpam.report.add.routes.ReportPhoneAddRoute
import pl.pburcon.phoneSpam.report.add.services.{ReportPhoneAddService, ReportPhoneAddServiceImpl}
import pl.pburcon.phoneSpam.report.entries.cassandra.PhoneNumberEntriesRepository
import pl.pburcon.phoneSpam.report.entries.routes._
import pl.pburcon.phoneSpam.report.entries.services._
import pl.pburcon.phoneSpam.report.latest.config.ReportPhoneLatestConfig
import pl.pburcon.phoneSpam.report.latest.redis.ReportPhoneLatestRedisClient
import pl.pburcon.phoneSpam.report.latest.routes.ReportPhoneLatestGetRoute
import pl.pburcon.phoneSpam.report.latest.services.{ReportPhoneLatestGetService, ReportPhoneLatestGetServiceImpl}
import pl.pburcon.phoneSpam.report.produce.kafka.KafkaReportPhoneProducer
import pl.pburcon.phoneSpam.report.summary.config.ReportPhoneSummaryConfig
import pl.pburcon.phoneSpam.report.summary.redis.ReportPhoneSummaryRedisClient
import pl.pburcon.phoneSpam.report.summary.routes.ReportPhoneSummaryGetRoute
import pl.pburcon.phoneSpam.report.summary.services.{ReportPhoneSummaryGetService, ReportPhoneSummaryGetServiceImpl}
import pl.pburcon.phoneSpam.report.top.config.ReportPhoneTopConfig
import pl.pburcon.phoneSpam.report.top.redis.{ReportPhoneTopRedisClient, ReportPhoneTopRedisClientImpl}
import pl.pburcon.phoneSpam.report.top.routes.ReportPhoneTopGetRoute
import pl.pburcon.phoneSpam.report.top.services.{ReportPhoneTopGetService, ReportPhoneTopGetServiceImpl}
import pl.pburcon.phoneSpam.util.http.{HttpConfig, _}
import pl.pburcon.phoneSpam.util.redis.RedisClient
import pureconfig.generic.auto._

import scala.concurrent.ExecutionContext

class HttpModule[F[_]: ConcurrentEffect: Parallel: Timer](
    val entriesRepository: PhoneNumberEntriesRepository[F],
    val kafkaReportPhoneProducer: KafkaReportPhoneProducer[F],
    val redisClient: RedisClient[F],
) {

  //
  // public module components
  //

  def buildHttpServer(ec: ExecutionContext): Resource[F, HttpServer[F]] =
    httpServerBuilder.buildHttpServer(ec)

  //
  // private module components
  //

  protected lazy val httpConfig: HttpConfig = load[HttpConfig]("http")

  protected lazy val rpLatestConfig: ReportPhoneLatestConfig   = load[ReportPhoneLatestConfig]("report-phone.latest")
  protected lazy val rpSummaryConfig: ReportPhoneSummaryConfig = load[ReportPhoneSummaryConfig]("report-phone.summary")
  protected lazy val rpTopConfig: ReportPhoneTopConfig         = load[ReportPhoneTopConfig]("report-phone.top")

  // TODO
  // note that those services are duplicated in kafkaConsumer module, reorganize
  // also, Klieisli can be used for dependency injection, maybe it's possible to simplify this structure?
  protected lazy val rpSummaryRedisClient: ReportPhoneSummaryRedisClient[F] = wire[ReportPhoneSummaryRedisClient[F]]
  protected lazy val rpTopRedisClient: ReportPhoneTopRedisClient[F]         = wire[ReportPhoneTopRedisClientImpl[F]]
  protected lazy val rpLatestRedisClient: ReportPhoneLatestRedisClient[F]   = wire[ReportPhoneLatestRedisClient[F]]

  protected lazy val rpas: ReportPhoneAddService[F]         = wire[ReportPhoneAddServiceImpl[F]]
  protected lazy val rpels: ReportPhoneEntryListService[F]  = wire[ReportPhoneEntryListServiceImpl[F]]
  protected lazy val rplgs: ReportPhoneLatestGetService[F]  = wire[ReportPhoneLatestGetServiceImpl[F]]
  protected lazy val rpsgs: ReportPhoneSummaryGetService[F] = wire[ReportPhoneSummaryGetServiceImpl[F]]
  protected lazy val rptgs: ReportPhoneTopGetService[F]     = wire[ReportPhoneTopGetServiceImpl[F]]

  protected lazy val httpErrorHandler: HttpErrorHandler[F] = wire[HttpErrorHandler[F]]

  lazy val httpApp: HttpApp[F] =
    Seq(
      wire[FailCheckRoute[F]],
      wire[HealthCheckRoute[F]],
      wire[ReportPhoneAddRoute[F]],
      wire[ReportPhoneEntriesListRoute[F]],
      wire[ReportPhoneLatestGetRoute[F]],
      wire[ReportPhoneSummaryGetRoute[F]],
      wire[ReportPhoneTopGetRoute[F]],
    ).map(_.route).reduce(_ <+> _).orNotFound

  protected lazy val httpServerBuilder: HttpServerBuilder[F] = wire[HttpServerBuilder[F]]

}
