package org.asem.orient.model

import com.tinkerpop.blueprints.impls.orient.OrientVertex
import java.security._
import java.util._

import spray.json._

/**
  * Class represents user for pharmacy input system
  * Created by gosha-user on 30.07.2016.
  */
case class PhUser(login: String, password: String, email: String = "", firstName: String = "", secondName: String = "", activated: Boolean = false, manager: Boolean = false) {
  require(login != null, "login should be provided")
//  require(password != null, "password should be provided")
//  require(email != null, "email should be provided")

  lazy val pwdHash = computeHash(password)
  
  override def toString = login + "," + email + "," + firstName + "," + secondName + "," + manager

  def computeHash(pwd: String): String = {
    if (pwd == null || pwd.isEmpty) {
      pwd
    }
    else {
      val dig = MessageDigest.getInstance("SHA1").digest(pwd.getBytes)
      Base64.getEncoder.encodeToString(dig)
    }
  }
  
  def unapply(user:PhUser) = Some (user.login, user.pwdHash, user.email, user.firstName, user.secondName, user.activated, user.manager)
}

/**
  * Object used to deserialize PhUser from string, using scala regexp unapply function
  */
object PhUser extends DefaultJsonProtocol {
  private val PhUserRegex = "(.*),(.*),(.*),(.*),(.*)".r

  def fromString(str: String): Option[PhUser] = str match {
    case PhUserRegex(login, email, firstName, secondName, manager) => Some(PhUser(
          login = login, 
          password = "", 
          email = email, 
          firstName = firstName, 
          secondName = secondName, 
          activated = true, 
          manager = manager.toBoolean))
    case _ => None
  }
  
  implicit def js2string(x: Option[JsValue]):String = {
    if (x.isDefined) {
      x.get match {
        case JsString(s) => s
        case _ => null
      }
    }
    else 
      null
  }

  def unapply(vtx: OrientVertex): Option[PhUser] = {
    Some(
      PhUser(
        login = vtx.getProperty[String]("login"), 
        password = "", 
        email = vtx.getProperty[String]("email"),
        firstName = vtx.getProperty[String]("firstName"),
        secondName = vtx.getProperty[String]("secondName"),
        activated = vtx.getProperty[Boolean]("activated"),
        manager = vtx.getProperty[Boolean]("manager")
      )
    )
  }

  def unapply(obj: JsObject): Option[PhUser] = {
    val fields = obj.fields
    Some (
      PhUser (
        fields.get("login"),
        fields.get("password"),
        fields.get("email"),
        fields.get("firstName"),
        fields.get("secondName"),
        fields.getOrElse("activated", JsBoolean(false)).asInstanceOf[JsBoolean].value,
        fields.getOrElse("manager", JsBoolean(false)).asInstanceOf[JsBoolean].value
      )
    )
  }

  private def getOrElse(value:String):String = {
    if (value == null) "" else value
  }

  implicit object PhUserJsonFormat extends RootJsonFormat[PhUser] {
    override def write(obj: PhUser): JsValue = {
      JsObject(
        "login" -> JsString(obj.login),
        "email" -> JsString(obj.email),
        "password" -> JsString(""),
        "firstName" -> JsString(getOrElse (obj.firstName)),
        "secondName" -> JsString(getOrElse (obj.secondName)),
        "activated" -> JsBoolean(obj.activated),
        "manager" -> JsBoolean(obj.manager)
      )
    }

    override def read(json: JsValue): PhUser = {
      val PhUser(user) = json.asJsObject
      user
    }
  }
}




