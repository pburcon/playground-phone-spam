package pl.pburcon.phoneSpam.app.modules

import cats.effect.{Concurrent, ContextShift, Resource}
import com.softwaremill.macwire.wire
import pl.pburcon.phoneSpam.app.config.ConfigLoader.load
import pl.pburcon.phoneSpam.app.config.RedisConfig
import pl.pburcon.phoneSpam.util.redis.{RedisClient, RedisClientBuilder}
import pureconfig.generic.auto._

final class RedisModule[F[_]: Concurrent: ContextShift] {

  //
  // public module components
  //

  def buildRedisClient(): Resource[F, RedisClient[F]] =
    redisClientBuilder.buildClient()

  //
  // private module components
  //

  protected lazy val redisConfig: RedisConfig                  = load[RedisConfig]("redis")
  protected lazy val redisClientBuilder: RedisClientBuilder[F] = wire[RedisClientBuilder[F]]
}
