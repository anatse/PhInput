import akka.actor.Actor
import org.asem.spray.security._
import spray.http.MediaTypes._
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.json._
import spray.routing._

import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by gosha-user on 17.07.2016.
  */
class PhinputServiceActor extends Actor with MyService with AuthServices {
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute ~ myRoute1 ~ authRoutes)

  override def listPhramasies(userId: String): Quiz = {
    Quiz("test", "value: " + userId)
  }
}

case class Quiz(name: String, value: String)

object Quiz extends DefaultJsonProtocol {
  implicit val format = jsonFormat2(Quiz.apply)
}

trait MyService extends HttpService with Authenticator {

  def listPhramasies(userId: String): Quiz;

  val myRoute1 = path("list") {
    authenticate(basicUserAuthenticator) {
      userId => get {
        respondWithMediaType(`application/json`) {
          complete(
            listPhramasies(userId = userId.login)
          )
        }
      }
    }
  }

  val myRoute =
    path("1") {
      get {
        respondWithMediaType(`text/html`) {
          // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Say hello to xxx
                  <i>spray-routing</i>
                  on
                  <i>spray-can</i>
                  !</h1>
              </body>
            </html>
          }
        }
      }
    }
}