package org.asem.orient.services

import org.asem.orient.model.Report
import org.asem.orient.services.ReportService
import org.joda.time.DateTime
import org.scalatest._
import akka.testkit._
import spray.routing._
import spray.http._
import org.asem.orient.model._
import org.asem.orient.services._
import org.scalatest._
import spray.testkit._
import spray.json._
import spray.httpx.SprayJsonSupport._
import HttpMethods._
import MediaTypes._
import HttpCharsets._
import StatusCodes._
import HttpHeaders._

/**
  * Created by gosha-user on 13.08.2016.
  */
class ReportTest  extends FlatSpec
                    with Matchers
                    with Directives
                    with ScalatestRouteTest
                    with LoginService
                    with ReportService {
  def actorRefFactory = system

  def setTestCookie(name:String, value:String):HttpRequest ⇒ HttpRequest = {
    req => {
      req.withHeaders(`Cookie`(HttpCookie(name, value)))
    }
  }

  val rep = Report(
    login = "user",
    created = DateTime.now(),
    modified = DateTime.now(),
    city = "moscow",
    street = "snayperskayaa",
    building = "10",
    pharmNet = "36.6",
    pharmacy = "36.6-1",
    agreements = "No aggreements",
    managerName = "Vasya",
    managerPhone = "322223",
    tradeRoomPhone = "1110000"
  )

  "Report service" should "convert all fields of report object to map in runtime" in {
    val map = ReportService.rep2Map(rep)
    map shouldNot be (null)
    map.isEmpty should be(false)
  }

  it should "create new report row" in {
    val err = ReportService.createReport(rep)
    err should be("")
  }

  it should "thrown an error when trying to create new report with the same address" in {
    val err = ReportService.createReport(rep)
    err should be("duplicate_index")
  }

  "Report http service" should "request for user credentials" in {
    Get("/report") ~> sealRoute(reportRoute) ~> check {
      status should equal(Unauthorized)
    }
  }

  var cookie = ""
  it should "login successfully with demo user" in {
    Post("/login", FormData(Seq("uname" -> "user", "upass" -> "user"))) ~> sealRoute(loginRoute)  ~> check {
      status should equal(OK)
      cookie = header[`Set-Cookie`] match {
        case Some(`Set-Cookie`(HttpCookie("user_token", content, _, _, _, _, _, _, _))) => content
        case _ => ""
      }

      cookie shouldNot be ("")
    }
  }

  var resp:JsValue = null
  it should "retrieve all reports for currently logged user" in {
    Get("/report") ~> setTestCookie ("user_token", cookie) ~> sealRoute(reportRoute) ~> check {
      status should equal(OK)
      resp = JsonParser(responseAs[String])
    }
  }

  it should "delete report by id" in {
    val Report(repo) = resp match {
      case o:JsObject => o
      case JsArray(a) => a.head.asJsObject
    }

    val err = ReportService.deleteById (repo)
    err should be (true)
  }
}
