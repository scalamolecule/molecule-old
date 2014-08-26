import sbt.Keys._
import sbt._


object MoleculeBuild extends Build with Boilerplate with Publishing {

  lazy val molecule = Project(
    id = "molecule",
    base = file("."),
    aggregate = Seq(moleculeCore, moleculeCoretest, moleculeDemo, moleculeExamples),
    settings = commonSettings ++ Seq(
      moduleName := "molecule-root",
      publish :=(),
      publishLocal :=()
    )
  )

  lazy val moleculeCore = Project(
    id = "molecule-core",
    base = file("core"),
    settings = commonSettings ++ publishSettings ++ Seq(
      moduleName := "molecule"
    )
  )

  lazy val moleculeCoretest = Project(
    id = "molecule-coretest",
    base = file("coretest"),
    dependencies = Seq(moleculeCore),
    settings = commonSettings ++ Seq(
      moleculeDefinitionDirectories(
//        "coretest/src/main/scala/molecule/types"
      ),
      publish :=(),
      publishLocal :=()
    )
  )

  lazy val moleculeDemo = Project(
    id = "molecule-demo",
    base = file("demo"),
    dependencies = Seq(moleculeCore),
    settings = commonSettings ++ Seq(
      moleculeDefinitionDirectories(
        "demo/src/main/scala/demo"
      ),
      publish :=(),
      publishLocal :=()
    )
  )

  lazy val moleculeExamples = Project(
    id = "molecule-examples",
    base = file("examples"),
    dependencies = Seq(moleculeCore),
    settings = commonSettings ++ Seq(
      moleculeDefinitionDirectories(
        "examples/src/main/scala/molecule/examples/dayOfDatomic",
        "examples/src/main/scala/molecule/examples/seattle"
      ),
      publish :=(),
      publishLocal :=()
    )
  )

  lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
    organization := "com.marcgrue",
    version := "0.1.2",
    scalaVersion := "2.11.1",
    scalacOptions := Seq("-feature", "-language:implicitConversions"),
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
}

trait Boilerplate {

  def moleculeDefinitionDirectories(inputDirs: String*) = sourceGenerators in Compile += Def.task[Seq[File]] {
    val sourceDir = (sourceManaged in Compile).value

    // generate source files
    val sourceFiles = DslBoilerplate.generate(sourceDir, inputDirs.toSeq)

    // Avoid re-generating boilerplate if nothing has changed when running `sbt compile`
    val cache = FileFunction.cached(
      streams.value.cacheDirectory / "moleculeBoilerplate",
      inStyle = FilesInfo.lastModified,
      outStyle = FilesInfo.hash
    ) {
      in: Set[File] => sourceFiles.toSet
    }
    cache(sourceFiles.toSet).toSeq
  }.taskValue

//  // Format file data for jar creation - if we want to jar the generated class files for speedier project rebuilds during development...
//  def files2TupleRec(pathPrefix: String, dir: File): Seq[Tuple2[File, String]] = {
//    sbt.IO.listFiles(dir) flatMap {
//      f => {
//        if (f.isFile && !f.name.endsWith(".DS_Store") && (f.name.endsWith(".scala") || f.name.endsWith(".class")))
//          Seq((f, s"${pathPrefix}${f.getName}"))
//        else
//          files2TupleRec(s"${pathPrefix}${f.getName}/", f)
//      }
//    }
//  }
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
    <url>https://github.com/marcgrue/molecule</url>
      <licenses>
        <license>
          <name>Apache License</name>
          <url>http://www.apache.org/licenses/</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:marcgrue/molecule.git</url>
        <connection>scm:git:git@github.com:marcgrue/molecule.git</connection>
      </scm>
      <developers>
        <developer>
          <id>marcgrue</id>
          <name>Marc Grue</name>
          <url>http://marcgrue.com</url>
        </developer>
      </developers>
}