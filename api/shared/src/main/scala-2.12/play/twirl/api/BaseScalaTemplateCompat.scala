/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.twirl.api

import scala.collection.JavaConverters._

private[api] trait BaseScalaTemplateCompat {
  final type IterableOnce[A] = TraversableOnce[A]
  final def mapIterableOnce[A, B](i: IterableOnce[A])(f: A => B): List[B] = i.map(f).toList

  final def javaListToScala[A](l: java.util.List[A]): scala.collection.mutable.Buffer[A] = l.asScala
}
