package ru.sbt

import akka.actor.ActorSystem
import akka.event.Logging
import akka.event.LoggingAdapter
import akka.http.scaladsl.ConnectionContext
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.ExceptionHandler
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.HttpsConnectionContext
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import com.softwaremill.session._
import com.softwaremill.session.CsrfDirectives._
import com.softwaremill.session.CsrfOptions._
import com.softwaremill.session.SessionDirectives._
import com.softwaremill.session.SessionOptions._

import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import ru.sbt.security.RSA
import ru.sbt.service.MainHttpService
import ru.sbt.utils.Config
import scala.concurrent.ExecutionContext

object Main extends App with Config {
  implicit val actorSystem = ActorSystem()
  implicit val executor: ExecutionContext = actorSystem.dispatcher
  implicit val logger: LoggingAdapter = Logging(actorSystem, getClass)
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit def myExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case th: Throwable =>
        extractUri { uri =>
          complete(HttpResponse(InternalServerError, entity = th.getMessage))
        }
  }
  
  val mySSLContext: SSLContext = {
    val context = SSLContext.getInstance("TLS")

    val keyStore: KeyStore = RSA.keyStore
    val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509");
    keyManagerFactory.init(keyStore, RSA.getPassword.getPassword);

    val trustManagerFactory: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509");
    trustManagerFactory.init(keyStore);

    context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
    context
  }

  val https: HttpsConnectionContext = ConnectionContext.https(mySSLContext)
  val httpService = new MainHttpService
  val commonRoutes = httpService.router

  Http().bindAndHandle(commonRoutes, httpHost, httpsPort, connectionContext = https)
  Http().bindAndHandle(commonRoutes, httpHost, httpPort)
}

