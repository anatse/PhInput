package ru.sbt.service

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.directives.WebSocketDirectives
import ru.sbt.service.ws.ProjectRooms
import ru.sbt.security.UserData

trait WSService extends Directives with WebSocketDirectives {
  implicit val im: Materializer
  implicit val as: ActorSystem

  def websocketRoute = {user:UserData => path("ws-project" / Segment) {
      projectId => handleWebSocketMessages(
        ProjectRooms.findOrCreate(projectId.toInt).websocketFlow(user.login)
      )
    }
  }
}
