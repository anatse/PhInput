package org.asem.orient.services

import org.asem.orient.Database
import org.asem.orient.model.entities.{JacksonJsonSupport, _}
import spray.http._
import MediaTypes._

/**
  * Service object define report service functions
  */
object ReportService extends BaseDB {
  /**
    * Function retrieves all reports for given cycleid and user login. Reports retrivied by several steps
    * <ul>
    *   <li>find cycle vertex by id in orientdb</li>
    *   <li>find user id from orientdb by login</li>
    *   <li>find all reports by calling PrjService.findReportsForUser
    * </ul>
    * @see PrjService
    * @param cycleId cycle identifier
    * @param login user login
    * @return list of erports
    */
    def findReportsForUser(cycleId:String, login:String):List[Report] = {
      Database.getTx( tx => {
        val PrjCycle(cycle) = tx.getVertex("#" + cycleId)
        val PrjUser(user) = findVertexByAttr(tx, "PhUser", "login", login).getOrElse(null)
        PrjService.findReportsForUser(cycle, user).apply(tx)
      })
  }
}

/**
  * Trait represents REST functions to work with reports
  */
trait ReportService extends BaseHttpService with JacksonJsonSupport {
  private val listReportsRouter = get {
    auth {
      user => path("report") {
        parameters('cycle) {
          cycle => respondWithMediaType(`application/json`) (complete (ReportService.findReportsForUser(cycle, user.login)))
        }
      }
    }
  }

//  private val addReportsRouter = post {
//    auth {
//      user => path("report") {
//        entity(as[Report]) {
//          report => ReportService.createReport(user.login, report) match {
//            case Left(rep) => respondWithStatus(StatusCodes.Created)(complete(rep))
//            case Right(s) => respondWithStatus(StatusCodes.Conflict) {complete(s)}
//          }
//        }
//      }
//    }
//  }
//
//  private val deleteReportRouter = delete {
//    auth {
//      user => path("report" / Segment) {
//        reportId => complete {
//          ReportService.deleteById(reportId)
//          "OK"
//        }
//      }
//    }
//  }
//
//  private val getReportRouter = get {
//    auth {
//      user => path("report" / Segment) {
//        reportId => respondWithMediaType(`application/json`) (complete ("{success: true, reportId: " + reportId + "}"))
//      }
//    }
//  }
//
//  private val updateReportRouter = put {
//    auth {
//      user => path("report" / Segment) {
//        reportId => entity(as[Report]) {
//          report => ReportService.changeReport(reportId, report) match {
//            case Left(rep) => respondWithStatus(StatusCodes.OK) { complete(rep) }
//            case Right(s) => respondWithStatus(StatusCodes.Conflict) { complete(s) }
//          }
//        }
//      }
//    }
//  }

  lazy val reportRoute = listReportsRouter //~ addReportsRouter ~ deleteReportRouter ~ getReportRouter ~ updateReportRouter
}
