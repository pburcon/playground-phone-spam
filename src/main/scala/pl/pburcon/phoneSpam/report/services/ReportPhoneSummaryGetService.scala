package pl.pburcon.phoneSpam.report.services

import cats.effect.Sync
import cats.implicits._
import pl.pburcon.phoneSpam.report.config.ReportPhoneSummaryConfig
import pl.pburcon.phoneSpam.report.domain.ReportPhoneSummary
import pl.pburcon.phoneSpam.report.domain.dto.ReportPhoneSummaryGetRequest
import pl.pburcon.phoneSpam.report.services.redis.ReportPhoneSummaryRedisClient
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.redis.{RedisClient, RedisRange}
import pl.pburcon.phoneSpam.util.tagged.JsonCodecs

trait ReportPhoneSummaryGetService[F[_]] {
  def findReports(request: ReportPhoneSummaryGetRequest): F[Seq[ReportPhoneSummary]]
}

class ReportPhoneSummaryGetServiceImpl[F[_]: Sync](
    redisClient: RedisClient[F],
    summaryRedisClient: ReportPhoneSummaryRedisClient[F],
    config: ReportPhoneSummaryConfig
) extends ReportPhoneSummaryGetService[F]
    with BaseLogging[F]
    with JsonCodecs {

  def findReports(request: ReportPhoneSummaryGetRequest): F[Seq[ReportPhoneSummary]] =
    redisClient.createCommands().use { implicit rc =>
      for {
        _       <- log(logger.debug(s"Find reports for number ${request.phoneNumber}"))
        range   <- Sync.delay(RedisRange(config.size).Ascending)
        reports <- summaryRedisClient.fetchSummary(request.phoneNumber, range.start, range.stop)
        _       <- log(logger.debug(s"Found ${reports.length} reports for number ${request.phoneNumber}"))
      } yield reports
    }

  private val Sync = implicitly[Sync[F]]
}
