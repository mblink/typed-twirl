/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.twirl.api

import scala.collection.immutable
import scala.collection.JavaConverters._

private[api] final class ValueOf[A](val value: A) extends AnyVal

object ValueOf {
  implicit def fromShapelessWitness[A](implicit w: shapeless.Witness.Aux[A]): ValueOf[A] = new ValueOf(w.value)
}

private[api] trait Compat {
  final def javaListToScala[A](l: java.util.List[A]): scala.collection.mutable.Buffer[A] = l.asScala

  final def checkStringContextLengths(sc: StringContext, args: scala.collection.Seq[Any]): Unit =
    sc.checkLengths(args)
}

private[api] trait FormatValueInstancesCompat {
  final implicit def traversableOnceFormatValue[T <: Appendable[T], F <: Format[T], V](implicit
      vFormatValue: FormatValue[T, F, V]
  ): FormatValue[T, F, TraversableOnce[V]] =
    FormatValue.instance((format: F, v: TraversableOnce[V]) =>
      v match {
        case s: immutable.Seq[?] => format.fill(s.map(vFormatValue(format, _)))
        case _                   => format.fill(v.map(vFormatValue(format, _)).toList)
      }
    )

  final implicit def traversableOnceNothingFormatValue[T <: Appendable[T], F <: Format[T]]
      : FormatValue[T, F, TraversableOnce[Nothing]] =
    FormatValue.instance((format: F, _: TraversableOnce[Nothing]) => format.empty)

  final implicit def optionFormatValue[T <: Appendable[T], F <: Format[T], V](implicit
      vFormatValue: FormatValue[T, F, V]
  ): FormatValue[T, F, Option[V]] =
    FormatValue.instance((format: F, o: Option[V]) => o.fold(format.empty)(vFormatValue(format, _)))

  final implicit def noneFormatValue[T <: Appendable[T], F <: Format[T]]: FormatValue[T, F, None.type] =
    FormatValue.instance((format: F, _: None.type) => format.empty)
}
