package org.asem.orient.model

import java.util.Date

import scala.language.postfixOps
import scala.language.implicitConversions
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import spray.json.{JsBoolean, JsNull, JsNumber, JsObject, JsString, JsValue, RootJsonFormat}

import scala.reflect.ClassTag
import scala.reflect.runtime._
import scala.reflect.runtime.universe._

/**
  * Created by gosha-user on 20.08.2016.
  */
trait BaseModelFuncs {
  val dateFormatter = ISODateTimeFormat.basicDate()

  /**
    * Functon generate map (field -> value) from all object fieds with public accessors
    *
    * @param rep report object to generate map
    * @return generated map object
    */
  def entity2Map[T : ClassTag](rep: T) = {
    import scala.reflect.runtime.universe._

    val rm = scala.reflect.runtime.currentMirror
    val accessors = rm.classSymbol(rep.getClass).toType.members.collect {
      case m: MethodSymbol if m.isGetter && m.isPublic => m
    }

    val instanceMirror = rm.reflect(rep)
    (for (acc <- accessors) yield acc.name.toString -> instanceMirror.reflectMethod(acc).apply()) toMap
  }

  /**
    * Function generate Json map from Report fields using scala reflection mechanizm
    * @param rep report objects
    * @return generated json map
    */
  implicit def entity2JsonMap[T : ClassTag](rep: T):Map[String, JsValue] = {
    import scala.reflect.runtime.universe._

    val rm = scala.reflect.runtime.currentMirror
    val accessors = rm.classSymbol(rep.getClass).toType.members.collect {
      case m: MethodSymbol if m.isGetter && m.isPublic => m
    }

    val instanceMirror = rm.reflect(rep)
    val seq = for {
      acc <- accessors
      value = instanceMirror.reflectMethod(acc).apply()
      if value != null
    } yield {
      acc.name.toString -> (
        value match {
          case s:String => JsString(s)
          case d:DateTime => JsString(dateFormatter.print(d))
          case i:Int => JsNumber(i)
          case b:Boolean => JsBoolean(b)
          case Some(b:Boolean) => JsBoolean(b)
          case None => JsNull
        })
    }

    seq.toMap
  }

  implicit def date2datetime (date:Date):DateTime = {
    new DateTime(date)
  }

  implicit def datetime2date (datetime: DateTime): Date = {
    datetime.toDate
  }

  implicit def js2string(x: Option[JsValue]):String = {
    x match {
      case Some(JsString(s)) => s
      case _ => null
    }
  }

  implicit def js2date(x: Option[JsValue]):DateTime = {
    x match {
      case Some(JsString(s)) => dateFormatter.parseDateTime(s)
      case _ => null
    }
  }

  implicit def js2int(x: Option[JsValue]):BigInt = {
    x match {
      case Some(JsNumber(n)) => n.toBigInt
      case _ => null
    }
  }

  implicit def js2double(x: Option[JsValue]):BigDecimal = {
    x match {
      case Some(JsNumber(n)) => n
      case _ => null
    }
  }

  implicit def js2boolean (x: Option[JsValue]):Boolean = {
    x match {
      case Some(JsBoolean(b)) => b
      case _ => false
    }
  }

  implicit def js2booleanOpt (x: Option[JsValue]):Option[Boolean] = {
    x match {
      case Some(JsBoolean(b)) => Some(b)
      case _ => None
    }
  }

  def createFromVertex[T : TypeTag](vtx: OrientVertex)(ctor: Int = 0): T = {
    val tt = typeTag[T]
    val ctr:MethodMirror = currentMirror.reflectClass(tt.tpe.typeSymbol.asClass).reflectConstructor (
      tt.tpe.members.filter(m => m.isMethod && m.asMethod.isConstructor).iterator.toSeq(ctor).asMethod
    )

    val args = for {
      param <- ctr.symbol.paramLists.head
    }
    yield {
      if (param.name.toString == "id")
        vtx.getId.toString.replace("#", "")
      else if (param.name.toString == "password")
        ""
      else param.typeSignature.toString match {
        case "String" => vtx.getProperty[String](param.name.toString)
        case "org.joda.time.DateTime" => val d: DateTime = vtx.getProperty[Date](param.name.toString); d
        case "BingInt" => vtx.getProperty[Int](param.name.toString)
        case "BigDecimal" => vtx.getProperty[Double](param.name.toString)
        case "Boolean" => vtx.getProperty[Boolean](param.name.toString)
        case "Option[Boolean]" => if (vtx.getProperty[Boolean](param.name.toString) != null) Some(vtx.getProperty[Boolean](param.name.toString)) else None
        case _ => vtx.getProperty[String](param.name.toString)
      }
    }

    ctr(args: _*).asInstanceOf[T]
  }

  def createFromJson[T : TypeTag](obj: JsObject)(ctor: Int = 0): T = {
    val tt = typeTag[T]
    val ctr:MethodMirror = currentMirror.reflectClass(tt.tpe.typeSymbol.asClass).reflectConstructor (
      tt.tpe.members.filter(m => m.isMethod && m.asMethod.isConstructor).iterator.toSeq(ctor).asMethod
    )

    val fields = obj.fields
    val args = for {
      param <- ctr.symbol.paramLists.head
    }
    yield {
      param.typeSignature.toString match {
        case "String" => val s:String = fields.get(param.name.toString); s
        case "org.joda.time.DateTime" => val d:DateTime = fields.get(param.name.toString); d
        case "Int" => val i:BigInt = fields.get(param.name.toString); i
        case "Double" => val db:BigDecimal = fields.get(param.name.toString); db
        case "Boolean" => val b:Boolean = fields.get(param.name.toString); b
        case "Option[Boolean]" => val b:Option[Boolean] = fields.get(param.name.toString); b
        case _ => val s:String = fields.get(param.name.toString); s
      }
    }

    ctr(args: _*).asInstanceOf[T]
  }
}