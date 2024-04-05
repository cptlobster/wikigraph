import scala.annotation.tailrec

class WikitextParser:
  def read_page(contents: String): List[String] =
    ???

  @tailrec private def until_brackets(contents: String): String =
    if contents.isEmpty then "" else
      contents.take(2) match
        case "[[" => contents.drop(2)
        case _ => until_brackets(contents.tail)

  private def dissect_link(contents: String): (String, String) =
    val con_split = split_first(contents, "]]")
    val rawlink = con_split._1
    val rest = con_split._2
    val link = split_first(rawlink, "|")._1
    (link, rest)

  private def split_first(contents: String, criteria: String): (String, String) =
    @tailrec def sp(con: String, acc: String): (String, String) =
      if con.isEmpty || con.startsWith(criteria) then (acc, con) else sp(con.tail, acc :+ con.head)
    sp(contents, "")
