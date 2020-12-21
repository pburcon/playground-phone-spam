package pl.pburcon.phoneSpam.util.redis

import cats.effect.{Concurrent, ContextShift, Resource}
import dev.profunktor.redis4cats.connection.{RedisClient => Redis4catsClient}
import dev.profunktor.redis4cats.data.RedisCodec
import dev.profunktor.redis4cats.effect.Log
import dev.profunktor.redis4cats.{Redis, RedisCommands}
import pl.pburcon.phoneSpam.util.logging.BaseLogging

class RedisClient[F[_]: Concurrent: ContextShift](client: Redis4catsClient) extends BaseLogging[F] {

  def createCommands(): Resource[F, RedisCommands[F, String, String]] =
    Redis[F].fromClient(client, RedisCodec.Utf8)

  def createCommands[K, V](codec: RedisCodec[K, V]): Resource[F, RedisCommands[F, K, V]] =
    Redis[F].fromClient(client, codec)

  private implicit def log: Log[F] =
    Redis4catsLog.fromLogger(logger)

}

object RedisClient {
  type StringCommands[F[_]] = RedisCommands[F, String, String]
}
