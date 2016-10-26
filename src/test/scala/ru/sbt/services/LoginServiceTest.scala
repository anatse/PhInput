package org.asem.orient.services

import org.scalatest._
import akka.http.scaladsl.model.FormData
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server._
import Directives._
import ru.sbt.service.MainHttpServiceTrait
import akka.http.scaladsl.model.headers._

class LoginServiceTest extends FlatSpec 
                    with Matchers 
                    with Directives 
                    with ScalatestRouteTest
                    with MainHttpServiceTrait {


//  def setTestCookie(name:String, value:String):HttpRequest ⇒ HttpRequest = {
//    req => {
//      req.withHeaders(`Cookie`(name, value))
//    }
//  }
  
//  var cookie = ""
//  "Login REST service" should "authorize with credentials provided" in {
//    Post("/", FormData(Map("uname" -> "user", "upass" -> "user"))) ~> routes ~> check {
//      println (status)
//      cookie = header[`Set-Cookie`] match {
//        case Some(`Set-Cookie`(HttpCookie("user_token", content, _, _, _, _, _, _, _))) => content
//        case _ => ""
//      }
//      println (cookie)
//    }
//  }
//  
//  it should "decrypt token" in {
//    Get("/") ~> setTestCookie ("user_token", cookie) ~> routes ~> check {
//      println (status)
//    }
//  }
//
//  it should "get tocken from cache" in {
//    Get("/") ~> setTestCookie ("user_token", cookie) ~> routes ~> check {
//      println (status)
//    }
//  }
}
