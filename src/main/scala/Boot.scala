import java.security.{KeyStore, SecureRandom}
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.asem.orient.services._
import org.asem.spray.security.RSA
import spray.can.Http
import spray.io.ServerSSLEngineProvider

import scala.concurrent.duration._

/**
 * Created by gosha-user on 16.07.2016.
 * @see http://spray.io/wjax
 */
object Boot extends App {
  implicit val system = ActorSystem("spray-server")

  // create and start our service actor
  val service = system.actorOf(Props[MainServiceActor], "pharm-service")
  val config = ConfigFactory.load()

  implicit val mySSLContext: SSLContext = {
    val context = SSLContext.getInstance("TLS")

    val keyStore: KeyStore = RSA.keyStore
    val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509");
    keyManagerFactory.init(keyStore, RSA.getPassword.getPassword);

    val trustManagerFactory: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509");
    trustManagerFactory.init(keyStore);

    context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
    context
  }

  implicit val myEngineProvider = ServerSSLEngineProvider { engine =>
    engine.setEnabledProtocols(Array("TLSv1.2"))
    engine
  }

  implicit val timeout = Timeout(5 seconds)
  IO(Http) ? Http.Bind(service, interface = config.getString("spray.can.server.host"), port = config.getInt("spray.can.server.port"))
}
