package org.asem.orient.model.entities

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import spray.http.{ContentTypes, HttpCharsets, HttpEntity, MediaTypes}
import spray.httpx.marshalling.Marshaller
import spray.httpx.unmarshalling._

/**
  * Created by gosha-user on 27.08.2016.
  */
trait JacksonJsonSupport {
  protected val jacksonModules = Seq(DefaultScalaModule)

  protected val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModules(jacksonModules:_*)
  mapper.registerModule(new JodaModule());

  protected implicit def jacksonJsonUnmarshaller[T : Manifest] =
    Unmarshaller[T](MediaTypes.`application/json`) {
      case x: HttpEntity.NonEmpty =>
        val jsonSource = x.asString(defaultCharset = HttpCharsets.`UTF-8`)
        mapper.readValue[T](jsonSource)
    }

  protected implicit def jacksonJsonMarshaller[T <: BaseEntity] = Marshaller.delegate[T, String](ContentTypes.`application/json`)(mapper.writeValueAsString(_))
  protected implicit def jacksonJsonMarshaller1[T <: List[BaseEntity]] = Marshaller.delegate[T, String](ContentTypes.`application/json`)(mapper.writeValueAsString(_))
//  protected implicit def jacksonJsonMarshaller3[T <: Report] = Marshaller.delegate[T, String](ContentTypes.`application/json`)(mapper.writeValueAsString(_))
//  protected implicit def jacksonJsonMarshaller43[T <: List[Report]] = Marshaller.delegate[T, String](ContentTypes.`application/json`)(mapper.writeValueAsString(_))
}
