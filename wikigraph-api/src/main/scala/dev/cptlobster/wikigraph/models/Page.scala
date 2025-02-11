package dev.cptlobster.wikigraph.models

case class Page(id: Int, rid: Int, title: String) {
  def linksFrom: List[Link] = ???
  def linksTo: List[Link] = ???
  def getUrl: String = s"https://en.wikipedia.org/wiki/$title"
}
