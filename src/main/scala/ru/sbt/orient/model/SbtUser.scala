package ru.sbt.orient.model

import java.security._
import java.util._

import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import ru.sbt.orient.model.entities.BaseEntity
import scala.language.implicitConversions

/**
  * Class represents user for pharmacy input system
  * Created by gosha-user on 30.07.2016.
  */
case class SbtUser(login: String, password: String, email: String = "", firstName: String = "", secondName: String = "", activated: Option[Boolean] = None, manager: Option[Boolean] = None, id:String = "") extends BaseEntity {
  require(login != null, "login should be provided")
  lazy val pwdHash = computeHash(password)
  
  override def toString = login + "," + email + "," + firstName + "," + secondName + "," + manager.getOrElse(false) + "," + id

  def computeHash(pwd: String): String = {
    if (pwd == null || pwd.isEmpty) {
      pwd
    }
    else {
      val dig = MessageDigest.getInstance("SHA1").digest(pwd.getBytes)
      Base64.getEncoder.encodeToString(dig)
    }
  }

  def unapply(user:SbtUser) = Some (user.login, user.pwdHash, user.email, user.firstName, user.secondName, user.activated, user.manager, user.id)
}

/**
  * Object used to deserialize PhUser from string, using scala regexp unapply function
  */
object SbtUser {
  private val SbtUserRegex = "(.*),(.*),(.*),(.*),(.*),(.*)".r

  def getId[B <: Vertex] (vtx:B):String = {
    val id = vtx.getId.toString
    id.substring(1, id.length-1)
  }

  def fromString(str: String): Option[SbtUser] =  str match {
    case SbtUserRegex(login, email, firstName, secondName, manager, id) => Some(SbtUser(
      login = login,
      password = "",
      email = email,
      firstName = firstName,
      secondName = secondName,
      activated = Some(true),
      id = id,
      manager = Some(manager match {
        case "null" => false
        case s:String => s.toBoolean
        case _ => false
      })))
    case _ => None
  }

  def unapply(vtx: OrientVertex): Option[SbtUser] = Some (
    SbtUser (
      id = getId (vtx),
      login = vtx.getProperty[String]("login"),
      password = "",
      email = vtx.getProperty[String]("email"),
      firstName = vtx.getProperty[String]("firstName"),
      secondName = vtx.getProperty[String]("secondName"),
      activated = Option(vtx.getProperty[Boolean]("activated")),
      manager = Option(vtx.getProperty[Boolean]("manager"))
    )
  )
}
