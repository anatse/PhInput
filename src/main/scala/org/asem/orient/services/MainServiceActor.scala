package org.asem.orient.services

import akka.actor.Actor
import com.typesafe.config.ConfigFactory
import spray.http._
import spray.routing._
/**
 * Created by gosha-user on 17.07.2016.
 * @see http://spray.io/documentation/1.2.2/spray-routing/predefined-directives-by-trait/
 * @see http://spray.io/documentation/1.2.2/spray-routing/scheme-directives/scheme/
 * @see http://spray.io/msug
 */
class MainServiceActor extends Actor 
                          with PhUserService
                          with LoginService
                          with ReportService
                          with StaticResourceService {
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context
  /**
   * login page URL, load from configuration (application.conf)
   */
  lazy val loginPage = ConfigFactory.load().getString("loginPage")

  /**
   * Override default rejection handler for aithentication rejections. 
   * Used to automatically redirect to login page. Redirection performs only for get requests
   * for other methods respond with Unauthorized exeption
   */
  implicit val rh = RejectionHandler {
    case AuthenticationFailedRejection(cause, challengeHeaders) :: _ => 
      get {
        redirect(loginPage, StatusCodes.TemporaryRedirect)
      } ~ (post | put | delete) {
        respondWithStatus(StatusCodes.Unauthorized) {
          complete("The resource requires authentication, which was not supplied with the request")
        }
      }
  }

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(
      loginRoute 
    ~ resourceRoute
    ~ userManagementRoute
    ~ reportRoute
  )
}

trait StaticResourceService extends HttpService {
  val resourceRoute = 
    pathPrefix("pub") { // public directory
      getFromDirectory("./src/webapp")
    }
}