/*
 * Copyright (C) from 2025 BondLink, 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.twirl.api

import scala.collection.immutable

private[api] trait FormatValueInstancesCompat {
  final implicit def iterableOnceFormatValue[T, F <: Format[T], V](implicit
      vFormatValue: FormatValue[T, F, V]
  ): FormatValue[T, F, IterableOnce[V]] =
    FormatValue.instance((format: F, iterable: IterableOnce[V]) =>
      iterable match {
        case s: immutable.Seq[V] => format.fill(s.map(vFormatValue(format, _)))
        case None                => format.empty
        case Some(v)             => vFormatValue(format, v)
        case i                   => format.fill(i.iterator.map(vFormatValue(format, _)).toList)
      }
    )

  final implicit def iterableOnceNothingFormatValue[T, F <: Format[T]]: FormatValue[T, F, IterableOnce[Nothing]] =
    FormatValue.instance((format: F, _: IterableOnce[Nothing]) => format.empty)
}
