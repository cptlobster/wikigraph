package dev.cptlobster.wikigraph

import dev.cptlobster.wikigraph.db.PostgresConnector
import dev.cptlobster.wikigraph.parser.{RawPage, WMIndexParser, WMXMLDumpParser, WikitextParser}

import java.io.{File, FileInputStream}
import scala.collection.parallel.CollectionConverters._

def parse_xmldump(src: String): Unit =
  val xparser: WMXMLDumpParser = WMXMLDumpParser()
  val wtparser: WikitextParser = WikitextParser()

  val f = File(src)
  val s = FileInputStream(f)

  val pages = xparser.get_pages(xparser.get_xml(s))
    .filter((rp: RawPage) => rp.namespace == 0)
    .map((rp: RawPage) =>
    val links: List[String] = wtparser.read_page(rp.contents)
    println(rp.title)
    Page(rp.title, rp.id, rp.namespace, links)
  )

  for (page <- pages) {
    println(s"${page.id} ${page.title}: ${page.linked_pages.mkString("[\"","\",\"","\"]")}")
  }

@main def parse_indexes(src: String): Unit =
  val idxparser: WMIndexParser = WMIndexParser()
  val dbconn: PostgresConnector = PostgresConnector("localhost", 5432, "wikigraph", "wikigraph")

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