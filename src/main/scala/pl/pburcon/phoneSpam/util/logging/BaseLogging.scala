package pl.pburcon.phoneSpam.util.logging

import cats.effect.{Resource, Sync}
import org.log4s.{Logger, getLogger}

trait BaseLogging[F[_]] {

  // TODO
  // is this OK with regards to referential transparency?
  // the other approach would be to wrap it in IO, which would result in a creation of a new object and a getLogger
  // search for every method call, this seems excessive, maybe Ref[]?
  protected lazy val logger: Logger = getLogger(getClass)

  protected def log(doLog: => Unit)(implicit f: Sync[F]): F[Unit] =
    f.delay(doLog)

  protected def logResource(doLog: => Unit)(implicit f: Sync[F]): Resource[F, Unit] =
    Resource.liftF(log(doLog))

}
