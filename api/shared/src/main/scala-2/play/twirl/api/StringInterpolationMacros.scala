/*
 * Copyright (C) from 2025 BondLink, 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.twirl.api

import scala.reflect.macros.blackbox.Context

final class StringInterpolationMacros(val c: Context) {
  import c.universe._

  private lazy val staticStringExprs = c.prefix.tree match {
    case Apply(_, List(Apply(_, strs))) => strs
    case _                              => c.abort(c.enclosingPosition, "Unexpected string interpolation invocation")
  }

  private def interpolateImpl[T, F <: Format[T]](format: c.Expr[F], argExprs: Seq[c.Expr[Any]])(implicit
      tTypeTag: c.WeakTypeTag[T],
      fTypeTag: c.WeakTypeTag[F]
  ): c.Expr[T] = {
    val tType      = tTypeTag.tpe
    val fType      = fTypeTag.tpe
    val arr        = Array.ofDim[c.Expr[T]](argExprs.size + staticStringExprs.size)
    val staticStrs = staticStringExprs.iterator
    val args       = argExprs.iterator
    arr(0) = c.Expr[T](q"$format.raw(${staticStrs.next()})")
    var i = 1
    while (staticStrs.hasNext) {
      val arg = args.next()
      arr(i) = c.Expr[T](
        q"_root_.scala.Predef.implicitly[_root_.play.twirl.api.FormatValue[$tType, $fType, ${arg.tree.tpe}]].apply($format, $arg)"
      )
      arr(i + 1) = c.Expr[T](q"$format.raw(${staticStrs.next()})")
      i += 2
    }
    c.Expr(q"$format.fill(_root_.scala.collection.immutable.Seq(..${arr.toIndexedSeq}))")
  }

  final def htmlImpl(args: c.Expr[Any]*): c.Expr[Html] =
    interpolateImpl[Html, HtmlFormat.type](c.Expr[HtmlFormat.type](q"_root_.play.twirl.api.HtmlFormat"), args)

  final def xmlImpl(args: c.Expr[Any]*): c.Expr[Xml] =
    interpolateImpl[Xml, XmlFormat.type](c.Expr[XmlFormat.type](q"_root_.play.twirl.api.XmlFormat"), args)

  final def jsImpl(args: c.Expr[Any]*): c.Expr[JavaScript] =
    interpolateImpl[JavaScript, JavaScriptFormat.type](
      c.Expr[JavaScriptFormat.type](q"_root_.play.twirl.api.JavaScriptFormat"),
      args
    )
}
