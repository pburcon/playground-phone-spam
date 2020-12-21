package pl.pburcon.phoneSpam.app.modules

import cats.effect.{ConcurrentEffect, Resource, Timer}
import cats.implicits.toSemigroupKOps
import com.softwaremill.macwire.wire
import fs2.kafka.KafkaProducer
import org.http4s.HttpApp
import org.http4s.server.Server
import org.http4s.syntax.kleisli.http4sKleisliResponseSyntaxOptionT
import pl.pburcon.phoneSpam.app.config.ConfigLoader.load
import pl.pburcon.phoneSpam.app.config.HttpConfig
import pl.pburcon.phoneSpam.healthCheck.routes.{FailCheckRoute, HealthCheckRoute}
import pl.pburcon.phoneSpam.report.config.{ReportPhoneLatestConfig, ReportPhoneSummaryConfig, ReportPhoneTopConfig}
import pl.pburcon.phoneSpam.report.domain.dto.ReportPhoneAddRequest
import pl.pburcon.phoneSpam.report.routes._
import pl.pburcon.phoneSpam.report.services._
import pl.pburcon.phoneSpam.report.services.redis._
import pl.pburcon.phoneSpam.util.http.{HttpErrorHandler, HttpServerBuilder}
import pl.pburcon.phoneSpam.util.redis.RedisClient
import pureconfig.generic.auto._

final class HttpModule[F[_]: ConcurrentEffect: Timer](
    val kafkaReportProducer: KafkaProducer[F, String, ReportPhoneAddRequest],
    val redisClient: RedisClient[F]
) {

  //
  // public module components
  //

  def buildHttpServer(): Resource[F, Server] =
    httpServerBuilder.buildHttpServer()

  //
  // private module components
  //

  protected lazy val httpConfig: HttpConfig = load[HttpConfig]("http")

  protected lazy val rpLatestConfig: ReportPhoneLatestConfig   = load[ReportPhoneLatestConfig]("report-phone.latest")
  protected lazy val rpSummaryConfig: ReportPhoneSummaryConfig = load[ReportPhoneSummaryConfig]("report-phone.summary")
  protected lazy val rpTopConfig: ReportPhoneTopConfig         = load[ReportPhoneTopConfig]("report-phone.top")

  protected lazy val rpSummaryRedisClient: ReportPhoneSummaryRedisClient[F] = wire[ReportPhoneSummaryRedisClient[F]]
  protected lazy val rpTopRedisClient: ReportPhoneTopRedisClient[F]         = wire[ReportPhoneTopRedisClient[F]]
  protected lazy val rpLatestRedisClient: ReportPhoneLatestRedisClient[F]   = wire[ReportPhoneLatestRedisClient[F]]

  protected lazy val rpAddService: ReportPhoneAddService[F]               = wire[ReportPhoneAddServiceImpl[F]]
  protected lazy val rpLatestGetService: ReportPhoneLatestGetService[F]   = wire[ReportPhoneLatestGetServiceImpl[F]]
  protected lazy val rpSummaryGetService: ReportPhoneSummaryGetService[F] = wire[ReportPhoneSummaryGetServiceImpl[F]]
  protected lazy val rpTopGetService: ReportPhoneTopGetService[F]         = wire[ReportPhoneTopGetServiceImpl[F]]

  protected lazy val httpErrorHandler: HttpErrorHandler[F] = wire[HttpErrorHandler[F]]

  protected lazy val httpApp: HttpApp[F] =
    Seq(
      wire[FailCheckRoute[F]],
      wire[HealthCheckRoute[F]],
      wire[ReportPhoneAddRoute[F]],
      wire[ReportPhoneLatestGetRoute[F]],
      wire[ReportPhoneSummaryGetRoute[F]],
      wire[ReportPhoneTopGetRoute[F]],
    ).map(_.route).reduce(_ <+> _).orNotFound

  protected lazy val httpServerBuilder: HttpServerBuilder[F] = wire[HttpServerBuilder[F]]

}
