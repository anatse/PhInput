package ru.sbt.service

import ru.sbt.orient.model.SbtUser
import ru.sbt.orient.model.entities.RequestStatus
import ru.sbt.security.OrientDBAuthenticator
import ru.sbt.security.UserData
import ru.sbt.service._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.RejectionHandler
import akka.http.scaladsl.server.directives._
import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.HttpChallenges
import akka.http.scaladsl.server.AuthenticationFailedRejection
import akka.http.scaladsl.server.AuthenticationFailedRejection._
import akka.stream.Materializer
import com.typesafe.config.ConfigFactory
import org.apache.commons.lang.RandomStringUtils
import ru.sbt.orient.model.JacksonJsonSupport

case class LoginData (login:String, pass:String)

class MainHttpService(implicit val logger: LoggingAdapter, implicit val im: Materializer, implicit val as: ActorSystem) 
                                          extends MainHttpServiceTrait 
                                          with OrientDBAuthenticator 
                                          with WSService {
  import com.softwaremill.session._
  import com.softwaremill.session.CsrfDirectives._
  import com.softwaremill.session.CsrfOptions._
  import com.softwaremill.session.SessionDirectives._
  import com.softwaremill.session.SessionOptions._
  
  val nextSessionId = RandomStringUtils.randomAscii(64)
  val sessionConfig = SessionConfig.default(nextSessionId)
  implicit val sessionManager = new SessionManager[UserData](sessionConfig)
  implicit val refreshTokenStorage = new InMemoryRefreshTokenStorage[UserData] {
    def log(msg: String) = logger.debug(msg)
  }

  def mySetSession(v: UserData) = setSession(refreshable, usingCookies, v)
  val myRequiredSession = requiredSession(refreshable, usingCookies)
  val myInvalidateSession = invalidateSession(refreshable, usingCookies)
  
  lazy val loginPage = ConfigFactory.load().getString("loginPage")

  val rh = RejectionHandler.newBuilder().handle {
    case AuthenticationFailedRejection(cause, challengeHeaders) => redirect(loginPage, TemporaryRedirect)
  }.result().withFallback(RejectionHandler.default)
  
  val loginRoute = pathPrefix("api") {
    path("do_login") {
      post {
        logger.debug(s"Trying to login")
        entity(as[LoginData]) {loginData =>
          logger.debug(s"Logging in $loginData.login")

          val userOpt = loadUserData(loginData.login, loginData.pass)
          userOpt match {
            case Some(user) => mySetSession(user) {
              setNewCsrfToken(checkHeader) { 
                complete(OK -> RequestStatus (success = true))
              }
            }

            case _ => val chal = HttpChallenges.oAuth2("example")
              complete (Unauthorized)
          }
        }
      }
    } ~
    path("do_logout") {
      post {
        myRequiredSession { session =>
          myInvalidateSession { 
            logger.debug(s"Logging out $session")
            complete(OK -> RequestStatus (success = true))
          }
        }
      }
    } ~
    path("current_login") {
      get {
        myRequiredSession { session => 
          logger.debug(s"Current session: $session.login")
          complete(OK -> RequestStatus (success = true, entity = session))
        }
      }
    } ~
    path("do_register") {
      post {
        logger.debug(s"Trying to register user")
        entity(as[SbtUser]) { newUser => 
          if (newUser.email == null || newUser.email.isEmpty) {
            complete(BadRequest -> RequestStatus (false, "Email should be provided"))
          }
          else {
            val createdUser = UserService.createUser(newUser)
            complete(Created -> RequestStatus (success = true, entity = createdUser))
          }
        }
      }
    }
  }

  lazy val router = randomTokenCsrfProtection(checkHeader) {
    handleRejections(rh) {
      routes ~ loginRoute ~ myRequiredSession {websocketRoute}
    }
  }
}

trait MainHttpServiceTrait extends JacksonJsonSupport {
  val routes = pathPrefix("pub") {
    getFromDirectory ("./src/webapp")
  }
}
