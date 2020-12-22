package pl.pburcon.phoneSpam.report.latest.routes

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import pl.pburcon.phoneSpam.report.latest.services.ReportPhoneLatestGetService
import pl.pburcon.phoneSpam.util.circe.CirceJsonCodecs
import pl.pburcon.phoneSpam.util.http.dto.HttpOkResponse
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.routes.BaseHttpRoute

class ReportPhoneLatestGetRoute[F[_]: Sync](service: ReportPhoneLatestGetService[F])
    extends BaseHttpRoute[F]
    with BaseLogging[F]
    with CirceJsonCodecs {

  override def route: HttpRoutes[F] =
    HttpRoutes.of {
      case GET -> Root / "reports" / "latest" =>
        processRequest()
    }

  private def processRequest() =
    for {
      response     <- service.fetchLatest()
      httpResponse <- HttpOkResponse.ok(response)
      _            <- log(logger.debug("Successfully finished processing ReportPhoneLatestGet request"))
    } yield httpResponse

}
