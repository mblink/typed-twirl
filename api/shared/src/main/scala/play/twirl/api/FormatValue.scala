/*
 * Copyright (C) from 2025 BondLink, 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.twirl.api

import java.util.{ List => JList }
import java.util.Optional
import scala.collection.JavaConverters._

sealed trait FormatValue[T, F <: Format[T], -V] { self =>
  def apply(format: F, value: V): T

  final def contramap[B](f: B => V): FormatValue[T, F, B] =
    FormatValue.instance((format: F, value: B) => self(format, f(value)))
}

object FormatValue extends FormatValueInstances0 {
  class Companion[T, F <: Format[T]] {
    @inline final def apply[V](implicit f: FormatValue[T, F, V]): FormatValue[T, F, V] = f

    final def instance[V](f: (F, V) => T): FormatValue[T, F, V] = FormatValue.instance(f)
  }

  @inline final def apply[T, F <: Format[T], V](implicit
      f: FormatValue[T, F, V]
  ): FormatValue[T, F, V] = f

  def instance[T, F <: Format[T], V](f: (F, V) => T): FormatValue[T, F, V] =
    new FormatValue[T, F, V] {
      def apply(format: F, value: V): T =
        value match {
          case null => format.empty
          case _    => f(format, value)
        }
    }
}

sealed trait FormatValueInstances0 extends FormatValueInstances1 {
  final implicit def tFormatValue[T, F <: Format[T]]: FormatValue[T, F, T] =
    FormatValue.instance((_: F, t: T) => t)

  final implicit def unitFormatValue[T, F <: Format[T]]: FormatValue[T, F, Unit] =
    FormatValue.instance((format: F, _: Unit) => format.empty)

  final implicit def byteFormatValue[T, F <: Format[T]]: FormatValue[T, F, Byte] =
    FormatValue.instance((format: F, byte: Byte) => format.escape(byte.toString))

  final implicit def javaByteFormatValue[T, F <: Format[T]]: FormatValue[T, F, java.lang.Byte] =
    FormatValue.instance((format: F, byte: java.lang.Byte) => format.escape(byte.toString))

  final implicit def booleanFormatValue[T, F <: Format[T]]: FormatValue[T, F, Boolean] =
    FormatValue.instance((format: F, bool: Boolean) => format.escape(bool.toString))

  final implicit def javaBooleanFormatValue[T, F <: Format[T]]: FormatValue[T, F, java.lang.Boolean] =
    FormatValue.instance((format: F, bool: java.lang.Boolean) => format.escape(bool.toString))

  final implicit def charFormatValue[T, F <: Format[T]]: FormatValue[T, F, Char] =
    FormatValue.instance((format: F, char: Char) => format.escape(char.toString))

  final implicit def javaCharacterFormatValue[T, F <: Format[T]]: FormatValue[T, F, java.lang.Character] =
    FormatValue.instance((format: F, char: java.lang.Character) => format.escape(char.toString))

  final implicit def doubleFormatValue[T, F <: Format[T]]: FormatValue[T, F, Double] =
    FormatValue.instance((format: F, dbl: Double) => format.escape(dbl.toString))

  final implicit def javaDoubleFormatValue[T, F <: Format[T]]: FormatValue[T, F, java.lang.Double] =
    FormatValue.instance((format: F, dbl: java.lang.Double) => format.escape(dbl.toString))

  final implicit def floatFormatValue[T, F <: Format[T]]: FormatValue[T, F, Float] =
    FormatValue.instance((format: F, float: Float) => format.escape(float.toString))

  final implicit def javaFloatFormatValue[T, F <: Format[T]]: FormatValue[T, F, java.lang.Float] =
    FormatValue.instance((format: F, float: java.lang.Float) => format.escape(float.toString))

  final implicit def intFormatValue[T, F <: Format[T]]: FormatValue[T, F, Int] =
    FormatValue.instance((format: F, int: Int) => format.escape(int.toString))

  final implicit def javaIntegerFormatValue[T, F <: Format[T]]: FormatValue[T, F, java.lang.Integer] =
    FormatValue.instance((format: F, int: java.lang.Integer) => format.escape(int.toString))

  final implicit def longFormatValue[T, F <: Format[T]]: FormatValue[T, F, Long] =
    FormatValue.instance((format: F, long: Long) => format.escape(long.toString))

  final implicit def javaLongFormatValue[T, F <: Format[T]]: FormatValue[T, F, java.lang.Long] =
    FormatValue.instance((format: F, long: java.lang.Long) => format.escape(long.toString))

  final implicit def shortFormatValue[T, F <: Format[T]]: FormatValue[T, F, Short] =
    FormatValue.instance((format: F, short: Short) => format.escape(short.toString))

  final implicit def javaShortFormatValue[T, F <: Format[T]]: FormatValue[T, F, java.lang.Short] =
    FormatValue.instance((format: F, short: java.lang.Short) => format.escape(short.toString))

  final implicit def stringFormatValue[T, F <: Format[T]]: FormatValue[T, F, String] =
    FormatValue.instance((format: F, string: String) => format.escape(string))

  final implicit def javaFileFormatValue[T, F <: Format[T]]: FormatValue[T, F, java.io.File] =
    FormatValue.instance((format: F, file: java.io.File) => format.escape(file.toString))

  final implicit def javaURLFormatValue[T, F <: Format[T]]: FormatValue[T, F, java.net.URL] =
    FormatValue.instance((format: F, url: java.net.URL) => format.escape(url.toString))

  final implicit def javaUUIDFormatValue[T, F <: Format[T]]: FormatValue[T, F, java.util.UUID] =
    FormatValue.instance((format: F, uuid: java.util.UUID) => format.escape(uuid.toString))

  final implicit def xmlNodeSeqFormatValue[T, F <: Format[T]]: FormatValue[T, F, scala.xml.NodeSeq] =
    FormatValue.instance((format: F, xml: scala.xml.NodeSeq) => format.raw(xml.toString))
}

sealed trait FormatValueInstances1 extends FormatValueInstancesCompat {
  final implicit def arrayFormatValue[T, F <: Format[T], V](implicit
      vFormatValue: FormatValue[T, F, V]
  ): FormatValue[T, F, Array[V]] =
    FormatValue.instance((format: F, arr: Array[V]) => format.fill(arr.view.map(vFormatValue(format, _)).toList))

  final implicit def javaListFormatValue[T, F <: Format[T], V](implicit
      vFormatValue: FormatValue[T, F, V]
  ): FormatValue[T, F, JList[V]] =
    FormatValue.instance((format: F, list: JList[V]) => format.fill(list.asScala.map(vFormatValue(format, _)).toList))

  final implicit def optionalFormatValue[T, F <: Format[T], V](implicit
      optionVFormatValue: FormatValue[T, F, Option[V]]
  ): FormatValue[T, F, Optional[V]] =
    FormatValue.instance((format: F, option: Optional[V]) =>
      optionVFormatValue(format, if (option.isPresent) Option(option.get) else None)
    )
}
