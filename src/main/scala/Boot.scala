import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import spray.can.Http
import java.io.FileInputStream
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

import org.asem.spray.security.RSA

import scala.concurrent.duration._
import spray.io.ServerSSLEngineProvider


/**
 * Created by gosha-user on 16.07.2016.
 */
object Boot extends App {
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[PhinputServiceActor], "pharm-service")
  val config = ConfigFactory.load()

  implicit val mySSLContext: SSLContext = {
    val context = SSLContext.getInstance("TLS")

    val keyStore:KeyStore = RSA.keyStore
    val keyManagerFactory:KeyManagerFactory = KeyManagerFactory.getInstance("SunX509");
    keyManagerFactory.init(keyStore, RSA.getPassword.getPassword);

    val trustManagerFactory:TrustManagerFactory = TrustManagerFactory.getInstance("SunX509");
    trustManagerFactory.init(keyStore);

    context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
    context
  }

  implicit val myEngineProvider = ServerSSLEngineProvider { engine =>
//    engine.setEnabledCipherSuites(Array("TLS_RSA_WITH_AES_256_CBC_SHA"))
    engine.setEnabledProtocols(Array("TLSv1.2"))
    engine
  }
  
  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = config.getString("spray.can.server.host"), port = config.getInt("spray.can.server.port"))
}
