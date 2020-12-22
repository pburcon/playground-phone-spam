package pl.pburcon.phoneSpam.testutils

import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.{HttpRoutes, Request, Response}
import org.scalatest.Assertion

import scala.concurrent.Future

trait RouteTestSetup extends TestSetup {

  def route: HttpRoutes[F]

  def check(request: Request[F])(checkF: Response[F] => Assertion): Future[Assertion] =
    route.orNotFound.run(request).map(checkF).runToFuture

}
