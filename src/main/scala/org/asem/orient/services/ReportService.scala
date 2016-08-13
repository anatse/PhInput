package org.asem.orient.services

import com.orientechnologies.orient.core.storage.ORecordDuplicatedException
import org.asem.orient.model.CookieAuthenticator._
import org.asem.orient.model.Report
import org.asem.orient.{Database, Query}
import spray.routing.HttpService
import org.asem.orient.model.CookieAuthenticator._
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.routing.{HttpService, _}
import MediaTypes._
import com.orientechnologies.orient.core.Orient
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import sun.security.provider.certpath.Vertex

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConversions._

/**
  * Created by gosha-user on 13.08.2016.
  */
object ReportService extends BaseDB {
  def findAllUserReports(login: String) = {
    val result = Query.executeQuery("select * from Report where login = :login", Map("login" -> login))
    for {row <- result} yield {
      val Report(report) = row
      report
    }
  }

  /**
    * Function save report to database
    *
    * @param report report object to save
    * @return error message if occurred
    */
  def createReport(report: Report): String = {
    try {
      val vtx = Database.getTx(addVertex("Report", rep2Map(report)))
      ""
    }
    catch {
      case e:ORecordDuplicatedException => "duplicate_index"
      case e:Exception => e.toString
    }
  }

  /**
    * Functon generate map (field -> value) from all object fieds with public accessors
    * @param rep report object to generate map
    * @return generated map object
    */
  def rep2Map(rep:Report) = {
    import scala.reflect.runtime.universe._

    val rm = scala.reflect.runtime.currentMirror
    val accessors = rm.classSymbol(rep.getClass).toType.members.collect {
      case m: MethodSymbol if m.isGetter && m.isPublic => m
    }

    val instanceMirror = rm.reflect(rep)
    (for(acc <- accessors) yield (acc.name.toString -> instanceMirror.reflectMethod(acc).apply())) toMap
  }

  /**
    * Function removes report record from database which can be found by city, street and building attributes
    * @param rep rfeport object to delete
    * @return success flag
    */
  def deleteById (rep:Report):Boolean = {
    Database.getTx(tx => {
      tx.getVertex(rep.id) match {
          case vtx:OrientVertex => {
            deleteVertex(tx, vtx)
          }
          case _ => false
      }

//      findVertexByAttrs(tx, "Report", Array("city", "street", "building"), Array(rep.city, rep.street, rep.building)) match {
//        case Some(vtx) => deleteVertex(tx, vtx)
//        case _ => false
//      }
    })
  }
}

/**
  * Trait represents REST functions to work with reports
  */
trait ReportService extends HttpService {
  private val listReportsRouter = get {
    authenticate(byCookie) {
      user => {
        path("report") {
          complete {
            ReportService.findAllUserReports(user.login).toArray[Report]
          }
        }
      }
    }
  }

  lazy val reportRoute = listReportsRouter
}
