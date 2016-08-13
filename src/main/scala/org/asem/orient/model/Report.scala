package org.asem.orient.model

import java.util.Date

import com.tinkerpop.blueprints.impls.orient.OrientVertex
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import spray.json.{JsString, _}

/**
  * Class represents report data used to store and retrieve information about whole visit into pharmacy
  * Created by gosha-user on 13.08.2016.
  */

/**
  * Class represents report data used to store and retrieve information about whole visit into pharmacy
  * Created by gosha-user on 13.08.2016.
  * @param login user login
  * @param city address city
  * @param street address street
  * @param building address building
  * @param pharmNet
  * @param pharmacy
  * @param agreements
  * @param managerName
  * @param managerPhone
  * @param tradeRoomPhone
  * @param id internal database identifier
  */
case class Report (
      login:String,
      created: DateTime,
      modified: DateTime,
      city:String,
      street: String,
      building: String,
      pharmNet: String,
      pharmacy: String,
      agreements: String,
      managerName: String,
      managerPhone: String,
      tradeRoomPhone: String,
      id:String = null)

/**
  * Object implements base functions for managing report case class.
  * For date manipulation it using scala wrapper for Joda time
  * Implemented functions:
  * <ul>
  *   <li>Json protocol</li>
  *   <li>from/to OrientVertex conversion</li>
  * </ul>
  */
object Report extends DefaultJsonProtocol {
  val dateFormatter = ISODateTimeFormat.basicDate()

  implicit def date2datetime (date:Date):DateTime = {
    new DateTime(date)
  }

  implicit def datetime2date (datetime: DateTime): Date = {
    datetime.toDate
  }

  def unapply(vtx: OrientVertex): Option[Report] = {
    Some(
      Report(
        login = vtx.getProperty[String]("login"),
        created = vtx.getProperty[Date]("created"),
        modified = vtx.getProperty[Date]("modified"),
        city = vtx.getProperty[String]("city"),
        street = vtx.getProperty[String]("street"),
        building = vtx.getProperty[String]("building"),
        pharmNet = vtx.getProperty[String]("pharmNet"),
        pharmacy = vtx.getProperty[String]("pharmacy"),
        agreements = vtx.getProperty[String]("agreements"),
        managerName = vtx.getProperty[String]("managerName"),
        managerPhone = vtx.getProperty[String]("managerPhone"),
        tradeRoomPhone = vtx.getProperty[String]("tradeRoomPhone"),
        id = vtx.getId.toString.replace("#", "")
      )
    )
  }

  implicit def js2string(x: Option[JsValue]):String = {
    if (x.isDefined) {
      x.get match {
        case JsString(s) => s
        case _ => null
      }
    }
    else
      null
  }

  implicit def js2date(x: Option[JsValue]):DateTime = {
    if (x.isDefined) {
      x.get match {
        case JsString(s) => dateFormatter.parseDateTime(s)
        case _ => null
      }
    }
    else
      null
  }

  def unapply(obj: JsObject): Option[Report] = {
    val fields = obj.fields
    Some (
      Report (
        login = fields.get("login"),
        created = fields.get("created"),
        modified = fields.get("modified"),
        city = fields.get("city"),
        street = fields.get("street"),
        building = fields.get("building"),
        pharmNet = fields.get("pharmNet"),
        pharmacy = fields.get("pharmacy"),
        agreements = fields.get("agreements"),
        managerName = fields.get("managerName"),
        managerPhone = fields.get("managerPhone"),
        tradeRoomPhone = fields.get("tradeRoomPhone"),
        id = fields.get("id")
      )
    )
  }

  /**
    * Function generate Json map from Report fields using scala reflection mechanizm
    * @param rep report objects
    * @return generated json map
    */
  implicit def rep2JsonMap(rep:Report):Map[String, JsValue] = {
    import scala.reflect.runtime.universe._

    val rm = scala.reflect.runtime.currentMirror
    val accessors = rm.classSymbol(rep.getClass).toType.members.collect {
      case m: MethodSymbol if m.isGetter && m.isPublic => m
    }

    val instanceMirror = rm.reflect(rep)
    val seq = for {
      acc <- accessors
      value = instanceMirror.reflectMethod(acc).apply()
      if (value != null)
    } yield {
      acc.name.toString -> (value match {
        case s:String => JsString(s)
        case d:DateTime => JsString(dateFormatter.print(d))
        case i:Int => JsNumber(i)
        case b:Boolean => JsBoolean(b)
      })
    }

    seq.toMap
  }

  implicit object PhUserJsonFormat extends RootJsonFormat[Report] {
    override def write(obj: Report): JsValue = {
      JsObject(fields = obj)
    }

    override def read(json: JsValue): Report = {
      val Report(report) = json.asJsObject
      report
    }
  }
}