package pl.pburcon.phoneSpam.report.top.routes

import com.softwaremill.macwire.wire
import org.http4s._
import pl.pburcon.phoneSpam.report.top.services.ReportPhoneTopGetService
import pl.pburcon.phoneSpam.testutils.RouteTestSetup
import pl.pburcon.phoneSpam.testutils.fixtures.ReportPhoneTopDtoFixture

class ReportPhoneTopGetRouteTest extends RouteTestSetup with ReportPhoneTopDtoFixture {

  behavior of "report phone top get route"

  private val topService            = mock[ReportPhoneTopGetService[F]]
  override def route: HttpRoutes[F] = wire[ReportPhoneTopGetRoute[F]].route

  it should "return top phones" in {
    val top1 = randomTop()
    val top2 = randomTop()

    (topService.fetchTop _)
      .expects()
      .returning(
        F.delay(Seq(top1, top2))
      )

    check(Request(Method.GET, Uri.unsafeFromString("/reports/top"))) { response =>
      response.status shouldBe Status.Ok
    }
  }

  it should "return top phones when db is empty" in {
    (topService.fetchTop _)
      .expects()
      .returning(
        F.delay(Seq.empty)
      )

    check(Request(Method.GET, Uri.unsafeFromString("/reports/top"))) { response =>
      response.status shouldBe Status.Ok
    }
  }

}
