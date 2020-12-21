package pl.pburcon.phoneSpam.report.services

import cats.effect.Sync
import cats.implicits._
import pl.pburcon.phoneSpam.report.config.{ReportPhoneLatestConfig, ReportPhoneSummaryConfig}
import pl.pburcon.phoneSpam.report.domain.dto.ReportPhoneAddRequest
import pl.pburcon.phoneSpam.report.domain.{ReportPhoneLatest, ReportPhoneSummary, ReportPhoneTop}
import pl.pburcon.phoneSpam.report.services.ReportPhoneAddService.Result
import pl.pburcon.phoneSpam.report.services.redis._
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.redis.{RedisClient, RedisRange}
import pl.pburcon.phoneSpam.util.tagged.JsonCodecs

trait ReportPhoneAddService[F[_]] {
  def addReport(request: ReportPhoneAddRequest): F[Result]
}

class ReportPhoneAddServiceImpl[F[_]: Sync](
    redisClient: RedisClient[F],
    latestRedisClient: ReportPhoneLatestRedisClient[F],
    summaryRedisClient: ReportPhoneSummaryRedisClient[F],
    topRedisClient: ReportPhoneTopRedisClient[F],
//    reportPhoneProducer: KafkaProducer[F, String, ReportPhoneAddRequest],
    latestConfig: ReportPhoneLatestConfig,
    summaryConfig: ReportPhoneSummaryConfig
) extends ReportPhoneAddService[F]
    with BaseLogging[F]
    with JsonCodecs {

//  def addReport(request: ReportPhoneAddRequest): F[Result] =
//    reportPhoneProducer.produce() { implicit rc =>
//      for {
//        _ <- log(logger.debug(s"Reporting ${request.phoneNumber} by ${request.user}"))
//        _ <- updateLatest(request)
//        _ <- updateSummary(request)
//        _ <- updateTop(request)
//        _ <- log(logger.debug(s"Reporting ${request.phoneNumber} by ${request.user} finished"))
//      } yield Result.Succeeded
//    }

  def addReport(request: ReportPhoneAddRequest): F[Result] =
    redisClient.createCommands().use { implicit rc =>
      for {
        _ <- log(logger.debug(s"Reporting ${request.phoneNumber} by ${request.user}"))
        _ <- updateLatest(request)
        _ <- updateSummary(request)
        _ <- updateTop(request)
        _ <- log(logger.debug(s"Reporting ${request.phoneNumber} by ${request.user} finished"))
      } yield Result.Succeeded
    }

  private def updateLatest(request: ReportPhoneAddRequest)(implicit rc: RedisClient.StringCommands[F]) =
    for {
      latest <- Sync.delay(ReportPhoneLatest.from(request))
      _      <- latestRedisClient.pushLatest(latest)
      _      <- latestRedisClient.trimLatest(latestConfig.size)
    } yield ()

  private def updateSummary(request: ReportPhoneAddRequest)(implicit rc: RedisClient.StringCommands[F]) =
    for {
      summary      <- Sync.delay(ReportPhoneSummary.from(request))
      _            <- summaryRedisClient.pushSummary(request.phoneNumber, summary)
      summaryRange <- Sync.delay(RedisRange(summaryConfig.size).Ascending)
      _            <- summaryRedisClient.trimSummary(request.phoneNumber, summaryRange.start, summaryRange.stop)
    } yield ()

  private def updateTop(request: ReportPhoneAddRequest)(implicit rc: RedisClient.StringCommands[F]) =
    for {
      top <- Sync.delay(ReportPhoneTop.from(request))
      _   <- topRedisClient.updateTop(top)
    } yield ()

  private val Sync = implicitly[Sync[F]]
}

object ReportPhoneAddService {
  sealed trait Result
  object Result {
    final case object Succeeded extends Result
  }
}
