package org.asem.orient.services

import com.tinkerpop.blueprints.impls.orient.{OrientEdge, OrientGraph, OrientVertex}
import org.joda.time.DateTime

/**
 * Created by gosha-user on 30.07.2016.
 * @see http://orientdb.com/docs/last/Roadmap.html
 */
trait BaseDB {
  def addVertex(clazz: String, params: Map[String, Any]): OrientGraph => OrientVertex = {
    tx => {
      var vtx = tx.addVertex("class:" + clazz, java.util.Collections.EMPTY_MAP)
      vertexUpdate(vtx, params)
      vtx.save()
      tx.commit()
      vtx
    }
  }

  def vertexUpdate (vtx:OrientVertex, params: Map[String, Any]): Unit = {
    params.foreach((e: (String, Any)) => {
      e._2 match {
        case d:DateTime => vtx.setProperty(e._1, d.toDate)
        case Some(b) => b
        case null =>
        case None =>
        case _ => vtx.setProperty(e._1, e._2)
      }
    })
  }

  def findVertexByAttr(tx: OrientGraph, clazz: String, attrName: String, attr: Object): Option[OrientVertex] = {
    val vtxs = tx.getVertices(clazz, Array(attrName), Array(attr))
    if (vtxs.iterator.hasNext) {
      Some(vtxs.iterator.next.asInstanceOf[OrientVertex])
    }
    else
      None
  }

  /**
    * Sample usage
    *   findVertexByAttrs(tx, "Report", Array("city", "street", "building"), Array(rep.city, rep.street, rep.building)) match {
    *    case Some(vtx) => deleteVertex(tx, vtx)
    *     case _ => false
    *   }
    *
    * @param tx
    * @param clazz
    * @param attrNames
    * @param attrValues
    * @return
    */
  def findVertexByAttrs(tx: OrientGraph, clazz: String, attrNames:Array[String], attrValues:Array[Object]): Option[OrientVertex] = {
    val vtxs = tx.getVertices(clazz, attrNames, attrValues)
    if (vtxs.iterator.hasNext) {
      Some(vtxs.iterator.next.asInstanceOf[OrientVertex])
    }
    else
      None
  }

  def deleteVertex(tx: OrientGraph, vtx: OrientVertex): Boolean = {
    try {
      tx.removeVertex(vtx)
      true
    } catch {
      case e: Exception => {
        false
      }
    }
  }
  
  def ~>(vtxTo:OrientVertex): OrientVertex => String => OrientEdge = {
    vtx => {
      label => {
        vtx.addEdge(label, vtxTo).asInstanceOf[OrientEdge]
      }
    }
  }
}
