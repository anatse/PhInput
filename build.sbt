organization := "org.asem"

name := "phinp"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature")

//SelectMainClass = "Boot.scala"

libraryDependencies ++= {
  val akkaV = "2.4.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray" %% "spray-caching" % sprayV,
    "io.spray" %% "spray-json" % "1.3.2",

    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.5",
    "org.scala-lang" % "scala-reflect" % "2.11.8",

    // Orient DB dependencies
    "com.orientechnologies" % "orientdb-core" % "2.2.7",
    "com.orientechnologies" % "orientdb-client" % "2.2.7",
    "com.orientechnologies" % "orientdb-jdbc" % "2.2.7",
    "com.orientechnologies" % "orientdb-graphdb" % "2.2.7",
    "com.orientechnologies" % "orientdb-distributed" % "2.2.7",
    "com.tinkerpop.blueprints" % "blueprints-core" % "2.6.0",
    "com.github.nscala-time" %% "nscala-time" % "2.12.0",

    "io.spray" %% "spray-testkit" % sprayV % "test",
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    "org.scalatest" % "scalatest_2.11" % "3.0.0" % "test"
  )
}

Revolver.settings