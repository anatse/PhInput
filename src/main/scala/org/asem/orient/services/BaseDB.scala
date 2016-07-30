package org.asem.orient.services

import com.tinkerpop.blueprints.impls.orient.{OrientGraph, OrientVertex}

/**
  * Created by gosha-user on 30.07.2016.
  */
trait BaseDB {
  def add (clazz:String, func:OrientVertex => BaseDB):OrientGraph => OrientVertex = {
    tx => {
      val vtx = tx.addVertex("class:" + clazz, java.util.Collections.EMPTY_MAP)
      func(vtx)
      vtx.save()
      vtx
    }
  }

  def ++ (propName:String, propValue:Any):OrientVertex => BaseDB = {
    vtx => {
      vtx.setProperty(propName, propValue)
      this
    }
  }
}
