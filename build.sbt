name         := "trakktor"
organization := "cat.aartigao"
scalaVersion := "2.12.4"
version      := "1.0.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.8",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.8" % Test,
  "com.typesafe.akka" %% "akka-stream" % "2.5.8",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.8" % Test,
  "com.typesafe.akka" %% "akka-http" % "10.1.0-RC1",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.0-RC1" % Test,
  "org.scalatest" %% "scalatest" % "3.0.4" % Test,
  "org.scalactic" %% "scalactic" % "3.0.4" % Test
)
