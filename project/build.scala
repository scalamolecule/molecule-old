import sbt.Keys._
import sbt._

object build extends Build with BuildSettings {
  lazy val molecule            = project in file(".") settings (_root: _*) aggregate (`molecule-core`, `molecule-coretest`, `molecule-examples`)
  lazy val `molecule-core`     = project in file("core") settings (_core: _*)
  lazy val `molecule-coretest` = project in file("coretest") settings (_coretest: _*) dependsOn `molecule-core`
  lazy val `molecule-examples` = project in file("examples") settings (_examples: _*) dependsOn `molecule-core`
}

trait BuildSettings extends Boilerplate with Publishing {
  val commonSettings = Defaults.coreDefaultSettings ++ Seq(
    organization := "com.marcgrue",
    version := "0.1.0",
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
  lazy val _coretest = commonSettings ++ Seq(packagedArtifacts := Map.empty, boilerplate(
      "coretest/src/main/scala/molecule/types"
    ))
  lazy val _examples = commonSettings ++ Seq(packagedArtifacts := Map.empty, boilerplate(
    "examples/src/main/scala/molecule/examples/seattle"
  ))
}

trait Boilerplate {

  def boilerplate(inputDirs: String*) = sourceGenerators in Compile += Def.task[Seq[File]] {
    val sourceDir = (sourceManaged in Compile).value

    // generate source files
    val sourceFiles = DslBoilerplate.generate(sourceDir, inputDirs.toSeq)

    // Avoid re-generating boilerplate if nothing has changed when running `sbt compile`
    val cache = FileFunction.cached(
      streams.value.cacheDirectory / "filesCache",
      inStyle = FilesInfo.lastModified,
      outStyle = FilesInfo.hash
    ) {
      in: Set[File] => sourceFiles.toSet
    }
    cache(sourceFiles.toSet).toSeq
  }.taskValue

  // Format file data for jar creation
  def files2TupleRec(pathPrefix: String, dir: File): Seq[Tuple2[File, String]] = {
    sbt.IO.listFiles(dir) flatMap {
      f => {
        if (f.isFile && !f.name.endsWith(".DS_Store") && (f.name.endsWith(".scala") || f.name.endsWith(".class")))
          Seq((f, s"${pathPrefix}${f.getName}"))
        else
          files2TupleRec(s"${pathPrefix}${f.getName}/", f)
      }
    }
  }
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