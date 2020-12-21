package pl.pburcon.phoneSpam.report.routes

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.circe.jsonOf
import org.http4s.dsl.io._
import org.http4s.{EntityDecoder, HttpRoutes, Request}
import pl.pburcon.phoneSpam.report.domain.dto.ReportPhoneSummaryGetRequest
import pl.pburcon.phoneSpam.report.services.ReportPhoneSummaryGetService
import pl.pburcon.phoneSpam.util.http.dto.HttpOkResponse
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.routes.BaseHttpRoute
import pl.pburcon.phoneSpam.util.tagged.JsonCodecs

class ReportPhoneSummaryGetRoute[F[_]: Sync](service: ReportPhoneSummaryGetService[F])
    extends BaseHttpRoute[F]
    with BaseLogging[F]
    with JsonCodecs {

  override def route: HttpRoutes[F] =
    HttpRoutes.of {
      case httpRequest @ GET -> Root / "reports" / "summaries" =>
        processRequest(httpRequest)
    }

  private def processRequest(httpRequest: Request[F]) =
    for {
      request      <- httpRequest.as[ReportPhoneSummaryGetRequest]
      response     <- service.findReports(request)
      httpResponse <- HttpOkResponse.ok(response)
      _            <- log(logger.debug("Successfully finished processing ReportPhoneSummaryGet request"))
    } yield httpResponse

  private implicit val requestDecoder: EntityDecoder[F, ReportPhoneSummaryGetRequest] =
    jsonOf[F, ReportPhoneSummaryGetRequest]
}
