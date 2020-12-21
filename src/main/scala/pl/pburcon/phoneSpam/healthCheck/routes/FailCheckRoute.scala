package pl.pburcon.phoneSpam.healthCheck.routes

import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.routes.BaseHttpRoute

class FailCheckRoute[F[_]: Sync] extends BaseHttpRoute[F] with BaseLogging[F] {

  override def route: HttpRoutes[F] =
    HttpRoutes.of {
      case GET -> Root / "fail" / withMessage =>
        logger.info(s"failCheck: $withMessage")
        throw new RuntimeException(withMessage)
    }

}
