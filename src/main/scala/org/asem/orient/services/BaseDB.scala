package org.asem.orient.services

import com.tinkerpop.blueprints.impls.orient.{OrientGraph, OrientVertex}

/**
 * Created by gosha-user on 30.07.2016.
 */
trait BaseDB {
  def addVertex(clazz: String, params: Map[String, Any]): OrientGraph => OrientVertex = {
    tx => {
      var vtx = tx.addVertex("class:" + clazz, java.util.Collections.EMPTY_MAP)
      params.foreach((e: (String, Any)) => {
        if (e._2 != null)
          vtx.setProperty(e._1, e._2)
      })

      try {
        vtx.save()
      } 
      catch {
        case ex: Exception => {
            vtx = null
        }
      }

      vtx
    }
  }

  def findVertexByAttr(tx: OrientGraph, clazz: String, attrName: String, attr: Object): Option[OrientVertex] = {
    val vtxs = tx.getVertices(clazz, Array(attrName), Array(attr))
    println("attrName: " + attrName + " = " + attr + ": " + vtxs.iterator())

    if (vtxs.iterator.hasNext)
      Some(vtxs.iterator.next.asInstanceOf[OrientVertex])
    else
      None
  }

  def deleteVertex(tx: OrientGraph, vtx: OrientVertex): Boolean = {
    try {
      tx.removeVertex(vtx)
      true
    } catch {
      case e: Exception => false
    }
  }
}
