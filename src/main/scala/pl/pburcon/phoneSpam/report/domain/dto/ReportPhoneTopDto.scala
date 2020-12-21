package pl.pburcon.phoneSpam.report.domain.dto

import pl.pburcon.phoneSpam.report.domain.ReportPhoneDomain.{PhoneNumber, ReportCount}

final case class ReportPhoneTopDto(
    phoneNumber: PhoneNumber,
    reportCount: ReportCount
)
