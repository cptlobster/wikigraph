package dev.cptlobster.wikigraph

/*
 * XML samples are obtained from Wikipedia's XML dumps and are used under the CC-BY-SA 3.0 License.
 */
class WMXMLDumpParserSuite extends munit.FunSuite:

  val sample1 = "<page>\n    <title>AccessibleComputing</title>\n    <ns>0</ns>\n    <id>10</id>\n    <redirect title=\"Computer accessibility\" />\n    <revision>\n      <id>1219062925</id>\n      <parentid>1219062840</parentid>\n      <timestamp>2024-04-15T14:38:04Z</timestamp>\n      <contributor>\n        <username>Asparagusus</username>\n        <id>43603280</id>\n      </contributor>\n      <comment>Restored revision 1002250816 by [[Special:Contributions/Elli|Elli]] ([[User talk:Elli|talk]]): Unexplained redirect breaking</comment>\n      <origin>1219062925</origin>\n      <model>wikitext</model>\n      <format>text/x-wiki</format>\n      <text bytes=\"111\" sha1=\"kmysdltgexdwkv2xsml3j44jb56dxvn\" xml:space=\"preserve\">#REDIRECT [[Computer accessibility]]\n\n{{rcat shell|\n{{R from move}}\n{{R from CamelCase}}\n{{R unprintworthy}}\n}}</text>\n      <sha1>kmysdltgexdwkv2xsml3j44jb56dxvn</sha1>\n    </revision>\n  </page>"
