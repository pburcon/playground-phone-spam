package pl.pburcon.phoneSpam.report.latest.redis

import pl.pburcon.phoneSpam.util.redis.domain.RedisKey

case object ReportPhoneLatestKey extends RedisKey {
  val key: String = "reports:latest"
}
