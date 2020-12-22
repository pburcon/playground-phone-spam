package pl.pburcon.phoneSpam.report.top.redis

import pl.pburcon.phoneSpam.util.redis.domain.RedisKey

case object PhoneReportTopKey extends RedisKey {
  val key: String = "reports:top"
}
