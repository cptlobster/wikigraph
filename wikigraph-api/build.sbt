val ScalatraVersion = "3.1.1"

ThisBuild / scalaVersion := "3.4.1"
ThisBuild / organization := "dev.cptlobster.wikigraph"

lazy val hello = (project in file("."))
  .settings(
    name := "wikigraph-api",
    version := "0.1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      // scalatra dependencies
      "org.scalatra" %% "scalatra-jakarta" % ScalatraVersion,
      "org.scalatra" %% "scalatra-json-jakarta" % ScalatraVersion,
      "org.scalatra" %% "scalatra-scalatest-jakarta" % ScalatraVersion % "test",
      "org.json4s"   %% "json4s-jackson" % "4.0.7",
      // servlet API
      "jakarta.servlet" % "jakarta.servlet-api" % "6.1.0" % "provided",
      // logging
      "ch.qos.logback" % "logback-classic" % "1.5.16" % "runtime"
    ),
  )

enablePlugins(SbtWar)

Test / fork := true
