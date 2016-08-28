package org.asem.orient.model

import java.security._
import java.util._

import com.tinkerpop.blueprints.impls.orient.OrientVertex
import spray.json._

import scala.language.implicitConversions

/**
  * Class represents user for pharmacy input system
  * Created by gosha-user on 30.07.2016.
  */
case class PhUser(login: String, password: String, email: String = "", firstName: String = "", secondName: String = "", activated: Option[Boolean] = None, manager: Option[Boolean] = None, id:String = "") {
  require(login != null, "login should be provided")
//  require(password != null, "password should be provided")
//  require(email != null, "email should be provided")

  lazy val pwdHash = computeHash(password)
  
  override def toString = login + "," + email + "," + firstName + "," + secondName + "," + manager.getOrElse(false)

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
object PhUser extends DefaultJsonProtocol with BaseModelFuncs {
  private val PhUserRegex = "(.*),(.*),(.*),(.*),(.*)".r

  def fromString(str: String): Option[PhUser] =  str match {
    case PhUserRegex(login, email, firstName, secondName, manager) => Some(PhUser(
      login = login,
      password = "",
      email = email,
      firstName = firstName,
      secondName = secondName,
      activated = Some(true),
      manager = Some(manager match {
        case "null" => false
        case s:String => s.toBoolean
        case _ => false
      })))
    case _ => None
  }

  def unapply(vtx: OrientVertex): Option[PhUser] = Some(createFromVertex[PhUser](vtx)(0))
  def unapply(obj: JsObject): Option[PhUser] = Some (createFromJson[PhUser](obj)(0))

  implicit object PhUserJsonFormat extends RootJsonFormat[PhUser] {
    override def write(obj: PhUser): JsValue = JsObject(fields = obj)
    override def read(json: JsValue): PhUser = createFromJson[PhUser](json.asJsObject)(0)
  }
}




