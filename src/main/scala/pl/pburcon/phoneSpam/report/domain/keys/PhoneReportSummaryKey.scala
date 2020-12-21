package pl.pburcon.phoneSpam.report.domain.keys

import pl.pburcon.phoneSpam.report.domain.ReportPhoneDomain.PhoneNumber
import pl.pburcon.phoneSpam.util.redis.RedisKey

final case class PhoneReportSummaryKey(phoneNumber: PhoneNumber) extends RedisKey {
  val key: String = s"reports:$phoneNumber:summaries"
}
