package org.asem
import collection.mutable.Stack
import org.scalatest._

class XmlTest extends FlatSpec with Matchers {

  "XML" should "be properly generated from array of map" in {
    val data = Array (
      Map ("name" -> "test1", "value" -> "test_value1"),
      Map ("name" -> "test1", "value" -> "test_value1")
    )
    
    val xml = for {
      row <- data
    } yield {
      <row>
        {
          for {prop <- row.keys}
          yield {
            <attribute name="{prop}" value="{row.get(prop)}"/>
          }
        }
      </row>
    }
    
    println (xml)
  }
}