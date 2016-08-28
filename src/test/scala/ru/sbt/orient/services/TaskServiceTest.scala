package org.asem.orient.services

import org.asem.orient.model.entities.{Comment, JsonMapper, Task}
import org.joda.time.DateTime
import org.scalatest._
import spray.http.HttpHeaders._
import spray.http.StatusCodes._
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.json._
import spray.routing._
import spray.testkit._

import scala.concurrent.duration._
import scala.language.postfixOps

class TaskServiceTest extends FlatSpec
                    with Matchers
                    with Directives
                    with ScalatestRouteTest
                    with LoginService
                    with TaskService 
{
  def actorRefFactory = system
  implicit val defaultTimeout = RouteTestTimeout(10 seconds)
  
  def setTestCookie(name:String, value:String):HttpRequest ⇒ HttpRequest = {
    req => {
      req.withHeaders(`Cookie`(HttpCookie(name, value)))
    }
  }
  
  "Task service" should "reject unsecuren requests when get task list called" in {
    Get("/task") ~> sealRoute(taskRoute) ~> check {
      status should equal(Unauthorized)
    }
  }
  
  var cookie = ""
  it should "login successfully with demo user" in {
    Post("/login", FormData(Seq("uname" -> "user", "upass" -> "user"))) ~> sealRoute(loginRoute)  ~> check {
      status should equal(OK)
      cookie = header[`Set-Cookie`] match {
        case Some(`Set-Cookie`(HttpCookie("user_token", content, _, _, _, _, _, _, _))) => content
        case _ => ""
      }

      cookie shouldNot be ("")
    }
  }
  
  var savedTask:Task = null
  it should "return list of tasks for secured GET HTTP request" in {
    Get("/task") ~> setTestCookie ("user_token", cookie) ~> sealRoute(taskRoute) ~> check {
      status should equal(OK)
      savedTask = responseAs[List[Task]].head
    }
  }
  
  it should "create add comemnt to task" in {
    val com = Comment(
        owner = "vasya", 
        comment = "Ничего не понял, повторите пож-та", 
        createDate = new DateTime
    )

    println ("savedTask : "  +savedTask)
    Post("/task/" + savedTask.id + "/comment", com) ~> setTestCookie ("user_token", cookie) ~> sealRoute(taskRoute) ~> check {
      status should equal(OK)
      println (responseAs[Comment])
    }
  }
  
  it should "create new Task through the Post HTTP request" in {
    val task = Task(
        id = null,
        name = "Тестовая задача",
        content = "Что то пошло не так",
        status = "Новый",
        assignedPerson = "user",
        changeDate = new DateTime(),
        deadLine =  new DateTime(),
        owner = "user",
        comments = null)

    Post("/task", task) ~> setTestCookie ("user_token", cookie) ~> sealRoute(taskRoute) ~> check {
      status should equal(OK)
      println (responseAs[Task])
    }
  }
}
