package org.asem.orient.services

import com.orientechnologies.orient.core.storage.ORecordDuplicatedException
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import org.asem.orient.model.CookieAuthenticator._
import org.asem.orient.model.Report
import org.asem.orient.{Database, Query}
import spray.http._
import MediaTypes._
import spray.httpx.SprayJsonSupport._
import spray.routing.HttpService

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global

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
  def createReport(report: Report): Either[OrientVertex, String] = {
    try {
      val vtx = Database.getTx(addVertex("Report", rep2Map(report).filterKeys(_ != "id")))
      Left(vtx)
    }
    catch {
      case e:ORecordDuplicatedException => Right("duplicate_index")
      case e:Exception => Right(e.toString)
    }
  }

  def changeReport(reportId:String, report: Report): Either[OrientVertex, String] = {
    Database.getTx(
      graph => {
        try {
          graph.getVertex("#" + reportId) match {
            case vtx: OrientVertex => {
              vertexUpdate (vtx, rep2Map(report).filterKeys(_ != "id"))
              vtx.save
              graph.commit()
              Left(vtx)
            }
            case _ => Right("Error updating record: " + "#" + reportId)
          }
        }
        catch {
          case e:Exception => Right(e.toString)
        }
      }
    )
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
    deleteById(rep.id)
  }

  def deleteById (id:String):Boolean = {
    Database.getTx(tx => {
      tx.getVertex("#" + id) match {
        case vtx:OrientVertex => {
          deleteVertex(tx, vtx)
        }
        case _ => false
      }
    })
  }
}

/**
  * Trait represents REST functions to work with reports
  */
trait ReportService extends HttpService {

  private val listReportsRouter = get {
    authenticate(byCookie) {
      user => setCookie(HttpCookie(name = "user_token", content = user.token, maxAge = Some(600))) {
        path("report") {
          complete {
            ReportService.findAllUserReports(user.login).toArray[Report]
          }
        }
      }
    }
  }

  private val addReportsRouter = post {
    authenticate(byCookie) {
      user => setCookie(HttpCookie(name = "user_token", content = user.token, maxAge = Some(600))) {
        path("report") {
          entity(as[Report]) {
            report => {
              ReportService.createReport(report) match {
                case Left(vtx) => {
                  val Report(rep) = vtx
                  respondWithStatus(StatusCodes.Created) { complete (rep)}
                }
                case Right(s) => respondWithStatus(StatusCodes.Conflict) { complete (s) }
              }
            }
          }
        }
      }
    }
  }

  private val deleteReportRouter = delete {
    authenticate(byCookie) {
      user => setCookie(HttpCookie(name = "user_token", content = user.token, maxAge = Some(600))) {
        path("report" / Segment) {
          reportId => {
            complete {
              ReportService.deleteById(reportId)
              "OK"
            }
          }
        }
      }
    }
  }

  private val getReportRouter = get {
    authenticate(byCookie) {
      user => setCookie(HttpCookie(name = "user_token", content = user.token, maxAge = Some(600))) {
        path("report" / Segment) {
          reportId => {
            respondWithMediaType(`application/json`)
            complete {
              "OK" + reportId
            }
          }
        }
      }
    }
  }

  private val updateReportRouter = put {
    authenticate(byCookie) {
      user => setCookie(HttpCookie(name = "user_token", content = user.token, maxAge = Some(600))) {
        path("report" / Segment) {
          reportId => {
            entity(as[Report]) {
              report => {
                ReportService.changeReport(reportId, report) match {
                  case Left(vtx) => {
                    val Report(rep) = vtx
                    respondWithStatus(StatusCodes.OK   ) { complete (rep)}
                  }
                  case Right(s) => respondWithStatus(StatusCodes.Conflict) { complete (s) }
                }
              }
            }
          }
        }
      }
    }
  }

  lazy val reportRoute = listReportsRouter ~ addReportsRouter ~ deleteReportRouter ~ getReportRouter ~ updateReportRouter
}
