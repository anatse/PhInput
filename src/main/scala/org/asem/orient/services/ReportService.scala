package org.asem.orient.services

import com.orientechnologies.orient.core.storage.ORecordDuplicatedException
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import org.asem.orient.model.Report
import org.asem.orient.{Database, Query}
import spray.http.MediaTypes._
import spray.http._
import spray.httpx.SprayJsonSupport._

import scala.collection.JavaConversions._

/**
  * Created by gosha-user on 13.08.2016.
  */
object ReportService extends BaseDB {
  import Report._

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
  def createReport(login: String, report: Report): Either[Report, String] = {
    try {
      val rep = Database.getTx {
        graph => {
          val Report(vtx) = addVertex("Report", entity2Map(report).filterKeys(_ != "id") + ("login" -> login)).apply(graph)
          vtx
        }
      }

      Left(rep)
    }
    catch {
      case e: ORecordDuplicatedException => Right("duplicate_index")
      case e: Exception => Right(e.toString)
    }
  }

  def changeReport(reportId: String, report: Report): Either[Report, String] = {
    Database.getTx(
      graph => {
        try {
          graph.getVertex("#" + reportId) match {
            case vtx: OrientVertex =>
              vertexUpdate(vtx, entity2Map(report).filterKeys(_ != "id"))
              vtx.save
              graph.commit()
              val Report(rep) = vtx
              Left(rep)
            case _ => Right("Error updating record: " + "#" + reportId)
          }
        }
        catch {
          case e: Exception => Right(e.toString)
        }
      }
    )
  }

  /**
    * Function removes report record from database which can be found by city, street and building attributes
    *
    * @param rep rfeport object to delete
    * @return success flag
    */
  def deleteById(rep: Report): Boolean = {
    deleteById(rep.id)
  }

  def deleteById(id: String): Boolean = {
    Database.getTx(tx => {
      tx.getVertex("#" + id) match {
        case vtx: OrientVertex =>
          deleteVertex(tx, vtx)
        case _ => false
      }
    })
  }
}

/**
  * Trait represents REST functions to work with reports
  */
trait ReportService extends BaseHttpService {
  private val listReportsRouter = get {
    auth {
      user => path("report") {
        complete (ReportService.findAllUserReports(user.login).toArray[Report])
      }
    }
  }

  private val addReportsRouter = post {
    auth {
      user => path("report") {
        entity(as[Report]) {
          report => ReportService.createReport(user.login, report) match {
            case Left(rep) => respondWithStatus(StatusCodes.Created)(complete(rep))
            case Right(s) => respondWithStatus(StatusCodes.Conflict) {complete(s)}
          }
        }
      }
    }
  }

  private val deleteReportRouter = delete {
    auth {
      user => path("report" / Segment) {
        reportId => complete {
          ReportService.deleteById(reportId)
          "OK"
        }
      }
    }
  }

  private val getReportRouter = get {
    auth {
      user => path("report" / Segment) {
        reportId => respondWithMediaType(`application/json`) (complete ("{success: true, reportId: " + reportId + "}"))
      }
    }
  }

  private val updateReportRouter = put {
    auth {
      user => path("report" / Segment) {
        reportId => entity(as[Report]) {
          report => ReportService.changeReport(reportId, report) match {
            case Left(rep) => respondWithStatus(StatusCodes.OK) { complete(rep) }
            case Right(s) => respondWithStatus(StatusCodes.Conflict) { complete(s) }
          }
        }
      }
    }
  }

  lazy val reportRoute = listReportsRouter ~ addReportsRouter ~ deleteReportRouter ~ getReportRouter ~ updateReportRouter
}
