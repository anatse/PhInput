package org.asem.orient

import com.tinkerpop.blueprints.impls.orient.{OrientGraph, OrientGraphFactory}
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConversions._

/**
  * Created by gosha-user on 17.07.2016.
  */
object Database {
  val config = ConfigFactory.load()

  lazy val pool = {
    new OrientGraphFactory (config.getString("database.url"), config.getString("database.user"), config.getString("database.password")).setupPool(1, 10)
  }

  def queryToXml (query: String, params:Map[String, Any]) = {
    val result = Query.executeQuery(query, params)
    <data>
      {
        for {
          row <- result
        } yield {
          <row>
            {
              for { prop <- row.getPropertyKeys } yield {
                <attr name={prop}>{row.getProperty(prop)}</attr>
              }
            }
          </row>
        }
      }
    </data>
  }
  
  def getTx[T] (performs: OrientGraph => T):T = {
    val g = pool.getTx
    try {
      performs (g)
    }
    finally {
      g.shutdown()
    }
  }
}

