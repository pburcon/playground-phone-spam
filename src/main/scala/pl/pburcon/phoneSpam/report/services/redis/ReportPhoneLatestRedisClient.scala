package pl.pburcon.phoneSpam.report.services.redis

import cats.effect._
import cats.implicits._
import dev.profunktor.redis4cats.effects.{Score, ScoreWithValue}
import io.circe.generic.auto._
import io.circe.syntax._
import pl.pburcon.phoneSpam.report.domain.ReportPhoneLatest
import pl.pburcon.phoneSpam.report.domain.keys.ReportPhoneLatestKey
import pl.pburcon.phoneSpam.util.json.ItemsParsing
import pl.pburcon.phoneSpam.util.json.ItemsParsing.parseItems
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.redis.RedisClient
import pl.pburcon.phoneSpam.util.tagged.JsonCodecs

import java.time.Instant

class ReportPhoneLatestRedisClient[F[_]: Sync] extends BaseLogging[F] with JsonCodecs {

  private val key = ReportPhoneLatestKey.key

  def pushLatest(latest: ReportPhoneLatest)(implicit rc: RedisClient.StringCommands[F]): F[Unit] =
    for {
      latestJson      <- F.delay(latest.asJson.noSpaces)
      latestTimestamp <- F.delay(Instant.now.getEpochSecond)
      latestSWV       <- F.delay(ScoreWithValue(Score(latestTimestamp.toDouble), latestJson))
      _               <- log(logger.debug(s"Pushing latest report $key: $latest"))
      _               <- rc.zAdd(key, None, latestSWV)
    } yield ()

  def trimLatest(size: Long)(implicit rc: RedisClient.StringCommands[F]): F[Unit] =
    for {
      start <- F.pure(0L)
      stop  <- F.delay(-size - 1)
      _     <- log(logger.debug(s"Trimming latest reports $key: [$start:$stop]"))

      // note that ZREMRANGEBYRANK (https://redis.io/commands/zremrangebyrank) technically does not have constant time complexity:
      // "Time complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements removed by the operation."
      // but, given our use-case - a tiny set which should usually have at most one element ready to be removed - we can
      // expect this operation to execute in effectively constant time
      _ <- rc.zRemRangeByRank(key, start, stop)
    } yield ()

  def fetchLatest(start: Long, stop: Long)(implicit rc: RedisClient.StringCommands[F]): F[Seq[ReportPhoneLatest]] =
    for {
      _ <- log(logger.debug(s"Retrieving latest reports $key: [$start:$stop]"))

      // same as above, ZREVRANGE (https://redis.io/commands/zrevrange) can be assumed to execute in effectively constant
      // time given our use-case
      rawLatest <- rc.zRevRange(key, start, stop)
      latest    <- parseLatest(rawLatest)
    } yield latest

  private def parseLatest(rawLatest: List[String]) =
    for {
      parsing <- F.delay(parseItems[ReportPhoneLatest](rawLatest))
      parsed  <- F.delay(translateParsing(parsing))
    } yield parsed

  // TODO investigate a way of passing the codec to the redis client
  private def translateParsing(reportParsing: ItemsParsing[ReportPhoneLatest]) =
    reportParsing
      .tapErrors(error => logger.error(error)(s"Failed to parse latest"))
      .getParsedItems

  private val F: Sync[F] = implicitly[Sync[F]]
}
