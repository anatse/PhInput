package org.asem.orient.model

import com.tinkerpop.blueprints.impls.orient.OrientVertex
import org.joda.time.DateTime
import spray.json._

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
object Report extends DefaultJsonProtocol with BaseModelFuncs {
  def unapply(vtx: OrientVertex): Option[Report] = {
    Some(createFromVertex[Report](vtx)(0))
  }

  def unapply(obj: JsObject): Option[Report] = {
    Some (createFromJson[Report](obj)(0))
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