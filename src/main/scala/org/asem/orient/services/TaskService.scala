package org.asem.orient.services

import java.util.Date

import com.tinkerpop.blueprints.Direction._
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import org.asem.orient.Database
import org.asem.orient.model.entities.{Comment, JacksonJsonSupport, Task}
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.routing.RequestContext

import scala.collection.JavaConversions._

object TaskService extends BaseDB {
  /**
    * Function ertrieves all task from database
     * @return list of tasks with comments
    */
  def findAllTasks = Database.getTx {tx => val vtxs = tx.getVerticesOfClass("Task"); for {vtx <- vtxs} yield {val Task (task) = vtx;task}}.toList

  private def task2map (task:Task) = Map( "name" -> task.name, "content" -> task.content, "status" -> task.status, "assignedPerson" -> task.assignedPerson,
    "changeDate" -> task.changeDate, "deadLine" -> task.deadLine, "owner" -> task.owner )

  /**
    * Function add task to database
    * @param task task object
    * @return nothing
    */
  def addTask (task:Task):Task = {
    import org.asem.Boot.{service => mainService}

    Database.getTx(
      tx => {
        val vtx = addVertex("Task", task2map(task)).apply(tx)
        val Task(ret) = vtx
        
        if (mainService != null)
          mainService ! Push("Hello new task")
    
        ret
      }
    )
  }

  def changeTask (task:Task):Either[Task, String] = {
    Database.getTx(
      graph => {
        try {
          graph.getVertex("#" + task.id) match {
            case vtx: OrientVertex => {
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
  
  def addComment (taskId:String, comment:Comment) = {
    Database.getTx(
      graph => {
        graph.getVertex("#" + taskId) match {
          case vtx: OrientVertex => {
            val comVtx = addVertex("Comment", Map(
              "owner" -> comment.owner, 
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
            val t = TaskService.addTask(task)
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
          complete {"Nope"}
        }
      }
    }
  }

  private val updateTaskRouter = put {
    auth {
      user => path("task" / Segment) {
        reportId => {
          entity(as[Task]) {
            task => TaskService.changeTask(task) match {
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
              TaskService.addComment(taskId, comment) match {
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
