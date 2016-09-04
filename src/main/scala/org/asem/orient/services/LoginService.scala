package org.asem.orient.services

import com.typesafe.config.ConfigFactory
import org.asem.spray.security.CookieAuthenticator._
import spray.http._
import MediaTypes._

import scala.concurrent.ExecutionContext.Implicits.global

trait LoginService extends BaseHttpService {
  val loginRoute = 
    path("login") {
      post {
        auth {
          user => respondWithMediaType(`text/html`) {
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
              respondWithMediaType(`text/html`) { complete (
                <html>
                  <h3>User named "{user.login}" successfully logged out. Redirecting to login page...</h3>
                  <script>
                    window.location = '{ConfigFactory.load().getString("loginPage")}';
                  </script>
                </html>
              )
            }
          }
        }
      }
    }
}
