package org.asem.orient.model

import org.asem.orient.model.entities.BaseEntity

/**
  * Created by gosha-user on 03.09.2016.
  */
final case class Operation (message:String, success:Boolean = false) extends BaseEntity
