package org.asem.orient.model

import java.util.Date
//import scala.reflect.runtime.{universe => ru}

/**
  * Created by gosha-user on 07.08.2016.
  */
//trait BaseJsonProtocol[T] extends DefaultJsonProtocol {
//  def members[T:ru.TypeTag](v: T)(implicit ev: ru.TypeTag[T]) = {
//     ru.typeOf[T].members
//  }
//} 

case class Project (name:String, startDate:Date)
case class VisitReq (typeName:String, num:Int)
case class PrjCycle (name:String, startDate:Date, endDate:Date, visits: List[VisitReq])
case class Address (city: String, streen: String, building: String);
case class PharmNet (name: String)
case class Pharmacy ()