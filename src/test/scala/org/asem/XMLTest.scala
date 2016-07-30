package org.asem

import org.asem.orient._
import org.asem.orient.model.PhUser
import org.asem.spray.security.RSA
import org.scalatest._

import scala.concurrent.Await
import scala.xml._
import scala.concurrent.duration._

class XMLTest extends FlatSpec with Matchers {

  "XML" should "be properly generated from array of map" in {
    val data = Array(
      Map("name" -> "test1", "value" -> "test_value1"),
      Map("name" -> "test1", "value" -> "test_value1")
    )

    val xml = <data>
      {for {
        row <- data
      } yield {
        <row>
          {for {prop <- row.keys} yield {
            <attr name={prop} value={row.get(prop).getOrElse("")}/>
        }}
        </row>
      }}
    </data>

    val printer = new PrettyPrinter(80, 2)
    println(printer.format(xml))
  }

  it should "read from database and store to XML" in {
    val xml = Database.queryToXml("SELECT * FROM PhUser", Map())
    val printer = new scala.xml.PrettyPrinter(80, 2)
    println(printer.format(xml))
  }

  "RSA" should "load keystore from file" in {
    RSA.isInitialized should be (true)
  }

  var encryptedData = ""
  it should "encrypt data using public key" in {
    encryptedData = RSA.encrypt("test data")
    encryptedData shouldNot be ("")
    println (encryptedData)
  }

  it should "decrypt data using private key" in {
    val decryptedData = Await.result (RSA.decrypt(encryptedData), 1 minutes)
    decryptedData should be ("test data")
    println (decryptedData)
  }

  val user = PhUser("demo", "demo", "demo@demo.org", "firstname", "secondName")
  "PhUser" should "be printed to string" in {
    user.toString should be ("demo,demo@demo.org,firstname,secondName")
  }

  it should "be unapplied from string" in {
    val restoredUser = PhUser.unapply(user.toString).getOrElse(null)
    restoredUser.toString should be (user.toString)
    restoredUser.pwdHash should be ("")
  }
}