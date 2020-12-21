package pl.pburcon.phoneSpam.report.services.redis

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import pl.pburcon.phoneSpam.report.domain.ReportPhoneDomain.PhoneNumber
import pl.pburcon.phoneSpam.report.domain.ReportPhoneSummary
import pl.pburcon.phoneSpam.report.domain.keys.PhoneReportSummaryKey
import pl.pburcon.phoneSpam.util.json.ItemsParsing
import pl.pburcon.phoneSpam.util.json.ItemsParsing.parseItems
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.redis.RedisClient
import pl.pburcon.phoneSpam.util.tagged.JsonCodecs

class ReportPhoneSummaryRedisClient[F[_]: Sync] extends BaseLogging[F] with JsonCodecs {

  private def buildKey(phoneNumber: PhoneNumber) = F.delay(PhoneReportSummaryKey(phoneNumber).key)

  def pushSummary(phoneNumber: PhoneNumber, summary: ReportPhoneSummary)(implicit
      rc: RedisClient.StringCommands[F]
  ): F[Unit] =
    for {
      summaryJson <- F.delay(summary.asJson.noSpaces)
      summaryKey  <- buildKey(phoneNumber)
      _           <- log(logger.debug(s"Adding report summary for '$summaryKey': $summaryJson"))
      _           <- rc.lPush(summaryKey, summaryJson)
    } yield ()

  def trimSummary(phoneNumber: PhoneNumber, start: Long, stop: Long)(implicit
      rc: RedisClient.StringCommands[F]
  ): F[Unit] =
    for {
      summaryKey <- buildKey(phoneNumber)
      _          <- log(logger.debug(s"Trimming report summary for '$summaryKey': [$start:$stop]"))
      _          <- rc.lTrim(summaryKey, start, stop)
    } yield ()

  def fetchSummary(phoneNumber: PhoneNumber, start: Long, stop: Long)(implicit
      rc: RedisClient.StringCommands[F]
  ): F[Seq[ReportPhoneSummary]] =
    for {
      summaryKey <- buildKey(phoneNumber)
      _          <- log(logger.debug(s"Retrieving report summary for '$summaryKey': [$start:$stop]"))
      rawSummary <- rc.lRange(summaryKey, start, stop)
      parsed     <- parseSummary(rawSummary, phoneNumber)
    } yield parsed

  private def parseSummary(rawSummary: List[String], phoneNumber: PhoneNumber) =
    for {
      parsing <- F.delay(parseItems[ReportPhoneSummary](rawSummary))
      parsed  <- F.delay(translateReportParsing(parsing, phoneNumber))
    } yield parsed

  // TODO investigate a way of passing the codec to the redis client
  private def translateReportParsing(reportParsing: ItemsParsing[ReportPhoneSummary], phoneNumber: PhoneNumber) =
    reportParsing
      .tapErrors(error => logger.error(error)(s"Failed to parse phone report for $phoneNumber"))
      .getParsedItems

  private val F = implicitly[Sync[F]]
}
