package pl.pburcon.phoneSpam.report.top.services

import cats.effect.Sync
import cats.implicits._
import pl.pburcon.phoneSpam.report.add.domain.dto.ReportPhoneAddRequest
import pl.pburcon.phoneSpam.report.add.services.ReportPhoneAddService.Result
import pl.pburcon.phoneSpam.report.top.domain.ReportPhoneTop
import pl.pburcon.phoneSpam.report.top.redis.{ReportPhoneTopRedisClient, ReportPhoneTopRedisCodecs}
import pl.pburcon.phoneSpam.util.adt.ADT
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.redis.RedisClient

import scala.util.control.NonFatal

trait ReportPhoneTopUpdateService[F[_]] {
  def updateTop(request: ReportPhoneAddRequest): F[Result]
}

class ReportPhoneTopUpdateServiceImpl[F[_]: Sync](
    redisClient: RedisClient[F],
    topRedisClient: ReportPhoneTopRedisClient[F],
) extends ReportPhoneTopUpdateService[F]
    with BaseLogging[F] {

  override def updateTop(request: ReportPhoneAddRequest): F[Result] =
    redisClient
      .createCommands(ReportPhoneTopRedisCodecs.codec)
      .use { implicit rc =>
        for {
          top <- Sync.delay(ReportPhoneTop.from(request))
          _   <- topRedisClient.updateTop(top)
        } yield Result.Succeeded.adt
      }
      .onError({ case NonFatal(t) => log(logger.error(t)("Updating top failed")) })

  private val Sync = implicitly[Sync[F]]
}

object ReportPhoneReportPhoneTopUpdateService {
  sealed trait Result extends ADT[Result]
  object Result {
    final case object Succeeded extends Result
  }
}
