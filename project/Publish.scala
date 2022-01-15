import sbt.Keys._
import sbt.{url, _}


object Publish {

  lazy private val snapshots = "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
  lazy private val releases  = "Sonatype OSS Staging" at "https://oss.sonatype.org/service/local/staging/deploy/maven2/"

  lazy val docs = if (sys.props.get("docs").contains("true")) withDocs else withoutDocs

  lazy val withDocs = Seq(
    publishMavenStyle := true,
    publishTo := (if (isSnapshot.value) Some(snapshots) else Some(releases)),
    Test / publishArtifact := false,
    Compile / doc / scalacOptions ++= Seq(
      "-doc-root-content", baseDirectory.value + "/src/main/scaladoc/rootdoc.txt",
      "-diagrams", "-groups",
      "-doc-version", version.value,
      "-doc-title", "Molecule",
      "-sourcepath", (ThisBuild / baseDirectory).value.toString,
      "-doc-source-url", s"https://github.com/scalamolecule/molecule/tree/master€{FILE_PATH}.scala#L1"
    ),
    pomIncludeRepository := (_ => false),
    homepage := Some(url("http://scalamolecule.org")),
    licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
    scmInfo := Some(ScmInfo(
      url("https://github.com/scalamolecule/molecule"),
      "scm:git:git@github.com:scalamolecule/molecule.git"
    )),
    description := "molecule",
    developers := List(
      Developer(
        id = "marcgrue",
        name = "Marc Grue",
        email = "marcgrue@gmail.com",
        url = url("http://marcgrue.com")
      )
    )
  )

  lazy val withoutDocs = Seq(
    Test / publishArtifact := false,
    doc / sources := Seq.empty,
    packageDoc / publishArtifact := false
  )

  lazy val not = Seq(
    publish / skip := true,
    publish := ((): Unit),
    publishLocal := ((): Unit),
    Compile / packageDoc / publishArtifact := false,
    Compile / doc / sources := Seq.empty
  )
}
