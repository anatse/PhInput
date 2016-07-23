package org.asem.spray.security

import java.security.MessageDigest
import java.util.Base64

import com.orientechnologies.orient.core.metadata.schema.OType
import org.asem.orient._
import spray.json._

import scala.collection.JavaConversions._

/**
  * Created by gosha-user on 17.07.2016.
  */
case class PhUser(login: String, private val password: Option[String] = None) {
  val pwdHash = computeHash(password.getOrElse(""))

  override def toString = "PhUser(login: " + login + ")"

  def computeHash(pwd: String): String = {
    val dig = MessageDigest.getInstance("SHA1").digest(pwd.getBytes)
    Base64.getEncoder.encodeToString(dig)
  }

  def checkUserExists(): Boolean = {
    val params = Map(
      "login" -> login.toLowerCase(),
      "password" -> pwdHash
    )

    val result = Query.executeQuery("SELECT * FROM PhUser WHERE login.toLowerCase() = :login and password = :password", params)
    if (result.size() == 1) {
      // TODO add users aditional attributes ...
      println("load from database... ")
      true
    }
    else
      false
  }

  def hasPermission(permission: String) = {
    true;
  }

  def createVertexType(): Unit = {
    Database.getTx(
      graph => {
        val vtx = graph.createVertexType("PhUser")
        vtx.createProperty("login", OType.STRING)
        vtx.createProperty("password", OType.STRING)
      }
    )
  }
}

/**
  * Object used to convert between JSON and Scala object
  *
  * @see https://github.com/spray/spray-json
  */
object PhUser extends DefaultJsonProtocol {
  implicit object PhUserJsonFormat extends RootJsonFormat[PhUser] {
    override def write(obj: PhUser): JsValue = {
      JsObject(
        "login" -> JsString(obj.login)
      )
    }

    override def read(json: JsValue): PhUser = {
      json.asJsObject.fields.foreach(field => {
        println(field)
      })

      json.asJsObject.getFields("login", "password") match {
        case Seq(JsString(login), JsString(password)) => new PhUser(login, Some(password))
        case _ => throw new DeserializationException("User expected")
      }
    }
  }
}