package dev.cptlobster.wikigraph.parser

import java.io.{BufferedReader, InputStream, InputStreamReader}
import scala.jdk.StreamConverters.*
import scala.util.matching.Regex

case class WMIndexParser():
  def read(input: InputStream): List[(Int, String)] =
    val rd: BufferedReader = new BufferedReader(new InputStreamReader(input, "UTF-8"))

    rd.lines().map(parseLine).toScala(List)

  def read(input: String): List[(Int, String)] =
    input.lines().map(parseLine).toScala(List)

  def parseLine(line: String): (Int, String) =
    val pattern: Regex = "[0-9]+:([0-9]+):(.+)$".r
    line match {
      case pattern(num, rest) => (num.toInt, rest)
      case _ => throw new IllegalArgumentException(s"Line did not match expected format: $line")
    }