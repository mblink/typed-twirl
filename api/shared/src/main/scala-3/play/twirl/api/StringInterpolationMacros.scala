/*
 * Copyright (C) from 2025 BondLink, 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.twirl.api

import scala.compiletime.summonInline
import scala.quoted.*

private[api] object StringInterpolationMacros {
  private def interpolateImpl[T: Type, F <: Format[T]: Type](
      formatExpr: Expr[F],
      scExpr: Expr[StringContext],
      argsExpr: Expr[Seq[Any]],
  )(using q: Quotes): Expr[T] = {
    import q.reflect.*

    val staticStrs0: Seq[String]                              = scExpr.valueOrAbort.parts
    val (staticStrs, staticStrsSize): (Iterator[String], Int) = (staticStrs0.iterator, staticStrs0.size)
    val (args, argsSize): (Iterator[Expr[Any]], Int) = argsExpr match {
      case Varargs(s) => (s.iterator, s.size)
      case _          => report.errorAndAbort("Unexpected string interpolation invocation")
    }

    var i               = 1
    val arr             = Array.ofDim[Expr[T]](staticStrsSize + argsSize)
    val formatStaticStr = () => '{ $formatExpr.raw(${ Expr(staticStrs.next) }) }

    arr(0) = formatStaticStr()

    while staticStrs.hasNext do {
      val formattedArg = args.next match { case '{ $x: x } => '{ summonInline[FormatValue[T, F, x]]($formatExpr, $x) } }
      val formattedStaticStr = formatStaticStr()

      arr(i) = formattedArg
      arr(i + 1) = formattedStaticStr
      i += 2
    }

    '{ $formatExpr.fill(${ Expr.ofSeq(arr.toSeq) }) }
  }

  def htmlImpl(scExpr: Expr[StringContext], argsExpr: Expr[Seq[Any]])(using q: Quotes): Expr[Html] =
    interpolateImpl('{ HtmlFormat }, scExpr, argsExpr)

  def xmlImpl(scExpr: Expr[StringContext], argsExpr: Expr[Seq[Any]])(using q: Quotes): Expr[Xml] =
    interpolateImpl('{ XmlFormat }, scExpr, argsExpr)

  def jsImpl(scExpr: Expr[StringContext], argsExpr: Expr[Seq[Any]])(using q: Quotes): Expr[JavaScript] =
    interpolateImpl('{ JavaScriptFormat }, scExpr, argsExpr)
}
