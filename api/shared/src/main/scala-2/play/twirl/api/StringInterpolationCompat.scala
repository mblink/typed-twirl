/*
 * Copyright (C) from 2025 BondLink, 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.twirl.api

import scala.language.experimental.macros
import scala.language.implicitConversions

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
final class StringInterpolationOps(private val sc: StringContext) extends AnyVal {
  final def html(args: Any*): Html = macro StringInterpolationMacros.htmlImpl
  final def xml(args: Any*): Xml = macro StringInterpolationMacros.xmlImpl
  final def js(args: Any*): JavaScript = macro StringInterpolationMacros.jsImpl
}

private[api] trait StringInterpolationCompat {
  @inline final implicit def toStringInterpolationOps(sc: StringContext): StringInterpolationOps =
    new StringInterpolationOps(sc)
}
