package org.asem.orient.services

import akka.actor.ActorRef
import com.typesafe.config.ConfigFactory
import spray.can.websocket._
import spray.can.websocket.frame._
import spray.http._
import spray.routing._

/**
 * Created by gosha-user on 17.07.2016.
 * @see http://spray.io/documentation/1.2.2/spray-routing/predefined-directives-by-trait/
 * @see http://spray.io/documentation/1.2.2/spray-routing/scheme-directives/scheme/
 * @see http://spray.io/msug
 * @see https://github.com/dcaoyuan/spray-websocket
 */
final case class Push(msg: String)

class MainServiceActor(val serverConnection: ActorRef) extends HttpServiceActor
  with WebSocketServerWorker
  with PhUserService
  with LoginService
  with ReportService
  with TaskService
  with StaticResourceService {
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  override def actorRefFactory = context

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

  override def receive = handshaking orElse businessLogicNoUpgrade orElse closeLogic

  def businessLogic: Receive = {
    // just bounce frames back for Autobahn testsuite
    case x @ (_: BinaryFrame | _: TextFrame) => {
      sender() ! x
    }

    case Push(msg) => {
      println (msg)
      send(TextFrame(msg))
    }

    case x: FrameCommandFailed =>
      log.error("frame command failed", x)

    case x: HttpRequest => // do something
  }

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def businessLogicNoUpgrade = runRoute(
    loginRoute
      ~ resourceRoute
      ~ userManagementRoute
      ~ reportRoute
      ~ taskRoute
  )
}

trait StaticResourceService extends HttpService {
  val resourceRoute =
    pathPrefix("pub") { // public directory
      getFromDirectory("./src/webapp")
    }
}
