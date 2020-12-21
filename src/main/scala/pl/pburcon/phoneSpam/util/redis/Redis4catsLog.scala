package pl.pburcon.phoneSpam.util.redis

import cats.effect.Sync
import dev.profunktor.redis4cats.effect.Log
import org.log4s.Logger

object Redis4catsLog {

  def fromLogger[F[_]](logger: Logger)(implicit f: Sync[F]): Log[F] =
    new Log[F] {
      override def debug(msg: => String): F[Unit] = f.delay(logger.debug(msg))
      override def error(msg: => String): F[Unit] = f.delay(logger.error(msg))
      override def info(msg: => String): F[Unit]  = f.delay(logger.info(msg))
    }

}
