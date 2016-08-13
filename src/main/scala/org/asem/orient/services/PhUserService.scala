package org.asem.orient.services

import com.tinkerpop.blueprints.impls.orient.{OrientGraph, OrientVertex}
import org.asem.orient.{Database, Query}
import org.asem.orient.model.CookieAuthenticator._
import org.asem.orient.model.PhUser._
import org.asem.orient.model.{PhUser, UserData}
import spray.http.MediaTypes._
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.routing.{HttpService, _}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by gosha-user on 30.07.2016.
  */
object PhUserService extends BaseDB {
  /**
    * Function create new user and activated it
    */
  def createUser(login: String, pasword: String, email: String, firstName: String, secondName: String): Unit = {
    val user = PhUser(login, pasword, email, firstName, secondName, Some(true))
    createUser(user)
  }

  /**
    * Function create user record in database
    */
  def createUser(user: PhUser): Unit = {
    Database.getTx(
      addVertex("PhUser",
        Map("login" -> user.login,
          "password" -> user.pwdHash,
          "email" -> user.email,
          "firstName" -> user.firstName,
          "secondName" -> user.secondName,
          "activated" -> user.activated,
          "manager" -> user.manager)
      )
    )
  }

  /**
    * Function registedr new user yet no activated
    */
  def registerNewUser(login: String, pasword: String, email: String, firstName: String, secondName: String) = {
    val user = PhUser(login, pasword, email, firstName, secondName)
    createUser(user)
  }

  /**
    * Fucntion retrieves user vertex by user login
    * @param login user login
    * @returnfound user vertex
    */
  def findUserByLogin(login: String): Option[OrientVertex] = {
    Database.getTx(tx => {
      findVertexByAttr(tx, "PhUser", "login", login)
    })
  }

  /**
    * Function activated inactive user
    */
  def activateUser(login: String): Boolean = {
    Database.getTx(
      graph => {
        findVertexByAttr(graph, "PhUser", "login", login) match {
          case Some(vtx: OrientVertex) => {
            vtx.setProperty("activated", true)
            vtx.save
            true
          }
          case _ => false
        }
      }
    )
  }

  /**
    * Function update user vertex in database
    * @param user user data
    * @return success flag
    */
  def changeUser(user: PhUser): Boolean = {
    try {
      Database.getTx(
        graph => {
          findVertexByAttr(graph, "PhUser", "login", user.login) match {
            case Some(vtx: OrientVertex) => {
              if (user.pwdHash != null && !user.pwdHash.isEmpty) vtx.setProperty("password", user.pwdHash)
              if (user.email != null) vtx.setProperty("email", user.email)
              if (user.firstName != null) vtx.setProperty("firstName", user.firstName)
              if (user.secondName != null) vtx.setProperty("secondName", user.secondName)

              if (user.manager.isDefined)
                vtx.setProperty("manager", user.manager.get)

              if (user.activated.isDefined)
                vtx.setProperty("activated", user.activated.get)

              vtx.save
              true
            }
            case _ => {
              false
            }
          }
        }
      )
    }
    catch {
      case e:Exception => false
    }
  }

  /**
    * Sample usage of implicit parameters. Function remove vertex from database
    * @param vtx vertex to remove
    * @param tx implicit parameter OroentGraph object. If not defined new created transaction will be used otherwize gioven tx object
    * @return operation status
    */
  def deleteUser(vtx: OrientVertex)(implicit tx: OrientGraph = null): Boolean = {
    if (tx != null) {
      deleteVertex(tx, vtx)
    }
    else
      Database.getTx(
        graph => {
          deleteVertex(graph, vtx)
        }
      )
  }

  /**
    * Function removes user by it's login. Used deleteUSer function with defined implicit parameter tx = curernt transaction
    * @param login login to be removed
    * @return operation status
    */
  def deleteUserByLogin(login: String): Boolean = {
    Database.getTx(
      graph => {
        findVertexByAttr(graph, "PhUser", "login", login) match {
          case Some(vtx: OrientVertex) => {
            deleteUser (vtx)(tx = graph)
            true
          }
          case _ => false
        }
      }
    )
  }

  /**
    * Function retrieves list of all users
    * @return all users from database
    */
  def findAllUsers () = {
    import java.util.{Collections => JavaCollections}

    import scala.collection.JavaConversions._

    val result = Query.executeQuery("select * from PhUser", JavaCollections.EMPTY_MAP)
    for {
      row <- result
    } yield {
      val PhUser(user) = row
      user
    }
  }
}

/**
  * The trait defines routes for user management service. For all functions except register authentification is required.
  * Implemented fucntions:
  * <ul>
  * <li>register - add new user</li>
  * <li>REST functions for update delete and retrieve user information</li>
  * </ul>
  */
trait PhUserService extends HttpService {
  private val registerRoute = post {
    path("user" / "register") {
       entity(as[PhUser]) {
         newUser => {
           if (newUser.email == null || newUser.email.isEmpty) {
             respondWithStatus(StatusCodes.BadRequest) {
               complete("{sucess: false, message: 'Email should be provided'}")
             }
           }
           else {
             PhUserService.createUser(newUser)
             respondWithStatus(StatusCodes.Created) {
               complete("{success: true, message: 'OK'}")
             }
           }
         }
       }
    }
  }

  private val changeUserRoute = put {
    authenticate(byCookie) {
      user => {
        path ("user" / Segment) {
          login => entity(as[PhUser]) {
            changePass => {
              if (changePass.login != login) {
                respondWithStatus(StatusCodes.BadRequest) { complete ("Fail")}
              }
              else {
                PhUserService.changeUser(changePass)
                complete("{success: true, message: 'OK'}")
              }
            }
          }
        }
      }
    }
  }
  
  private def isManager (user:UserData, f: StandardRoute):RequestContext => Unit = {
    if (user.manager) {
      f
    }
    else {
      respondWithStatus(StatusCodes.Forbidden) {
        complete {
          "{success: false, message: 'Forbidden " + user.login + " = " + user.manager + "'}"
        }
      }
    }
  }
  
  private val deleteUserRoute = delete {
    authenticate(byCookie) {
      user => {
        path ("user" / Segment) {
          login => {
            isManager (user, 
              complete {
                PhUserService.deleteUserByLogin(login)
                "{success: true, message: 'OK'}"
              }
            )
          }
        }
      }
    }
  }
  
  private val getUserByLoginRoute = get {
    authenticate(byCookie) {
      user => {
        path ("user" / Segment) {
          login => {
            respondWithMediaType(`application/json`) {
              isManager (user, 
                complete {
                  val vtx = PhUserService.findUserByLogin(login)
                  if (vtx.isDefined) {
                    val PhUser(user) = vtx.get
                    user
                  }
                  else {
                    "{success: false, message: 'no data'}"
                  }
                }
              )
            }
          }
        }
      }
    }
  }
  
  private val userListRoute = get {
    path ("user") {
      respondWithMediaType(`application/json`) {
        complete {
          val users = PhUserService.findAllUsers;
          users.toArray[PhUser]
        }
      }
    }
  }

  val userManagementRoute = registerRoute ~ changeUserRoute ~ deleteUserRoute ~ getUserByLoginRoute ~ userListRoute
}
