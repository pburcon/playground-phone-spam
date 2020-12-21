package pl.pburcon.phoneSpam.util.routes

import org.http4s.HttpRoutes

trait BaseHttpRoute[F[_]] {
  def route: HttpRoutes[F]
}

//trait BaseHttpRoute[RawRequest, Request, Response] {
//  def route: HttpRoutes[IO]
//
//  def parser: BaseHttpRequestParser[IO, RawRequest]
//  def validator: BaseHttpRequestValidator[IO, RawRequest, Request, Nothing]
//  def service: BaseHttpService[IO, Request, Response]
//}
