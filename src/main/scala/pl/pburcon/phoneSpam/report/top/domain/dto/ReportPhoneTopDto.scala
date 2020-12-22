package pl.pburcon.phoneSpam.report.top.domain.dto

import pl.pburcon.phoneSpam.report.common.domain.ReportPhoneDomain.{PhoneNumber, ReportCount}

final case class ReportPhoneTopDto(
    phoneNumber: PhoneNumber,
    reportCount: ReportCount
)
