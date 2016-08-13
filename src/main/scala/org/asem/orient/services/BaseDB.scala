package org.asem.orient.services

import com.tinkerpop.blueprints.impls.orient.{OrientEdge, OrientGraph, OrientVertex}
import org.joda.time.DateTime

/**
 * Created by gosha-user on 30.07.2016.
 */
trait BaseDB {
  def addVertex(clazz: String, params: Map[String, Any]): OrientGraph => OrientVertex = {
    tx => {
      var vtx = tx.addVertex("class:" + clazz, java.util.Collections.EMPTY_MAP)
      params.foreach((e: (String, Any)) => {
        if (e._2.isInstanceOf[DateTime])
          vtx.setProperty(e._1, e._2.asInstanceOf[DateTime].toDate)
        else if (e._2 != null)
          vtx.setProperty(e._1, e._2)
      })

      vtx.save()
      tx.commit()
      vtx
    }
  }

  def findVertexByAttr(tx: OrientGraph, clazz: String, attrName: String, attr: Object): Option[OrientVertex] = {
    val vtxs = tx.getVertices(clazz, Array(attrName), Array(attr))
    if (vtxs.iterator.hasNext) {
      Some(vtxs.iterator.next.asInstanceOf[OrientVertex])
    }
    else
      None
  }

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
