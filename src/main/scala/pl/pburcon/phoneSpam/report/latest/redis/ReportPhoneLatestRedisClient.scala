package pl.pburcon.phoneSpam.report.latest.redis

import cats.effect._
import cats.implicits._
import dev.profunktor.redis4cats.RedisCommands
import dev.profunktor.redis4cats.data.RedisCodec
import dev.profunktor.redis4cats.effects.{Score, ScoreWithValue}
import pl.pburcon.phoneSpam.report.latest.domain.ReportPhoneLatest
import pl.pburcon.phoneSpam.util.circe.CirceJsonCodecs
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.redis.codecs.RedisJsonCodecs

import java.time.Instant

class ReportPhoneLatestRedisClient[F[_]: Sync] extends BaseLogging[F] {

  type LatestCommands = RedisCommands[F, String, ReportPhoneLatest]

  def pushLatest(latest: ReportPhoneLatest)(implicit rc: LatestCommands): F[Unit] =
    for {
      latestSwv <- Sync.delay(ScoreWithValue(Score(Instant.now.getEpochSecond.toDouble), latest))
      _         <- log(logger.debug(s"Pushing latest report $key: $latest"))
      _         <- rc.zAdd(key, None, latestSwv)
    } yield ()

  def trimLatest(size: Long)(implicit rc: LatestCommands): F[Unit] =
    for {
      start <- Sync.pure(0L)
      stop  <- Sync.delay(-size - 1)
      _     <- log(logger.debug(s"Trimming latest reports $key: [$start:$stop]"))

      // note that ZREMRANGEBYRANK (https://redis.io/commands/zremrangebyrank) technically does not have constant time complexity:
      // "Time complexity: O(log(N)+M) with N being the number of elements in the sorted set and M the number of elements removed by the operation."
      // but, given our use-case - a tiny set which should usually have at most one element ready to be removed - we can
      // expect this operation to execute in effectively constant time
      _ <- rc.zRemRangeByRank(key, start, stop)
    } yield ()

  def fetchLatest(start: Long, stop: Long)(implicit rc: LatestCommands): F[Seq[ReportPhoneLatest]] =
    for {
      _ <- log(logger.debug(s"Retrieving latest reports $key: [$start:$stop]"))

      // same as above, ZREVRANGE (https://redis.io/commands/zrevrange) can be assumed to execute in effectively constant
      // time given our use-case
      latest <- rc.zRevRange(key, start, stop)
    } yield latest

  private val key = ReportPhoneLatestKey.key

  private val Sync: Sync[F] = implicitly[Sync[F]]
}

object ReportPhoneLatestRedisCodecs extends RedisJsonCodecs with CirceJsonCodecs {
  import io.circe.generic.auto._
  val codec: RedisCodec[String, ReportPhoneLatest] = deriveRedisJsonCodec
}
