package org.asem.orient.services

import java.util.Date

import com.tinkerpop.blueprints.Direction._
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import org.asem.orient.Database
import org.asem.orient.model.entities.{Comment, JacksonJsonSupport, Task, EdgeNames}
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.routing.RequestContext

import scala.collection.JavaConversions._
import org.asem.Boot.{PushToChildren, service => mainService}

object TaskService extends BaseDB {
  /**
    * Function ertrieves all task from database
    * @return list of tasks with comments
    */
  def findAllTasks = Database.getTx {tx => val vtxs = tx.getVerticesOfClass("Task"); for {vtx <- vtxs} yield {val Task (task) = vtx;task}}.toList.sortWith((a:Task,b:Task) => a.changeDate.isAfter(b.changeDate))

  private def notifyByWebSocket (msg:String) = { if (mainService != null) mainService ! PushToChildren(msg) }

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
        notifyByWebSocket("TASKS_UPDATED:ADDED")
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
              val oldStatus = vtx.getProperty("status")
              if (task.status == "В работе" && oldStatus != task.status) {
                val asp = graph.getVertex("#" + userId).getProperty[String]("login")
                vertexUpdate (vtx, task2map(task.copy(assignedPerson = asp)))
              }
              else
                vertexUpdate (vtx, task2map(task))
              
              vtx.save
              // add edge between user and task
              

              graph.commit()
              notifyByWebSocket("TASKS_UPDATED:CHANGED")
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
              notifyByWebSocket("TASKS_UPDATED:DELETED")
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
            notifyByWebSocket("TASKS_UPDATED:COMMENT_ADDED")
            Left(comment)
          }
          case _ => Right("Error add comment to task: " + "#" + taskId)
        }
      }
    )
  }
}

trait TaskService extends BaseHttpService with JacksonJsonSupport {
  private val listTasksRouter = get {
    auth {
      user => path("task") {
        complete {
          val tasks = TaskService.findAllTasks
          tasks
        }
      }
    }
  }

  private val addTaskRouter = post {
    auth {
      user => path("task") {
        entity(as[Task]) {
          task => {
            val t = TaskService.addTask(task, user.id)
            println (t)
            complete {t}
          }
        }
      }
    }
  }

  private val deleteTaskRouter = delete {
    auth {
      user => path("task" / Segment) {
        reportId => {
          TaskService.deleteTask(reportId, user.id) match {
            case Left(rep) => respondWithStatus(StatusCodes.OK   ) { complete (rep)}
            case Right(s) => respondWithStatus(StatusCodes.InternalServerError) { complete (s) }
          }
        }
      }
    }
  }

  private val updateTaskRouter = put {
    auth {
      user => path("task" / Segment) {
        reportId => {
          entity(as[Task]) {
            task => TaskService.changeTask(task, user.id) match {
              case Left(rep) => respondWithStatus(StatusCodes.OK   ) { complete (rep)}
              case Right(s) => respondWithStatus(StatusCodes.Conflict) { complete (s) }
            }
          }
        }
      }
    }
  }

  private val addTaskCommentRouter = post {
    auth {
      user => path("task" / Segment / "comment") {
        taskId => {
          entity(as[Comment]) {
            comment => {
              TaskService.addComment(taskId, comment, user.id) match {
                case Left(com) => respondWithStatus(StatusCodes.OK ) { complete (com)}
                case Right(s) => respondWithStatus(StatusCodes.Conflict) { complete (s) }
              }
            }
          }
        }
      }
    }
  }

  lazy val taskRoute = listTasksRouter ~ addTaskRouter ~ deleteTaskRouter ~ updateTaskRouter ~ addTaskCommentRouter
}
