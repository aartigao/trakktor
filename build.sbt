name         := "trakktor"
organization := "cat.aartigao"
scalaVersion := "2.12.4"
version      := "1.0.0-SNAPSHOT"

val akkaVersion = "2.5.8"
val akkaHttpVersion = "10.0.11"
val logbackVersion = "1.2.3"
val scalaTestVersion = "3.0.4"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
  "org.scalactic" %% "scalactic" % scalaTestVersion % Test
)

scalacOptions ++= Seq("-language:postfixOps","-deprecation", "-feature")