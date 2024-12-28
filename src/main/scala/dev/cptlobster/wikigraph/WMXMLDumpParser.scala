package dev.cptlobster.wikigraph

import java.io.InputStream
import scala.jdk.CollectionConverters.*
import scala.xml.{Node, NodeSeq, XML}

case class RawPage(title: String, id: Int, namespace: Int, contents: String)

case class WMXMLDumpParser():
  def get_xml(stream: InputStream): NodeSeq =
    val xml = XML.load(stream)
    xml \ "mediawiki"

  def get_namespaces(nodes: NodeSeq): Map[Int, String] =
    (nodes \ "namespace").map((node: Node) => ((node \@ "key").toInt, node.text)).toMap[Int, String]

  def get_pages(nodes: NodeSeq): Seq[RawPage] =
    for (node <- nodes if node.label == "page") yield parse_page(node)

  def parse_page(node: Node): RawPage =
    RawPage(
      (node \ "title").head.text,
      (node \ "id").head.text.toInt,
      (node \ "ns").head.text.toInt,
      ((node \ "revision").head \ "text").head.text)