package pl.pburcon.phoneSpam.report.domain.keys

import pl.pburcon.phoneSpam.util.redis.RedisKey

case object PhoneReportTopKey extends RedisKey {
  val key: String = "reports:top"
}
