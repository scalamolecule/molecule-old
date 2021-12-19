import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoPlugin.autoImport._

object Settings extends SettingsDatomic with SettingsMolecule {

  val baseSettings: Seq[Def.Setting[_]] = Seq(
    organization := "org.scalamolecule",
    organizationName := "ScalaMolecule",
    organizationHomepage := Some(url("http://www.scalamolecule.org")),
    ThisBuild / version := "1.0.1",
    ThisBuild / scalaVersion := "2.13.7",
    crossScalaVersions := Seq("2.12.15", "2.13.7"),
    scalacOptions := List(
      "-feature",
      //      "-unchecked",
      "-deprecation",
      //      "-language:_",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-language:higherKinds",
      "-language:existentials",
      "-Yrangepos",
      //      "-explaintypes",
      //      "-Ywarn-value-discard",
      //      "-Ywarn-extra-implicit",
      //      "-Ywarn-unused",
    ) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 13)) => Seq("-Ymacro-annotations") // for @TxFns macro annotation
      case _             => Nil
    }),
    resolvers ++= Seq(
      "clojars" at "https://clojars.org/repo",
      // If using datomic-pro/starter
      "my.datomic.com" at "https://my.datomic.com/repo",
      Resolver.mavenLocal
    ),
    Compile / unmanagedSourceDirectories ++= {
      (Compile / unmanagedSourceDirectories).value.map { dir =>
        CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, 13)) => file(dir.getPath ++ "-2.13+")
          case _             => file(dir.getPath ++ "-2.13-")
        }
      }
    },
    ThisBuild / versionScheme := Some("early-semver")
  )

  val js: Seq[Def.Setting[_]] = Seq(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.0.0",
      "io.github.cquiroz" %%% "scala-java-time" % "2.3.0",
      // This creates quite a lot of locales code but is needed on the js side.
      // See https://github.com/cquiroz/scala-java-time/issues/69
      "io.github.cquiroz" %%% "scala-java-time-tzdb" % "2.3.0"
    ),
    jsEnv := new JSDOMNodeJSEnv(
      JSDOMNodeJSEnv.Config()
        // for some reason still needed with Scala.js 1.8
        // https://github.com/scala-js/scala-js-js-envs/issues/12
        .withArgs(List("--dns-result-order=ipv4first"))
    )
  )

  val jvm: Seq[Def.Setting[_]] = {
    Seq(
      libraryDependencies ++= Seq(
        "org.scalamolecule" %% "datomic-client-api-java-scala" % "1.0.2",
        "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.1",
        "com.typesafe.akka" %% "akka-stream" % "2.6.17",
        "com.typesafe.akka" %% "akka-serialization-jackson" % "2.6.17",
        "com.typesafe.akka" %% "akka-actor" % "2.6.17",
        "com.typesafe.akka" %% "akka-actor-typed" % "2.6.17",
        "com.typesafe.akka" %% "akka-slf4j" % "2.6.17",
        "com.typesafe.akka" %% "akka-protobuf-v3" % "2.6.17",
        "com.typesafe.akka" %% "akka-http" % "10.2.6",
        "ch.megard" %% "akka-http-cors" % "1.1.2",

        // Free, but proprietary Client dev-local dependency needed for client/dev-local
        // Please download from https://cognitect.com/dev-tools and install locally per included instructions
        "com.datomic" % "dev-local" % datomicDevLocalVersion
      ) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 13)) => Nil
        case _             =>
          // For @TxFns macro annotation on Scala 2.12
          sbt.compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full) :: Nil
      }),
    ) ++ (if (datomicProtocol == "free") {
      Seq(libraryDependencies += "com.datomic" % "datomic-free" % "0.9.5697")
    } else {
      Seq(
        // To use Datomic Pro, please download from https://www.datomic.com/get-datomic.html
        // and install locally per included instructions
        libraryDependencies += "com.datomic" % "datomic-pro" % datomicProVersion,
        excludeDependencies += ExclusionRule("com.datomic", "datomic-free"),
        credentials += Credentials(Path.userHome / ".sbt" / ".credentials")
      )
    })
  }


  val shared: Seq[Def.Setting[_]] = baseSettings ++ Seq(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scala-js" %%% "scala-js-macrotask-executor" % "1.0.0",
      "com.lihaoyi" %%% "utest" % "0.7.10",
      "io.suzaku" %%% "boopickle" % "1.4.0",
      "com.github.cornerman" %%% "sloth" % "0.4.0"
    ),

    testFrameworks += new TestFramework("moleculeTests.setup.MoleculeTestFramework"),

    // Ensure clojure loads correctly for async tests run from sbt
    Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat,

    //        // Temporarily limit number of tests to be compiled by sbt (comment out this whole sbt setting to test all)
    //        // Note that intellij doesn't recognize this setting - here you can right click on files and exclude
    //        unmanagedSources / excludeFilter := {
    //          val sharedTests = (baseDirectory.value / "../shared/src/test/scala/moleculeTests/tests").getCanonicalPath
    //          val allowed     = Seq(
    ////            sharedTests + "/core/attr",
    ////            sharedTests + "/core/attrMap",
    ////            sharedTests + "/core/bidirectionals",
    ////            sharedTests + "/core/crud",
    ////            sharedTests + "/core/equality",
    ////            sharedTests + "/core/expression",
    ////            sharedTests + "/core/input1",
    ////            sharedTests + "/core/input2",
    ////            sharedTests + "/core/input3",
    ////            sharedTests + "/core/json",
    ////            sharedTests + "/core/nested",
    ////            sharedTests + "/core/obj",
    ////            sharedTests + "/core/ref",
    ////            sharedTests + "/db/datomic/composite",
    ////            sharedTests + "/db/datomic/entity",
    ////            sharedTests + "/db/datomic/generic",
    ////            sharedTests + "/db/datomic/schemaDef",
    ////            sharedTests + "/db/datomic/time",
    ////            sharedTests + "/db/datomic/txMetaData",
    //            sharedTests + "/examples/datomic/dayOfDatomic",
    ////            sharedTests + "/examples/datomic/mbrainz",
    ////            sharedTests + "/examples/datomic/seattle",
    ////            sharedTests + "/examples/gremlin/gettingStarted",
    ////            sharedTests + "/Adhoc.scala",
    //          )
    //          new SimpleFileFilter(f =>
    //            f.getCanonicalPath.startsWith(sharedTests) && !allowed.exists(p => f.getCanonicalPath.startsWith(p))
    //          )
    //        }
  )


  val tests: Seq[Def.Setting[_]] = Seq(
    // Find scala version specific jars in respective libs
    unmanagedBase := {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 13)) => file(unmanagedBase.value.getPath ++ "/2.13")
        case _             => file(unmanagedBase.value.getPath ++ "/2.12")
      }
    },

    // Run tests for all systems sequentially to avoid data locks with db
    // Only applies on JVM. On JS platform there's no parallelism anyway.
    Test / parallelExecution := false,

    buildInfoPackage := "moleculeBuildInfo",
    buildInfoKeys := Seq[BuildInfoKey](
      name, version, scalaVersion, sbtVersion,
      "datomicProtocol" -> datomicProtocol,
      "datomicHome" -> datomicHome,
      "datomicProVersions" -> datomicProVersions,
      "datomicProVersion" -> datomicProVersion,
      "datomicDevLocalVersions" -> datomicDevLocalVersions,
      "datomicDevLocalVersion" -> datomicDevLocalVersion
    ),

    // Let IntelliJ detect sbt-molecule-created jars in unmanaged lib directories
    exportJars := true
  )
}
