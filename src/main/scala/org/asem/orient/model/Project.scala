package org.asem.orient.model

import com.tinkerpop.blueprints.impls.orient.OrientVertex
import org.joda.time.DateTime
import spray.json.{DefaultJsonProtocol, JsObject, JsValue, RootJsonFormat}

/**
  * Created by gosha-user on 07.08.2016.
  */
case class Project (name:String, startDate:DateTime)
object Project extends DefaultJsonProtocol with BaseModelFuncs {
  def unapply(vtx: OrientVertex): Option[Project] = Some(createFromVertex[Project](vtx)(0))
  def unapply(obj: JsObject): Option[Project] = Some (createFromJson[Project](obj)(0))

  implicit object ProjectJsonFormat extends RootJsonFormat[Project] {
    override def write(obj: Project): JsValue = JsObject(fields = obj)
    override def read(json: JsValue): Project = createFromJson[Project](json.asJsObject)(0)
  }
}

case class VisitReq (typeName:String, num:BigInt)
case class PrjCycle (name:String, startDate:DateTime, endDate:DateTime, visits: List[VisitReq])
case class Address (city: String, streen: String, building: String)
case class PharmNet (name: String)
case class Pharmacy ()
