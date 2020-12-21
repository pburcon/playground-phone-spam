package pl.pburcon.phoneSpam.report.domain

import pl.pburcon.phoneSpam.report.domain.ReportPhoneDomain.{ReportComment, ReportRating, UserName}
import pl.pburcon.phoneSpam.report.domain.dto.ReportPhoneAddRequest

final case class ReportPhoneSummary(
    user: UserName,
    rating: ReportRating,
    comment: ReportComment
)

object ReportPhoneSummary {
  def from(request: ReportPhoneAddRequest): ReportPhoneSummary = {
    import io.scalaland.chimney.dsl._
    request.into[ReportPhoneSummary].transform
  }
}
