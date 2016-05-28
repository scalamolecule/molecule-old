

lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
  organization := "org.scalamolecule",
  version := "0.6.2",
  scalaVersion := "2.11.8",
  scalacOptions := Seq("-feature", "-language:implicitConversions", "-Yrangepos"),
  resolvers ++= Seq(
    "datomic" at "http://files.datomic.com/maven",
    "clojars" at "http://clojars.org/repo",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
  ),
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "com.datomic" % "datomic-pro" % "0.9.5359",
    "org.specs2" %% "specs2" % "2.4.11"
  ),
  // Remove Java directories
  unmanagedSourceDirectories in Compile <<= (scalaSource in Compile) (Seq(_)),
  unmanagedSourceDirectories in Test <<= (scalaSource in Test) (Seq(_))
)


lazy val molecule = project.in(file("."))
  .settings(moduleName := "molecule-root")
  .aggregate(moleculeCore, moleculeCoretest, moleculeExamples)
  .settings(commonSettings ++ noPublishSettings)


lazy val moleculeCore = project.in(file("core"))
  .settings(moduleName := "molecule")
  .settings(commonSettings ++ publishSettings)


lazy val moleculeCoretest = project.in(file("coretest"))
  .settings(moduleName := "molecule-coretest")
  .dependsOn(moleculeCore)
  .settings(commonSettings ++ noPublishSettings)

  // Add schema definition directories
  .settings(Seq(definitionDirectories(
  "coretest/src/main/scala/molecule/part",
  "coretest/src/main/scala/molecule/util"
)))

lazy val moleculeExamples = project.in(file("examples"))
  .settings(moduleName := "molecule-examples")
  .dependsOn(moleculeCore)
  .settings(commonSettings ++ noPublishSettings)

  // Add schema definition directories
  .settings(Seq(definitionDirectories(
    "examples/src/main/scala/molecule/examples/dayOfDatomic",
    "examples/src/main/scala/molecule/examples/seattle",
    "examples/src/main/scala/molecule/examples/mbrainz",
    "examples/src/main/scala/molecule/examples/graph"
  )))


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


lazy val snapshots = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
lazy val releases = "Sonatype OSS Staging" at "https://oss.sonatype.org/service/local/staging/deploy/maven2/"

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo <<= version((v: String) => Some(if (v.trim endsWith "SNAPSHOT") snapshots else releases)),
  publishArtifact in Test := false,
  pomIncludeRepository := (_ => false),
  homepage := Some(url("https://github.com/marcgrue/molecule")),
  licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  scmInfo := Some(ScmInfo(url("https://github.com/scalamolecule/molecule"), "scm:git:git@github.com:scalamolecule/molecule.git")),
  pomExtra := (
    <developers>
      <developer>
        <id>marcgrue</id>
        <name>Marc Grue</name>
        <url>http://marcgrue.com</url>
      </developer>
    </developers>
    )
)

lazy val noPublishSettings = Seq(
  publish :=(),
  publishLocal :=(),
  publishArtifact := false
)