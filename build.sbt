// Copyright (C) from 2025 BondLink, 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>

import Dependencies._

import sbtcrossproject.CrossPlugin.autoImport.crossProject
import org.scalajs.jsenv.nodejs.NodeJSEnv
import java.util.Properties
import java.io.StringWriter

Global / onChangedBuildSource := ReloadOnSourceChanges

val ScalaTestVersion = "3.2.19"

def parserCombinators(scalaVersion: String) = "org.scala-lang.modules" %% "scala-parser-combinators" % {
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, 12)) => "1.1.2"
    case _             => "2.4.0"
  }
}

ThisBuild / publishTo := Some("BondLink S3".at("s3://bondlink-maven-repo"))

lazy val twirl = project
  .in(file("."))
  .settings(
    crossScalaVersions := Nil, // workaround so + uses project-defined variants
    publish / skip     := true,
    (Compile / headerSources) ++=
      ((baseDirectory.value ** ("*.properties" || "*.md" || "*.sbt" || "*.scala.html"))
        --- (baseDirectory.value ** "target" ** "*")
        --- (baseDirectory.value / "compiler" / "version.properties")).get ++
        (baseDirectory.value / "project" ** "*.scala" --- (baseDirectory.value ** "target" ** "*")).get
  )
  .aggregate(apiJvm, apiJs, parser, compiler, plugin)

lazy val nodeJs = {
  if (System.getProperty("NODE_PATH") != null)
    new NodeJSEnv(NodeJSEnv.Config().withExecutable(System.getProperty("NODE_PATH")))
  else
    new NodeJSEnv()
}

lazy val api = crossProject(JVMPlatform, JSPlatform)
  .in(file("api"))
  .enablePlugins(Common)
  .settings(
    name  := "typed-twirl-api",
    jsEnv := nodeJs,
    // hack for GraalVM, see: https://github.com/scala-js/scala-js/issues/3673
    // and https://github.com/playframework/twirl/pull/339
    testFrameworks := List(
      new TestFramework(
        "org.scalatest.tools.Framework",
        "org.scalatest.tools.ScalaTestFramework"
      )
    ),
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %%% "scala-xml" % "2.3.0",
      "org.scalatest"          %%% "scalatest" % ScalaTestVersion % Test,
    ) ++ (scalaVersion.value match {
      case v @ (Scala212 | Scala213) => Seq("org.scala-lang" % "scala-reflect" % v % "provided")
      case Scala33                   => Seq()
    })
  )

lazy val apiJvm = api.jvm
lazy val apiJs  = api.js

lazy val parser = project
  .in(file("parser"))
  .enablePlugins(Common)
  .settings(
    name := "typed-twirl-parser",
    libraryDependencies += parserCombinators(scalaVersion.value),
    libraryDependencies += "com.github.sbt"  % "junit-interface" % "0.13.3"         % Test,
    libraryDependencies += "org.scalatest" %%% "scalatest"       % ScalaTestVersion % Test,
  )

lazy val compiler = project
  .in(file("compiler"))
  .enablePlugins(Common, BuildInfoPlugin)
  .settings(
    name := "typed-twirl-compiler",
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, _)) =>
          // only for scala < 3
          Seq("org.scala-lang" % "scala-compiler" % scalaVersion.value % Test)
        case _ => Seq("org.scala-lang" %% "scala3-compiler" % scalaVersion.value % Test)
      }
    },
    libraryDependencies += parserCombinators(scalaVersion.value),
    libraryDependencies += "org.scalameta" %% "parsers" % "4.13.4",
    run / fork                             := true,
    buildInfoKeys                          := Seq[BuildInfoKey](scalaVersion),
    buildInfoPackage                       := "play.twirl.compiler",
    publish                                := publish.dependsOn(saveCompilerVersion).value,
    publishLocal                           := publishLocal.dependsOn(saveCompilerVersion).value
  )
  .aggregate(parser)
  .dependsOn(apiJvm % Test, parser % "compile->compile;test->test")

lazy val plugin = project
  .in(file("sbt-twirl"))
  .enablePlugins(SbtPlugin)
  .dependsOn(compiler)
  .settings(
    name                                    := "sbt-typed-twirl",
    organization                            := "bondlink",
    scalaVersion                            := Scala212,
    crossScalaVersions                      := Seq(Scala212, Scala36),
    libraryDependencies += "org.scalatest" %%% "scalatest" % ScalaTestVersion % Test,
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "2.12" =>
          sbtVersion.value
        case _ =>
          "2.0.0-M4"
      }
    },
    Compile / resourceGenerators += generateVersionFile.taskValue,
    scriptedLaunchOpts += version.apply { v => s"-Dproject.version=$v" }.value,
    // both `locally`s are to work around sbt/sbt#6161
    scriptedDependencies := {
      locally { val _ = scriptedDependencies.value }
      locally {
        val _ = publishLocal
          .all(
            ScopeFilter(
              inAnyProject
            )
          )
          .value
      }
      ()
    },
  )

// Version file
def generateVersionFile =
  Def.task {
    val version = (apiJvm / Keys.version).value
    val file    = (Compile / resourceManaged).value / "twirl.version.properties"
    val content = s"twirl.api.version=$version"
    IO.write(file, content)
    Seq(file)
  }

def saveCompilerVersion =
  Def.task {
    val props  = new Properties()
    val writer = new StringWriter()
    val file   = baseDirectory.value / "version.properties"
    props.setProperty("twirl.compiler.version", version.value)
    props.store(writer, "")
    IO.write(file, writer.getBuffer.toString)
    Seq(file)
  }

addCommandAlias(
  "validateCode",
  List(
    "headerCheckAll",
    "scalafmtSbtCheck",
    "scalafmtCheckAll",
    "javafmtCheckAll",
  ).mkString(";")
)

addCommandAlias(
  "format",
  List(
    "scalafmtSbt",
    "scalafmtAll",
    "javafmtAll",
  ).mkString(";")
)
