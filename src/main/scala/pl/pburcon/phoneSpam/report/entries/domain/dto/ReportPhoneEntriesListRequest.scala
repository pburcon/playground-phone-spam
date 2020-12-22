package pl.pburcon.phoneSpam.report.entries.domain.dto

import pl.pburcon.phoneSpam.report.common.domain.ReportPhoneDomain.PhoneNumber
import pl.pburcon.phoneSpam.util.cassandra.paging.CassandraPagingDomain.PagingStateSerialized

final case class ReportPhoneEntriesListRequest(
    phoneNumber: PhoneNumber,
    pagingState: Option[PagingStateSerialized]
)
