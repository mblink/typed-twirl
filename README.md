<!--- Copyright (C) from 2025 BondLink, 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com> -->

# Typed Twirl

Twirl is the [Play][play-site] [template engine][docs]. See the Play [documentation for the template engine][docs] for
more information about the template syntax.

Typed Twirl is a variant that limits the types of values that can be rendered in a template to only those for which
there exists an instance of the [`FormatValue` typeclass](api/shared/src/main/scala/play/twirl/api/FormatValue.scala).

The motivation is to eliminate the chance of accidentally rendering a value using it's `toString` representation.
For example, using `cats.data.NonEmptyList`, it's easy to forget to call `.toList` and end up with this:

```scala
<ul>
  @NonEmptyList.of(1, 2, 3).map { i => <li>@i</li> }
</ul>

// what you intended to render
<ul><li>1</li><li>2</li><li>3</li></ul>
// what you actually get
<ul>NonEmptyList(&lt;li&gt;1&lt;/li&gt;, &lt;li&gt;2&lt;/li&gt;, &lt;li&gt;3&lt;/li&gt;)</ul>
```

## sbt-typed-twirl

Twirl can also be used outside of Play. An sbt plugin is provided for easy
integration with Scala or Java projects.

> sbt-typed-twirl requires sbt 1.3.0 or higher.

To add the sbt plugin to your project add the sbt plugin dependency in
`project/plugins.sbt`:

```scala
resolvers += "bondlink-maven-repo" at "https://s3.amazonaws.com/bondlink-maven-repo"
addSbtPlugin("bondlink" % "sbt-typed-twirl" % "0.3.2")
```

```scala
someProject.enablePlugins(SbtTwirl)
```

If you only have a single project and are using a `build.sbt` file, create a
root project and enable the twirl plugin like this:

```scala
lazy val root = (project in file(".")).enablePlugins(SbtTwirl)
```

### Template files

Twirl template files are expected to be placed under `src/main/twirl` or
`src/test/twirl`, similar to `scala` or `java` sources. The source locations for
template files can be configured.

Template files must be named `{name}.scala.{ext}` where `ext` can be `html`,
`js`, `xml`, or `txt`.

The Twirl template compiler is automatically added as a source generator for
both the `main`/`compile` and `test` configurations. When you run `compile` or
`Test/compile` the Twirl compiler will generate Scala source files from the
templates and then these Scala sources will be compiled along with the rest of
your project.

### Additional imports

To add additional imports for the Scala code in template files, use the
`templateImports` key. For example:

```scala
TwirlKeys.templateImports += "org.example._"
```

### Source directories

To configure the source directories where template files will be found, use the
`compileTemplates / sourceDirectories` key. For example, to have template
sources alongside Scala or Java source files:

```scala
Compile / TwirlKeys.compileTemplates / sourceDirectories := (Compile / unmanagedSourceDirectories).value
```

## Credits

The name *twirl* was thought up by the [Spray team][spray] and refers to the
magic `@` character in the template language, which is sometimes called "twirl".

The first stand-alone version of Twirl was created by the [Spray team][spray].

An optimized version of the Twirl parser was contributed by the
[Scala IDE team][scala-ide].

[play-site]: https://www.playframework.com
[docs]: https://www.playframework.com/documentation/latest/ScalaTemplates
[spray]: https://github.com/spray
[scala-ide]: https://github.com/scala-ide
