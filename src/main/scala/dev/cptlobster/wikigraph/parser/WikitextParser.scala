package dev.cptlobster.wikigraph.parser

import scala.annotation.tailrec

/**
 * Parser for reading links from a WikiText page
 */
case class WikitextParser():
  /**
   * Get all the links present in a single WikiText page.
   * @param contents WikiText formatted page
   * @return A list of page titles linked to by this page
   */
  def readPage(contents: String): List[String] =
    @tailrec def recReadPage(contents: String, links: List[String]): List[String] =
      if contents.isEmpty then links else
        val (link, remainder) = dissectLink(untilBrackets(contents))
        link match
          case "" => recReadPage(remainder, links)
          case _ => recReadPage(remainder, link :: links)

    recReadPage(contents, Nil)

  /**
   * Drop all contents before and including the beginning of a link (double square bracket)
   * @param contents String to extract from
   * @return All contents to the right of the beginning of the link
   */
  @tailrec private def untilBrackets(contents: String): String =
    if contents.isEmpty then "" else
      contents.take(2) match
        case "[[" => contents.drop(2)
        case _ => untilBrackets(contents.tail)

  /**
   * Extract the first possible link from the source string (This requires being called at the beginning of a link to
   * work properly!)
   * @param contents String to extract from
   * @return A tuple containing the link text and the rest of the content. If it fails to reach the end of the link, the
   *         right side will be empty, if it fails to get the linked page the left side will be empty.
   */
  private def dissectLink(contents: String): (String, String) =
    val (rawlink, rest) = splitFirst(contents, "]]")
    val (link, _) = splitFirst(rawlink, "|")
    (link, rest)

  /**
   * Split a string at the first instance of an exactly matching delimiter
   * @param contents String to split
   * @param criteria The exact match of what string to split on
   * @return A tuple containing the left and right sides of the split. If the split fails, the right side will be an
   *         empty string.
   */
  private def splitFirst(contents: String, criteria: String): (String, String) =
    @tailrec def sp(con: String, acc: String): (String, String) =
      if con.isEmpty || con.startsWith(criteria) then (acc, con) else sp(con.tail, acc :+ con.head)
    sp(contents, "")
