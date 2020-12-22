package pl.pburcon.phoneSpam.report.summary.domain.dto

import pl.pburcon.phoneSpam.report.common.domain.ReportPhoneDomain.PhoneNumber

final case class ReportPhoneSummaryGetRequest(
    phoneNumber: PhoneNumber,
)
