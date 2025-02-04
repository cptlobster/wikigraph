package dev.cptlobster.wikigraph.parser

import dev.cptlobster.wikigraph.parser.WikitextParser

/*
 * WikiText samples are obtained from Wikipedia and are used under the CC-BY-SA 3.0 License.
 */
class WikitextParserSuite extends munit.FunSuite {
  val wp: WikitextParser = WikitextParser()
  // Sample 1 from "Everything Relative" (https://en.wikipedia.org/wiki/Everything_Relative)
  val sample1 = "'''''Everything Relative''''' is a 1996 American [[comedy-drama]] [[independent film]] written and directed by [[Sharon Pollack (filmmaker)|Sharon Pollack]]. It centers around a weekend reunion of seven women who were friends and political activists in college. The film has been compared to [[The Big Chill (film)|The Big Chill]] and [[Return of the Secaucus Seven]] in terms of theme and structure.<ref>{{cite web |url= http://www.sfgate.com/movies/article/FILM-REVIEW-Affection-Rescues-Relative-2859749.php |title=Affection Rescues 'Relative' |last=Guthmann|first=Edward|work=[[San Francisco Chronicle]]|date=January 10, 1997|access-date=2016-05-24}}</ref> It was presented at the 1996 [[Sundance Film Festival]] as part of the American Spectrum lineup.<ref name=\"Sundance\">{{cite web |url=http://www.filmscouts.com/scripts/festarchive.cfm?name=sundan96/films |title=Film Scouts: 1996 Sundance Film Festival Program |access-date= 2016-05-24}}</ref>"
  // TODO: add more samples

  // ensure that the WikiText parser works
  test("Can get linktext from a WikiText document") {
    val obtained = wp.readPage(sample1)
    // expected link texts
    val expected = "Sundance Film Festival" :: "San Francisco Chronicle" :: "Return of the Secaucus Seven" ::
      "The Big Chill (film)" :: "Sharon Pollack (filmmaker)" :: "independent film" :: "comedy-drama" :: Nil
    for (link <- obtained) {
      assert(expected.contains(clue(link)))
    }
  }
}
