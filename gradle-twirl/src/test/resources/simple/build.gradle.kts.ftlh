plugins {
  application
  id("bondlink")
}

repositories {
  mavenCentral()
  mavenLocal()
}

dependencies {
  implementation("bondlink:twirl-api_${scalaVersion}:${twirlVersion}")
}

twirl {
  scalaVersion.set("${scalaVersion}")
}

sourceSets {
  main {
    twirl {
      templateImports.add("java.lang._")
      templateImports.add("a.b.%format%._")
      constructorAnnotations.add("@java.lang.Deprecated()")
    }
  }
}
