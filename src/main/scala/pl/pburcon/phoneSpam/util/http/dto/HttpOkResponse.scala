package pl.pburcon.phoneSpam.util.http.dto

import cats.effect.Sync
import io.circe.Encoder
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._

object HttpOkResponse {

  def ok[F[_]](implicit Sync: Sync[F]): F[Response[F]] =
    Sync.delay(Response(Status.Ok).withEntity(HttpMessage("OK").asJson))

  def ok[F[_], T](response: T)(implicit Sync: Sync[F], e: Encoder[T]): F[Response[F]] =
    Sync.delay(Response(Status.Ok).withEntity(response.asJson))
}
