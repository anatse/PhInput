package org.asem.orient.services

import com.tinkerpop.blueprints.impls.orient.OrientVertex
import com.tinkerpop.blueprints.{Parameter, Vertex}
import org.asem.orient.Database

/**
  * Created by gosha-user on 30.07.2016.
  */
class PhUser extends BaseDB {
  def createUser = {
    Database.getTx(
      graph => {
        add("PhUser", {
          ++ ("asd", "asd")
          ++ ("asd1", "asd")
          ++ ("asd2", "asd")
        })
      }
    )
  }
}
