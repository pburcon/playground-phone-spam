package pl.pburcon.phoneSpam.report.top.redis

import cats.effect.Sync
import cats.implicits._
import dev.profunktor.redis4cats.RedisCommands
import dev.profunktor.redis4cats.data.RedisCodec
import dev.profunktor.redis4cats.effects.ScoreWithValue
import pl.pburcon.phoneSpam.report.common.domain.ReportPhoneDomain.ReportCount
import pl.pburcon.phoneSpam.report.top.domain.ReportPhoneTop
import pl.pburcon.phoneSpam.report.top.domain.dto.ReportPhoneTopDto
import pl.pburcon.phoneSpam.util.circe.CirceJsonCodecs
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.redis.codecs.RedisJsonCodecs

trait ReportPhoneTopRedisClient[F[_]] {
  final type TopCommands = RedisCommands[F, String, ReportPhoneTop]

  def updateTop(top: ReportPhoneTop)(implicit commands: TopCommands): F[Unit]
  def fetchTop(start: Long, stop: Long)(implicit commands: TopCommands): F[Seq[ReportPhoneTopDto]]
}

class ReportPhoneTopRedisClientImpl[F[_]: Sync] extends ReportPhoneTopRedisClient[F] with BaseLogging[F] {

  private val key         = PhoneReportTopKey.key
  private val incrementBy = 1.0

  def updateTop(top: ReportPhoneTop)(implicit commands: TopCommands): F[Unit] =
    for {
      _ <- log(logger.debug(s"Updating top $key with: $top"))
      _ <- commands.zIncrBy(key, top, incrementBy)
    } yield ()

  def fetchTop(start: Long, stop: Long)(implicit commands: TopCommands): F[Seq[ReportPhoneTopDto]] =
    for {
      _      <- log(logger.debug(s"Fetching top $key: [$start:$stop]"))
      rawTop <- commands.zRevRangeWithScores(key, start, stop)
    } yield parseTop(rawTop)

  private def parseTop(rawTop: List[ScoreWithValue[ReportPhoneTop]]) =
    rawTop.map(swv => buildDto(swv.value, swv.score.value))

  private def buildDto(top: ReportPhoneTop, count: Double) =
    ReportPhoneTopDto(top.phoneNumber, ReportCount(count.toLong))

}

object ReportPhoneTopRedisCodecs extends RedisJsonCodecs with CirceJsonCodecs {
  import io.circe.generic.auto._
  val codec: RedisCodec[String, ReportPhoneTop] = deriveRedisJsonCodec
}
