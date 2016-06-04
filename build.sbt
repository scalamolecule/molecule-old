lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
  organization := "org.scalamolecule",
  version := "0.7.0",
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
  .settings(commonSettings ++ noPublishSettings)
  .aggregate(moleculeCore, moleculeCoretest, moleculeExamples)


lazy val moleculeCore = project.in(file("core"))
  .settings(moduleName := "molecule")
  .settings(commonSettings ++ publishSettings)


lazy val moleculeCoretest = project.in(file("coretest"))
  .dependsOn(moleculeCore)
  .enablePlugins(MoleculePlugin)
  .settings(commonSettings ++ noPublishSettings)
  .settings(
    moduleName := "molecule-coretest",
    moleculeSchemas := Seq(
      "molecule/part",
      "molecule/util"
    )
  )

lazy val moleculeExamples = project.in(file("examples"))
  .dependsOn(moleculeCore)
  .enablePlugins(MoleculePlugin)
  .settings(commonSettings ++ noPublishSettings)
  .settings(
    moduleName := "molecule-examples",
    moleculeSchemas := Seq(
      "molecule/examples/dayOfDatomic",
      "molecule/examples/graph",
      "molecule/examples/mbrainz",
      "molecule/examples/seattle"
    )
  )


lazy val snapshots = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
lazy val releases = "Sonatype OSS Staging" at "https://oss.sonatype.org/service/local/staging/deploy/maven2/"

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo <<= version((v: String) => Some(if (v.trim endsWith "SNAPSHOT") snapshots else releases)),
  publishArtifact in Test := false,
  pomIncludeRepository := (_ => false),
  homepage := Some(url("http://scalamolecule.org")),
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