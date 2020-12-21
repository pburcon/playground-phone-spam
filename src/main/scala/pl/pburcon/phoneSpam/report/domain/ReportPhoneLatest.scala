package pl.pburcon.phoneSpam.report.domain

import pl.pburcon.phoneSpam.report.domain.ReportPhoneDomain.PhoneNumber
import pl.pburcon.phoneSpam.report.domain.dto.ReportPhoneAddRequest

final case class ReportPhoneLatest(
    phoneNumber: PhoneNumber
)

object ReportPhoneLatest {
  def from(request: ReportPhoneAddRequest): ReportPhoneLatest = {
    import io.scalaland.chimney.dsl._
    request.into[ReportPhoneLatest].transform
  }
}
