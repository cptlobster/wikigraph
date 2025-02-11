package dev.cptlobster.wikigraph

import org.scalatra.test.scalatest._

class WikigraphServletTests extends ScalatraFunSuite {

  addServlet(classOf[WikigraphServlet], "/*")

  test("GET / on WikigraphServlet should return status 200") {
    get("/") {
      status should equal (200)
    }
  }

}
