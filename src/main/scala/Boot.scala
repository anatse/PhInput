import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import spray.can.Http

import scala.concurrent.duration._


/**
  * Created by gosha-user on 16.07.2016.
  */
object Boot extends App {
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[PhinputServiceActor], "pharm-service")
  val config = ConfigFactory.load()

  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = config.getString("spray.can.server.host"), port = config.getInt("spray.can.server.port"))
}
