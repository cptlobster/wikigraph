package dev.cptlobster.wikigraph.db

import dev.cptlobster.wikigraph.Page
import org.neo4j.driver.*
import org.neo4j.driver.Values.parameters

import java.lang.AutoCloseable

case class Neo4jConnector(uri: String, username: String, password: String) extends AutoCloseable:
  private val driver: Driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password))

  @Override def close(): Unit = driver.close()

  def configure(): Unit =
    val session: Session = driver.session(SessionConfig.builder().withDatabase("neo4j").build())
    session.executeWrite(tx =>
      val query = Query("CREATE INDEX ids FOR (p:Page) on (p.name)" +
        "CREATE INDEX titles FOR (p:Page) ON (p.title)" +
        "CREATE CONSTRAINT unique_ids FOR (p:Page) REQUIRE p.id IS UNIQUE")
      val res0 = tx.run(query)
    )

  def push_page(p: Page): Unit =
    val session: Session = driver.session(SessionConfig.builder().withDatabase("neo4j").build())
    session.executeWrite(tx =>
      val query = Query("MERGE ($id:Page {p.namespace: $namespace, p.title = $title})", parameters(
        "id", p.id,
        "namespace", p.namespace,
        "title", p.title
      ))
      val res0 = tx.run(query)
    )
    session.close()

  def push_pages(ps: List[Page]): Unit =
    val session: Session = driver.session(SessionConfig.builder().withDatabase("neo4j").build())
    for (p <- ps)
      session.executeWrite(tx =>
        val query = Query("MERGE ($id:Page {p.namespace: $namespace, p.title = $title})", parameters(
          "id", p.id,
          "namespace", p.namespace,
          "title", p.title
        ))
        val res0 = tx.run(query)
      )
    session.close()
