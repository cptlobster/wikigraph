package dev.cptlobster.wikigraph.parser

/*
 * Index samples are obtained from Wikipedia's XML dumps and are used under the CC-BY-SA 3.0 License.
 */
class WMIndexParserSuite extends munit.FunSuite:
  val sample1: String = "567:10:AccessibleComputing\n567:12:Anarchism\n567:13:AfghanistanHistory\n567:14:AfghanistanGeography\n567:15:AfghanistanPeople\n567:18:AfghanistanCommunications\n567:19:AfghanistanTransportations\n567:20:AfghanistanMilitary\n567:21:AfghanistanTransnationalIssues\n567:23:AssistiveTechnology\n567:24:AmoeboidTaxa\n567:25:Autism spectrum"
  val ids: List[Int] = List(10, 12, 13, 14, 15, 18, 19, 20, 21, 23, 24, 25)
  val names: List[String] = List("AccessibleComputing", "Anarchism", "AfghanistanHistory", "AfghanistanGeography", "AfghanistanPeople", "AfghanistanCommunications", "AfghanistanTransportations", "AfghanistanMilitary", "AfghanistanTransnationalIssues", "AssistiveTechnology", "AmoeboidTaxa", "Autism spectrum")
  val expected1: List[(Int, String)] = ids.zip(names)

  test("can convert an index into a list") {
    val parser = WMIndexParser()

    assertEquals(clue(parser.read(sample1)), expected1)
  }