package pl.pburcon.phoneSpam.report.summary.redis

import cats.effect.Sync
import cats.implicits._
import dev.profunktor.redis4cats.RedisCommands
import dev.profunktor.redis4cats.data.RedisCodec
import pl.pburcon.phoneSpam.report.common.domain.ReportPhoneDomain.PhoneNumber
import pl.pburcon.phoneSpam.report.summary.domain.ReportPhoneSummary
import pl.pburcon.phoneSpam.util.circe.CirceJsonCodecs
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.redis.codecs.RedisJsonCodecs

class ReportPhoneSummaryRedisClient[F[_]: Sync] extends BaseLogging[F] {

  type SummaryCommands = RedisCommands[F, String, ReportPhoneSummary]

  def pushSummary(phoneNumber: PhoneNumber, summary: ReportPhoneSummary)(implicit rc: SummaryCommands): F[Unit] =
    for {
      summaryKey <- buildKey(phoneNumber)
      _          <- log(logger.debug(s"Adding report summary for '$summaryKey': $summary"))
      _          <- rc.lPush(summaryKey, summary)
    } yield ()

  def trimSummary(phoneNumber: PhoneNumber, start: Long, stop: Long)(implicit rc: SummaryCommands): F[Unit] =
    for {
      summaryKey <- buildKey(phoneNumber)
      _          <- log(logger.debug(s"Trimming report summary for '$summaryKey': [$start:$stop]"))
      _          <- rc.lTrim(summaryKey, start, stop)
    } yield ()

  def fetchSummary(phoneNumber: PhoneNumber, start: Long, stop: Long)(implicit
      rc: SummaryCommands
  ): F[Seq[ReportPhoneSummary]] =
    for {
      summaryKey <- buildKey(phoneNumber)
      _          <- log(logger.debug(s"Retrieving report summary for '$summaryKey': [$start:$stop]"))
      summary    <- rc.lRange(summaryKey, start, stop)
    } yield summary

  private def buildKey(phoneNumber: PhoneNumber) = Sync.delay(PhoneReportSummaryKey(phoneNumber).key)

  private val Sync = implicitly[Sync[F]]
}

object ReportPhoneSummaryRedisCodecs extends RedisJsonCodecs with CirceJsonCodecs {
  import io.circe.generic.auto._
  val codec: RedisCodec[String, ReportPhoneSummary] = deriveRedisJsonCodec
}
