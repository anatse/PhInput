package ru.sbt.orient

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
  
//  def pushMessageToClients (msg:Any) = {
//    import org.asem.Boot._
//    service ! msg
//  }
  
  def queryToXml (query: String, params:Map[String, Any]) = {
    getTx (
      tx => {
        val result = Query.executeQuery(tx, query, params)
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
      })
  }

  def queryToXml (tag:String, query: String, params:Map[String, Any]) = {
    val result = Query.executeQuery(query, params)
    val r = <data>
    {
      for {
        row <- result
      } yield {
        <row>
          {
          for { prop <- row.getPropertyKeys } 
            yield {
              <attr name={prop}>{row.getProperty(prop)}</attr>
            }
          }
        </row>
      }
    }
    </data>

    r.copy(label = tag)
  }
  
  def getTx[T] (performs: OrientGraph => T):T = {
    val g = pool.getTx
    try {
      val res = performs (g)
      g.commit
      res
    }
    catch {
      case e:Throwable => g.rollback
        throw e
    }
    finally {
      g.shutdown()
    }
  }
}

