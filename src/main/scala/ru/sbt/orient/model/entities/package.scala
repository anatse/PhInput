package ru.sbt.orient.model

/**
  * Created by gosha-user on 28.08.2016.
  */
package object entities {
  import java.util.Date

  import com.tinkerpop.blueprints.Direction._
  import com.tinkerpop.blueprints.Vertex
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
      def out(label: String):Vertex = {
        val vtxs = vtx.getVertices(OUT, label)
        if (vtxs.iterator().hasNext)
          vtxs.iterator().next()
        else
          null
      }
      
      def outF(label: String, field:String) = {
        val vtxs = vtx.getVertices(OUT, label)
        if (vtxs.iterator().hasNext)
          vtxs.head.prop[String](field)
        else
          ""
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
  object Comment {
    def unapply[B <: Vertex](vtx: B): Option[Comment] = if (vtx != null) Some(Comment(id = vtx.id, owner = vtx.prop("owner"), comment = vtx.prop("comment"), createDate = vtx.prop[Date]("createDate"))) else None
  }

  case class Task(id: String, name: String, content: String, comments: Seq[Comment], status: String, assignedPerson: String, changeDate: DateTime, deadLine: DateTime, owner: String) extends BaseEntity
  object Task {
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

  case class RequestStatus (success: Boolean, msg:String = null, entity:Any = null) extends BaseEntity
}
