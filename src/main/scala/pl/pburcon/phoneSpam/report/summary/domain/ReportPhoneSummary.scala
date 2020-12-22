package pl.pburcon.phoneSpam.report.summary.domain

import pl.pburcon.phoneSpam.report.add.domain.dto.ReportPhoneAddRequest
import pl.pburcon.phoneSpam.report.common.domain.ReportPhoneDomain.{PhoneNumberRating, ReportComment, UserName}

final case class ReportPhoneSummary(
    user: UserName,
    rating: PhoneNumberRating,
    comment: ReportComment
)

object ReportPhoneSummary {
  def from(request: ReportPhoneAddRequest): ReportPhoneSummary = {
    import io.scalaland.chimney.dsl._
    request.into[ReportPhoneSummary].transform
  }
}
