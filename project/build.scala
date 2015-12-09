
import sbt.Keys._
import sbt._

object MoleculeBuild extends Build with Boilerplate with Publishing {

  lazy val molecule = Project(
    id = "molecule",
    base = file("."),
    aggregate = Seq(moleculeCore, moleculeCoretest, moleculeExamples),
    settings = commonSettings ++ Seq(
//      exportJars := true,
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
      definitionDirectories(
        "coretest/src/main/scala/molecule/part",
        "coretest/src/main/scala/molecule/util"
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

      definitionDirectories(
        "examples/src/main/scala/molecule/examples/dayOfDatomic",
        "examples/src/main/scala/molecule/examples/seattle",
        "examples/src/main/scala/molecule/examples/mbrainz",
        "examples/src/main/scala/molecule/examples/graph"
      ),
      publish :=(),
      publishLocal :=()

//    ,mappings in (Compile, packageSrc) ++= {
//      val allGeneratedFiles = ((sourceManaged in Compile).value ** "*") filter { _.isFile }
//        println("@@@@@@@@@@ allGeneratedFiles: " + allGeneratedFiles.toString)
//      allGeneratedFiles.get pair relativeTo((sourceManaged in Compile).value)
//    }
    )
  )

  lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
    organization := "org.scalamolecule",
    version := "0.5.0-SNAPSHOT",
    scalaVersion := "2.11.7",
    scalacOptions := Seq("-feature", "-language:implicitConversions", "-Yrangepos"),
    resolvers ++= Seq(
      "datomic" at "http://files.datomic.com/maven",
      "clojars" at "http://clojars.org/repo",
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots")
    ),
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "com.datomic" % "datomic-free" % "0.9.5327",
      "com.chuusai" %% "shapeless" % "2.0.0",
      "org.scalaz" %% "scalaz-core" % "7.1.3",
      "net.liftweb" %% "lift-json" % "3.0-M6",
      "org.specs2" %% "specs2" % "2.4.11" //% "test"
    ),
    // Remove Java directories
    unmanagedSourceDirectories in Compile <<= (scalaSource in Compile)(Seq(_)),
    unmanagedSourceDirectories in Test <<= (scalaSource in Test)(Seq(_))
  )
}

trait Boilerplate {

  def definitionDirectories(domainDirs: String*) = sourceGenerators in Compile += Def.task[Seq[File]] {
    val sourceDir = (sourceManaged in Compile).value

    // generate source files
    val sourceFiles = MoleculeBoilerplate.generate(sourceDir, domainDirs.toSeq)

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
        <url>git@github.com:scalamolecule/molecule.git</url>
        <connection>scm:git:git@github.com:scalamolecule/molecule.git</connection>
      </scm>
      <developers>
        <developer>
          <id>marcgrue</id>
          <name>Marc Grue</name>
          <url>http://marcgrue.com</url>
        </developer>
      </developers>
}