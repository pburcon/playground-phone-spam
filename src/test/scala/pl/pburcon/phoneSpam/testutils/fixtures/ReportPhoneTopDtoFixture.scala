package pl.pburcon.phoneSpam.testutils.fixtures

import pl.pburcon.phoneSpam.report.common.domain.ReportPhoneDomain.{PhoneNumber, ReportCount}
import pl.pburcon.phoneSpam.report.top.domain.dto.ReportPhoneTopDto

trait ReportPhoneTopDtoFixture extends CommonFixture {

  def randomTop(
      phoneNumber: PhoneNumber = randomStringTagged,
      reportCount: ReportCount = randomLongTagged
  ): ReportPhoneTopDto = ReportPhoneTopDto(phoneNumber, reportCount)

}
object ReportPhoneTopDtoFixture
