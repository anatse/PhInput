package org.asem.orient.model

import java.security._
import java.util.{Base64, Date}

import org.asem.orient.Database
import spray.json.{DefaultJsonProtocol, DeserializationException, JsNumber, JsObject, JsString, JsValue, RootJsonFormat}

/**
  * Class represents user for pharmacy input system
  * Created by gosha-user on 30.07.2016.
  */
case class PhUser(login: String, private val password: String, email: String = "", firstName: String = "", secondName: String = "", activated: Boolean = false) {
  lazy val pwdHash = computeHash(password)

  override def toString = login + "," + email + "," + firstName + "," + secondName

  def computeHash(pwd: String): String = {
    if (pwd.isEmpty) {
      pwd
    }
    else {
      val dig = MessageDigest.getInstance("SHA1").digest(pwd.getBytes)
      Base64.getEncoder.encodeToString(dig)
    }
  }
}

/**
  * Object used to deserialize PhUser from string, using scala regexp unapply function
  */
object PhUser extends DefaultJsonProtocol {
  val PhUserRegex = "(.*),(.*),(.*),(.*)".r

  def unapply(str: String): Option[PhUser] = str match {
    case PhUserRegex(login, email, firstName, secondName) => Some(PhUser(login, "", email, firstName, secondName, true))
    case _ => None
  }

  def createUser(user: PhUser): PhUser = {
    Database.getTx(
      graph => {
        val vtx = graph.addVertex("class:PhUser", java.util.Collections.EMPTY_MAP)
        vtx.setProperty("login", user.login)
        vtx.setProperty("password", user.pwdHash)
        vtx.setProperty("email", user.email)
        vtx.setProperty("firstName", user.firstName)
        vtx.setProperty("secondName", user.secondName)
        vtx.setProperty("activated", if (user.activated) 1 else 0)
        vtx.save()
      }
    )

    user
  }

  implicit object PhUserJsonFormat extends RootJsonFormat[PhUser] {
    override def write(obj: PhUser): JsValue = {
      JsObject(
        "login" -> JsString(obj.login)
      )
    }

    override def read(json: JsValue): PhUser = {
      val jso = json.asJsObject
      jso.getFields("login", "password") match {
        case Seq(JsString(login), JsString(password)) => new PhUser(login, password)
        case _ => throw new DeserializationException("User expected")
      }
    }
  }

}




