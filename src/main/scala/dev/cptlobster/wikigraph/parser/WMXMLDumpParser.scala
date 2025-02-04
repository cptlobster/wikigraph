package dev.cptlobster.wikigraph.parser

import cats.syntax.apply.*
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

  def getPages: List[RawPage] =
    Splitter.xml("mediawiki" \ "page").as[RawPage].parseToList.parse(source)

  def testPages(): Unit =
    Splitter.xml("mediawiki" \ "page").as[RawPage].parseTap(println).parse(source)