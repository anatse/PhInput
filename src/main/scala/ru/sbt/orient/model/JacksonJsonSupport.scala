package ru.sbt.orient.model

import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.parboiled2.ParserInput
import akka.util.ByteString
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda._
import com.fasterxml.jackson.module.scala._
import com.fasterxml.jackson.module.scala.experimental._
import akka.http.scaladsl.unmarshalling.{ FromByteStringUnmarshaller, FromEntityUnmarshaller, Unmarshaller, FromRequestUnmarshaller , PredefinedFromEntityUnmarshallers}
import akka.http.scaladsl.util.FastFuture
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.HttpCharsets._
import akka.http.scaladsl.marshalling._
import ru.sbt.orient.model.entities.BaseEntity

/**
  * Created by gosha-user on 27.08.2016.
  */
trait JacksonJsonSupport extends PredefinedFromEntityUnmarshallers {
  import com.fasterxml.jackson.annotation.JsonInclude._
  private val jacksonModules = Seq(DefaultScalaModule)
  protected val mapper = new ObjectMapper() with ScalaObjectMapper

  // Configure object mapper
  mapper.setSerializationInclusion(Include.NON_EMPTY)
  mapper.registerModules(jacksonModules:_*)
  mapper.registerModule(new JodaModule())
  
  implicit def jacksonJsonUnmarshaller[T : Manifest]: FromEntityUnmarshaller[T] =
    Unmarshaller.byteStringUnmarshaller.forContentTypes(`application/json`).mapWithCharset { (data, charset) ⇒
      val input = if (charset == `UTF-8`) ParserInput(data.toArray)
        else ParserInput(data.decodeString(charset.nioCharset))
      mapper.readValue[T](input.sliceString(0, input.length))
  }

  implicit def jacksonJsonValueMarshaller[T <: BaseEntity]: ToEntityMarshaller[T] = {
    Marshaller.StringMarshaller.wrap(`application/json`)(mapper.writeValueAsString)
  }
  
  implicit def jacksonJsonListMarshaller[T <: List[BaseEntity]]: ToEntityMarshaller[T] = {
    Marshaller.StringMarshaller.wrap(`application/json`)(mapper.writeValueAsString)
  }
}
