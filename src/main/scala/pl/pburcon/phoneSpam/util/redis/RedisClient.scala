package pl.pburcon.phoneSpam.util.redis

import cats.effect.{Concurrent, ContextShift, Resource}
import dev.profunktor.redis4cats.connection.{RedisClient => Redis4catsClient}
import dev.profunktor.redis4cats.data.RedisCodec
import dev.profunktor.redis4cats.effect.Log
import dev.profunktor.redis4cats.{Redis, RedisCommands}
import org.log4s.getLogger
import pl.pburcon.phoneSpam.util.circe.CirceJsonCodecs
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.redis.codecs.RedisJsonCodecs
import pl.pburcon.phoneSpam.util.redis.logging.Redis4catsLog

trait RedisClient[F[_]] {
  def createCommands(): Resource[F, RedisCommands[F, String, String]]
  def createCommands[K, V](codec: RedisCodec[K, V]): Resource[F, RedisCommands[F, K, V]]
}

class RedisClientImpl[F[_]: Concurrent: ContextShift](client: Redis4catsClient)
    extends RedisClient[F]
    with BaseLogging[F]
    with RedisJsonCodecs
    with CirceJsonCodecs {

  def createCommands(): Resource[F, RedisCommands[F, String, String]] =
    Redis[F].fromClient(client, RedisCodec.Utf8)

  def createCommands[K, V](codec: RedisCodec[K, V]): Resource[F, RedisCommands[F, K, V]] =
    Redis[F].fromClient(client, codec)

  private implicit val logF: Log[F] =
    Redis4catsLog.fromLogger(getLogger(RedisCommands.getClass))

}
