package org.asem.orient.services

import org.asem.orient.Database
import org.asem.orient.model.Operation
import org.asem.orient.model.entities._
import spray.http.MediaTypes._
import spray.http.StatusCodes._

/**
  * Created by gosha-user on 03.09.2016.
  */
object ProjectService {
  /**
    * Get user object by login
    * @param login login
    * @return project user object
    */
  def getUserByLogin (login: String) = Database.getTx (PrjService.findPrjUserByLogin(login))

  /**
    * retrieves user projects
    * @param userId user identifier
    * @return projects list
    */
  def findAllProjects (userId:String): List[Project] = Database.getTx (PrjService.findAllProjects(userId))
  def findActivePrjCycles (prjId:String, userId:String) = Database.getTx (PrjService.findActivePrjCycles(prjId, userId))
  def findCycleReports (cycleId:String) = Database.getTx (PrjService.findAllReports(cycleId))
  def findCycleReportsForUser (cycleId:String, userId:String) = Database.getTx (PrjService.findReportsForUser(cycleId, userId))
  def findPharmacyByAddress (cityName:String, streetName:String, buildingName:String) = Database.getTx (PrjService.findPharmacyByAddress(cityName, streetName, buildingName))
  def findPharmNet (cityName:String, streetName:String, buildingName:String) =  Database.getTx (PrjService.findPharmacyByAddress(cityName, streetName, buildingName))
  def addUserToProject (prjId:String, userId:String, isManager:Boolean) = Database.getTx (PrjService.addUserToProject(prjId, userId, isManager))
  def addUserToCycle (cycleId:String, userId:String) = Database.getTx (PrjService.addUserToPrjCycle(cycleId, userId))
}

trait ProjectService extends BaseHttpService with JacksonJsonSupport {
  private val getCurrentProjectUser = get {
    auth {
      user => path("project" / "curuser") {
        respondWithMediaType(`application/json`) (
          ProjectService.getUserByLogin(user.login) match {
            case Right(user) => complete (user)
            case Left(error) => complete (Operation(error))
          }
        )
      }
    }
  }

  private val getUserProjects = get {
    auth {
      user => path("project") {
        respondWithMediaType(`application/json`) (complete (ProjectService.findAllProjects(user.id)))
      }
    }
  }

  private val getActivePrjCycles = get {
    auth {
      user => path("project" / Segment / "cycle") {
        prjId => respondWithMediaType(`application/json`)(complete(ProjectService.findActivePrjCycles(prjId, user.id)))
      }
    }
  }

  private val getCycleReports = get {
    auth {
      user => path("cycle" / Segment / "report") {
        cycleId => respondWithMediaType(`application/json`)(
          user.manager match {
            case true => complete (ProjectService.findCycleReports (cycleId) )
            case false => complete (ProjectService.findCycleReportsForUser (cycleId, user.id) )
          }
        )
      }
    }
  }

  private val getPharmacies = get {
    auth {
      user => path("pharmacy") {
        parameters('city, 'street, 'building) {
          (city, street, building) => ProjectService.findPharmacyByAddress (city, street, building) match {
            case Right(ph) => respondWithMediaType(`application/json`) (complete (ph))
            case Left(error) => respondWithStatus(BadRequest) (complete (Operation(error)))
          }
        }
      }
    }
  }

  private val addUserToProject = put {
    auth {
      user => path("project" / Segment / "user") {
        prjId => parameters ('userId, 'isManager.as[Boolean]) {
          (userId, isManager) => respondWithMediaType(`application/json`)(
            ProjectService.addUserToProject(prjId, userId, isManager) match {
              case true => complete (Operation("OK", true))
              case false => complete (Operation("Cannot perform operation"))
            }
          )
        }
      }
    }
  }

  private val addUserToCycle = put {
    auth {
      user => path("cycle" / Segment / "user") {
        cycleId => parameters ('userId) {
          (userId) => respondWithMediaType(`application/json`)(
            ProjectService.addUserToCycle(cycleId, userId) match {
              case true => complete (Operation("OK", true))
              case false => complete (Operation("Cannot perform operation"))
            }
          )
        }
      }
    }
  }

  val projectRouter = getCurrentProjectUser ~
    getUserProjects ~
    getActivePrjCycles ~
    getCycleReports ~
    getPharmacies ~
    addUserToProject ~
    addUserToCycle
}
