package pl.pburcon.phoneSpam.util.logging

import cats.effect.{Resource, Sync}
import org.log4s.{Logger, getLogger}

trait BaseLogging[F[_]] {

  protected lazy val logger: Logger = getLogger(getClass)

  protected lazy val loggingClassName: String = getClass.getSimpleName

  protected def log(doLog: => Unit)(implicit Sync: Sync[F]): F[Unit] =
    Sync.delay(doLog)

  protected def logResource(doLog: => Unit)(implicit Sync: Sync[F]): Resource[F, Unit] =
    Resource.liftF(log(doLog))

  protected def logStream(doLog: => Unit)(implicit Sync: Sync[F]): fs2.Stream[F, Unit] =
    fs2.Stream.eval(log(doLog))

}
