package pl.pburcon.phoneSpam.app.config

import scala.concurrent.duration.Duration

final case class RedisConfig(
    url: String,
    timeoutDuration: Duration
)
