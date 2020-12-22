package pl.pburcon.phoneSpam.util.http

import cats.effect.ExitCode
import fs2.Stream
import org.http4s.server.blaze.BlazeServerBuilder

/**
  * Wrapper on `BlazeServerBuilder` that ensures a `Stream` based resource than can be used in our app setup, and also
  * ensures the created server will serve requests properly until the app shutdown.
  */
trait HttpServer[F[_]] {
  def startServer(): Stream[F, ExitCode]
}

class BlazeHttpServer[F[_]](builder: BlazeServerBuilder[F]) extends HttpServer[F] {

  def startServer(): Stream[F, ExitCode] =
    builder.serve

}
