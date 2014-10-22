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
        "coretest/src/main/scala/molecule/util"
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
//        "demo/src/main/scala/demo"
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
    scalaVersion := "2.11.3",
    scalacOptions := Seq("-feature", "-language:implicitConversions"),
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots"),
      "datomic" at "http://files.datomic.com/maven",
      "clojars" at "http://clojars.org/repo"
    ),
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "commons-codec" % "commons-codec" % "1.6",
      "com.datomic" % "datomic-free" % "0.9.4956",
      "com.chuusai" %% "shapeless" % "2.0.0",
      "org.specs2" %% "specs2" % "2.3.11" % "test"
    ),
    fork := true,
//    javaOptions in run += "-Xmx8G -Xgcpolicy:gencon -XX:MaxPermSize=2000m -Xms1000m -Xmn100m -Xverbose:settings"
//    javaOptions in run += "-Xmx8G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=2000m -Xms1000m -Xmn10m -verbosegc -XX:+PrintGCDetails"
//    javaOptions += "-Xmx8G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=2000m -Xms1000m -Xmn5m -verbosegc -XX:+PrintGCDetails" // 60s
//    javaOptions += "-Xmx8G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=2000m -Xms1000m -Xmn50m -verbosegc -XX:+PrintGCDetails" // 84s
//    javaOptions += "-Xmx8G -Xgcpolicy:gencon -XX:MaxPermSize=2000m -Xms1000m -Xmn5m -verbosegc -XX:+PrintGCDetails" // 83s
//    javaOptions += "-Xmx8G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=2000m -Xms1000m -Xmn5m -verbosegc -XX:+PrintGCDetails" // 81
//    javaOptions += "-Xmx8G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=2000m -Xms1000m -Xmn2m" // 86s
//    javaOptions += "-Xmx8G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=2000m -Xms1000m -Xmn2m" // + fork 62s
//    javaOptions in run += "-Xmx8G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=2000m -Xms1000m -Xmn2m" //  62s
//    javaOptions in run += "-Xmx8G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=2000m -Xms1000m -Xmn2m" //  + "in run" 98s !
//    javaOptions in compile += "-Xmx8G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=2000m -Xms1000m -Xmn2m" // + "in compile" 81
//    javaOptions += "-Xmx8G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=2000m -Xms1000m -Xmn2m" // - "in compile" 66
  // --------------------
//    javaOptions += "-Xmx8G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=2000m -Xms1000m -Xmn2m" // 30s
//    javaOptions += "-Xmx8G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=2000m -Xms1000m -Xmn5m" // 24s
//    javaOptions += "-Xmx8G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=2000m -Xms1000m -Xmn10m" // 24s
//    javaOptions += "-Xmx8G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=1000m -Xms500m -Xmn10m" // 24s
//    javaOptions += "-Xmx8G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=4000m -Xms500m -Xmn10m" // 34s
//    javaOptions += "-Xmx8G -XX:MaxPermSize=4000m -Xms500m -Xmn10m" // 24s
//    javaOptions += "-Xmx8G -XX:MaxPermSize=2000m -Xms500m -Xmn10m" // 28s
//    javaOptions += "-Xmx8G -XX:MaxPermSize=2000m -Xms500m -Xmn5m" // 30s
//    javaOptions += "-Xmx8G -XX:MaxPermSize=4000m -Xms500m -Xmn5m" // 29s
//    javaOptions += "-Xmx8G -XX:MaxPermSize=4000m -Xms500m -Xmn10m" // 33s
//    javaOptions += "-Xmx8G -XX:MaxPermSize=4000m -Xms500m -Xmn5m" // 31
//    javaOptions += "-Xmx8G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=4000m -Xms500m -Xmn5m" // 33
//    javaOptions += "-Xmx8G -Xgcpolicy:gencon -XX:MaxPermSize=4000m -Xms500m -Xmn5m" // 38
//    javaOptions += "-Xmx8G -XX:MaxPermSize=2000m -Xms1000m -Xmn5m" // 28
//    javaOptions += "-Xmx2G -XX:MaxPermSize=2000m -Xms1000m -Xmn5m" // 36
//    javaOptions += "-Xmx2G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=2000m -Xms1000m -Xmn5m" // 38
//    javaOptions += "-Xmx10G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=2000m -Xms500m -Xmn5m" // 26
//    javaOptions += "-Xmx10G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=2000m -Xms100m -Xmn10m" // 24
//    javaOptions += "-Xmx16G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=3000m -Xms100m -Xmn10m" // 24
//    javaOptions += "-Xmx16G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=3000m -Xms100m -Xmn5m" // 24
//    javaOptions += "-Xmx16G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=3000m -Xms500m -Xmn5m" // 24
//    javaOptions += "-Xmx16G -XX:MaxPermSize=3000m -Xms500m -Xmn5m" // 31
//    javaOptions += "-Xmx16G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=3000m -Xms500m -Xmn5m" // 24
//    javaOptions += "-Xmx16G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=3000m -Xms500m -Xmn5m" // 24
//    javaOptions += "-Xmx16G -XX:+UseConcMarkSweepGC -XX:MaxPermSize=3000m -Xms1000m -Xmn5m" // 27
//    javaOptions += "-Xmx16G -XX:+UseConcMarkSweepGC -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=3000m -Xms500m -Xmn5m" // 24
//    javaOptions += "-Xmx16G -Xss1M -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 21
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 21
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms3000m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 26
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled", // + cleenKeepFiles 23
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled", // 38
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled", // 33
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 20
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms1000m -Xmn8m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 21
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn8m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 25
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn4m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 23
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 22
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 38 (without fork)
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 37 (without fork)
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // java 1.7, 71s
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 30
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 31
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // java 1.6, 78s
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 23
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 27
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // java 1.8, 68s
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 23
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 27
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 21s
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 24
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 22
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 35s `= new
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 31
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 29 `: e
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 34
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 21
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 39 + cleanKF
//    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled", // 28
    javaOptions += "-Xmx16G -Xss1G -XX:MaxPermSize=3000m -Xms500m -Xmn5m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled" // 22
//    cleanKeepFiles ++= Seq("resolution-cache", "streams").map(target.value / _)
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