package pl.pburcon.phoneSpam.util.cats.effect

import cats.Functor
import cats.effect.Timer

import java.time.Instant

object TimerInstant {

  /**
    * It's a bit prettier this way.
    */
  @inline def now[F[_]: Functor]()(implicit Timer: Timer[F]): F[Instant] =
    Timer.clock.instantNow

}
