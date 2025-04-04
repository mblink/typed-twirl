/*
 * Copyright (C) from 2025 BondLink, 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.twirl.api
package test

import java.util.ArrayList
import java.util.Optional
import java.util.{ List => JList }
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class StringInterpolationSpec extends AnyWordSpec with Matchers {

  "StringInterpolation" should {
    "leave string parts untouched" in {
      val p = html"<p>"
      p.body mustBe "<p>"
    }
    "escape interpolated arguments" in {
      val arg = "<"
      val p   = html"<p>$arg</p>"
      p.body mustBe "<p>&lt;</p>"
    }
    "leave nested templates untouched" in {
      val p   = html"<p></p>"
      val div = html"<div>$p</div>"
      div.body mustBe "<div><p></p></div>"
    }
    "display arguments as they would be displayed in a template" in {
      html"${Some("a")} $None".body mustBe "a "
      html"${Optional.of("a")} $None".body mustBe "a "
      html"${Seq("a", "b")}".body mustBe "ab"
      val javaList: JList[String] = new ArrayList();
      javaList.add("a")
      javaList.add("b")
      html"${javaList}".body mustBe "ab"
    }
  }
}
