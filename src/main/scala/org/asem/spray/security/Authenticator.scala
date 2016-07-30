package org.asem.spray.security

import org.asem.orient.Database
import spray.routing.authentication.{BasicAuth, UserPass}
import spray.routing.directives.AuthMagnet

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by gosha-user on 17.07.2016.
  */
trait Authenticator {
  def basicUserAuthenticator(implicit ec: ExecutionContext): AuthMagnet[PhUserOld] = {
    def validateUser(userPass: Option[UserPass]): Option[PhUserOld] = {
      var user:PhUserOld = null
      if (userPass.isDefined) {
        user = Await.result(Database.findUser(PhUserOld(userPass.get.user, Some(userPass.get.pass))), 1 seconds)
        if (user != null)
          println (user.login)
        else
          println ("User not found")
      }

      Option(user)
    }

    def authenticator(userPass: Option[UserPass]): Future[Option[PhUserOld]] = Future {
      validateUser(userPass)
    }

    BasicAuth(authenticator _, realm = "Private API")
  }
}
