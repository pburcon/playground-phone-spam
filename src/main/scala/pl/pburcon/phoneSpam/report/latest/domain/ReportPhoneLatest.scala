package pl.pburcon.phoneSpam.report.latest.domain

import pl.pburcon.phoneSpam.report.add.domain.dto.ReportPhoneAddRequest
import pl.pburcon.phoneSpam.report.common.domain.ReportPhoneDomain.PhoneNumber

final case class ReportPhoneLatest(
    phoneNumber: PhoneNumber
)

object ReportPhoneLatest {
  def from(request: ReportPhoneAddRequest): ReportPhoneLatest = {
    import io.scalaland.chimney.dsl._
    request.into[ReportPhoneLatest].transform
  }
}
