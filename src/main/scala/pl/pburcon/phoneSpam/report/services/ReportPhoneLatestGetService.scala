package pl.pburcon.phoneSpam.report.services

import cats.effect.Sync
import cats.implicits._
import pl.pburcon.phoneSpam.report.config.ReportPhoneLatestConfig
import pl.pburcon.phoneSpam.report.domain.ReportPhoneLatest
import pl.pburcon.phoneSpam.report.services.redis.ReportPhoneLatestRedisClient
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.redis.{RedisClient, RedisRange}

trait ReportPhoneLatestGetService[F[_]] {
  def fetchLatest(): F[Seq[ReportPhoneLatest]]
}

class ReportPhoneLatestGetServiceImpl[F[_]: Sync](
    redis: RedisClient[F],
    client: ReportPhoneLatestRedisClient[F],
    latestConfig: ReportPhoneLatestConfig
) extends ReportPhoneLatestGetService[F]
    with BaseLogging[F] {

  override def fetchLatest(): F[Seq[ReportPhoneLatest]] = {
    redis.createCommands().use { implicit rc =>
      for {
        _      <- log(logger.debug("Retrieving latest"))
        range  <- Sync.delay(RedisRange(latestConfig.size).Ascending)
        latest <- client.fetchLatest(range.start, range.stop)
        _      <- log(logger.debug("Successfully finished fetching latest"))
      } yield latest
    }
  }

  private val Sync = implicitly[Sync[F]]
}
