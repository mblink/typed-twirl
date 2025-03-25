/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.twirl

import scala.reflect.ClassTag

package object api extends Compat {
  type FormatHtml[-V] = FormatValue[Html, HtmlFormat.type, V]
  object FormatHtml extends FormatValue.Companion[Html, HtmlFormat.type]

  type FormatTxt[-V] = FormatValue[Txt, TxtFormat.type, V]
  object FormatTxt extends FormatValue.Companion[Txt, TxtFormat.type]

  type FormatXml[-V] = FormatValue[Xml, XmlFormat.type, V]
  object FormatXml extends FormatValue.Companion[Xml, XmlFormat.type]

  type FormatJavaScript[-V] = FormatValue[JavaScript, JavaScriptFormat.type, V]
  object FormatJavaScript extends FormatValue.Companion[JavaScript, JavaScriptFormat.type]

  /**
   * Brings the template engine as a
   * [[http://docs.scala-lang.org/overviews/core/string-interpolation.html string interpolator]].
   *
   * Basic usage:
   *
   * {{{
   *   import play.twirl.api.StringInterpolation
   *
   *   val name = "Martin"
   *   val htmlFragment: Html = html"&lt;div&gt;Hello \$name&lt;/div&gt;"
   * }}}
   *
   * Three interpolators are available: `html`, `xml` and `js`.
   */
  implicit class StringInterpolation(val sc: StringContext) extends AnyVal {
    def html(args: FormatValue.Formatted[Html, HtmlFormat.type]*): Html = interpolate(args, HtmlFormat)

    def xml(args: FormatValue.Formatted[Xml, XmlFormat.type]*): Xml = interpolate(args, XmlFormat)

    def js(args: FormatValue.Formatted[JavaScript, JavaScriptFormat.type]*): JavaScript =
      interpolate(args, JavaScriptFormat)

    def interpolate[T <: Appendable[T]: ClassTag, F <: Format[T]](
        args: Seq[FormatValue.Formatted[T, F]],
        format: F
    ): T = {
      checkStringContextLengths(sc, args)
      val array       = Array.ofDim[T](args.size + sc.parts.size)
      val strings     = sc.parts.iterator
      val expressions = args.iterator
      array(0) = format.raw(strings.next())
      var i = 1
      while (strings.hasNext) {
        array(i) = expressions.next().formatted
        array(i + 1) = format.raw(strings.next())
        i += 2
      }
      format.fill(array.toIndexedSeq)
    }
  }
}
