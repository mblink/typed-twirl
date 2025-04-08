/*
 * Copyright (C) from 2025 BondLink, 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.twirl

package object api extends StringInterpolationCompat {
  type FormatHtml[-V] = FormatValue[Html, HtmlFormat.type, V]
  object FormatHtml extends FormatValue.Companion[Html, HtmlFormat.type]

  type FormatTxt[-V] = FormatValue[Txt, TxtFormat.type, V]
  object FormatTxt extends FormatValue.Companion[Txt, TxtFormat.type]

  type FormatXml[-V] = FormatValue[Xml, XmlFormat.type, V]
  object FormatXml extends FormatValue.Companion[Xml, XmlFormat.type]

  type FormatJavaScript[-V] = FormatValue[JavaScript, JavaScriptFormat.type, V]
  object FormatJavaScript extends FormatValue.Companion[JavaScript, JavaScriptFormat.type]
}
