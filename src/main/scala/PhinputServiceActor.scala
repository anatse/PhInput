import akka.actor.Actor

import com.typesafe.config.ConfigFactory
import org.asem.spray.security._
import spray.http.MediaTypes._
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.json._
import spray.routing._

import scala.concurrent.ExecutionContext.Implicits.global
import spray.routing.authentication._
import spray.routing.directives.CachingDirectives._
import HttpHeaders._
import CacheDirectives._


/**
 * Created by gosha-user on 17.07.2016.
 * @see http://spray.io/documentation/1.2.2/spray-routing/predefined-directives-by-trait/
 * @see http://spray.io/documentation/1.2.2/spray-routing/scheme-directives/scheme/
 */
class PhinputServiceActor extends Actor with MyService with AuthServices {
  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute ~ myRoute1 ~ uploadRoute ~ authRoutes)

}

case class Quiz(name: String, value: String)

object Quiz extends DefaultJsonProtocol {
  implicit val format = jsonFormat2(Quiz.apply)
}

trait MyService extends HttpService with Authenticator {

  def extractUser(userPass: UserPass): String = userPass.user
  val config = ConfigFactory.parseString("John = p4ssw0rd")

  val route =
    sealRoute {
      path("secured") {
        authenticate(BasicAuth(realm = "secure site", config = config, createUser = extractUser _)) {
          userName =>
            complete(s"The user is '$userName'")
        }
      }
    }
  
  

  val myRoute1 = 
//    path("") {
//    authenticate(basicUserAuthenticator) {
//      userId =>
//        get {
//          respondWithMediaType(`application/json`) {
//            complete(
//              listPhramasies(userId = userId.login)
//            )
//          }
//        }
//    }
//  } ~ 
  path("") {
      get {
        redirect("/pub/index.html", StatusCodes.PermanentRedirect)
      }
  }
  
  val myRoute =
//    autoChunkFileBytes(5120000) {
      pathPrefix("pub") {
//        respondWithHeader(`Cache-Control`(`public`, `max-age`(60L*60L*24L))) {
//          compressResponse(Gzip) {
              getFromDirectory("C:\\Projects\\phinp-mv\\src\\webapp")
//          }
//        }
      } ~ path("users_" / "type") {
        post {
          respondWithStatus(201) {
            complete {
              PhUser.createVertexType()
              "OK"
            }
          }
      }
//    }
  }

  val uploadRoute = {
    path("upload") {
      post {
        decompressRequest() {
          entity(as[MultipartFormData]) { formData =>
            complete(formData.fields.map { _.entity.asString }.flatten.foldLeft("")(_ + _))
          }
        }
        //        formField('imageupload.as[Array[Byte]]) { (imageupload) =>
        //          // import spray.httpx.SprayJsonSupport._
        //          val fos: FileOutputStream = new FileOutputStream("filename");
        //          try {
        //            fos.write(imageupload);
        //          } 
        //          finally {
        //            fos.close();
        //          }
        //          complete {
        //            "0"
        //          }
        //        }
      }

  }

}
    }