package dev.cptlobster.wikigraph

import java.io.InputStream
import scala.jdk.CollectionConverters.*
import scala.xml.{Node, NodeSeq, XML}

case class WMXMLDumpParser():
  def get_xml(stream: InputStream): NodeSeq =
    val xml = XML.load(stream)
    xml \\ "mediawiki"

  def get_namespaces(nodes: NodeSeq): Map[Int, String] =
    (nodes \\ "namespace").map((node: Node) => ((node \@ "key").toInt, node.text)).toMap[Int, String]

  def get_pages(nodes: NodeSeq): List[Page] =
    (for (node <- nodes if node.label == "page") yield parse_page(node)).toList

  def parse_page(node: Node): Page =
    Page(
      (node \\ "title").text,
      (node \\ "id").text.toInt,
      (node \\ "ns").text.toInt,
      ((node \\ "revision").head \\ "text").text)