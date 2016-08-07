package org.asem.orient.services

import org.asem.orient.model.PhUser._
import org.scalatest._
import spray.http.HttpHeaders._
import spray.http.StatusCodes._
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.routing._
import spray.testkit._

class PhUserServiceTest extends FlatSpec 
                    with Matchers 
                    with Directives 
                    with ScalatestRouteTest
                    with LoginService
                    with PhUserService {
  def actorRefFactory = system

  "Register new user rest" should "return new created user" in {
    val user = Map("login" -> "demo", "password" -> "demo", "email" -> "demo@demo.com")
    Post("/user/register", user) ~> sealRoute(userManagementRoute) ~> check {
      status should equal(Created)
      responseAs[String] should equal("OK demo")
    }
  }
  
  it should "generate Bad Request rejection if email is not provided" in {
    val user = Map("login" -> "demo", "password" -> "demo")
    Post("/user/register", user) ~> sealRoute(userManagementRoute) ~> check {
      status should equal(BadRequest)
      val resp = responseAs[String]
    }
  }
  
  var cookie = ""
  it should "login with new created user" in {
    Post("/login", FormData(Seq("uname" -> "demo", "upass" -> "demo"))) ~> sealRoute(loginRoute)  ~> check {
      status should equal(OK)
      cookie = header[`Set-Cookie`] match {
        case Some(`Set-Cookie`(HttpCookie("user_token", content, _, _, _, _, _, _, _))) => content
        case _ => ""
      }

      cookie shouldNot be ("")
    }
  }

  def setCookie1(name:String, value:String):HttpRequest ⇒ HttpRequest = {
    req => {
      req.withHeaders(`Cookie`(HttpCookie(name, value)))
    }
  }

//  it should "throw an exception when trying to change password for another user" in {
//    val user = Map("login" -> "demo1", "password" -> "demo", "email" -> "demo@demo.com")
//    Put("/user/changePass", user) ~> setCookie1 ("user_token", cookie) ~> sealRoute(userManagementRoute) ~> check {
//      status should equal(OK)
//    }
//  }
}
