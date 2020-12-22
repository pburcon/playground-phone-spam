package pl.pburcon.phoneSpam.report.add.services

import cats.effect.Sync
import cats.implicits._
import pl.pburcon.phoneSpam.report.add.domain.dto.ReportPhoneAddRequest
import pl.pburcon.phoneSpam.report.add.services.ReportPhoneAddService.Result
import pl.pburcon.phoneSpam.report.produce.kafka.KafkaReportPhoneProducer
import pl.pburcon.phoneSpam.util.adt.ADT
import pl.pburcon.phoneSpam.util.logging.BaseLogging

trait ReportPhoneAddService[F[_]] {
  def addReport(request: ReportPhoneAddRequest): F[Result]
}

class ReportPhoneAddServiceImpl[F[_]: Sync](
    reportPhoneProducer: KafkaReportPhoneProducer[F],
) extends ReportPhoneAddService[F]
    with BaseLogging[F] {

  def addReport(request: ReportPhoneAddRequest): F[Result] =
    for {
      _ <- log(logger.info(s"Reporting ${request.phoneNumber} by ${request.user}"))
      _ <- reportPhoneProducer.produceOne(request)
      _ <- log(logger.info(s"Reporting ${request.phoneNumber} by ${request.user} finished"))
    } yield Result.Succeeded

}

object ReportPhoneAddService {
  sealed trait Result extends ADT[Result]
  object Result {
    final case object Succeeded extends Result
  }
}
