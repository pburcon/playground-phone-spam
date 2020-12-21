package pl.pburcon.phoneSpam.healthCheck.routes

import cats.effect.Sync
import cats.implicits._
import org.http4s._
import org.http4s.dsl.io._
import pl.pburcon.phoneSpam.util.http.dto.HttpOkResponse
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.routes.BaseHttpRoute

class HealthCheckRoute[F[_]: Sync] extends BaseHttpRoute[F] with BaseLogging[F] {

  override val route: HttpRoutes[F] =
    HttpRoutes.of {
      case GET -> Root / "healthCheck" =>
        for {
          response <- HttpOkResponse.ok
          _        <- log(logger.debug("healthCheck - OK"))
        } yield response
    }

}
