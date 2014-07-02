import sbt.Keys._
import sbt._


object build extends Build with BuildSettings {
  lazy val root                = project in file(".") settings (_root: _*) aggregate (molecule, `molecule-examples`)
  lazy val molecule            = project in file("core") settings (_core: _*)
  lazy val `molecule-examples` = project in file("examples") settings (_examples: _*) dependsOn molecule
}

trait BuildSettings extends Publishing {
  val commonSettings = Defaults.defaultSettings ++ Seq(
    organization := "org.scaladatomic",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.11.1",
    scalacOptions := Seq(
      "-feature",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-Xfatal-warnings",
      "-deprecation",
      "-unchecked"),
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots"),
      "datomic" at "http://files.datomic.com/maven",
      "clojars" at "http://clojars.org/repo"
    ),
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "com.datomic" % "datomic-free" % "0.9.4766.11",
      "com.chuusai" %% "shapeless" % "2.0.0",
      "org.specs2" %% "specs2" % "2.3.11" % "test"
    )
  )
  lazy val _root     = commonSettings :+ (packagedArtifacts := Map.empty)
  lazy val _core     = commonSettings
  lazy val _examples = commonSettings ++ Seq(
    packagedArtifacts := Map.empty,
    sourceGenerators in Compile <+= sourceManaged in Compile map { srcDir =>
      DslBoilerplate.generate(srcDir, Seq(
        "examples/src/main/scala/molecule/examples/seattle"
      ))
    }
  )
}

trait Publishing {
  lazy val snapshots = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
  lazy val releases  = "Sonatype OSS Staging" at "https://oss.sonatype.org/service/local/staging/deploy/maven2/"

  lazy val publishSettings = Seq(
    publishMavenStyle := true,
    publishTo <<= version((v: String) => Some(if (v.trim endsWith "SNAPSHOT") snapshots else releases)),
    publishArtifact in Test := false,
    pomIncludeRepository := (_ => false),
    pomExtra := projectPomExtra
  )

  lazy val projectPomExtra =
    <url>https://github.com/scaladatomic/molecule</url>
      <licenses>
        <license>
          <name>Apache License</name>
          <url>http://www.apache.org/licenses/</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:scaladatomic/molecule.git</url>
        <connection>scm:git:git@github.com:scaladatomic/molecule.git</connection>
      </scm>
      <developers>
        <developer>
          <id>marcgrue</id>
          <name>Marc Grue</name>
          <url>http://marcgrue.com</url>
        </developer>
      </developers>
}