package ru.sbt.service.ws

import akka.actor.ActorSystem

object ProjectRooms {
  var projectRooms: Map[Int, ProjectRoom] = Map.empty[Int, ProjectRoom]

  def findOrCreate(number: Int)(implicit actorSystem: ActorSystem): ProjectRoom = projectRooms.getOrElse(number, createNewChatRoom(number))

  private def createNewChatRoom(number: Int)(implicit actorSystem: ActorSystem): ProjectRoom = {
    val chatroom = ProjectRoom(number)
    projectRooms += number -> chatroom
    chatroom
  }
}
