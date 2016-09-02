package org.asem

import java.security.{KeyStore, SecureRandom}
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}

import _root_.spray.can.Http
import _root_.spray.can.server.UHttp
import _root_.spray.io.ServerSSLEngineProvider
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.asem.orient.services._
import org.asem.spray.security.RSA

import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Created by gosha-user on 16.07.2016.
 * @see http://spray.io/wjax
 */
object Boot extends App {
  final case class PushToChildren(msg: String)

  object WebSocketServer {
    def props() = Props(classOf[WebSocketServer])
  }

  class WebSocketServer extends Actor with ActorLogging {
    def receive = {
      // when a new connection comes in we register a WebSocketConnection actor as the per connection handler
      case Http.Connected(remoteAddress, localAddress) =>
        val serverConnection = sender()
        val conn = context.actorOf(Props(classOf[MainServiceActor], serverConnection))
        serverConnection ! Http.Register(conn)
        
      case PushToChildren(msg: String) =>
        val children = context.children
        children.foreach(ref => ref ! Push(msg))
    }
  }

  implicit val system = ActorSystem("spray-server")

  val service = system.actorOf(Props[WebSocketServer], "sber-spray-service") // for websocket
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
  IO(UHttp) ? Http.Bind(service, interface = config.getString("spray.can.server.host"), port = config.getInt("spray.can.server.port"))
  
//  while (true) {
//    var msg = readLine("Give a message and hit ENTER to push message to the children ...\n")
//    service ! PushToChildren(msg)
//  }
}