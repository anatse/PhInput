package org.asem.orient.services

import org.scalatest._
import spray.http.StatusCodes._
import spray.http._
import spray.routing._
import spray.testkit._

class LoginServiceTest extends FlatSpec 
                    with Matchers 
                    with Directives 
                    with ScalatestRouteTest
                    with LoginService {

  def actorRefFactory = system

  // Prepare data
  PhUserService.findUserByLogin("user") match {
      case Some(user) => None
      case _ => PhUserService.createUser(
        login = "user",
        pasword = "user",
        email = "user@demo.com",
        firstName = "Demo",
        secondName = "Demos"
      )
  }

  "Login REST service" should "return unauthorized (401) if no credentials provided" in {
    Post("/login", FormData(Seq("uname" -> "", "upass" -> ""))) ~> sealRoute(loginRoute) ~> check {
      status should equal(Unauthorized)
    }
  }

  it should "permanent redirect for success logged user" in {
    Post("/login", FormData(Seq("uname" -> "user", "upass" -> "user"))) ~> sealRoute(loginRoute) ~> check {
      status should equal(OK)
    }
  }

  it should "fail logging user with wrong credentials" in {
    Post("/login", FormData(Seq("uname" -> "user", "upass" -> "xxxx"))) ~> sealRoute(loginRoute) ~> check {
      status should equal(Unauthorized)
    }
  }

  it should "success logging user with name in wrong case" in {
    Post("/login", FormData(Seq("uname" -> "User", "upass" -> "user"))) ~> sealRoute(loginRoute) ~> check {
      status should equal(OK)
    }
  }

  it should "fail if using get instead of put http method" in {
    Get("/login?uname=User&upass=user") ~> sealRoute(loginRoute) ~> check {
      status should equal(MethodNotAllowed)
    }
  }
  
//  it should "success logout logged user" in {
//    Get("/logout") ~> loginRoute ~> check {
//      responseAs[String] should equal("The user was logged out")
//      header[`Set-Cookie`] should equal(Some(`Set-Cookie`(HttpCookie("userName", content = "deleted", expires = Some(DateTime.MinValue)))))
//    }
//  }
}
