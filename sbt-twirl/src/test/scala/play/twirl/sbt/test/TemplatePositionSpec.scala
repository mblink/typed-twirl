/*
 * Copyright (C) from 2025 BondLink, 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.twirl.sbt.test

import java.io.File
import play.twirl.sbt.TemplateProblem.TemplateMapping
import play.twirl.sbt.TemplateProblem.TemplatePosition
import org.scalatest.Inspectors
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TemplatePositionSpec extends AnyWordSpec with Matchers with Inspectors {

  "TemplatePosition" should {
    "toString" should {
      "have the source path" in {
        val file     = new File("/some/path/file.scala.html")
        val location = TemplateMapping.Location(line = 10, column = 2, offset = 22, content = "some content")
        val tp       = new TemplatePosition(Option(file), Option(location))

        tp.toString mustBe "/some/path/file.scala.html:10\nsome content"
      }

      "not have the source path when it is empty" in {
        val location = TemplateMapping.Location(line = 10, column = 2, offset = 22, content = "some content")
        val tp       = new TemplatePosition(None, Option(location))

        tp.toString mustNot include("/some/path/file.scala.html")
      }

      "have line if present" in {
        val file     = new File("/some/path/file.scala.html")
        val location = TemplateMapping.Location(line = 10, column = 2, offset = 22, content = "some content")
        val tp       = new TemplatePosition(Option(file), Option(location))

        tp.toString must include("10")
      }

      "not have line when it is empty" in {
        val file = new File("/some/path/file.scala.html")
        val tp   = new TemplatePosition(Option(file), None /* means no location for the error, then no line */ )

        tp.toString mustBe "/some/path/file.scala.html"
      }

      "have line content if present" in {
        val file     = new File("/some/path/file.scala.html")
        val location = TemplateMapping.Location(line = 10, column = 2, offset = 22, content = "some content")
        val tp       = new TemplatePosition(Option(file), Option(location))

        tp.toString must include("some content")
      }

      "not have line content when it is missing" in {
        val file = new File("/some/path/file.scala.html")
        val tp   = new TemplatePosition(Option(file), None /* means no location for the error, then no offset */ )

        tp.toString mustBe "/some/path/file.scala.html"
      }

      "not have line content when it is empty" in {
        val file     = new File("/some/path/file.scala.html")
        val location = TemplateMapping.Location(line = 10, column = 2, offset = 22, content = "")
        val tp       = new TemplatePosition(Option(file), Option(location))

        tp.toString mustBe "/some/path/file.scala.html:10"
      }
    }
  }
}
