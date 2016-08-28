/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.asem.orient.model.entities

trait JsonMapper[B] {
  import com.fasterxml.jackson.databind._
  import com.fasterxml.jackson.datatype.joda._
  import com.fasterxml.jackson.module.scala._
  import com.fasterxml.jackson.module.scala.experimental._

  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.registerModule(new JodaModule());
  
  def read(src: String)(implicit m: Manifest[B]):B = {
    mapper.readValue[B](src)
  }

  def write(rep:B):String = {
    mapper.writeValueAsString(rep)
  }
}

object JsonMapper {
  import com.fasterxml.jackson.databind._
  import com.fasterxml.jackson.datatype.joda._
  import com.fasterxml.jackson.module.scala._
  import com.fasterxml.jackson.module.scala.experimental._

  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.registerModule(new JodaModule());

  def read[B](src: String)(implicit m: Manifest[B]):B = {
    mapper.readValue[B](src)
  }

  def write[B](rep:B):String = {
    mapper.writeValueAsString(rep)
  }
}
