package dev.cptlobster.wikigraph

import dev.cptlobster.wikigraph.db.PostgresConnector
import dev.cptlobster.wikigraph.parser.{RawPage, WMIndexParser, WMXMLDumpParser, WikitextParser}

import java.io.{File, FileInputStream, InputStream}
import scala.collection.parallel.CollectionConverters.*

val wtparser: WikitextParser = WikitextParser()
val idxparser: WMIndexParser = WMIndexParser()
val dbconn: PostgresConnector = PostgresConnector("localhost", 5432, "wikigraph", "wikigraph")

@main def parse_xmldump(src: String): Unit =
  val f = File(src)

  if (f.isDirectory) {
    // Get list of files and directories
    val files = f.listFiles().toList.par.filter(file => file.getName.contains(".xml"))

    // Iterate over the files and print their names
    for (file <- files) {
      val s = FileInputStream(file)
      parsePages(s)
    }
  } else {
    val s = FileInputStream(f)
    parsePages(s)
  }

def parsePages(s: InputStream): Unit =
  val pages = WMXMLDumpParser(s).getPages
    .filter((rp: RawPage) => rp.namespace == 0)
    .map((rp: RawPage) =>
    val links: List[String] = wtparser.readPage(rp.contents)
    println(rp.title)
    Page(rp.title, rp.id, rp.rid, rp.namespace, links)
  )

  dbconn.pushPages(pages)

  for (page <- pages) {
    println(s"${page.id} ${page.title}: ${page.linked_pages.mkString("[\"","\",\"","\"]")}")
  }

def parse_indexes(src: String): Unit =
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