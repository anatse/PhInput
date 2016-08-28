package org.asem.orient.services

import org.asem.orient.Database
import org.asem.orient.model.entities.{PrjService, PrjUser, _}
import org.joda.time.DateTime
import org.scalatest._
import spray.routing._
import spray.testkit._

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
  
  "Report" should "erad from Json" in {
    Database.getTx {
      tx => {
//        val prj = PrjService.addProject(Project("", "test project", new DateTime()))

        val prjs = PrjService.findAllProjects(PrjUser("#33:0", "user")).apply(tx)
//        println (prjs)

        val prjc = PrjService.findActivePrjCycles(prjs.head, PrjUser("#33:0", "user")).apply(tx).head
        println (prjc)

        var reps = PrjService.findReportsForUser (prjc, PrjUser("#33:0", "user")).apply(tx)
        println ("Reps1: " + Report.write(reps.head))

        reps = PrjService.findAllReports (prjc).apply(tx)
        println ("reps2: " + Report.write(reps.head))
      }
    }

    val pc = PrjCycle (
      id = "101", 
      name = "name", 
      startDate = new DateTime(), 
      endDate = new DateTime(), 
      visits = List[VisitReq]()
    )
    
    val pu = PrjUser (login = "user",
        email = "user@demo.com",
        firstName = "Demo",
        secondName = "Demos",
        id = "#33:2"
      )
      
    val ph = Pharmacy (
      id  = "String", 
      name = "String", 
      chiefPhone = "String", 
      chiefName = "String", 
      tradeRoomPhone = "String", 
      pharmNet = null, 
      cityCode = "String",
      cityName = "String",
      streetCode = "String",
      streetName =   "String",
      buildingCode = "String",
      buildingName = "String"
    )
    
    val rep = Report(
      "100",
      pc,
      pu,
      ph,
      List[Drug](),
      true,
      pu
    )
    
    val json = Report.write (rep)
//    println (json)

    val rep1:Report = Report.read (json)
//    println (rep1)
  }

//  def setTestCookie(name:String, value:String):HttpRequest ⇒ HttpRequest = {
//    req => {
//      req.withHeaders(`Cookie`(HttpCookie(name, value)))
//    }
//  }
//
//  val rep = Report(
//    login = "user",
//    created = DateTime.now(),
//    modified = DateTime.now(),
//    city = "moscow",
//    street = "snayperskayaa",
//    building = "10",
//    pharmNet = "36.6",
//    pharmacy = "36.6-1",
//    agreements = "No aggreements",
//    managerName = "Vasya",
//    managerPhone = "322223",
//    tradeRoomPhone = "1110000"
//  )
//
//  "Report service" should "convert all fields of report object to map in runtime" in {
//    val map = Report.entity2Map(rep)
//    map should not be empty
//  }
//
//  var repSaved:Report = _
//  it should "create new report row" in {
//    ReportService.createReport(rep.login, rep) match {
//      case Left(v) =>
//        v should not be null
//        repSaved = v match {
//          case x:Report => x
//        }
//      case Right(s) => fail("error creating report " + s)
//    }
//  }
//
//  it should "thrown an error when trying to create new report with the same address" in {
//    ReportService.createReport(rep.login, rep) match {
//      case Left(v) => fail("test not passed new record created")
//      case Right(err) => err should be("duplicate_index")
//    }
//  }
//
//  it should "delete report by id" in {
//    val err = ReportService.deleteById (repSaved)
//    err should be (true)
//  }
//
//  "Report http service" should "reject request without user credentials" in {
//    Get("/report") ~> sealRoute(reportRoute) ~> check {
//      status should equal(Unauthorized)
//    }
//  }
//
//  var cookie = ""
//  it should "login successfully with demo user" in {
//    Post("/login", FormData(Seq("uname" -> "user", "upass" -> "user"))) ~> sealRoute(loginRoute)  ~> check {
//      status should equal(OK)
//      cookie = header[`Set-Cookie`] match {
//        case Some(`Set-Cookie`(HttpCookie("user_token", content, _, _, _, _, _, _, _))) => content
//        case _ => ""
//      }
//
//      cookie shouldNot be ("")
//    }
//  }
//
//  var resp:JsValue = _
//  it should "create new report fro currently logged user" in {
//    Post("/report", rep) ~> setTestCookie ("user_token", cookie) ~> sealRoute(reportRoute) ~> check {
//      status should equal(Created)
//      val Report(repo) = JsonParser(responseAs[String]) match {
//        case o:JsObject => o
//      }
//
//      repo.login should be("user")
//    }
//  }
//
//  it should "retrieve all reports for currently logged user" in {
//    Get("/report") ~> setTestCookie ("user_token", cookie) ~> sealRoute(reportRoute) ~> check {
//      status should equal(OK)
//      resp = JsonParser(responseAs[String])
//      val Report(repo) = resp match {
//        case o:JsObject => o
//        case JsArray(a) => a.head.asJsObject
//      }
//
//      repSaved = repo
//    }
//  }
//
//  it should "update fields in report object" in {
//    val user = Map("street" -> "lenina")
//    Put("/report/" + repSaved.id, user) ~> setTestCookie ("user_token", cookie) ~> sealRoute(reportRoute) ~> check {
//      println (responseAs[String])
//
//      status should equal(OK)
//      resp = JsonParser(responseAs[String])
//      val Report(repo) = resp match {
//        case o:JsObject => o
//        case JsArray(a) => a.head.asJsObject
//      }
//
//      repSaved = repo
//      repSaved.street should be ("lenina")
//    }
//  }
//
//  it should "delete report by report id" in {
//    Delete("/report/" + repSaved.id) ~> setTestCookie ("user_token", cookie) ~> sealRoute(reportRoute) ~> check {
//      status should equal(OK)
//    }
//  }
}
