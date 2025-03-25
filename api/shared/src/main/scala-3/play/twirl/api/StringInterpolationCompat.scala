/*
 * Copyright (C) from 2025 BondLink, 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.twirl.api

private[api] trait StringInterpolationCompat { self =>

  /**
   * This and `given StringInterpolation below` are a hack to let both of these styles of imports work as they do in
   * Scala 2:
   *
   * {{{
   * import play.twirl.api.*
   * import play.twirl.api.StringInterpolation
   * }}}
   */
  extension (inline sc: StringContext) {
    inline final def html(inline args: Any*): Html     = ${ StringInterpolationMacros.htmlImpl('sc, 'args) }
    inline final def xml(inline args: Any*): Xml       = ${ StringInterpolationMacros.xmlImpl('sc, 'args) }
    inline final def js(inline args: Any*): JavaScript = ${ StringInterpolationMacros.jsImpl('sc, 'args) }
  }

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
  given StringInterpolation: {} with {
    export self.html
    export self.xml
    export self.js
  }
}
