package pl.pburcon.phoneSpam.util.future

import cats.effect.Async

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait AsyncFuture {
  def deferAsync[F[_], T](ec: ExecutionContext)(thunk: => Future[T])(implicit Async: Async[F]): F[T] =
    Async.async(callback =>
      thunk.onComplete {
        case Success(value)     => callback(Right(value))
        case Failure(exception) => callback(Left(exception))
      }(ec)
    )
}

object AsyncFuture extends AsyncFuture
