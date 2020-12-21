package pl.pburcon.phoneSpam.util.effect

import cats.effect.{Resource, Sync}

trait ResourceSync {

  def delay[F[_], T](thunk: => T)(implicit Sync: Sync[F]): Resource[F, T] =
    Resource.liftF(Sync.delay(thunk))

}

object ResourceSync extends ResourceSync
