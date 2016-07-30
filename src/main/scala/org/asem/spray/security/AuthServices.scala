package org.asem.spray.security


import spray.http.MediaTypes._
import spray.httpx.SprayJsonSupport._
import spray.routing.{HttpService, Route}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by gosha-user on 23.07.2016.
  */
trait AuthServices extends HttpService with Authenticator {
  val authRoutes: Route = authenticate(basicUserAuthenticator) {
    userId => path("users" / Segment) { login =>
      get {
        respondWithStatus(403) {
          complete {
            "access denied"
          }
        }
      } ~ post {
        entity(as[PhUserOld]) {
          user => {
            respondWithMediaType(`application/json`) {
              complete {
                user
              }
            }
          }
        }
      }
    }
  }
}
