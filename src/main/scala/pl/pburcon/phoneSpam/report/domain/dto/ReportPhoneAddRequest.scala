package pl.pburcon.phoneSpam.report.domain.dto

import pl.pburcon.phoneSpam.report.domain.ReportPhoneDomain.{PhoneNumber, ReportComment, ReportRating, UserName}

final case class ReportPhoneAddRequest(
    phoneNumber: PhoneNumber,
    user: UserName,
    rating: ReportRating,
    comment: ReportComment
)
