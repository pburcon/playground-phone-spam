package pl.pburcon.phoneSpam.report.services.redis

import cats.effect.Sync
import cats.implicits._
import dev.profunktor.redis4cats.effects
import io.circe.generic.auto._
import io.circe.syntax._
import pl.pburcon.phoneSpam.report.domain.ReportPhoneDomain.ReportCount
import pl.pburcon.phoneSpam.report.domain.ReportPhoneTop
import pl.pburcon.phoneSpam.report.domain.dto.ReportPhoneTopDto
import pl.pburcon.phoneSpam.report.domain.keys.PhoneReportTopKey
import pl.pburcon.phoneSpam.util.json.ItemsParsing
import pl.pburcon.phoneSpam.util.json.ItemsParsing.parseItemsWithScore
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.redis.RedisClient
import pl.pburcon.phoneSpam.util.tagged.JsonCodecs

class ReportPhoneTopRedisClient[F[_]: Sync] extends BaseLogging[F] with JsonCodecs {

  private val key         = PhoneReportTopKey.key
  private val incrementBy = 1.0

  def updateTop(top: ReportPhoneTop)(implicit rc: RedisClient.StringCommands[F]): F[Unit] =
    for {
      topJson <- F.delay(top.asJson.noSpaces)
      _       <- log(logger.debug(s"Updating top $key with: $topJson"))
      _       <- rc.zIncrBy(key, topJson, incrementBy)
    } yield ()

  def fetchTop(start: Long, stop: Long)(implicit rc: RedisClient.StringCommands[F]): F[Seq[ReportPhoneTopDto]] =
    for {
      _      <- log(logger.debug(s"Fetching top $key: [$start:$stop]"))
      rawTop <- rc.zRevRangeWithScores(key, start, stop)
      top    <- parseTop(rawTop)
    } yield top

  private def parseTop(rawTop: List[effects.ScoreWithValue[String]]) =
    for {
      parsing <- F.delay(parseItemsWithScore(rawTop, buildDto))
      parsed  <- F.delay(translateReportParsing(parsing))
    } yield parsed

  private def buildDto(top: ReportPhoneTop, count: Double) =
    ReportPhoneTopDto(top.phoneNumber, ReportCount(count.toLong))

  // TODO investigate a way of passing the codec to the redis client
  private def translateReportParsing(reportParsing: ItemsParsing[ReportPhoneTopDto]) =
    reportParsing
      .tapErrors(e => logger.error(e)(s"Failed to parse report top"))
      .getParsedItems

  private val F = implicitly[Sync[F]]
}
