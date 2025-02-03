package dev.cptlobster.wikigraph.parser

import scala.util.matching.Regex
import scala.jdk.StreamConverters._

import java.io.{BufferedReader, InputStream, InputStreamReader}

case class WMIndexParser():
  def read(input: InputStream): List[(Int, String)] =
    val rd: BufferedReader = new BufferedReader(new InputStreamReader(input, "UTF-8"))

    rd.lines().map(parseLine).toScala(List)

  def read(input: String): List[(Int, String)] =
    input.lines().map(parseLine).toScala(List)

  def parseLine(line: String): (Int, String) =
    val pattern: Regex = "[0-9]+:([0-9]+):(.+)$".r
    val res0 = pattern.findAllIn(line)
    (res0.group(1).toInt, res0.group(2))