val scala3Version = "3.4.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "wikigraph",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version
  )

// SPaC version is reused a few times
val spacVersion = "0.12.1"

libraryDependencies ++= Seq(
  // scala modules
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.2.0",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.4.0",
  // commons-compress for reading BZip2 compressed files
  "org.apache.commons" % "commons-compress" % "1.27.1",
  // SPaC for parsing XML data
  "io.dylemma" %% "spac-core" % spacVersion,
  "io.dylemma" %% "xml-spac" % spacVersion,
  "io.dylemma" %% "xml-spac-javax" % spacVersion,
  // Database functionality
  "org.neo4j.driver" % "neo4j-java-driver" % "5.28.1",
  "org.postgresql" % "postgresql" % "42.7.5",
  // testing
  "org.scalameta" %% "munit" % "1.1.0" % Test
)
