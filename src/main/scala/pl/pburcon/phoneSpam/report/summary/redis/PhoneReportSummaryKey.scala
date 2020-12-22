package pl.pburcon.phoneSpam.report.summary.redis

import pl.pburcon.phoneSpam.report.common.domain.ReportPhoneDomain.PhoneNumber
import pl.pburcon.phoneSpam.util.redis.domain.RedisKey

final case class PhoneReportSummaryKey(phoneNumber: PhoneNumber) extends RedisKey {
  val key: String = s"reports:$phoneNumber:summaries"
}
