package org.asem.orient.model

import com.typesafe.config.ConfigFactory
import org.asem.orient.Query
import org.asem.spray.security.RSA
import spray.http.HttpForm
import spray.httpx.unmarshalling._
import spray.routing.AuthenticationFailedRejection.CredentialsMissing
import spray.routing._
import spray.routing.authentication._

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

case class UserData (login:String, token:String, manager:Boolean)

/**
  * Created by gosha-user on 30.07.2016.
  */
class CookieAuthenticator {
  val config = ConfigFactory.load()

  def loadUser(login: String, pwdHash: String): Option[PhUser] = {
    val params = Map(
      "login" -> login,
      "password" -> pwdHash
    )

    // .toLowerCase()
    val result = Query.executeQuery("SELECT * FROM PhUser WHERE login = :login and password = :password", params)
    if (result.size() == 1) {
      val PhUser(user) = result.get(0)
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
  private def extractCredentials(ctx: RequestContext): Option[UserData] = {
    val userToken = ctx.request.cookies.find(_.name == "user_token")
    var ret: Option[UserData] = None

    if (userToken.isEmpty) {
      val formd = ctx.request.as[HttpForm]
      if (formd.isRight) {
        val user:PhUser = formd.right.get.fields match {
          case Seq((name, nameval:String), (pass, passval:String)) => PhUser (nameval, passval)
          case _ => null
        }

        if (user != null) {
          val pu = loadUser(user.login, user.pwdHash)
          if (pu.isDefined) {
            ret = Some(UserData(pu.get.login, RSA.encrypt(pu.get.toString), pu.get.manager.getOrElse(false)))
          }
        }
      }
    }
    else {
      val userData = Await.result(RSA.decrypt(userToken.get.content), 10.minutes)
      val pu = PhUser.fromString (userData)

      if (pu.isDefined) {
        ret = Some(UserData(pu.get.login, userToken.get.content, pu.get.manager.getOrElse(false)))
      }
    }

    ret
  }

  /**
   * Value (function) used to check & retrieve current credentials
   */
  val byCookie: ContextAuthenticator[UserData] = {
    ctx => Future {
      extractCredentials(ctx) match {
        case Some(user) => Right(user)
        case _ => Left(AuthenticationFailedRejection(CredentialsMissing, List()))
      } 
    }
  }
}

object CookieAuthenticator extends CookieAuthenticator
