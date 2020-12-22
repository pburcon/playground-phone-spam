package pl.pburcon.phoneSpam.util.redis.config

import scala.concurrent.duration.Duration

final case class RedisConfig(
    url: String,
    timeoutDuration: Duration
)
