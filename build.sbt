val scala3Version = "3.4.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "wikigraph",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.apache.commons" % "commons-compress" % "1.26.1",
      "org.scala-lang.modules" %% "scala-xml" % "2.2.0",
      "org.scalameta" %% "munit" % "0.7.29" % Test
    )
  )
