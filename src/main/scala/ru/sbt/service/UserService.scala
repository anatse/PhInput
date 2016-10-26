package ru.sbt.service

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import ru.sbt.orient.Database
import ru.sbt.orient.Query
import ru.sbt.orient.model.SbtUser

object UserService extends BaseDB {
  /**
    * Function create new user and activated it
    */
  def createUser(login: String, pasword: String, email: String, firstName: String, secondName: String): Unit = {
    val user = SbtUser(login, pasword, email, firstName, secondName, Some(true))
    createUser(user)
  }

  /**
    * Function create user record in database
    */
  def createUser(user: SbtUser): SbtUser = {
    val SbtUser(createdUser) = Database.getTx(
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

    createdUser
  }

  /**
    * Function registedr new user yet no activated
    */
  def registerNewUser(login: String, pasword: String, email: String, firstName: String, secondName: String) = {
    val user = SbtUser(login, pasword, email, firstName, secondName)
    createUser(user)
  }

  /**
    * Fucntion retrieves user vertex by user login
    * @param login user login
    * @return found user vertex
    */
  def findUserByLogin(login: String): Option[OrientVertex] = {
    Database.getTx(tx => {
      findVertexByAttr(tx, "SbtUser", "login", login)
    })
  }

  /**
    * Function activated inactive user
    */
  def activateUser(login: String): Boolean = {
    Database.getTx(
      graph => {
        findVertexByAttr(graph, "PhUser", "login", login) match {
          case Some(vtx: OrientVertex) =>
            vtx.setProperty("activated", true)
            vtx.save
            true
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
  def changeUser(user: SbtUser): Boolean = {
    try {
      Database.getTx(
        graph => {
          findVertexByAttr(graph, "PhUser", "login", user.login) match {
            case Some(vtx: OrientVertex) =>
              if (user.pwdHash != null && !user.pwdHash.isEmpty) vtx.setProperty("password", user.pwdHash)
              if (user.email != null) vtx.setProperty("email", user.email)
              if (user.firstName != null) vtx.setProperty("firstName", user.firstName)
              if (user.secondName != null) vtx.setProperty("secondName", user.secondName)
              if (user.manager.isDefined) vtx.setProperty("manager", user.manager.get)
              if (user.activated.isDefined) {
                vtx.setProperty("activated", user.activated.get)
                println ("user activated: " + vtx.getProperty[Boolean]("activated"))
              }

              vtx.save()
              true
            case _ =>
              false
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
          case Some(vtx: OrientVertex) =>
            deleteUser (vtx)(tx = graph)
            true
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
      val SbtUser(user) = row
      user
    }
  }
}
