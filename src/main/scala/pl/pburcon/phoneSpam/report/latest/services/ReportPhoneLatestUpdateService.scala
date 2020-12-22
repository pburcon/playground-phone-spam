package pl.pburcon.phoneSpam.report.latest.services

import cats.effect.Sync
import cats.implicits._
import pl.pburcon.phoneSpam.report.add.domain.dto.ReportPhoneAddRequest
import pl.pburcon.phoneSpam.report.latest.config.ReportPhoneLatestConfig
import pl.pburcon.phoneSpam.report.latest.domain.ReportPhoneLatest
import pl.pburcon.phoneSpam.report.latest.redis.{ReportPhoneLatestRedisClient, ReportPhoneLatestRedisCodecs}
import pl.pburcon.phoneSpam.report.latest.services.ReportPhoneLatestUpdateService.Result
import pl.pburcon.phoneSpam.util.adt.ADT
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.redis.RedisClient

import scala.util.control.NonFatal

trait ReportPhoneLatestUpdateService[F[_]] {
  def updateLatest(request: ReportPhoneAddRequest): F[Result]
}

class ReportPhoneLatestUpdateServiceImpl[F[_]: Sync](
    redisClient: RedisClient[F],
    latestRedisClient: ReportPhoneLatestRedisClient[F],
    latestConfig: ReportPhoneLatestConfig,
) extends ReportPhoneLatestUpdateService[F]
    with BaseLogging[F] {

  override def updateLatest(request: ReportPhoneAddRequest): F[Result] =
    redisClient
      .createCommands(ReportPhoneLatestRedisCodecs.codec)
      .use { implicit rc =>
        for {
          latest <- Sync.delay(ReportPhoneLatest.from(request))
          _      <- log(logger.info(s"Updating latest: $latest"))
          _      <- latestRedisClient.pushLatest(latest)
          _      <- latestRedisClient.trimLatest(latestConfig.size)
          _      <- log(logger.info(s"Updating latest finished: $latest"))
        } yield Result.Succeeded.adt
      }
      .onError({ case NonFatal(t) => log(logger.error(t)("Updating latest failed")) })

  private val Sync = implicitly[Sync[F]]
}

object ReportPhoneLatestUpdateService {
  sealed trait Result extends ADT[Result]
  object Result {
    final case object Succeeded extends Result
  }
}
