package org.asem.orient.model

import com.typesafe.config.ConfigFactory
import org.asem.orient.Query
import org.asem.spray.security.RSA
import spray.routing.AuthenticationFailedRejection.CredentialsMissing
import spray.routing.{AuthenticationFailedRejection, RequestContext}
import spray.routing.authentication._

import scala.concurrent.{Await, Future}
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

case class UserData (login:String, token:String)

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

    println ("user to find: " + params)

    // .toLowerCase()
    val result = Query.executeQuery("SELECT * FROM PhUser WHERE login = :login and password = :password", params)
    println ("result: " + result.size())
    if (result.size() == 1) {
      val row = result.get(0)
      println ("user found")

      Some(PhUser(row.getProperty("login"), "",
        row.getProperty("email"),
        row.getProperty("firstName"),
        row.getProperty("secondName")))
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

    if (!userToken.isDefined) {
//      ctx.request.headers.toMap
      val queryParams = ctx.request.uri.query.toMap

      val user: Option[PhUser] = for {
        id <- queryParams.get("uname")
        secret <- queryParams.get("upass")
      } yield PhUser(id, secret)

      if (user.isDefined) {
        val pu = loadUser(user.get.login, user.get.pwdHash)
        if (pu.isDefined) {
          ret = Some(UserData(pu.get.login, RSA.encrypt(pu.get.toString)))
        }
      }
    }
    else {
      val userData = Await.result(RSA.decrypt(userToken.get.content), 10.minutes)
      val pu = PhUser.unapply(userData)
      if (pu.isDefined) {
        ret = Some(UserData(pu.get.login, userToken.get.content))
      }
    }

    ret
  }

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
