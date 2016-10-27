package ru.sbt.service.ws

import akka.actor._
import akka.http.scaladsl.model.ws.{ Message, TextMessage }
import akka.stream.FlowShape
import akka.stream.OverflowStrategy
import akka.stream.scaladsl._

class ProjectRoom(roomId: Int, actorSystem: ActorSystem) {
  private[this] val chatRoomActor = actorSystem.actorOf(Props(classOf[ProjectRoomActor], roomId))

  def websocketFlow(user: String): Flow[Message, Message, Any] = {
    val graph = GraphDSL.create(Source.actorRef[ChatMessage](5, OverflowStrategy.fail)) { 
      implicit builder: GraphDSL.Builder[ActorRef] =>
        import GraphDSL.Implicits._
        source =>
      
        // flow used as input, it takes Messages
        val fromWebsocket = builder.add(
          Flow[Message].collect {
            case TextMessage.Strict(txt) => IncomingMessage(user, txt)
          }
        )

        // flow used as output, it returns Messages
        val backToWebsocket = builder.add(
          Flow[ChatMessage].map {
            case ChatMessage(author, text) => TextMessage(s"[$author]: $text")
          }
        )

        // send messages to the actor, if sent also UserLeft(user) before stream completes.
        val messengerActorSink = Sink.actorRef[ChatEvent](chatRoomActor, UserLeft("left"))

        // a merger for two pipes
        val merge = builder.add(Merge[ChatEvent](2))

        //Materialized value of Actor who sits in the chatroom
        val actorAsSource = builder.materializedValue.map(actor => UserJoined(user, actor))

        fromWebsocket ~> merge.in(0)

        //If Source actor is just created, it should be sent as UserJoined and registered as particiant in the room

        actorAsSource ~> merge.in(1)

        //Merges both pipes above and forwards messages to chatroom represented by ChatRoomActor

        merge ~> messengerActorSink

        //Actor already sits in chatRoom so each message from room is used as source and pushed back into the websocket

        source ~> backToWebsocket

        FlowShape(fromWebsocket.in, backToWebsocket.out)
      }

      //Factory method allows for materialization of this Source
      Flow.fromGraph(graph)
  }

  def sendMessage(message: ChatMessage): Unit = chatRoomActor ! message
}

object ProjectRoom {
  def apply(roomId: Int)(implicit actorSystem: ActorSystem) = new ProjectRoom(roomId, actorSystem)
}