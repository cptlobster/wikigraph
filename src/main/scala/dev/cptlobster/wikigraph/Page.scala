package dev.cptlobster.wikigraph

case class Page(title: String, id: Int, namespace: Int, linked_ids: List[Int])
