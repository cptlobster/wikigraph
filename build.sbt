val scala3Version = "3.4.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "wikigraph",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.apache.commons" % "commons-compress" % "1.27.1",
      "org.scala-lang.modules" %% "scala-xml" % "2.3.0",
      "org.scalameta" %% "munit" % "1.0.3" % Test
    )
  )
