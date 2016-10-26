package ru.sbt.service

import scala.concurrent.ExecutionContext.Implicits.global
import com.tinkerpop.blueprints.Direction._
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import ru.sbt.orient.Database
import ru.sbt.orient.model.entities._
import ru.sbt.orient.model._

import scala.collection.JavaConversions._

sealed trait TaskOperations { 
  def name:String 
  def func(task:Task, userId:String):Any
  def unapply(value: String): Option[TaskOperations] = if (name == value) Some(this) else None
}

case object AddTask extends TaskOperations { 
  val name = "ADD"
  def func (task:Task, userId:String) = TaskService.addTask(task, userId)
}

case object DelTask extends TaskOperations { 
  val name = "DEL"
  def func (task:Task, userId:String) = TaskService.deleteTask(task.id, userId)
}

case object ChangeTask extends TaskOperations { 
  val name = "CHANGE"
  def func (task:Task, userId:String) = TaskService.changeTask(task, userId)
}

case class TaskOperation(operation:String, task: Task)

object TaskService extends BaseDB {
  /**
    * https://github.com/google/code-prettify
    * http://markup.su/highlighter/
    * Function ertrieves all task from database
    * @return list of tasks with comments
    */
  def findAllTasks = Database.getTx {tx => val vtxs = tx.getVerticesOfClass("Task"); for {vtx <- vtxs} yield {val Task (task) = vtx;task}}.toList.sortWith((a:Task,b:Task) => a.changeDate.isAfter(b.changeDate))

  private def task2map (task:Task) = Map( "name" -> task.name, "content" -> task.content, "status" -> task.status, "assignedPerson" -> task.assignedPerson,
    "changeDate" -> task.changeDate, "deadLine" -> task.deadLine, "owner" -> task.owner )

  /**
    * Function add task to database
    * @param task task object
    * @return nothing
    */
  def addTask (task:Task, userId:String):Task = {
    Database.getTx(
      tx => {
        val vtx = addVertex("Task", task2map(task)).apply(tx)
        val Task(ret) = vtx
        ret
      }
    )
  }

  def changeTask (task:Task, userId:String):Either[Task, String] = {
    Database.getTx(
      graph => {
        try {
          graph.getVertex("#" + task.id) match {
            case vtx: OrientVertex => {
              val oldStatus:String = vtx.getProperty("status")
              if (task.status == "В работе" && oldStatus != task.status) {
                val asp = graph.getVertex("#" + userId).getProperty[String]("login")
                vertexUpdate (vtx, task2map(task.copy(assignedPerson = asp)))
              }
              else
                vertexUpdate (vtx, task2map(task))
              
              vtx.save
              graph.commit()
              Left(task)
            }
            case _ => Right("Error updating record: " + "#" + task.id)
          }
        }
        catch {
          case e:Exception => Right(e.toString)
        }
      }
    )
  }

  def deleteTask (taskId: String, userId:String):Either[String, String] = {
    Database.getTx(
      graph => {
        try {
          graph.getVertex("#" + taskId) match {
            case vtx: OrientVertex => {
              graph.removeVertex(vtx)
              graph.commit()
              Left("DELETE SUCCESSFULL")
            }
            case _ => Right("Error deleting record: " + "#" + taskId)
          }
        }
        catch {
          case e:Exception => Right(e.toString)
        }
      }
    )
  }

  def addComment (taskId:String, comment:Comment, userId:String) = {
    Database.getTx(
      graph => {
        graph.getVertex("#" + taskId) match {
          case vtx: OrientVertex => {
            val comVtx = addVertex("Comment", Map(
              "owner" -> graph.getVertex("#" + userId).getProperty[String]("login"),
              "comment" -> comment.comment,
              "createDate" -> comment.createDate
            )).apply(graph)

            vtx.addEdge("com", comVtx)
            graph.commit()
            Left(comment)
          }
          case _ => Right("Error add comment to task: " + "#" + taskId)
        }
      }
    )
  }
}