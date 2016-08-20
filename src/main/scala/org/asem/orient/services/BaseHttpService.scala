package org.asem.orient.services

import org.asem.orient.model.CookieAuthenticator._
import org.asem.orient.model.UserData
import spray.http._
import spray.routing._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by gosha-user on 20.08.2016.
  */
trait BaseHttpService extends HttpService {
  def auth(f: UserData => Route): Route = {
    authenticate(byCookie) {
      user => {
        (setCookie(HttpCookie(name = "user_token", content = user.token, maxAge = Some(6000))) compose f).apply(user)
      }
    }
  }
}
