package pl.pburcon.phoneSpam.util.redis

import cats.effect.{Concurrent, ContextShift, Resource}
import dev.profunktor.redis4cats.connection.{RedisClient => Redis4catsClient}
import dev.profunktor.redis4cats.effect.Log
import io.lettuce.core.{ClientOptions, TimeoutOptions}
import pl.pburcon.phoneSpam.app.config.RedisConfig
import pl.pburcon.phoneSpam.util.java.JavaTimeConverters._
import pl.pburcon.phoneSpam.util.logging.BaseLogging

class RedisClientBuilder[F[_]: Concurrent: ContextShift](redisConfig: RedisConfig) extends BaseLogging[F] {

  def buildClient(): Resource[F, RedisClient[F]] =
    Redis4catsClient[F]
      .withOptions(redisConfig.url, lettuceClientOptions)
      .map(new RedisClient(_))

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

  private implicit def log: Log[F] =
    Redis4catsLog
      .fromLogger(logger)

}

//class RedisCommandsFactory[F[_]: Concurrent](redisConfig: RedisConfig)(implicit cs: ContextShift[F])
//    extends BaseLogging[F] {
//
//  type RedisCommands = Redis4catsCommands[F, String, String]
//
//  def createCommands(): Resource[F, RedisCommands] =
//    for {
//      _              <- logResource(logger.debug("Obtaining Redis Commands connection"))
//      r4catsClient   <- redis4catsClient
//      r4catsCommands <- Redis[F].fromClient(r4catsClient, redis4catsCodec)
//      _              <- logResource(logger.debug("Obtained Redis Commands connection"))
//    } yield r4catsCommands
//
//  private val lettuceTimeoutOptions =
//    TimeoutOptions
//      .builder()
//      .fixedTimeout(redisConfig.timeoutDuration.asJava)
//      .build()
//
//  private val lettuceClientOptions =
//    ClientOptions
//      .builder()
//      .autoReconnect(true)
//      .suspendReconnectOnProtocolFailure(true)
//      .timeoutOptions(lettuceTimeoutOptions)
//      .build()
//
//  private implicit val redis4catsIoLog: Log[F] = Redis4catsLog.fromLogger(logger)
//
//  private val redis4catsClient = RedisClient[F].withOptions(redisConfig.url, lettuceClientOptions)
//  private val redis4catsCodec  = RedisCodec.Utf8
//
//}
