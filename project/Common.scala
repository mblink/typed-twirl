/*
 * Copyright (C) from 2025 BondLink, 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

import sbt.Keys._

import sbt._
import sbt.plugins.JvmPlugin
import Dependencies._

object Common extends AutoPlugin {

  override def trigger = noTrigger

  override def requires = JvmPlugin

  val repoName = "typed-twirl"

  val javacParameters = Seq(
    "-encoding",
    "UTF-8",
    "-Xlint:-options",
    "--release",
    "17",
    "-Xlint:deprecation",
    "-Xlint:unchecked"
  )

  val scalacParameters = Seq(
    "-deprecation",
    "-feature",
    "-unchecked",
    "-encoding",
    "utf8"
  )

  def crossScalacOptions(version: String) = {
    CrossVersion.partialVersion(version) match {
      case Some((2, n)) if n < 12 =>
        scalacParameters ++ Seq(
          "-release:17",
          "-Ywarn-unused:imports",
          "-Xlint:nullary-unit",
          "-Xlint",
          "-Ywarn-dead-code"
        )
      case _ => scalacParameters
    }
  }

  override def projectSettings =
    Seq(
      scalaVersion       := Scala212,
      crossScalaVersions := ScalaVersions,
      scalacOptions ++= crossScalacOptions(scalaVersion.value),
      javacOptions ++= javacParameters
    )

  override def globalSettings =
    Seq(
      version      := "0.1.0-SNAPSHOT",
      organization := "bondlink",
      homepage     := Some(url(s"https://github.com/mblink/$repoName")),
      licenses     := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")),
      developers += Developer(
        "playframework",
        "The Play Framework Contributors",
        "contact@playframework.com",
        url("https://github.com/playframework")
      ),
      description := "Twirl"
    )

}
