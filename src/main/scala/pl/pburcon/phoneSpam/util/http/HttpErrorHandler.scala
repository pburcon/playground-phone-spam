package pl.pburcon.phoneSpam.util.http

import cats.effect.Sync
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.server.ServiceErrorHandler
import pl.pburcon.phoneSpam.util.http.dto.HttpError
import pl.pburcon.phoneSpam.util.logging.BaseLogging

import java.time.Instant
import java.util.UUID
import scala.util.control.NonFatal

class HttpErrorHandler[F[_]: Sync] extends ServiceErrorHandler[F] with BaseLogging[F] {

  override def apply(req: Request[F]): PartialFunction[Throwable, F[Response[F]]] = {
    val method        = req.method
    val path          = req.pathInfo
    val remoteAddress = req.remoteAddr.getOrElse("<unknown>")
    val errorId       = UUID.randomUUID.toString

    PartialFunction.fromFunction {
      case mf: MessageFailure =>
        F.delay {
          logger.debug(mf)(s"($errorId) Message failure handling request: $method $path from $remoteAddress")
          mf.toHttpResponse[F](req.httpVersion)
        }

      case NonFatal(t) =>
        F.delay {
          logger.error(t)(s"($errorId) Error servicing request: $method $path from $remoteAddress")
          Response[F](Status.InternalServerError, req.httpVersion).withEntity(HttpError(Instant.now, errorId).asJson)
        }
    }
  }

  private val F = implicitly[Sync[F]]
}
