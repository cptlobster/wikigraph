package dev.cptlobster.wikigraph.parser

import dev.cptlobster.wikigraph.parser.WikitextParser

/*
 * WikiText samples are obtained from Wikipedia and are used under the CC-BY-SA 3.0 License.
 */
class WikitextParserSuite extends munit.FunSuite {
  // Sample 0
  val sample0 = "This is a link: [[Title|Display text]] and here's an image: [[File:Example.jpg|thumb|[[Nested link]]]]"
  // Sample 1 from "Everything Relative" (https://en.wikipedia.org/wiki/Everything_Relative)
  val sample1 = "'''''Everything Relative''''' is a 1996 American [[comedy-drama]] [[independent film]] written and directed by [[Sharon Pollack (filmmaker)|Sharon Pollack]]. It centers around a weekend reunion of seven women who were friends and political activists in college. The film has been compared to [[The Big Chill (film)|The Big Chill]] and [[Return of the Secaucus Seven]] in terms of theme and structure.<ref>{{cite web |url= http://www.sfgate.com/movies/article/FILM-REVIEW-Affection-Rescues-Relative-2859749.php |title=Affection Rescues 'Relative' |last=Guthmann|first=Edward|work=[[San Francisco Chronicle]]|date=January 10, 1997|access-date=2016-05-24}}</ref> It was presented at the 1996 [[Sundance Film Festival]] as part of the American Spectrum lineup.<ref name=\"Sundance\">{{cite web |url=http://www.filmscouts.com/scripts/festarchive.cfm?name=sundan96/films |title=Film Scouts: 1996 Sundance Film Festival Program |access-date= 2016-05-24}}</ref>"
  // Sample 2 from "Anarchism" (https://en.wikipedia.org/wiki/Anarchism)
  val sample2 = "=== The arts ===\n{{Main|Anarchism and the arts}}\n[[File:Apple Harvest by Camille Pissarro.jpg|thumb|340px|''Les chataigniers a Osny'' (1888) by anarchist painter [[Camille Pissarro]] is a notable example of blending anarchism and the arts.{{Sfn|Antliff|1998|p=99}}]]\nThe connection between anarchism and art was quite profound during the classical era of anarchism, especially among artistic currents that were developing during that era such as futurists, surrealists and others.{{Sfn|Mattern|2019|p=592}} In literature,\nanarchism was mostly associated with the [[New Apocalyptics]] and the [[neo-romanticism]] movement.{{Sfn|Gifford|2019|p=577}} In music, anarchism has been associated with music scenes such as [[Punk subculture|punk]].{{Sfnm|1a1=Marshall|1y=1993|1pp=493–49\n4|2a1=Dunn|2y=2012|3a1=Evren|3a2=Kinna|3a3=Rouselle|3y=2013|p=138}} Anarchists such as [[Leo Tolstoy]] and [[Herbert Read]] stated that the border between the artist and the non-artist, what separates art from a daily act, is a construct produced by the a\nlienation caused by capitalism and it prevents humans from living a joyful life.{{Sfn|Mattern|2019|pp=592–593}}\n\nOther anarchists advocated for or used art as a means to achieve anarchist ends.{{Sfn|Mattern|2019|p=593}} In his book ''Breaking the Spell: A History of Anarchist Filmmakers, Videotape Guerrillas, and Digital Ninjas'', Chris Robé claims that &quot;anarch\nist-inflected practices have increasingly structured movement-based video activism.&quot;{{Sfn|Robé|2017|p=44}} Throughout the 20th century, many prominent anarchists ([[Peter Kropotkin]], [[Emma Goldman]], [[Gustav Landauer]] and [[Camillo Berneri]]) and\n publications such as ''[[Anarchy (magazine)|Anarchy]]'' wrote about matters pertaining to the arts.{{Sfn|Miller|Dirlik|Rosemont|Augustyn|2019|p=1}}\n\nThree overlapping properties made art useful to anarchists. It could depict a critique of existing society and hierarchies, serve as a prefigurative tool to reflect the anarchist ideal society and even turn into a means of direct action such as in protest\ns. As it appeals to both emotion and reason, art could appeal to the whole human and have a powerful effect.{{Sfn|Mattern|2019|pp=593–596}} The 19th-century [[neo-impressionist]] movement had an ecological aesthetic and offered an example of an anarchist\nperception of the road towards socialism.{{Sfn|Antliff|1998|p=78}} In ''Les chataigniers a Osny'' by anarchist painter [[Camille Pissarro]], the blending of aesthetic and social harmony is prefiguring an ideal anarchistic agrarian community.{{Sfn|Antliff|\n1998|p=99}}"
  // TODO: add more samples

  test("Can parse links") {
    val obtained = WikitextParser.readPage(sample0)
    // expected link texts
    val expected = List("Title", "File:Example.jpg", "Nested link")
    for (link <- obtained) {
      assert(expected.contains(clue(link)))
    }
  }

  // ensure that the WikiText parser works
  test("Can get linktext from a WikiText document") {
    val obtained = WikitextParser.readPage(sample1)
    // expected link texts
    val expected = List("Sundance Film Festival", "San Francisco Chronicle", "Return of the Secaucus Seven",
      "The Big Chill (film)", "Sharon Pollack (filmmaker)", "independent film", "comedy-drama")
    for (link <- obtained) {
      assert(expected.contains(clue(link)))
    }
  }

  test("ignores file links") {
    val obtained = WikitextParser.readPage(sample2)
    // expected link texts
    val expected = List("File:Apple Harvest by Camille Pissarro.jpg", "Camille Pissarro", "New Apocalyptics",
      "neo-romanticism", "Punk subculture", "Leo Tolstoy", "Herbert Read", "Peter Kropotkin", "Emma Goldman",
      "Gustav Landauer", "Camillo Berneri", "Anarchy (magazine)", "neo-impressionist", "Camille Pissarro")
    for (link <- obtained) {
      assert(expected.contains(clue(link)))
    }
  }
}
