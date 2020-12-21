package pl.pburcon.phoneSpam.report.routes

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.{EntityDecoder, HttpRoutes, Request}
import pl.pburcon.phoneSpam.report.domain.dto.ReportPhoneAddRequest
import pl.pburcon.phoneSpam.report.services.ReportPhoneAddService
import pl.pburcon.phoneSpam.util.http.dto.HttpOkResponse
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.routes.BaseHttpRoute
import pl.pburcon.phoneSpam.util.tagged.JsonCodecs

class ReportPhoneAddRoute[F[_]: Sync](service: ReportPhoneAddService[F])
    extends BaseHttpRoute[F]
    with BaseLogging[F]
    with JsonCodecs {

  override def route: HttpRoutes[F] =
    HttpRoutes.of {
      case httpRequest @ POST -> Root / "reports" =>
        processRequest(httpRequest)
    }

  private def processRequest(httpRequest: Request[F]) =
    for {
      request      <- httpRequest.as[ReportPhoneAddRequest]
      _            <- service.addReport(request)
      httpResponse <- HttpOkResponse.ok
      _            <- log(logger.debug(s"Successfully finished processing $request"))
    } yield httpResponse

  private implicit val requestDecoder: EntityDecoder[F, ReportPhoneAddRequest] =
    jsonOf[F, ReportPhoneAddRequest]

}
