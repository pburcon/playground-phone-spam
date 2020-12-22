package pl.pburcon.phoneSpam.report.summary.services

import cats.effect.Sync
import cats.implicits._
import pl.pburcon.phoneSpam.report.add.domain.dto.ReportPhoneAddRequest
import pl.pburcon.phoneSpam.report.add.services.ReportPhoneAddService.Result
import pl.pburcon.phoneSpam.report.summary.config.ReportPhoneSummaryConfig
import pl.pburcon.phoneSpam.report.summary.domain.ReportPhoneSummary
import pl.pburcon.phoneSpam.report.summary.redis.{ReportPhoneSummaryRedisClient, ReportPhoneSummaryRedisCodecs}
import pl.pburcon.phoneSpam.util.adt.ADT
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.redis.RedisClient
import pl.pburcon.phoneSpam.util.redis.domain.RedisRange

import scala.util.control.NonFatal

trait ReportPhoneSummaryUpdateService[F[_]] {
  def updateSummary(request: ReportPhoneAddRequest): F[Result]
}

class ReportPhoneSummaryUpdateServiceImpl[F[_]: Sync](
    redisClient: RedisClient[F],
    summaryRedisClient: ReportPhoneSummaryRedisClient[F],
    summaryConfig: ReportPhoneSummaryConfig,
) extends ReportPhoneSummaryUpdateService[F]
    with BaseLogging[F] {

  def updateSummary(request: ReportPhoneAddRequest): F[Result] =
    redisClient
      .createCommands(ReportPhoneSummaryRedisCodecs.codec)
      .use { implicit rc =>
        for {
          summary      <- Sync.delay(ReportPhoneSummary.from(request))
          _            <- summaryRedisClient.pushSummary(request.phoneNumber, summary)
          summaryRange <- Sync.delay(RedisRange(summaryConfig.size).Ascending)
          _            <- summaryRedisClient.trimSummary(request.phoneNumber, summaryRange.start, summaryRange.stop)
        } yield Result.Succeeded.adt
      }
      .onError({ case NonFatal(t) => log(logger.error(t)("Updating summary failed")) })

  private val Sync = implicitly[Sync[F]]
}

object ReportPhoneSummaryUpdateService {
  sealed trait Result extends ADT[Result]
  object Result {
    final case object Succeeded extends Result
  }
}
