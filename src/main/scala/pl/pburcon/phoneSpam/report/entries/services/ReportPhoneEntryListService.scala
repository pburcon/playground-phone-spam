package pl.pburcon.phoneSpam.report.entries.services

import cats.effect.Sync
import cats.implicits._
import com.datastax.driver.core.PagingState
import pl.pburcon.phoneSpam.report.common.domain.ReportPhoneDomain.PhoneNumber
import pl.pburcon.phoneSpam.report.entries.cassandra.PhoneNumberEntriesRepository
import pl.pburcon.phoneSpam.report.entries.domain.PhoneNumberEntry
import pl.pburcon.phoneSpam.report.entries.domain.dto.ReportPhoneEntriesListRequest
import pl.pburcon.phoneSpam.report.entries.services.ReportPhoneEntryListService.Result
import pl.pburcon.phoneSpam.util.adt.ADT
import pl.pburcon.phoneSpam.util.cassandra.paging.CassandraPagingDomain.{PagingIsExhausted, PagingStateSerialized}
import pl.pburcon.phoneSpam.util.logging.BaseLogging

import scala.util.control.NonFatal

/**
  * Listing full reports for a given phone number.
  */
trait ReportPhoneEntryListService[F[_]] {

  /**
    * List all reports for a phone number and paging state given in the request.
    */
  def listEntries(request: ReportPhoneEntriesListRequest): F[Result]
}

object ReportPhoneEntryListService {
  sealed trait Result extends ADT[Result]
  object Result {
    final case class Succeeded(
        entries: Seq[PhoneNumberEntry],
        pagingState: Option[PagingStateSerialized],
        isExhausted: PagingIsExhausted
    ) extends Result

    object NotFound extends Result
  }
}

class ReportPhoneEntryListServiceImpl[F[_]: Sync](
    repository: PhoneNumberEntriesRepository[F],
) extends ReportPhoneEntryListService[F]
    with BaseLogging[F] {

  private val fetchSize = 2 // TODO config, and more reasonable default please...

  override def listEntries(request: ReportPhoneEntriesListRequest): F[Result] =
    for {
      _      <- log(logger.info(s"Retrieving entries for ${request.phoneNumber}"))
      result <- fetchEntries(request.phoneNumber, request.pagingState)
      _      <- log(logger.info(s"Finished retrieving entries for ${request.phoneNumber}"))
    } yield result

  private def fetchEntries(phoneNumber: PhoneNumber, maybePS: Option[PagingStateSerialized]) =
    repository
      .fetchByPhoneNumber(phoneNumber, maybePS.map(ps => PagingState.fromString(ps)), fetchSize)
      .map {
        case lr if lr.records.isEmpty =>
          Result.NotFound
        case lr =>
          Result.Succeeded(
            lr.records,
            lr.state.map(PagingStateSerialized(_)),
            PagingIsExhausted(lr.result.isExhausted())
          )
      }
      .onError({
        case NonFatal(e) => log(logger.error(e)(s"Failed retrieving entries for $phoneNumber / $maybePS"))
      })
}
