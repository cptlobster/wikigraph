package dev.cptlobster.wikigraph

import dev.cptlobster.wikigraph.db.PostgresConnector
import dev.cptlobster.wikigraph.parser.{RawPage, WMIndexParser, WMXMLDumpParser, WikitextParser}

import java.io.{File, FileInputStream, InputStream}
import scala.collection.parallel.CollectionConverters.*

val idxparser: WMIndexParser = WMIndexParser()
val dbconn: PostgresConnector = PostgresConnector("localhost", 5432, "wikigraph", "wikigraph")

@main def parseXmlDump(src: String): Unit =
  println("WikiGraph XML Parser")
  val f = File(src)

  if (f.isDirectory) {
    println("Directory detected. Reading XML files...")
    // Get list of files and directories
    val files = f.listFiles().toList.filter(file => file.getName.contains(".xml"))

    // Iterate over the files and print their names
    for (file <- files) {
      val s = FileInputStream(file)
      parsePages(s)
    }
  } else {
    println("Single file detected. Reading...")
    val s = FileInputStream(f)
    parsePages(s)
  }

def parsePages(s: InputStream): Unit =
  WMXMLDumpParser(s).testPages()
  val pages = WMXMLDumpParser(s).getPages
    .filter((rp: RawPage) => rp.namespace == 0)
    .map((rp: RawPage) =>
    val links: List[String] = WikitextParser.readPage(rp.contents)
    println(s"Parsed ${rp.title}")
    Page(rp.title, rp.id, rp.rid, rp.namespace, links)
  )
  println(s"Got ${pages.size} pages.")

  pages.foreach(println)
//  dbconn.pushPages(pages)

  for (page <- pages) {
    println(s"${page.id} ${page.title}: ${page.linked_pages.mkString("[\"","\",\"","\"]")}")
  }

def parseIndexes(src: String): Unit =
  val f = File(src)

  if (f.isDirectory) {
    // Get list of files and directories
    val files = f.listFiles().toList.par.filter(file => file.getName.contains("index"))

    // Iterate over the files and print their names
    for (file <- files) {
      val s = FileInputStream(file)
      val pages = idxparser.read(s)

      dbconn.pushPagesNsless(pages)
    }
  } else {
    val s = FileInputStream(f)
    val pages = idxparser.read(s)

    dbconn.pushPagesNsless(pages)
  }