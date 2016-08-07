package org.asem.orient.services

import com.typesafe.config.ConfigFactory
import org.asem.orient.model.CookieAuthenticator._
import spray.http._
import spray.routing._

import scala.concurrent.ExecutionContext.Implicits.global

trait LoginService extends HttpService {
  val loginRoute =
    path("login") {
      post {
        authenticate(byCookie) {
          user => setCookie(HttpCookie(name = "user_token", content = user.token, maxAge = Some(600))) {
              complete {
                <html>
                  <h3>User named "{user.login}" successfully logged in. Redirecting to main page...</h3>
                  <script>
                    window.location = '{ConfigFactory.load().getString("mainPage")}';
                  </script>
                </html>
              }
            }
        }
      }
    } ~ path("logout") {
      (get | post) {
        authenticate(byCookie) {
          user =>
            deleteCookie("user_token") {
              complete {
                <html>
                  <h3>User named "{user.login}" successfully logged out. Redirecting to login page...</h3>
                  <script>
                    window.location = '{ConfigFactory.load().getString("loginPage")}'; 
                  </script>
                </html>
              }
            }
        }
      }
    }
}
