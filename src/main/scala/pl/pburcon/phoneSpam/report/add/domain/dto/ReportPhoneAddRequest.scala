package pl.pburcon.phoneSpam.report.add.domain.dto

import pl.pburcon.phoneSpam.report.common.domain.ReportPhoneDomain.{PhoneNumber, PhoneNumberRating, ReportComment, UserName}

final case class ReportPhoneAddRequest(
    phoneNumber: PhoneNumber,
    user: UserName,
    rating: PhoneNumberRating,
    comment: ReportComment
)
