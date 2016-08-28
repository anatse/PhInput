import sbt.Keys._

organization := "org.asem"

name := "phinp"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature")

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Spray repository"    at "http://repo.spray.io/",
  "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Central" at "http://central.maven.org/maven2"
)

libraryDependencies ++= {
  val akkaV = "2.+"
  val sprayV = "1.+"
  val orientV = "2.+"
  
  Seq(
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray" %% "spray-caching" % sprayV,
    "io.spray" %% "spray-json" % "1.3.2",

    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.5",
    "org.scala-lang" % "scala-reflect" % "2.11.8",

    // Orient DB dependencies
    "com.orientechnologies" % "orientdb-core" % orientV,
    "com.orientechnologies" % "orientdb-client" % orientV,
    "com.orientechnologies" % "orientdb-jdbc" % orientV,
    "com.orientechnologies" % "orientdb-graphdb" % orientV,
    "com.orientechnologies" % "orientdb-distributed" % orientV,
    "com.tinkerpop.blueprints" % "blueprints-core" % "2.6.0",
    "com.github.nscala-time" %% "nscala-time" % "2.+",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.+",
    "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.+",
    "com.wandoulabs.akka" %% "spray-websocket" % "0.1.4",

    "io.spray" %% "spray-testkit" % sprayV % "test",
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    "org.scalatest" % "scalatest_2.11" % "3.0.0" % "test"
  )
}

Revolver.settings