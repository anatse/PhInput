package org.asem.orient.services

import com.tinkerpop.blueprints.impls.orient.OrientVertex
import org.scalatest._

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

  var user:OrientVertex = _
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
