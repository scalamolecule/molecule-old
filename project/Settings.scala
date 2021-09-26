import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._
import sbt.Keys._
import sbtbuildinfo.BuildInfoPlugin.autoImport._

object Settings extends SettingsDatomic with SettingsMolecule {

  val baseSettings: Seq[Def.Setting[_]] = Seq(
    organization := "org.scalamolecule",
    organizationName := "ScalaMolecule",
    organizationHomepage := Some(url("http://www.scalamolecule.org")),
    ThisBuild / version := "0.26.0-SNAPSHOT",
    crossScalaVersions := Seq("2.12.13", "2.13.6"),
    ThisBuild / scalaVersion := "2.13.6",
    scalacOptions := List(
      //      "-feature",
      //      "-unchecked",
      //      "-deprecation",
      ////      "-explaintypes",
      //      "-feature",
      ////      "-language:_",
      //      "-Xlint",
      //      "-Yrangepos",
      //      "-Ywarn-value-discard",
      //      "-Ywarn-extra-implicit",
      //      "-Ywarn-unused",

      "-feature",
      "-deprecation",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-language:higherKinds",
      "-language:existentials",
      "-Yrangepos"
    ) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 13)) => Seq("-Ymacro-annotations")
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
    }
  )

  val client: Seq[Def.Setting[_]] = Seq(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.1.0",
      "io.github.cquiroz" %%% "scala-java-time" % "2.2.2",
      "io.github.cquiroz" %%% "scala-java-time-tzdb" % "2.2.2"
    ),
    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv(),
    Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat
  )

  val server: Seq[Def.Setting[_]] = {
    Seq(
      libraryDependencies ++= Seq(
        "org.specs2" %% "specs2-core" % "4.10.6",
        "org.scalamolecule" %% "datomic-client-api-java-scala" % "0.7.0",
        "com.typesafe.akka" %% "akka-stream" % "2.6.14",
        "com.typesafe.akka" %% "akka-serialization-jackson" % "2.6.14",
        "com.typesafe.akka" %% "akka-actor" % "2.6.14",
        "com.typesafe.akka" %% "akka-actor-typed" % "2.6.14",
        "com.typesafe.akka" %% "akka-slf4j" % "2.6.14",
        "com.typesafe.akka" %% "akka-protobuf-v3" % "2.6.14",
        "com.typesafe.akka" %% "akka-http" % "10.2.4",
        "ch.megard" %% "akka-http-cors" % "1.1.1"
      ),
      // Ensure clojure loads correctly for async tests run from sbt
      Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat
    ) ++ (if (datomicProtocol == "free") {
      Seq(libraryDependencies += "com.datomic" % "datomic-free" % "0.9.5697")
    } else {
      Seq(
        // Please download from https://cognitect.com/dev-tools and install locally per included instructions
        libraryDependencies += "com.datomic" % "datomic-pro" % datomicProVersion,
        excludeDependencies += ExclusionRule("com.datomic", "datomic-free"),
        credentials += Credentials(Path.userHome / ".sbt" / ".credentials")
      )
    })
  }


  val shared: Seq[Def.Setting[_]] = baseSettings ++ Seq(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "com.lihaoyi" %%% "utest" % "0.7.10",
      "io.suzaku" %%% "boopickle" % "1.3.3",
      "com.github.cornerman" %%% "sloth" % "0.3.0",
      //      "com.github.cornerman" %%% "sloth" % "0.3.1-SNAPSHOT",

      "org.specs2" %%% "specs2-core" % "4.10.6"
    ),
    //    testFrameworks += new TestFramework("utest.runner.Framework"),
    //    testFrameworks += new TestFramework("moleculeTests.core.MoleculeTestFramework"),
    testFrameworks += new TestFramework("moleculeTests.setup.MoleculeTestFramework"),

    // Temporarily limit number of tests to be compiled (comment out to test all)
    unmanagedSources / excludeFilter := {
      val sharedTests = (baseDirectory.value / "../shared/src/test/scala/moleculeTests/tests").getCanonicalPath
      val allowed     = Seq(
        //        sharedTests + "/core/attr",
        //        sharedTests + "/core/attrMap",
        //        sharedTests + "/core/bidirectionals",
        //        sharedTests + "/core/composite",
        //        sharedTests + "/core/crud",
        //        sharedTests + "/core/equality",
        //        sharedTests + "/core/expression",
        //        sharedTests + "/core/generic",
        //        sharedTests + "/core/input1",
        //        sharedTests + "/core/input2",
        //        sharedTests + "/core/input3",
        //        sharedTests + "/core/ref",
        //        sharedTests + "/core/json",
        //        sharedTests + "/core/nested",
        //        sharedTests + "/core/obj",
        //        sharedTests + "/core/runtime",

        sharedTests + "/core/schemaDef",

        //        sharedTests + "/core/time",
        //        sharedTests + "/core/transaction",
        //        sharedTests + "/core/txMetaData",

        sharedTests + "/Adhoc.scala",
      )
      new SimpleFileFilter(f =>
        f.getCanonicalPath.startsWith(sharedTests) && !allowed.exists(p => f.getCanonicalPath.startsWith(p))
      )
    },

    buildInfoKeys := Seq[BuildInfoKey](
      name, version, scalaVersion, sbtVersion,
      "datomicProtocol" -> datomicProtocol,
      "datomicHome" -> datomicHome,
      "datomicProVersions" -> datomicProVersions,
      "datomicProVersion" -> datomicProVersion,
      "datomicDevLocalVersions" -> datomicDevLocalVersions,
      "datomicDevLocalVersion" -> datomicDevLocalVersion
    ),
    buildInfoPackage := "moleculeBuildInfo"
  )


  val tests: Seq[Def.Setting[_]] = Seq(
    libraryDependencies ++= Seq(
      // Proprietary Client dev-local dependency needed for tests against dev-local
      // Please download from https://cognitect.com/dev-tools and install locally per included instructions
      "com.datomic" % "dev-local" % datomicDevLocalVersion
    ),

    // Find scala version specific jars in respective libs
    unmanagedBase := {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 13)) => file(unmanagedBase.value.getPath ++ "/2.13")
        case _             => file(unmanagedBase.value.getPath ++ "/2.12")
      }
    },

    // Let IntelliJ detect sbt-molecule-created jars in unmanaged lib directory
    exportJars := true,

    // Run tests for all systems sequentially to avoid data locks with db
    // Only applies on JVM. On JS platform there's no parallelism anyway.
    Test / parallelExecution := false
  )
}
