/*
 * Copyright (C) from 2025 BondLink, 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.twirl.api

import scala.language.implicitConversions

/**
 * Imports that provide Twirl language features.
 *
 * This includes:
 *
 *   - \@defining
 *   - \@using
 *   - iterable/option/string as boolean for if statements
 *   - default values (maybeFoo ? defaultFoo)
 */
object TwirlFeatureImports {

  /**
   * Provides the `@defining` language feature, that lets you set a local val that can be reused.
   *
   * @param a
   *   The defined val.
   * @param handler
   *   The block to handle it.
   */
  def defining[A, B](a: A)(handler: A => B): B = handler(a)

  /** Provides the `@using` language feature. */
  def using[T](t: T): T = t

  /** Adds "truthiness" to iterables, making them false if they are empty. */
  implicit def twirlIterableToBoolean(x: Iterable[?]): Boolean = x != null && !x.isEmpty

  /** Adds "truthiness" to options, making them false if they are empty. */
  implicit def twirlOptionToBoolean(x: Option[?]): Boolean = x != null && x.isDefined

  /** Adds "truthiness" to strings, making them false if they are empty. */
  implicit def twirlStringToBoolean(x: String): Boolean = x != null && !x.isEmpty

  /**
   * Provides default values, such that an empty sequence, string, option, false boolean, or null will render the
   * default value.
   */
  sealed class TwirlDefaultValue[A](default: A)(empty: A => Boolean) {
    final def ?:(a: A): A = if (empty(a)) default else a
  }

  implicit class TwirlBooleanDefaultValue(default: Boolean)     extends TwirlDefaultValue(default)(_ == false)
  implicit class TwirlIntDefaultValue(default: Int)             extends TwirlDefaultValue(default)(_ == 0)
  implicit class TwirlStringDefaultValue(default: String)       extends TwirlDefaultValue(default)(_ == "")
  implicit class TwirlListDefaultValue[A](default: List[A])     extends TwirlDefaultValue(default)(_.isEmpty)
  implicit class TwirlSeqDefaultValue[A](default: Seq[A])       extends TwirlDefaultValue(default)(_.isEmpty)
  implicit class TwirlOptionDefaultValue[A](default: Option[A]) extends TwirlDefaultValue(default)(_.isEmpty)
}
