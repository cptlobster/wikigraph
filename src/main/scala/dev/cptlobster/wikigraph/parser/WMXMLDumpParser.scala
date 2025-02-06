package dev.cptlobster.wikigraph.parser

import cats.syntax.apply.*
import io.dylemma.spac.Splitter
import io.dylemma.spac.xml.*
import dev.cptlobster.wikigraph.Page
import dev.cptlobster.wikigraph.parser.WikitextParser

import java.io.InputStream

case class RawPage(title: String, id: Int, rid: Int, namespace: Int, contents: String)

case class WMXMLDumpParser(stream: InputStream):
  private val source = JavaxSource.fromInputStream(stream)
  private val wtparser = WikitextParser()

  implicit val PageParser: XmlParser[RawPage] = (
    Splitter.xml(* \ "title").text.parseFirst,
    Splitter.xml(* \ "ns").text.map(_.toInt).parseFirst,
    Splitter.xml(* \ "id").text.map(_.toInt).parseFirst,
    Splitter.xml(* \ "revision" \ "id").text.map(_.toInt).parseFirst,
    Splitter.xml(* \ "revision" \ "text").text.parseFirst
  ).mapN(RawPage.apply)

  private val MWParser: XmlTransformer[RawPage] = Splitter.xml("mediawiki" \ "page").as[RawPage]

  def getPages: List[RawPage] =
    MWParser.parseToList.parse(source)

  def testPages(): Unit =
    MWParser.parseTap(rp => {
      val p = rpToPage(rp)
      print(s"\r${p.title}: ${p.linked_pages.size} links")
    }).parse(source)

  private def rpToPage(rp: RawPage): Page =
    val links: List[String] = wtparser.readPage(rp.contents)
    Page(rp.title, rp.id, rp.rid, rp.namespace, links)