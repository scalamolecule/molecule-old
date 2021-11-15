import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoPlugin.autoImport._

object Settings extends SettingsDatomic with SettingsMolecule {

  val baseSettings: Seq[Def.Setting[_]] = Seq(
    organization := "org.scalamolecule",
    organizationName := "ScalaMolecule",
    organizationHomepage := Some(url("http://www.scalamolecule.org")),
    ThisBuild / version := "1.0.0-SNAPSHOT",
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
    }
  )

  val js: Seq[Def.Setting[_]] = Seq(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.2.0",
      "io.github.cquiroz" %%% "scala-java-time" % "2.3.0",
      "io.github.cquiroz" %%% "scala-java-time-tzdb" % "2.3.0"
    ),
    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()
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
      })
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
      "com.lihaoyi" %%% "utest" % "0.7.10",
      "io.suzaku" %%% "boopickle" % "1.4.0",
      "com.github.cornerman" %%% "sloth" % "0.3.0"
    ),

    testFrameworks += new TestFramework("moleculeTests.setup.MoleculeTestFramework"),

    //    // Temporarily limit number of tests to be compiled (comment out this whole sbt setting to test all)
    //    unmanagedSources / excludeFilter := {
    //      val sharedTests = (baseDirectory.value / "../shared/src/test/scala/moleculeTests/tests").getCanonicalPath
    //      val allowed     = Seq(
    //                sharedTests + "/core/attr",
    //        //        sharedTests + "/core/attrMap",
    //        //        sharedTests + "/core/bidirectionals",
    //        //        sharedTests + "/core/crud",
    //        //        sharedTests + "/core/equality",
    //        //        sharedTests + "/core/expression",
    //        //        sharedTests + "/core/input1",
    //        //        sharedTests + "/core/input2",
    //        //        sharedTests + "/core/input3",
    //        //        sharedTests + "/core/json",
    //        //        sharedTests + "/core/nested",
    //        //        sharedTests + "/core/obj",
    //        //        sharedTests + "/core/ref",
    //        //        sharedTests + "/db/datomic/composite",
    //        //        sharedTests + "/db/datomic/entity",
    //        //        sharedTests + "/db/datomic/generic",
    //        //        sharedTests + "/db/datomic/schemaDef",
    //        //        sharedTests + "/db/datomic/time",
    //        //        sharedTests + "/db/datomic/txMetaData",
    //        //        sharedTests + "/examples/datomic/dayOfDatomic",
    //        //        sharedTests + "/examples/datomic/mbrainz",
    //        //        sharedTests + "/examples/datomic/seattle",
    //        //        sharedTests + "/examples/gremlin/gettingStarted",
    //        sharedTests + "/Adhoc.scala",
    //      )
    //      new SimpleFileFilter(f =>
    //        f.getCanonicalPath.startsWith(sharedTests) && !allowed.exists(p => f.getCanonicalPath.startsWith(p))
    //      )
    //    },
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
    )
  )
}
