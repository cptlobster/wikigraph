package dev.cptlobster.wikigraph

import dev.cptlobster.wikigraph.{Page, PartitionedFileInputStream}

import java.io.InputStream
import java.util
import scala.xml.{Node, NodeSeq, XML}
import scala.jdk.CollectionConverters._

case class WMXMLDumpParser():
  def openPartitions(partitions: List[String]): PartitionedFileInputStream =
    val pl: util.ArrayList[String] = new util.ArrayList[String](partitions.asJava)
    new PartitionedFileInputStream(partitions)

  def get_xml(stream: InputStream): NodeSeq =
    val xml = XML.load(stream)
    xml \\ "mediawiki"

  def get_namespaces(nodes: NodeSeq): Map[Int, String] =
    (nodes \\ "namespace").map((node: Node) => ((node \@ "key").toInt, node.text)).toMap[Int, String]

  def get_pages(nodes: NodeSeq): Unit =
    for (node <- nodes if node.label == "page") yield
      Page(
        (node \\ "title").text,
        (node \\ "id").text.toInt,
        (node \\ "ns").text.toInt,
        ((node \\ "revision").head \\ "text").text)