package pl.pburcon.phoneSpam.util.http

import cats.effect.{ConcurrentEffect, Resource, Timer}
import org.http4s.HttpApp
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder
import pl.pburcon.phoneSpam.app.config.HttpConfig

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

class HttpServerBuilder[F[_]: ConcurrentEffect: Timer](
    httpConfig: HttpConfig,
    httpApp: HttpApp[F],
    httpErrorHandler: HttpErrorHandler[F]
) {

  private lazy implicit val executionContext: ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newWorkStealingPool())

  def buildHttpServer(): Resource[F, Server] =
    BlazeServerBuilder[F](executionContext)
      .bindHttp(httpConfig.port, httpConfig.host)
      .withHttpApp(httpApp)
      .withServiceErrorHandler(httpErrorHandler)
      .withoutBanner
      .resource

}
