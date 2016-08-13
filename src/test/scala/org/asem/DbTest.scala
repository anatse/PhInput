package org.asem

import com.orientechnologies.orient.core.storage.ORecordDuplicatedException
import com.tinkerpop.blueprints.impls.orient.OrientVertex
import org.asem.orient._
import org.asem.orient.services.PhUserService
import org.scalatest._

import scala.xml._
import scala.concurrent.duration._

class UserServiceTest extends FlatSpec with Matchers {

  "User" should "be stored in database" in {
    PhUserService.createUser(
      login = "demo",
      pasword = "demo",
      email = "demo@demo.com",
      firstName = "Demo",
      secondName = "Demos"
    )
  }

  var user:OrientVertex = null
  it should "be found in database " in {
    user = PhUserService.findUserByLogin("demo") match {
      case Some(user) => user
      case _ => null
    }

    user shouldNot be(null)
  }
  
  "Found user" should "has login equals to demo" in {
    val login:String = user.getProperty("login")
    login should be("demo")
  }
  
  it should "be deleted from database" in {
    val flag = PhUserService.deleteUser (user)
    flag should be(true)
  }
}
