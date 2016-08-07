package org.asem.orient.services

import com.tinkerpop.blueprints.impls.orient.{OrientGraph, OrientVertex}
import org.asem.orient.Database
import org.asem.orient.model.CookieAuthenticator._
import org.asem.orient.model.PhUser
import org.asem.orient.model.PhUser._
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.routing.{HttpService, _}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by gosha-user on 30.07.2016.
  */
object PhUserService extends BaseDB {
  /**
    * Fucntion create new user and activated it
    */
  def createUser(login: String, pasword: String, email: String, firstName: String, secondName: String): Unit = {
    val user = PhUser(login, pasword, email, firstName, secondName, true)
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
          "activated" -> (if (user.activated) 1 else 0))
      )
    )
  }

  /**
    * Function registedr new user yet no activated
    */
  def registerNewUser(login: String, pasword: String, email: String, firstName: String, secondName: String) = {
    val user = PhUser(login, pasword, email, firstName, secondName, false)
    createUser(user)
  }

  def findUserByLogin(login: String): Option[OrientVertex] = {
    Database.getTx(tx => {
      findVertexByAttr(tx, "PhUser", "login", login)
    })
  }

  /**
    * Fucntion activated inactive user
    */
  def activateUser(login: String): Boolean = {
    Database.getTx(
      graph => {
        findVertexByAttr(graph, "PhUser", "login", login) match {
          case Some(vtx: OrientVertex) => {
            vtx.setProperty("activated", 1)
            vtx.save
            true
          }
          case _ => false
        }
      }
    )
  }

  def changeUser(user: PhUser): Boolean = {
    Database.getTx(
      graph => {
        findVertexByAttr(graph, "PhUser", "login", user.login) match {
          case Some(vtx: OrientVertex) => {
            if (user.email != null) vtx.setProperty("password", user.pwdHash)
            if (user.email != null) vtx.setProperty("email", user.email)
            if (user.firstName != null) vtx.setProperty("firstName", user.firstName)
            if (user.secondName != null) vtx.setProperty("secondName", user.secondName)
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
}

trait PhUserService extends HttpService {
  val userManagementRoute =
    path("user" / "register") {
      post {
        entity(as[PhUser]) {
          newUser => {
            PhUserService.createUser(newUser)
            respondWithStatus(StatusCodes.Created) {
              complete("OK " + newUser.login)
            }
          }
        }
      }
    } ~ authenticate(byCookie) {
      currentUser => pathPrefix("user" / Segment) {
        setCookie(HttpCookie(name = "user_token", content = currentUser.token, maxAge = Some(600)))
        login => pathEnd {
          put {
            entity(as[PhUser]) {
              changePass => {
                if (currentUser.login != login) {
                  reject(ValidationRejection("Cannot change password for another user"))
                }
                else {
                  PhUserService.changeUser(changePass)
                  complete {
                    "OK"
                  }
                }
              }
            }
          } ~ delete {
            PhUserService.deleteUserByLogin(login)
            complete {
              "OK"
            }
          }
        }
      }
    }
}
