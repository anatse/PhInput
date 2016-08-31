package org.asem.orient.model

import java.util.Collections

/**
  * Created by gosha-user on 28.08.2016.
  */
package object entities {
  import java.util.Date

  import com.orientechnologies.orient.core.db.record.OTrackedList
  import com.orientechnologies.orient.core.id.ORecordId
  import com.orientechnologies.orient.core.record.impl.ODocument
  import com.tinkerpop.blueprints.Direction._
  import com.tinkerpop.blueprints.Vertex
  import com.tinkerpop.blueprints.impls.orient.{OrientGraph, OrientVertex}
  import org.asem.orient.Query
  import org.joda.time.DateTime

  import scala.collection.JavaConversions._
  import scala.language.implicitConversions

  object DateTimeHelpers {
    implicit def date2datetime(date: Date): DateTime = new DateTime(date)
    implicit def datetime2date(datetime: DateTime): Date = datetime.toDate
  }

  import DateTimeHelpers._

  object VertexHelpers {
    implicit class VertexHelper[B <: Vertex](val vtx:B) {
      val id:String = vtx.getId.toString.replace("#", "")
      def prop[B](name: String) = vtx.getProperty[B](name)
      def field[T](name: String)(implicit m:Manifest[T]):T = vtx.asInstanceOf[OrientVertex].getRecord[ODocument].field(name, m.runtimeClass)
      def asDoc = vtx.asInstanceOf[OrientVertex].getRecord[ODocument]
      def out(label: String):Vertex = {
        val vtxs = vtx.getVertices(OUT, label)
        if (vtxs.iterator().hasNext)
          vtxs.iterator().next()
        else
          null
      }

      def in(label: String):Vertex = {
        val vtxs = vtx.getVertices(IN, label)
        if (vtxs.iterator().hasNext)
          vtxs.iterator().next()
        else
          null
      }

      def out(label: String*):Iterable[Vertex] = vtx.getVertices(OUT, label:_*)
      def in(label: String*):Iterable[Vertex] = vtx.getVertices(IN, label:_*)
    }
  }

  import VertexHelpers._

  trait BaseEntity

  case class Comment(id:String = null, owner: String, comment: String, createDate: DateTime) extends BaseEntity
  object Comment extends JsonMapper[Comment] {
    def unapply(comment: String): Option[Comment] = Some(read(comment))
    def unapply[B <: Vertex](vtx: B): Option[Comment] = if (vtx != null) Some(Comment(id = vtx.id, owner = vtx.prop("owner"), comment = vtx.prop("comment"), createDate = vtx.prop[Date]("createDate"))) else None
  }

  case class Task(id: String, name: String, content: String, comments: Seq[Comment], status: String, assignedPerson: String, changeDate: DateTime, deadLine: DateTime, owner: String) extends BaseEntity
  object Task extends JsonMapper[Task] {
    def unapply(comment: String): Option[Task] = Some(read(comment))
    def unapply[B <: Vertex](vtx: B): Option[Task] = if (vtx != null) Some(
      Task(
        id = vtx.id,
        name = vtx.prop("name"),
        content = vtx.prop("content"),
        comments = {for {com:Vertex <- vtx.out()} yield {val Comment(c) = com; c}}.toList,
        status = vtx.prop("status"),
        assignedPerson = vtx.prop("assignedPerson"),
        changeDate = vtx.prop[Date]("changeDate"),
        deadLine = vtx.prop[Date]("deadLine"),
        owner = vtx.prop("owner")
      ))
      else None
  }

  case class PrjUser(id: String = null, login: String, firstName: String = "", secondName: String = "", email: String = "") extends BaseEntity
  object PrjUser extends JsonMapper[PrjUser] {
    def unapply(user: PhUser): Option[PrjUser] = Some(PrjUser(user.id, user.login, user.firstName, user.secondName, user.email))
    def unapply(user: String): Option[PrjUser] = Some(read(user))
    def unapply[B <: Vertex](vtx: B): Option[PrjUser] = if (vtx != null) Some(PrjUser(id = vtx.id, login = vtx.prop("login"))) else None
  }

  case class Project(id: String = null, name: String, startDate: DateTime) extends BaseEntity
  object Project extends JsonMapper[Project] {
    def unapply(project: String): Option[Project] = Some(read(project))
    def unapply[B <: Vertex](vtx: B): Option[Project] = if (vtx != null) Some(
      Project(id = vtx.id, name = vtx.prop("name"), startDate = vtx.prop[Date]("startDate"))
    ) else None
  }

  case class VisitReq(typeName: String, num: BigInt)
  case class PrjCycle(id: String = null, name: String, startDate: DateTime, endDate: DateTime, visits: Iterable[VisitReq]) extends BaseEntity
  object PrjCycle extends JsonMapper[PrjCycle] {
    def unapply(prjCycle: String): Option[PrjCycle] = Some(read(prjCycle))
    def unapply[B <: Vertex](vtx: B): Option[PrjCycle] = if (vtx == null) None else {
      val visits:OTrackedList[ODocument] = vtx.field("visits")
      Some(
        PrjCycle(
          id = vtx.id, name = vtx.prop("name"), startDate = vtx.prop[Date]("startDate"), endDate = vtx.prop[Date]("endDate"),
          for {visitRec <- visits} yield {
            VisitReq(visitRec.field("type", classOf[String]), visitRec.field[Int]("num", classOf[Integer]))
          }
        )
      )
    }
  }

  case class PharmNet(id: String = null, name: String, contract: String) extends BaseEntity
  object PharmNet extends JsonMapper[PharmNet] {
    def unapply[B <: Vertex](vtx: B): Option[PharmNet] = if (vtx != null) Some (PharmNet (id = vtx.id, name = vtx.prop("name"), contract = vtx.prop("contract"))) else None
  }

  case class Pharmacy(id: String = null, name: String, chiefPhone: String, chiefName: String, tradeRoomPhone: String,
                      pharmNet: PharmNet, cityCode: String, cityName: String, streetCode: String, streetName: String, buildingCode: String,
                      buildingName: String, contractExt: String = "") extends BaseEntity
  object Pharmacy extends JsonMapper[Pharmacy] {
    def unapply[B <: Vertex](vtx: B): Option[Pharmacy] = if (vtx != null)
      Some (
        Pharmacy (
          id = vtx.id,
          name = vtx.prop("name"),
          chiefPhone = vtx.prop("chiefPhone"),
          chiefName = vtx.prop("chiefName"),
          tradeRoomPhone = vtx.prop("tradeRoomPhone"),
          pharmNet = PharmNet.unapply(vtx.out("PhNet")).getOrElse(null),
          cityCode = vtx.prop("cityCode"),
          cityName = vtx.prop("cityName"),
          streetCode = vtx.prop("streetCode"),
          streetName = vtx.prop("streetName"),
          buildingCode = vtx.prop("buildingCode"),
          buildingName = vtx.prop("buildingName"),
          contractExt = vtx.prop("contractExt")
        )
      )
      else None
  }

  case class Drug(name: String, existence:Boolean, price: BigDecimal)
  case class Report(id: String = null, createDate:DateTime, cycle: PrjCycle, owner: PrjUser, pharmacy: Pharmacy, drugs: Iterable[Drug], checked: Boolean = false, checker: PrjUser) extends BaseEntity
  object Report extends JsonMapper[Report] {
    import java.math.{BigDecimal => JavaDecimal}
    def unapply(project: String): Option[Report] = Some(read(project))
    def unapply[B <: Vertex](vtx: B): Option[Report] = {
      val rep = Report(
        id = vtx.id,
        createDate = vtx.prop[Date]("createDate"),
        cycle = PrjCycle.unapply(vtx.out("ReportCycle")).getOrElse(null),
        owner = PrjUser.unapply(vtx.in("ReportWorker")).getOrElse(null),
        pharmacy = Pharmacy.unapply(vtx.out("ReportPharm")).getOrElse(null),
        drugs = null,
        checker = PrjUser.unapply(vtx.out("ReportChecker")).getOrElse(null)
      )

      val drugsList: OTrackedList[ODocument] = vtx.field("drugs")
      if (drugsList != null) {
        val drugs = for {drugRec <- drugsList} yield {
          Drug(
            name = drugRec.field("name", classOf[String]),
            existence = drugRec.field("existence", classOf[Boolean]),
            price = drugRec.field[JavaDecimal]("price", classOf[JavaDecimal])
          )
        }

        Some(rep.copy(drugs = drugs))
      }
      else 
        Some (rep)
    }
  }

  object PrjService {
    import java.util.{Map => JavaMap, List => JavaList}
    val ID_PATTERN = "([0-9]+:[0-9]+)".r

    /**
      * Function used to check format of identifier
      * @param id identifier to check
      * @return identifier if suitable or throw an error
      */
    private def checkId (id:String):String = id match {
      case ID_PATTERN(c) => c
      case _ => throw new IllegalArgumentException ("Wrong id is provided: " + id)
    }
    
    private def findVertexByAttrs(tx: OrientGraph, clazz: String, attrNames:Array[String], attrValues:Array[Object]): Option[Vertex] = {
      val vtxs = tx.getVertices(clazz, attrNames, attrValues)
      if (vtxs.iterator.hasNext) {
        Some(vtxs.iterator.next.asInstanceOf[Vertex])
      }
      else
        None
    }

    /**
      * Fucntin retrieves all the projects for given user
      * @param user user
      * @return list of projects
      */
    def findAllProjects (user:PrjUser):OrientGraph => List[Project] = tx => tx.getVertex("#" + user.id) match {
      case vtx: Vertex => {for (vtxPrj <- vtx.getVertices(OUT, "Worker", "Employee", "Manager", "Owner")) yield {val Project(prj) = vtxPrj; prj}}.toList
      case _ => null
    }

    /**
      * Fucntion retrieves all project cycles for given user and project. Project cycles also filtered by date (current date)
      * @param prj project
      * @param user user
      * @return active project cycles
      */
    def findActivePrjCycles(prj:Project, user:PrjUser):OrientGraph => List[PrjCycle] = {
      val SQL_QUERY = """select *
                        |from PrjCycle
                        |where in() in [:projectId, :userId] and SYSDATE() between startDate and endDate
                        |order by startDate""".stripMargin.replaceAll("\n", " ")
      val params = Map("projectId" -> new ORecordId("#" + prj.id), "userId" -> new ORecordId("#" + user.id))
      tx => {
        val vtxs = Query.executeQuery(tx, SQL_QUERY, params)
        for (vtxPrj <- vtxs) yield { val PrjCycle (prj) = vtxPrj; prj}
      }.toList
    }

    /**
      * Function retrieves proj cycle by projcycle id
      * @param id identifier of project cycle
      * @return project cycle
      */
    def findPrjCycleById (id: String): OrientGraph => PrjCycle = tx => {val PrjCycle(pc) = tx.getVertex("#" + id); pc}

    /**
      * Function retirves all reports for given project cycle
      * @param cycle project cycle
      * @return list of reports for this cycle w/o any filtering
      */
    def findAllReports (cycle:PrjCycle): OrientGraph => List[Report] = tx => {
      val SQL_QUERY = "select expand(in('ReportCycle')) from #" + checkId(cycle.id)
      val vtxs = Query.executeQuery(tx, SQL_QUERY, Collections.EMPTY_MAP)
      println ("vtxs: " + vtxs + ", " + SQL_QUERY)
      for (vtxRep <- vtxs) yield {
        val Report (rep) = vtxRep; rep
      }
    }.toList

    /**
      * Funсtion retrieves reports for given project cycle and user (owner of the report)
      * @param cycle project cycle
      * @param user user
      * @return list of reports
      */
    def findReportsForUser (cycle:PrjCycle, user:PrjUser): OrientGraph => List[Report] = tx => {
      val SQL_QUERY = "select expand(in('ReportCycle')) from :cycleId where in('ReportCycle').in('ReportWorker') in [:userId]"
      val params = Map("cycleId" -> new ORecordId("#" + cycle.id), "userId" -> new ORecordId("#" + user.id))
      val vtxs = Query.executeQuery(tx, SQL_QUERY, params)
      for (vtxRep <- vtxs) yield {
        val Report (rep) = vtxRep; rep
      }
    }.toList

    /**
      * Function add new project
      * @param prj project
      * @return created project
      */
    def addProject(prj:Project, user:PrjUser):OrientGraph => Project = {
      tx => {
        val map:JavaMap[String, Object] = Map("name" -> prj.name, "startDate" -> prj.startDate.toDate)
        val vtx = tx.addVertex("class:Project", map)
        findVertexByAttrs(tx, "PhUser", Array("login"), Array(user.login)) match {
          case Some(uVtx) => uVtx.addEdge ("Manager", vtx)
          case  _ => throw new IllegalArgumentException("owner not found: " + user.login)
        }
        
        val Project(p) = vtx
        p
      }
    }

    /**
      * Fucntion add new cycle to project. Also with required visits
      * @param cycle project cycle
      * @return created project cycle
      */
    def addPrjCycle (cycle: PrjCycle, prjId:String): OrientGraph => PrjCycle = {
      tx => {
        val prjVtx = tx.getVertex ("#" + prjId)
        if (prjVtx == null)
          throw new IllegalArgumentException (s"project {prjId} not found")

        val map:JavaMap[String, Object] = Map("name" -> cycle.name,"startDate" -> cycle.startDate.toDate,"endDate" -> cycle.endDate.toDate)
        val vtx = tx.addVertex("class:PrjCycle", map)
        val doc = vtx.asDoc
        
        val visitList:JavaList[ODocument] = {for {visit <- cycle.visits} yield {new ODocument("VisitReq").field("num", visit.num).field("type", visit.typeName)}}.toList
        doc.field ("visits", visitList)

        // Add edge
        prjVtx.addEdge ("E", vtx)
        val PrjCycle(p) = vtx
        p
      }
    }
    
    def addDrugsToReport (vtx:Vertex, drugs:Iterable[Drug]):Unit = {
      val drugList:JavaList[ODocument] = drugs.map(
        drug => new ODocument("Drug").field("name", drug.name).field("existence", drug.existence).field("price", drug.price)
      ).toList
      vtx.asDoc.field ("drugs", drugList)
    }
    
    protected def findOrAddPharmacy (pharm:Pharmacy)(implicit tx:OrientGraph):Vertex = {
      findVertexByAttrs(tx, "Pharmacy", Array("cityName", "streetName", "buildingName"), Array(pharm.cityName, pharm.streetName, pharm.buildingName)) match {
        case Some(vtx: OrientVertex) => vtx
        case _ => addPharmacy(pharm)
      }
    }
    
    protected def findOrAddPharmNet (pharmNet:PharmNet)(implicit tx:OrientGraph):Vertex = {
      findVertexByAttrs(tx, "PharmNet", Array("name"), Array(pharmNet.name)) match {
        case Some(vtx: OrientVertex) => vtx
        case _ => addPharmNet(pharmNet)
      }
    }

    protected def addPharmNet (pharmNet:PharmNet)(implicit tx:OrientGraph):Vertex = {
      val map:JavaMap[String, Object] = Map("name" -> pharmNet.name, "contract" -> pharmNet.contract)
      tx.addVertex ("class:PharmNet", map)
    }

    def addPharmacy (pharm:Pharmacy)(implicit tx:OrientGraph):Vertex = {
      val map:JavaMap[String, Object] = Map(
        "name" -> pharm.name,
        "chiefPhone" -> pharm.chiefPhone,
        "chiefName" -> pharm.chiefName,
        "tradeRoomPhone" -> pharm.tradeRoomPhone,
        "cityCode" -> pharm.cityCode,
        "cityName" -> pharm.cityName,
        "streetCode" -> pharm.streetCode,
        "streetName" -> pharm.streetName,
        "buildingCode" -> pharm.buildingCode,
        "buildingName" -> pharm.buildingName,
        "contractExt" -> pharm.contractExt
      )

      val phVtx = tx.addVertex ("class:Pharmacy", map)
      // find or add PharmNet
      if (pharm.pharmNet != null) {
        val phNetVtx = findOrAddPharmNet(pharm.pharmNet)
        phVtx.addEdge("PhNet", phNetVtx)
      }

      phVtx
    }
    
    def addReport (rep:Report):OrientGraph => Report = {
      tx => {
        implicit val gtx = tx
        val map:JavaMap[String, Object] = Map("createDate" -> rep.createDate.toDate)

        // There are several document should be create inside the transaction
        // First of all create report vertex
        val repVtx = tx.addVertex ("class:Report", map)
        // Adding drugs to report, if exists
        addDrugsToReport (repVtx, rep.drugs)

        // Find or add pharmacy and pharmnet
        val phVtx = findOrAddPharmacy (rep.pharmacy)
        repVtx.addEdge ("ReportPharm", phVtx)
        
        // Add link to owner
        findVertexByAttrs(tx, "PhUser", Array("login"), Array(rep.owner.login)) match {
          case Some(vtx) => repVtx.addEdge ("ReportWorker", vtx)
          case  _ => throw new IllegalArgumentException("owner not found: " + rep.owner.login)
        }

        // Add link to PrjCycle
        repVtx.addEdge("ReportCycle", tx.getVertex("#" + rep.cycle.id))
        val Report(repRet) = repVtx
        repRet
      }
    }
  }
}
