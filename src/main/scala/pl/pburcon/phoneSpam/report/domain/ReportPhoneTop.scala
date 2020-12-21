package pl.pburcon.phoneSpam.report.domain

import pl.pburcon.phoneSpam.report.domain.ReportPhoneDomain.PhoneNumber
import pl.pburcon.phoneSpam.report.domain.dto.ReportPhoneAddRequest

final case class ReportPhoneTop(
    phoneNumber: PhoneNumber
)

object ReportPhoneTop {
  def from(request: ReportPhoneAddRequest): ReportPhoneTop = {
    import io.scalaland.chimney.dsl._
    request.into[ReportPhoneTop].transform
  }
}
