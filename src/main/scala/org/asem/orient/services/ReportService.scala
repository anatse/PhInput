package org.asem.orient.services

import org.asem.orient.Database
import org.asem.orient.model.entities.{JacksonJsonSupport, _}
import spray.http._
import MediaTypes._

object ReportService {
  def findReportsForUser (cycleId:String, login:String):List[Report] = {
    if (cycleId.isEmpty) {
      List()
    }
    else
      Database.getTx {
        tx => val user = PhUserService.findUserByLogin (login) match {
          case Some(vtx) => val PrjUser(usr) = vtx; usr
          case _ => throw new IllegalArgumentException(s"User {login} not found")
        }

        val cycle = PrjService.findPrjCycleById (cycleId)(tx)
        PrjService. findReportsForUser(cycleId, login)(tx)
      }
  }

  def addReport (login:String, rep:Report):Report = {
    Database.getTx {
      tx => val user = PhUserService.findUserByLogin (login) match {
        case Some(vtx) => val PrjUser(usr) = vtx; usr
        case _ => throw new IllegalArgumentException(s"User {login} not found")
      }
 
      PrjService.addReport (rep.copy(owner = user))(tx)
    }
  }
}

/**
  * Trait represents REST functions to work with reports
  */
trait ReportService extends BaseHttpService with JacksonJsonSupport {
  private val listReportsRouter = get {
    auth {
      user => path("report") {
        parameters('cycleId) {
          cycleId => respondWithMediaType(`application/json`) (complete (ReportService.findReportsForUser(cycleId, user.login)))
        }
      }
    }
  }

  private val addReportsRouter = post {
    auth {
      user => path("report") {
        entity(as[Report]) {
          report => respondWithMediaType(`application/json`) (complete (ReportService.addReport(user.login, report)))
        }
      }
    }
  }
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

  lazy val reportRoute = {}//listReportsRouter ~ addReportsRouter //~ deleteReportRouter ~ getReportRouter ~ updateReportRouter
}
