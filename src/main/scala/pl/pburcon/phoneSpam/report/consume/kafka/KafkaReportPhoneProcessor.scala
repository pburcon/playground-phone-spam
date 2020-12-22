package pl.pburcon.phoneSpam.report.consume.kafka

import cats.Parallel
import cats.effect.Sync
import cats.implicits._
import pl.pburcon.phoneSpam.report.add.domain.dto.ReportPhoneAddRequest
import pl.pburcon.phoneSpam.report.latest.services.ReportPhoneLatestUpdateService
import pl.pburcon.phoneSpam.report.entries.services._
import pl.pburcon.phoneSpam.report.summary.services.ReportPhoneSummaryUpdateService
import pl.pburcon.phoneSpam.report.top.services.ReportPhoneTopUpdateService
import pl.pburcon.phoneSpam.util.logging.BaseLogging

import scala.util.control.NonFatal

trait KafkaReportPhoneProcessor[F[_]] {
  def process(reportPhoneAddRequest: ReportPhoneAddRequest): F[Unit]
}

class KafkaReportPhoneProcessorImpl[F[_]: Sync: Parallel](
    reportPhoneEntryAddService: ReportPhoneEntryAddService[F],
    reportPhoneLatestUpdateService: ReportPhoneLatestUpdateService[F],
    reportPhoneSummaryUpdateService: ReportPhoneSummaryUpdateService[F],
    reportPhoneTopUpdateService: ReportPhoneTopUpdateService[F],
) extends KafkaReportPhoneProcessor[F]
    with BaseLogging[F] {

  def process(request: ReportPhoneAddRequest): F[Unit] =
    (for {
      _ <- log(logger.debug(s"Processing ${request.phoneNumber} by ${request.user}"))
      _ <- processInParallel(request)
      _ <- log(logger.debug(s"Processing ${request.phoneNumber} by ${request.user} finished"))
    } yield ()).recover({
      case NonFatal(t) => logger.error(t)(s"Processing  ${request.phoneNumber} by ${request.user} failed")
    })

  private def processInParallel(request: ReportPhoneAddRequest): F[Unit] =
    (
      reportPhoneEntryAddService.addEntry(request),
      reportPhoneLatestUpdateService.updateLatest(request),
      reportPhoneSummaryUpdateService.updateSummary(request),
      reportPhoneTopUpdateService.updateTop(request)
    ).parTupled.void

}
