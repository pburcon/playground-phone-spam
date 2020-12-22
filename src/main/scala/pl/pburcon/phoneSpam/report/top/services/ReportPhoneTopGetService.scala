package pl.pburcon.phoneSpam.report.top.services

import cats.effect.Sync
import cats.implicits._
import pl.pburcon.phoneSpam.report.top.config.ReportPhoneTopConfig
import pl.pburcon.phoneSpam.report.top.domain.dto.ReportPhoneTopDto
import pl.pburcon.phoneSpam.report.top.redis.{ReportPhoneTopRedisClient, ReportPhoneTopRedisCodecs}
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.redis.RedisClient
import pl.pburcon.phoneSpam.util.redis.domain.RedisRange

trait ReportPhoneTopGetService[F[_]] {
  def fetchTop(): F[Seq[ReportPhoneTopDto]]
}

class ReportPhoneTopGetServiceImpl[F[_]: Sync](
    redisClient: RedisClient[F],
    client: ReportPhoneTopRedisClient[F],
    topConfig: ReportPhoneTopConfig
) extends ReportPhoneTopGetService[F]
    with BaseLogging[F] {

  override def fetchTop(): F[Seq[ReportPhoneTopDto]] =
    redisClient.createCommands(ReportPhoneTopRedisCodecs.codec).use { implicit rc =>
      for {
        _     <- log(logger.debug("Retrieving top"))
        range <- Sync.delay(RedisRange(topConfig.size).Ascending)
        top   <- client.fetchTop(range.start, range.stop)
        _     <- log(logger.debug("Successfully finished fetching top"))
      } yield top
    }

  private val Sync = implicitly[Sync[F]]

}
