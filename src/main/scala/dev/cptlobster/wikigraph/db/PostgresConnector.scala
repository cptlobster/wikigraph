package dev.cptlobster.wikigraph.db

import dev.cptlobster.wikigraph.Page

import java.sql.{DriverManager, PreparedStatement}
import java.util.Properties

case class PostgresConnector(url: String, port: Int, username: String, password: String):

  val con_st = s"jdbc:postgresql://$url:$port/wikigraph"
  val props = new Properties()

  props.setProperty("user", username)
  props.setProperty("password", password)

  private def insertPageQuery(stmt: PreparedStatement, id: Int, title: String): Unit =
    stmt.setInt(1, id)
    stmt.setString(2, title)

    stmt.executeQuery()

  private def insertPageQuery(stmt: PreparedStatement, page: Page): Unit =
    stmt.setInt(1, page.id)
    stmt.setString(2, page.title)
    stmt.setInt(3, page.namespace)

    stmt.executeQuery()

  private def insertLinkQuery(stmt: PreparedStatement, from: Int, to: Int): Unit =
    stmt.setInt(1, from)
    stmt.setInt(2, to)

    stmt.executeQuery()

  def pushPage(page: Page): Unit =
    val connection = DriverManager.getConnection(con_st, props)
    val stmt = connection.prepareStatement("INSERT INTO pages (id, title, namespace) VALUES (?, ?, ?)")

    insertPageQuery(stmt, page)

    stmt.close()
    connection.close()

  def pushPages(pages: List[Page]): Unit =
    val connection = DriverManager.getConnection(con_st, props)
    val stmt = connection.prepareStatement("INSERT INTO pages (id, title, namespace) VALUES (?, ?, ?)")

    for (page <- pages) { insertPageQuery(stmt, page) }

    stmt.close()
    connection.close()

  def pushPageNsless(page: (Int, String)): Unit =
    val connection = DriverManager.getConnection(con_st, props)
    val stmt = connection.prepareStatement("INSERT INTO pages (id, title) VALUES (?, ?)")

    val (id, title) = page

    insertPageQuery(stmt, id, title)

    stmt.close()
    connection.close()

  def pushPagesNsless(pages: List[(Int, String)]): Unit =
    val connection = DriverManager.getConnection(con_st, props)
    val stmt = connection.prepareStatement("INSERT INTO pages (id, title) VALUES (?, ?)")

    for ((id, title) <- pages) {
      insertPageQuery(stmt, id, title)
    }

    stmt.close()
    connection.close()

  def pushLinks(links: List[(Int, Int)]): Unit =
    val connection = DriverManager.getConnection(con_st, props)
    val stmt = connection.prepareStatement("INSERT INTO links (l_from, l_to) VALUES (?, ?)")

    for ((from, to) <- links) { insertLinkQuery(stmt, from, to) }

    stmt.close()
    connection.close()