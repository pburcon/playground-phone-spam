package pl.pburcon.phoneSpam.report.domain.dto

import pl.pburcon.phoneSpam.report.domain.ReportPhoneDomain.PhoneNumber

final case class ReportPhoneSummaryGetRequest(
    phoneNumber: PhoneNumber,
)
