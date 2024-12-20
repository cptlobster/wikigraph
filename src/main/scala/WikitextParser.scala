import scala.annotation.tailrec

case class WikitextParser():
  /**
   * Get all the links present in a single WikiText page.
   * @param contents WikiText formatted page
   * @return A list of page titles linked to by this page
   */
  def read_page(contents: String): List[String] =
    @tailrec def rec_read_page(contents: String, links: List[String]): List[String] =
      if contents.isEmpty then links else
        val (link, remainder) = dissect_link(until_brackets(contents))
        link match
          case "" => rec_read_page(remainder, links)
          case _ => rec_read_page(remainder, link :: links)

    rec_read_page(contents, Nil)

  /**
   * Drop all contents before and including the beginning of a link (double square bracket)
   * @param contents String to extract from
   * @return All contents to the right of the beginning of the link
   */
  @tailrec private def until_brackets(contents: String): String =
    if contents.isEmpty then "" else
      contents.take(2) match
        case "[[" => contents.drop(2)
        case _ => until_brackets(contents.tail)

  /**
   * Extract the first possible link from the source string (This requires being called at the beginning of a link to
   * work properly!)
   * @param contents String to extract from
   * @return A tuple containing the link text and the rest of the content. If it fails to reach the end of the link, the
   *         right side will be empty, if it fails to get the linked page the left side will be empty.
   */
  private def dissect_link(contents: String): (String, String) =
    val (rawlink, rest) = split_first(contents, "]]")
    val (link, _) = split_first(rawlink, "|")
    (link, rest)

  /**
   * Split a string at the first instance of an exactly matching delimiter
   * @param contents String to split
   * @param criteria The exact match of what string to split on
   * @return A tuple containing the left and right sides of the split. If the split fails, the right side will be an
   *         empty string.
   */
  private def split_first(contents: String, criteria: String): (String, String) =
    @tailrec def sp(con: String, acc: String): (String, String) =
      if con.isEmpty || con.startsWith(criteria) then (acc, con) else sp(con.tail, acc :+ con.head)
    sp(contents, "")
