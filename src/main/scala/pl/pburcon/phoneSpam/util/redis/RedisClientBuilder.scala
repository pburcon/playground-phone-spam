package pl.pburcon.phoneSpam.util.redis

import cats.effect.{Concurrent, ContextShift, Resource}
import dev.profunktor.redis4cats.connection.{RedisClient => Redis4catsClient}
import dev.profunktor.redis4cats.effect.Log
import io.lettuce.core.{ClientOptions, TimeoutOptions}
import org.log4s.getLogger
import pl.pburcon.phoneSpam.util.java.JavaTimeConverters._
import pl.pburcon.phoneSpam.util.redis.config.RedisConfig
import pl.pburcon.phoneSpam.util.redis.logging.Redis4catsLog

class RedisClientBuilder[F[_]: Concurrent: ContextShift](redisConfig: RedisConfig) {

  def buildClient(): Resource[F, RedisClient[F]] =
    Redis4catsClient[F]
      .withOptions(redisConfig.url, lettuceClientOptions)
      .map(new RedisClientImpl(_))

  private def lettuceTimeoutOptions =
    TimeoutOptions
      .builder()
      .fixedTimeout(redisConfig.timeoutDuration.asJava)
      .build()

  private def lettuceClientOptions =
    ClientOptions
      .builder()
      .autoReconnect(true)
      .suspendReconnectOnProtocolFailure(true)
      .timeoutOptions(lettuceTimeoutOptions)
      .build()

  private implicit val log: Log[F] =
    Redis4catsLog.fromLogger(getLogger(classOf[RedisClient[F]]))

}
