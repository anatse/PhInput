package ru.sbt.security

import com.softwaremill.session.SessionSerializer
import com.softwaremill.session.MultiValueSessionSerializer
import ru.sbt.orient.Query
import ru.sbt.orient.model.SbtUser

import scala.collection.JavaConversions._
import scala.concurrent.duration._
import scala.concurrent._
import akka.event.LoggingAdapter
import akka.http.impl.util._
import akka.http.scaladsl.server.directives.CookieDirectives._
import akka.http.scaladsl.common._
import akka.http.scaladsl.server._
import akka.http.scaladsl.util.FastFuture._
import akka.http.scaladsl.unmarshalling._
import akka.http.scaladsl.server.AuthenticationFailedRejection
import akka.http.scaladsl.server.AuthenticationFailedRejection._
import scala.util.Try

case class UserData (login:String, manager:Boolean, id:String)

object UserData {
  implicit def serializer: SessionSerializer[UserData, String] = new MultiValueSessionSerializer(
    toMap = {t: UserData => Map(
        "userName" -> t.login, 
        "id" -> t.id,
        "manager" -> t.manager.toString
      )
    },
    fromMap = {m: Map[String, String] => Try(UserData(m("userName"), m("manager").toBoolean,  m("id")))}
  )
}

/**
  * Created by gosha-user on 30.07.2016.
  */
trait OrientDBAuthenticator {
  private def loadUser(login: String, pwdHash: String): Option[SbtUser] = {
    val params = Map(
      "login" -> login,
      "password" -> pwdHash
    )

    val result = Query.executeQuery("SELECT * FROM PhUser WHERE login = :login and password = :password", params)
    if (result.size() == 1) {
      val vtx = result.get(0)
      val SbtUser(user) = result.get(0)
      Some(user)
    }
    else
      None
  }

  /**
    * Function get information about current user from special cookie (user_token)
    * If that cookie not found then get information from query parameters (uname, upass)
    *
    * @param ctx RequestContext object
    * @return extracted credentials if possible
   */
  def loadUserData(userName:String, userPwd:String)(implicit logger: LoggingAdapter): Option[UserData] =  {
    val user = SbtUser (userName, userPwd)
    val pu = loadUser(user.login, user.pwdHash)
    pu match {
      case Some(usr) => 
        if (logger.isDebugEnabled)
          logger.debug ("user found: " + usr.login + ", " + usr.toString)
        Some(UserData(usr.login, usr.manager.getOrElse(false), usr.id))

      case _ => None
    }
  }
}
