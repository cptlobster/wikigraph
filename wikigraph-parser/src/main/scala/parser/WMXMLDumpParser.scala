package dev.cptlobster.wikigraph
package parser

import dev.cptlobster.wikigraph.Page

import java.io.InputStream
import io.dylemma.spac.Splitter
import io.dylemma.spac.xml.*
import cats.syntax.apply._

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

  def filterMapPages(f: RawPage => Boolean, m: Page => Unit): Unit =
    println("filterMapPages")
    MWParser.parseTap(rp => if f(rp) then m(rpToPage(rp))).parse(source)

  private def rpToPage(rp: RawPage): Page =
    try
      val links: List[String] = WikitextParser.readPage(rp.contents)
      Page(rp.title, rp.id, rp.rid, rp.namespace, links)
    catch
      case e: Exception =>
        println(s"Exception caught at entry ${rp.title} (${rp.id})")
        throw e