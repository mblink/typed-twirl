/*
 * Copyright (C) from 2025 BondLink, 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.twirl.api

import scala.collection.immutable

private[api] trait FormatValueInstancesCompat {
  final implicit def traversableOnceFormatValue[T, F <: Format[T], V](implicit
      vFormatValue: FormatValue[T, F, V]
  ): FormatValue[T, F, TraversableOnce[V]] =
    FormatValue.instance((format: F, v: TraversableOnce[V]) =>
      v match {
        case s: immutable.Seq[?] => format.fill(s.map(vFormatValue(format, _)))
        case _                   => format.fill(v.map(vFormatValue(format, _)).toList)
      }
    )

  final implicit def traversableOnceNothingFormatValue[T, F <: Format[T]]: FormatValue[T, F, TraversableOnce[Nothing]] =
    FormatValue.instance((format: F, _: TraversableOnce[Nothing]) => format.empty)

  final implicit def optionFormatValue[T, F <: Format[T], V](implicit
      vFormatValue: FormatValue[T, F, V]
  ): FormatValue[T, F, Option[V]] =
    FormatValue.instance((format: F, o: Option[V]) => o.fold(format.empty)(vFormatValue(format, _)))

  final implicit def noneFormatValue[T, F <: Format[T]]: FormatValue[T, F, None.type] =
    FormatValue.instance((format: F, _: None.type) => format.empty)
}
