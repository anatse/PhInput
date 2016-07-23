package org.asem.orient

import com.tinkerpop.blueprints.impls.orient.{OrientGraph, OrientGraphFactory}
import com.typesafe.config.ConfigFactory
import org.asem.spray.security.PhUser
import spray.caching.{Cache, LruCache}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by gosha-user on 17.07.2016.
  */
object Database {
  val config = ConfigFactory.load()

  lazy val pool = {
    new OrientGraphFactory (config.getString("database.url"), config.getString("database.user"), config.getString("database.password")).setupPool(1, 10)
  }

  val userCache:Cache[PhUser] = LruCache()

  def getTx (performs: OrientGraph => Any):Any = {
    val g = pool.getTx
    try {
      performs (g)
    }
    finally {
      g.shutdown()
    }
  }

  def findUser(usr: PhUser): Future[PhUser] = userCache(usr) {
    if (usr.checkUserExists())
      usr
    else
      null
  }
}

