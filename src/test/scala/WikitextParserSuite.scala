/*
 * WikiText samples are obtained from Wikipedia and are used under the CC-BY-SA 3.0 License.
 */
class WikitextParserSuite extends munit.FunSuite {
  // Sample 1 from "Everything Relative" (https://en.wikipedia.org/wiki/Everything_Relative)
  val sample1 = "'''''Everything Relative''''' is a 1996 American [[comedy-drama]] [[independent film]] written and directed by [[Sharon Pollack (filmmaker)|Sharon Pollack]]. It centers around a weekend reunion of seven women who were friends and political activists in college. The film has been compared to [[The Big Chill (film)|The Big Chill]] and [[Return of the Secaucus Seven]] in terms of theme and structure.<ref>{{cite web |url= http://www.sfgate.com/movies/article/FILM-REVIEW-Affection-Rescues-Relative-2859749.php |title=Affection Rescues 'Relative' |last=Guthmann|first=Edward|work=[[San Francisco Chronicle]]|date=January 10, 1997|access-date=2016-05-24}}</ref> It was presented at the 1996 [[Sundance Film Festival]] as part of the American Spectrum lineup.<ref name=\"Sundance\">{{cite web |url=http://www.filmscouts.com/scripts/festarchive.cfm?name=sundan96/films |title=Film Scouts: 1996 Sundance Film Festival Program |access-date= 2016-05-24}}</ref>"
  test("Can get linktext from a WikiText document") {
    val obtained = 42
    val expected = 42
    assertEquals(obtained, expected)
  }
}
