/*
 * Copyright (C) from 2025 BondLink, 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.twirl.compiler
package test

import play.twirl.api._
import scala.collection.immutable
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TemplateUtilsSpec extends AnyWordSpec with Matchers {

  "Templates" should {
    "provide a HASH util" in {
      Hash("itShouldWork".getBytes, Nil) must be("31c0c4e0e142fe9b605fff44528fedb3dd8ae254")
    }

    "provide a Format API" when {
      "HTML for example" in {
        case class Html(_text: String) extends BufferedContent[Html](Nil, _text) {
          val contentType = "text/html"
        }

        object HtmlFormat extends Format[Html] {
          def raw(text: String)                   = Html(text)
          def escape(text: String)                = Html(text.replace("<", "&lt;"))
          def empty                               = Html("")
          def fill(elements: immutable.Seq[Html]) = Html("")
        }

        val html = HtmlFormat.raw("<h1>").body + HtmlFormat.escape("Hello <world>").body + HtmlFormat.raw("</h1>").body

        html must be("<h1>Hello &lt;world></h1>")
      }

      "Text for example" in {
        case class Text(_text: String) extends BufferedContent[Text](Nil, _text) {
          val contentType = "text/plain"
        }

        object TextFormat extends Format[Text] {
          def raw(text: String)                   = Text(text)
          def escape(text: String)                = Text(text)
          def empty                               = Text("")
          def fill(elements: immutable.Seq[Text]) = Text("")
        }

        val text = TextFormat.raw("<h1>").body + TextFormat.escape("Hello <world>").body + TextFormat.raw("</h1>").body

        text must be("<h1>Hello <world></h1>")
      }
    }
  }
}
