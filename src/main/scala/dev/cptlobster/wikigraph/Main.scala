package dev.cptlobster.wikigraph

import dev.cptlobster.wikigraph.db.PostgresConnector
import dev.cptlobster.wikigraph.parser.{WMIndexParser, WMXMLDumpParser}

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
    val files = f.listFiles().toList.par.filter(file => file.getName.contains(".xml"))

    println("Indexing pages...")
    val hm = hashIndexes(f)

    def parseAndPush(s: Page): Unit =
      if s.namespace == 0 then
        val link_ids = for (title <- s.linked_pages) yield { (s.id, hm(title)) }

        dbconn.pushPage(s)
        dbconn.pushLinks(link_ids)

    def parsePages(s: InputStream): Unit =
      WMXMLDumpParser(s).mapPages(parseAndPush)

    println("Parsing page links...")
    // Iterate over the files and print their names
    for (file <- files) {
      val s = FileInputStream(file)
      parsePages(s)
    }
  } else {
    throw Exception("Must be a directory")
  }

  //  dbconn.pushPages(pages)

def hashIndexes(f: File): Map[String, Int] =
  val files = f.listFiles().toList.par.filter(file => file.getName.contains(".xml"))
  (for (file <- files;
        (id, title) <- idxparser.read(FileInputStream(file))) yield {
    (title, id)
  }).toMap.seq