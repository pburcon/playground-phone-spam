package pl.pburcon.phoneSpam.util.http

import cats.effect.{ConcurrentEffect, Resource, Timer}
import org.http4s.HttpApp
import org.http4s.server.blaze.BlazeServerBuilder
import pl.pburcon.phoneSpam.util.cats.effect.ResourceSync

import scala.concurrent.ExecutionContext

class HttpServerBuilder[F[_]: ConcurrentEffect: Timer](
    httpConfig: HttpConfig,
    httpApp: HttpApp[F],
    httpErrorHandler: HttpErrorHandler[F]
) {

  def buildHttpServer(ec: ExecutionContext): Resource[F, HttpServer[F]] =
    ResourceSync.delay {
      val builder = BlazeServerBuilder[F](ec)
        .bindHttp(httpConfig.port, httpConfig.host)
        .withHttpApp(httpApp)
        .withServiceErrorHandler(httpErrorHandler)
        .withoutBanner

      new BlazeHttpServer(builder)
    }

}
