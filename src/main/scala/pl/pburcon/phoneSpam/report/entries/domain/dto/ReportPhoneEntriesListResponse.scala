package pl.pburcon.phoneSpam.report.entries.domain.dto

import pl.pburcon.phoneSpam.report.entries.domain.PhoneNumberEntry
import pl.pburcon.phoneSpam.report.entries.services.ReportPhoneEntryListService
import pl.pburcon.phoneSpam.util.cassandra.paging.CassandraPagingDomain.{PagingIsExhausted, PagingStateSerialized}

final case class ReportPhoneEntriesListResponse(
    entries: Seq[PhoneNumberEntry],
    pagingState: Option[PagingStateSerialized],
    isExhausted: PagingIsExhausted
)

object ReportPhoneEntriesListResponse {
  def from(result: ReportPhoneEntryListService.Result.Succeeded): ReportPhoneEntriesListResponse = {
    import io.scalaland.chimney.dsl._
    result.into[ReportPhoneEntriesListResponse].transform
  }
}
