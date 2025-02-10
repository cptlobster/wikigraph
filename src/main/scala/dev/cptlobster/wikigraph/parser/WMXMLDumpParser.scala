package dev.cptlobster.wikigraph.parser

import cats.syntax.apply.*
import dev.cptlobster.wikigraph.Page
import io.dylemma.spac.Splitter
import io.dylemma.spac.xml.*

import java.io.InputStream

case class RawPage(title: String, id: Int, rid: Int, namespace: Int, contents: String)

case class WMXMLDumpParser(stream: InputStream):
  private val source = JavaxSource.fromInputStream(stream)

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
      println(s"${p.title}: ${p.linked_pages.size} links")
    }).parse(source)

  def mapPages(f: Page => Unit): Unit =
    MWParser.parseTap(rp => f(rpToPage(rp))).parse(source)

  private def rpToPage(rp: RawPage): Page =
    val links: List[String] = WikitextParser.readPage(rp.contents)
    Page(rp.title, rp.id, rp.rid, rp.namespace, links)