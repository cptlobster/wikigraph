package dev.cptlobster.wikigraph
package db

import dev.cptlobster.wikigraph.Page

import java.sql.{DriverManager, PreparedStatement}
import java.util.Properties

case class PostgresConnector(url: String, port: Int, username: String, password: String):

  val conSt = s"jdbc:postgresql://$url:$port/wikigraph"
  val props = new Properties()

  props.setProperty("user", username)
  props.setProperty("password", password)

  private def insertPageQuery(stmt: PreparedStatement, id: Int, title: String): Unit =
    stmt.setInt(1, id)
    stmt.setString(2, title)

    stmt.executeUpdate()

  private def insertPageQuery(stmt: PreparedStatement, page: Page): Unit =
    stmt.setInt(1, page.id)
    stmt.setInt(2, page.rid)
    stmt.setString(3, page.title)
    stmt.setInt(4, page.namespace)

    stmt.executeUpdate()

  private def insertLinkQuery(stmt: PreparedStatement, from: Int, to: Int): Unit =
    stmt.setInt(1, from)
    stmt.setInt(2, to)

    stmt.executeUpdate()

  def pushPage(page: Page): Unit =
    val connection = DriverManager.getConnection(conSt, props)
    connection.setAutoCommit(true)
    val stmt = connection.prepareStatement("INSERT INTO pages (id, rid, title, namespace) VALUES (?, ?, ?, ?) ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title, namespace = EXCLUDED.namespace, rid = EXCLUDED.rid")

    insertPageQuery(stmt, page)

    stmt.close()
    connection.close()

  def pushPages(pages: List[Page]): Unit =
    val connection = DriverManager.getConnection(conSt, props)
    connection.setAutoCommit(true)
    val stmt = connection.prepareStatement("INSERT INTO pages (id, rid, title, namespace) VALUES (?, ?, ?, ?) ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title, namespace = EXCLUDED.namespace, rid = EXCLUDED.rid")

    for (page <- pages) {
      insertPageQuery(stmt, page)
      print(s"Pushed ${page.id}: ${page.title}\r")
    }

    stmt.close()
    connection.close()

  def pushPageIndex(page: (Int, String)): Unit =
    val connection = DriverManager.getConnection(conSt, props)
    connection.setAutoCommit(true)
    val stmt = connection.prepareStatement("INSERT INTO pages (id, title) VALUES (?, ?) ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title")

    val (id, title) = page

    insertPageQuery(stmt, id, title)

    stmt.close()
    connection.close()

  def pushPageIndexes(pages: List[(Int, String)]): Unit =
    val connection = DriverManager.getConnection(conSt, props)
    connection.setAutoCommit(true)
    val stmt = connection.prepareStatement("INSERT INTO pages (id, title) VALUES (?, ?) ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title")

    for ((id, title) <- pages) {
      insertPageQuery(stmt, id, title)
      print(s"Pushed $id: $title\r")
    }

    stmt.close()
    connection.close()

  def pushLinks(links: List[(Int, Int)]): Unit =
    val connection = DriverManager.getConnection(conSt, props)
    connection.setAutoCommit(true)
    val stmt = connection.prepareStatement("INSERT INTO links (l_from, l_to) VALUES (?, ?) ON CONFLICT DO NOTHING")

    for ((from, to) <- links) {
      insertLinkQuery(stmt, from, to)
      print(s"Pushed link: $from -> $to\r")
    }

    stmt.close()
    connection.close()