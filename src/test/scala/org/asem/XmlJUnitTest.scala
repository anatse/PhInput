/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.asem

import org.junit._
import Assert._
import org.asem.orient._

class XmlJUnitTest {

  @Before
  def setUp: Unit = {
  }

  @After
  def tearDown: Unit = {
  }

  @Test
  def example:Unit = {
    val data = Array(
      Map("name" -> "test1", "value" -> "test_value1"),
      Map("name" -> "test1", "value" -> "test_value1")
    )

    val xml = <data>
      {
        for {
          row <- data
        } yield {
          <row>
            {
              for { prop <- row.keys } yield {
                <attr name={prop} value={row.get(prop).getOrElse("")}/>
              }
            }
          </row>
        }
      }
    </data>

    val printer = new scala.xml.PrettyPrinter(80, 2)
    println (printer.format(xml))
  }
  
  @Test
  def testDb ():Unit = {
    val xml = Database.queryToXml("SELECT * FROM User", Map())
    val printer = new scala.xml.PrettyPrinter(80, 2)
    println (printer.format(xml))
  }
}
