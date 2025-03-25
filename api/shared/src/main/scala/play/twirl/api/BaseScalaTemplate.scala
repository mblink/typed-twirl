/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.twirl.api

sealed abstract class BaseScalaTemplateLP[T <: Appendable[T], F <: Format[T]](format: F) {
  def _display_[V](v: V)(implicit formatValue: FormatValue[T, F, V]): T = formatValue(format, v)
}

case class BaseScalaTemplate[T <: Appendable[T], F <: Format[T]](format: F) extends BaseScalaTemplateLP[T, F](format) {
  // The overloaded methods are here for speed. The compiled templates
  // can take advantage of them for a 12% performance boost
  def _display_(x: String): T            = if (x eq null) format.empty else format.escape(x)
  def _display_(x: Unit): T              = format.empty
  def _display_(x: scala.xml.NodeSeq): T = if (x eq null) format.empty else format.raw(x.toString())
  def _display_(x: T): T                 = if (x eq null) format.empty else x
}
