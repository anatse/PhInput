package ru.sbt.service.ws

import akka.actor.{Actor, ActorRef}
import akka.event.LoggingAdapter
import ru.sbt.orient.model.JacksonJsonSupport
import ru.sbt.service.AddTask
import ru.sbt.service.ChangeTask
import ru.sbt.service.DelTask
import ru.sbt.service.TaskOperation

class ProjectRoomActor(roomId: Int)(implicit logger: LoggingAdapter) extends Actor with JacksonJsonSupport {
  var participants: Map[String, ActorRef] = Map.empty[String, ActorRef]

  override def receive: Receive = {
    case UserJoined(name, actorRef) =>
      participants += name -> actorRef
      broadcast(SystemMessage(s"User $name joined channel..."))

    case UserLeft(name) =>
      if (logger.isDebugEnabled)
        logger.debug(s"User $name left channel[$roomId]")

      broadcast(SystemMessage(s"User $name left channel[$roomId]"))
      participants -= name

    case msg: IncomingMessage =>
      val TaskDTORegex = "\\{(.*)\\}".r

      msg.message match {
        // For object messages 
        case TaskDTORegex(task) =>
          val taskDTO:TaskOperation = mapper.readValue (msg.message)
          taskDTO.operation match {
            case AddTask(task) => task.func(taskDTO.task, msg.sender)
            case DelTask(task) => task.func(taskDTO.task, msg.sender)
            case ChangeTask(task) => task.func(taskDTO.task, msg.sender)

            case _ => 
              if (logger.isDebugEnabled)
                logger.debug(s"Unknow task operation: $taskDTO.operation")
          }

        // For simple text messages
        case _ => broadcast(ChatMessage (msg.sender, msg.message))
      }
  }

  def broadcast(message: ChatMessage): Unit = participants.values.foreach(_ ! message)
}
