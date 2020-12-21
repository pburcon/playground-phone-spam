package pl.pburcon.phoneSpam.report.routes

import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import pl.pburcon.phoneSpam.report.services.ReportPhoneTopGetService
import pl.pburcon.phoneSpam.util.http.dto.HttpOkResponse
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.routes.BaseHttpRoute
import pl.pburcon.phoneSpam.util.tagged.JsonCodecs

class ReportPhoneTopGetRoute[F[_]: Sync](service: ReportPhoneTopGetService[F])
    extends BaseHttpRoute[F]
    with BaseLogging[F]
    with JsonCodecs {

  override def route: HttpRoutes[F] =
    HttpRoutes.of {
      case GET -> Root / "reports" / "top" =>
        processRequest()
    }

  private def processRequest() =
    for {
      response     <- service.fetchTop()
      httpResponse <- HttpOkResponse.ok(response)
      _            <- log(logger.debug("Successfully finished processing ReportPhoneTopGet request"))
    } yield httpResponse
}