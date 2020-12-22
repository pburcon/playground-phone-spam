package pl.pburcon.phoneSpam.report.top.domain

import pl.pburcon.phoneSpam.report.add.domain.dto.ReportPhoneAddRequest
import pl.pburcon.phoneSpam.report.common.domain.ReportPhoneDomain.PhoneNumber

final case class ReportPhoneTop(
    phoneNumber: PhoneNumber
)

object ReportPhoneTop {
  def from(request: ReportPhoneAddRequest): ReportPhoneTop = {
    import io.scalaland.chimney.dsl._
    request.into[ReportPhoneTop].transform
  }
}
