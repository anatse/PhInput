package org.asem.spray.security

import java.security.MessageDigest
import java.util.{Base64, Date}

import com.orientechnologies.orient.core.metadata.schema.OType
import com.tinkerpop.blueprints.{Parameter, Vertex}
import org.asem.orient._
import spray.json.{JsObject, JsString, _}

import scala.collection.JavaConversions._

/**
  * Created by gosha-user on 17.07.2016.
  */
case class PhUser(login: String, private val password: Option[String] = None, attributes:Map[String, Any] = Map()) {
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
}

/**
  * Object used to convert between JSON and Scala object
  *
  * @see https://github.com/spray/spray-json
  */
object PhUser extends DefaultJsonProtocol {
  def createVertexType(): Unit = {
    Database.getTx(
      graph => {
        val vtx = graph.createVertexType("PharmUser")
        vtx.createProperty("login", OType.STRING).setMandatory(true).setNotNull(true)
        vtx.createProperty("password", OType.STRING).setMandatory(true).setNotNull(true)
        vtx.createProperty("firstName", OType.STRING).setMandatory(true).setNotNull(true)
        vtx.createProperty("secondName", OType.STRING).setMandatory(true).setNotNull(true)
        graph.createKeyIndex("login", classOf[Vertex], new Parameter("type", "UNIQUE"), new Parameter("class", "PharmUser"));
      }
    )
  }

  implicit object PhUserJsonFormat extends RootJsonFormat[PhUser] {
    override def write(obj: PhUser): JsValue = {
      if (obj.attributes != null) {
        val sequence = for {
          attr <- (obj.attributes + ("login" -> obj.login))
        } yield {
          val value = attr._2 match {
            case s:String => JsString (s)
            case s:Long => JsNumber (s)
            case d:Date => JsString ({
                              val format = new java.text.SimpleDateFormat("dd-MM-yyyy")
                              format.format(d)
                            })
            case d:BigDecimal => JsNumber(d)
            case i:Int => JsNumber(i)
            case others  => JsString("not found converter for: " + others.getClass.getName)
          }

          attr._1 -> value
        }

        JsObject (
          sequence
        )
      }
      else {
        JsObject (
          "login" -> JsString(obj.login)
        )
      }
    }

    override def read(json: JsValue): PhUser = {
      val jso = json.asJsObject
      jso.getFields("login", "password") match {
        case Seq(JsString(login), JsString(password)) => {
          val map = for {
            field <- jso.fields
            if (field._1 != "login" && field._2 != "password")
          } yield {
            (field._1 -> {
              field._2 match {
                case s:JsString => s.value
                case n:JsNumber => n.value
                case others => "error convert from " + others.getClass.getName
              }
            })
          }

          new PhUser(login, Some(password), map)
        }
        case _ => throw new DeserializationException("User expected")
      }
    }
  }
}