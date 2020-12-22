package pl.pburcon.phoneSpam.report.entries.domain

import pl.pburcon.phoneSpam.report.common.domain.ReportPhoneDomain._

import java.time.Instant

final case class PhoneNumberEntry(
    id: PhoneNumberEntryId,
    phoneNumber: PhoneNumber,
    timestamp: Instant,
    user: UserName,
    comment: ReportComment,
    rating: PhoneNumberRating,
)
