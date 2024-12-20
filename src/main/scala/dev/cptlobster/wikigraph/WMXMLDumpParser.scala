package dev.cptlobster.wikigraph

import dev.cptlobster.wikigraph.PartitionedFileInputStream

import scala.xml.XML

case class WMXMLDumpParser():
  def openPartitions(partitions: List[String]): PartitionedFileInputStream = ???
