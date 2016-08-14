package org.asem.orient.services

import org.joda.time.DateTime
import org.scalatest._
import spray.http._
import spray.routing._
import spray.testkit._
import HttpHeaders._
import StatusCodes._
import org.asem.orient.model.Report
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import spray.json._

/**
  * Created by gosha-user on 13.08.2016.
  */
class ReportServiceTest extends FlatSpec
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
    map should not be empty
  }

  var repSaved:Report = null
  it should "create new report row" in {
    ReportService.createReport(rep.login, rep) match {
      case Left(v) => {
        v should not be null
        repSaved = v match {
          case x:Report => x
        }
      }
      case Right(s) => fail("error creating reopport " + s)
    }
  }

  it should "thrown an error when trying to create new report with the same address" in {
    ReportService.createReport(rep.login, rep) match {
      case Left(v) => fail("test not passed new record created")
      case Right(err) => err should be("duplicate_index")
    }
  }

  it should "delete report by id" in {
    val err = ReportService.deleteById (repSaved)
    err should be (true)
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
  it should "create new report fro currently logged user" in {
    Post("/report", rep) ~> setTestCookie ("user_token", cookie) ~> sealRoute(reportRoute) ~> check {
      status should equal(Created)
      val Report(repo) = JsonParser(responseAs[String]) match {
        case o:JsObject => o
      }

      repo.login should be("user")
    }
  }

  it should "retrieve all reports for currently logged user" in {
    Get("/report") ~> setTestCookie ("user_token", cookie) ~> sealRoute(reportRoute) ~> check {
      status should equal(OK)
      resp = JsonParser(responseAs[String])
      val Report(repo) = resp match {
        case o:JsObject => o
        case JsArray(a) => a.head.asJsObject
      }

      repSaved = repo
    }
  }

  it should "update fields in report object" in {
    val user = Map("street" -> "lenina")
    Put("/report/" + repSaved.id, user) ~> setTestCookie ("user_token", cookie) ~> sealRoute(reportRoute) ~> check {
      println (responseAs[String])

      status should equal(OK)
      resp = JsonParser(responseAs[String])
      val Report(repo) = resp match {
        case o:JsObject => o
        case JsArray(a) => a.head.asJsObject
      }

      repSaved = repo
      repSaved.street should be ("lenina")
    }
  }

  it should "delete report by report id" in {
    Delete("/report/" + repSaved.id) ~> setTestCookie ("user_token", cookie) ~> sealRoute(reportRoute) ~> check {
      status should equal(OK)
    }
  }
}
