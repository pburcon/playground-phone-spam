package pl.pburcon.phoneSpam.report.entries.services

import cats.effect.{Async, Sync, Timer}
import cats.implicits._
import pl.pburcon.phoneSpam.report.add.domain.dto.ReportPhoneAddRequest
import pl.pburcon.phoneSpam.report.add.services.ReportPhoneAddService.Result
import pl.pburcon.phoneSpam.report.common.domain.ReportPhoneDomain.PhoneNumberEntryId
import pl.pburcon.phoneSpam.report.entries.cassandra.PhoneNumberEntriesRepository
import pl.pburcon.phoneSpam.report.entries.domain.PhoneNumberEntry
import pl.pburcon.phoneSpam.util.adt.ADT
import pl.pburcon.phoneSpam.util.cats.effect.TimerInstant
import pl.pburcon.phoneSpam.util.logging.BaseLogging

import java.time.Instant
import scala.util.control.NonFatal

trait ReportPhoneEntryAddService[F[_]] {
  def addEntry(entry: ReportPhoneAddRequest): F[Result]
}

object ReportPhoneEntryAddService {
  sealed trait Result extends ADT[Result]
  object Result {
    case object Succeeded extends Result
  }
}

class ReportPhoneEntryAddServiceImpl[F[_]: Async: Timer](
    repository: PhoneNumberEntriesRepository[F],
) extends ReportPhoneEntryAddService[F]
    with BaseLogging[F] {

  override def addEntry(request: ReportPhoneAddRequest): F[Result] =
    (for {
      _     <- log(logger.info(s"Adding report entry for ${request.phoneNumber} by ${request.user}"))
      now   <- TimerInstant.now[F]()
      entry <- buildEntry(request, now)
      _     <- insertEntry(entry)
      _     <- log(logger.info(s"Finished adding report entry for ${request.phoneNumber} by ${request.user}"))
    } yield Result.Succeeded.adt).onError({
      case NonFatal(e) =>
        log(logger.error(e)(s"Failed adding report entry for ${request.phoneNumber} by ${request.user}"))
    })

  private def buildEntry(request: ReportPhoneAddRequest, now: Instant) =
    Sync.delay {
      import io.scalaland.chimney.dsl._
      request
        .into[PhoneNumberEntry]
        .withFieldConst(_.id, PhoneNumberEntryId.generate)
        .withFieldConst(_.timestamp, now)
        .transform
    }

  private def insertEntry(entry: PhoneNumberEntry) =
    repository.insertEntry(entry)

  private implicit val Sync: Sync[F] = implicitly[Sync[F]]
}
