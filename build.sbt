organization := "org.asem"

name := "phinp"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

//SelectMainClass = "Boot.scala"

libraryDependencies ++= {
  val akkaV = "2.4.8"
  val sprayV = "1.3.3"
  Seq(
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray" %% "spray-caching" % sprayV,
    "io.spray" %% "spray-json" % "1.3.2",

    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.5",

    // Orient DB dependencies
    "com.orientechnologies" % "orientdb-core" % "2.2.4",
    "com.orientechnologies" % "orientdb-client" % "2.2.4",
    "com.orientechnologies" % "orientdb-jdbc" % "2.2.4",
    "com.orientechnologies" % "orientdb-graphdb" % "2.2.4",
    "com.orientechnologies" % "orientdb-distributed" % "2.2.4",
    "com.tinkerpop.blueprints" % "blueprints-core" % "2.6.0",

    "io.spray" %% "spray-testkit" % sprayV % "test",
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    //"org.specs2" %% "specs2-core" % "3.8.4" % "test",
    "org.scalatest" % "scalatest_2.11" % "2.2.6" % "test"
  )
}

Revolver.settings