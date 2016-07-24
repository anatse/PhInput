import akka.actor.Actor
import org.asem.spray.security._
import spray.routing._

//import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by gosha-user on 17.07.2016.
  */
class PhinputServiceActor extends Actor with HtmlProvideService with AuthServices {
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(htmlRoute ~ authRoutes)

}

trait HtmlProvideService extends HttpService with Authenticator {
  val htmlRoute = pathPrefix("public") {
      getFromResourceDirectory("public")
  }
}