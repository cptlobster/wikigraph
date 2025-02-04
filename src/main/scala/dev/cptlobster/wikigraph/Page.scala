package dev.cptlobster.wikigraph

case class Page(title: String, id: Int, rid: Int, namespace: Int, linked_pages: List[String])
