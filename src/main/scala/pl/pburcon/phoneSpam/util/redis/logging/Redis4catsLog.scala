package pl.pburcon.phoneSpam.util.redis.logging

import cats.effect.Sync
import dev.profunktor.redis4cats.effect.Log
import org.log4s.Logger

object Redis4catsLog {

  def fromLogger[F[_]](logger: Logger)(implicit Sync: Sync[F]): Log[F] =
    new Log[F] {
      override def debug(msg: => String): F[Unit] = Sync.delay(logger.debug(msg))
      override def error(msg: => String): F[Unit] = Sync.delay(logger.error(msg))
      override def info(msg: => String): F[Unit]  = Sync.delay(logger.info(msg))
    }

}
