// Copyright (C) from 2025 BondLink, 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>

lazy val root = (project in file(".")).enablePlugins {
  // Make sure scalajs plugin is not available
  val sjsPluginName = "org.scalajs.sbtplugin.ScalaJSPlugin"
  try Class.forName(sjsPluginName)
  catch {
    case _: ClassNotFoundException => // do nothing
    case _ => throw new IllegalStateException(s"Found $sjsPluginName, but scalajs should not be required!")
  }
  // Add the twirl plugin
  SbtTwirl
}
