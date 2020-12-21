package pl.pburcon.phoneSpam.util.redis

final case class RedisRange(size: Long) {
  assert(size > 0)

  lazy val Ascending: RedisRangeBoundaries  = RedisRangeBoundaries(0, size - 1)
  lazy val Descending: RedisRangeBoundaries = RedisRangeBoundaries(-1, -size - 1)
}

final case class RedisRangeBoundaries(start: Long, stop: Long)
