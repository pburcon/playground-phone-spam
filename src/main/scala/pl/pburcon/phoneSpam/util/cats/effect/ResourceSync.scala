package pl.pburcon.phoneSpam.util.cats.effect

import cats.effect.{Resource, Sync}

trait ResourceSync {

  /**
    * A shortcut for `Resource.liftF(Sync.delay(thunk))`
    */
  def delay[F[_], T](thunk: => T)(implicit Sync: Sync[F]): Resource[F, T] =
    Resource.liftF(Sync.delay(thunk))

}

object ResourceSync extends ResourceSync
