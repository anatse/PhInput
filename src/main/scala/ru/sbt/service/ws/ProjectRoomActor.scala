package ru.sbt.service.ws

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import akka.event.LoggingAdapter
import ru.sbt.orient.model.JacksonJsonSupport
import ru.sbt.orient.model.entities.Task
import ru.sbt.orient.model.entities.Comment
import ru.sbt.service.AddComment
import ru.sbt.service.AddTask
import ru.sbt.service.ChangeTask
import ru.sbt.service.DelTask
import ru.sbt.service.TaskOperation

class ProjectRoomActor(roomId: Int) extends Actor with JacksonJsonSupport {
  var participants: Map[String, ActorRef] = Map.empty[String, ActorRef]
  val logger: LoggingAdapter = Logging.getLogger(context.system, this)

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
      println (msg.message)

      msg.message match {
        // For object messages 
        case TaskDTORegex(task) =>
          val taskDTO:TaskOperation = mapper.readValue[TaskOperation] (msg.message)
          taskDTO.operation match {
            case AddTask(task) => 
              val t = task.func(taskDTO.task, msg.sender)
              broadcast(ChatMessage (msg.sender, "New task added: " + t.asInstanceOf[Task].id))

            case DelTask(task) => 
              val t = task.func(taskDTO.task, msg.sender)
              t.asInstanceOf[Either[String, String]] match {
                case Left(s) => broadcast(ChatMessage (msg.sender, "Task deleted successfull"))
                case Right(s)=> broadcast(ChatMessage (msg.sender, "Error deleting task: " + s))
              }

            case ChangeTask(task) => 
              val t = task.func(taskDTO.task, msg.sender)
              t.asInstanceOf[Either[Task, String]] match {
                case Left(s) => broadcast(ChatMessage (msg.sender, "Task changed: " + s.name))
                case Right(s) => broadcast(ChatMessage (msg.sender, "Error changing task: " + s))
              }

            case AddComment(task) => 
              val t = task.func(taskDTO.task, msg.sender)
              t match {
                case x:List[Either[Comment, String]@unchecked] => x.foreach {
                  case Left(s) => broadcast(ChatMessage (msg.sender, "Comment added: " + s))
                  case Right(s) => broadcast(ChatMessage (msg.sender, "Error adding comment: " + s))
                }
              }

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
