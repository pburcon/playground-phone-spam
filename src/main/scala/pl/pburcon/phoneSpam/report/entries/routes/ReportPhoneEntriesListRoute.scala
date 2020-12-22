package pl.pburcon.phoneSpam.report.entries.routes

import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.jsonOf
import org.http4s.dsl.io._
import pl.pburcon.phoneSpam.report.entries.domain.dto.{ReportPhoneEntriesListRequest, ReportPhoneEntriesListResponse}
import pl.pburcon.phoneSpam.report.entries.services.ReportPhoneEntryListService
import pl.pburcon.phoneSpam.util.circe.CirceJsonCodecs
import pl.pburcon.phoneSpam.util.http.dto.HttpOkResponse
import pl.pburcon.phoneSpam.util.logging.BaseLogging
import pl.pburcon.phoneSpam.util.routes.BaseHttpRoute

class ReportPhoneEntriesListRoute[F[_]: Sync](service: ReportPhoneEntryListService[F])
    extends BaseHttpRoute[F]
    with BaseLogging[F]
    with CirceJsonCodecs {

  override def route: HttpRoutes[F] =
    HttpRoutes.of {
      case httpRequest @ GET -> Root / "reports" =>
        processRequest(httpRequest)
    }

  private def processRequest(httpRequest: Request[F]) =
    for {
      request      <- httpRequest.as[ReportPhoneEntriesListRequest]
      response     <- service.listEntries(request)
      _            <- log(logger.debug("Successfully finished processing ReportPhoneEntriesListRequest request"))
      httpResponse <- buildResponseDto(response)
    } yield httpResponse

  private def buildResponseDto(response: ReportPhoneEntryListService.Result) =
    response match {
      case s: ReportPhoneEntryListService.Result.Succeeded => HttpOkResponse.ok(ReportPhoneEntriesListResponse.from(s))
      case ReportPhoneEntryListService.Result.NotFound     => Response.notFound[F].pure[F]
    }

  private implicit val requestDecoder: EntityDecoder[F, ReportPhoneEntriesListRequest] =
    jsonOf[F, ReportPhoneEntriesListRequest]

}
