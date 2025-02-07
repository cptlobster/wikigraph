package dev.cptlobster.wikigraph.parser

import scala.util.parsing.combinator.RegexParsers

object WikitextParser extends RegexParsers:
  override val skipWhitespace = false

  def document: Parser[List[String]] =
    rep(text | link) ^^ { _.flatten }

  // ignore anything that isn't part of a link
  def text: Parser[List[String]] =
    // The regex uses a negative lookahead to stop before a "[[" sequence
    """(?s)(?:(?!\[\[).)+""".r ^^ { _ => Nil }

  def link: Parser[List[String]] = "[[" ~> linkBody <~ "]]"

  // the body contains a link and (optionally) display component
  def linkBody: Parser[List[String]] =
    (linkTarget ~ opt("|" ~> linkDisplay)) ^^ {
      case target ~ maybeDisplay =>
        // return the outer link’s target (trimmed) together with any nested links.
        target.trim :: maybeDisplay.getOrElse(Nil)
    }

  // anything before the | or closing brackets
  def linkTarget: Parser[String] = """(?s).+?(?=(\||]]))""".r

  // The display component is parsed as more text, that could contain more links (i.e. in files)
  def linkDisplay: Parser[List[String]] =
    rep(textInLink | link) ^^ { _.flatten }

  // match text until we hit either an opening or closing link
  def textInLink: Parser[List[String]] =
    """(?s).+?(?=(\[\[|]]))""".r ^^ { _ => Nil }

  /**
   * Read a WikiText document and parse the link titles
   * @param input The WikiText document to read
   * @return A list of all link titles
   */
  def readPage(input: String): List[String] =
    parseAll(document, input) match {
      case Success(result, _) => result
      case failure: NoSuccess => sys.error(s"${failure.msg} (at: ${failure.next.pos})")
    }