val scala3Version = "3.4.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "wikigraph",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-parallel-collections" % "1.2.0",
      "org.apache.commons" % "commons-compress" % "1.27.1",
      "org.scala-lang.modules" %% "scala-xml" % "2.3.0",
      "org.neo4j.driver" % "neo4j-java-driver" % "5.27.0",
      "org.postgresql" % "postgresql" % "42.7.5",
      "org.scalameta" %% "munit" % "1.1.0" % Test
    )
  )
