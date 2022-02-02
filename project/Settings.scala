import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Keys._
import sbt.{ClassLoaderLayeringStrategy, Test, _}
import sbtbuildinfo.BuildInfoPlugin.autoImport._
import sbtmolecule.MoleculePlugin.autoImport.{moleculeDataModelPaths, moleculePluginActive, moleculeSchemaConversions}

object Settings extends SettingsDatomic {

  lazy val baseSettings: Seq[Def.Setting[_]] = Seq(
    organization := "org.scalamolecule",
    organizationName := "ScalaMolecule",
    organizationHomepage := Some(url("http://www.scalamolecule.org")),
    ThisBuild / version := "1.1.0",
    ThisBuild / scalaVersion := "2.13.8",
    crossScalaVersions := Seq("2.12.15", "2.13.8"),
    scalacOptions := List(
      "-feature",
      "-deprecation",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-language:higherKinds",
      "-language:existentials",
      "-Yrangepos",
    ) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 13)) => Seq("-Ymacro-annotations") // for @TxFns macro annotation
      case _             => Nil
    }),
    resolvers ++= Seq(
      "clojars" at "https://clojars.org/repo"
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


  lazy val js: Seq[Def.Setting[_]] = Seq(
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


  lazy val jvm: Seq[Def.Setting[_]] = {
    Seq(
      libraryDependencies ++= Seq(
        // Datomic peer dependency
        "com.datomic" % "datomic-free" % datomicFreeVersion,

        // Force newer janino compiler than datomic-free uses (necessary for using tx fns with datomic-free)
        "org.codehaus.janino" % "commons-compiler" % "3.0.12",
        "org.codehaus.janino" % "commons-compiler-jdk" % "3.0.12",

        // Datomic client dependencies transiently resolved
        "org.scalamolecule" %% "datomic-client-api-java-scala" % "1.0.3",

        // Akka dependencies for MoleculeRpcResponse
        "com.typesafe.akka" %% "akka-actor-typed" % "2.6.17",
        "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.1",
      ) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 13)) => Nil
        case _             =>
          // For @TxFns macro annotation on Scala 2.12
          sbt.compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full) :: Nil
      }),
    )
  }


  lazy val shared: Seq[Def.Setting[_]] = baseSettings ++ Seq(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scala-js" %%% "scala-js-macrotask-executor" % "1.0.0",
      "com.lihaoyi" %%% "utest" % "0.7.10",
      "io.suzaku" %%% "boopickle" % "1.4.0",
      "com.github.cornerman" %%% "sloth" % "0.4.0"
    )
  )


  lazy val tests: Seq[Def.Setting[_]] = Seq(

    // Let IntelliJ detect sbt-molecule-created jars in unmanaged lib directories
    exportJars := true,

    // Find scala version specific jars in respective libs
    unmanagedBase := {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 13)) => file(unmanagedBase.value.getPath ++ "/2.13")
        case _             => file(unmanagedBase.value.getPath ++ "/2.12")
      }
    },

    testFrameworks += new TestFramework("moleculeTests.setup.MoleculeTestFramework"),

    // Run tests for all systems sequentially to avoid data locks with db
    // Only applies on JVM. On JS platform there's no parallelism anyway.
    Test / parallelExecution := false,

    // Ensure clojure loads correctly for async tests run from sbt
    Test / classLoaderLayeringStrategy := ClassLoaderLayeringStrategy.Flat,


    buildInfoPackage := "moleculeBuildInfo",
    buildInfoKeys := Seq[BuildInfoKey](
      name, version, scalaVersion, sbtVersion,
      "datomicDistributionsDir" -> datomicDistributionsDir,
      "datomicHome" -> datomicHome,
      "datomicProtocol" -> datomicProtocol,
      "datomicUseFree" -> datomicUseFree,

      "datomicFreeVersions" -> datomicFreeVersions,
      "datomicProVersions" -> datomicProVersions,
      "datomicDevLocalVersions" -> datomicDevLocalVersions,

      "datomicFreeVersion" -> datomicFreeVersion,
      "datomicProVersion" -> datomicProVersion,
      "datomicDevLocalVersion" -> datomicDevLocalVersion
    ),


    // Generate Molecule boilerplate code with `sbt clean compile -Dmolecule=true`
    moleculePluginActive := sys.props.get("molecule").contains("true"),

    // We need schema conversions for mBrainz
    moleculeSchemaConversions := true, // (default is false)

    // Multiple directories with data models
    moleculeDataModelPaths := Seq(
      "moleculeTests/dataModels/core/base",
      "moleculeTests/dataModels/core/bidirectionals",
      "moleculeTests/dataModels/core/ref",
      "moleculeTests/dataModels/core/schemaDef",

      "moleculeTests/dataModels/examples/datomic/dayOfDatomic",
      "moleculeTests/dataModels/examples/datomic/mbrainz",
      "moleculeTests/dataModels/examples/datomic/seattle",
      "moleculeTests/dataModels/examples/gremlin/gettingStarted"
    ),

    // Temporarily limit number of tests to be compiled by sbt (comment out this whole sbt setting to test all)
    // Note that intellij doesn't recognize this setting - here you can right click on files and exclude
    unmanagedSources / excludeFilter := {
      val sharedTests = (baseDirectory.value / "../shared/src/test/scala/moleculeTests/tests").getCanonicalPath
      val allowed     = Seq(
        //        sharedTests + "/core/attr",
        //        sharedTests + "/core/attrMap",
        //        sharedTests + "/core/bidirectionals",
        //        sharedTests + "/core/crud",
        //        sharedTests + "/core/equality",
        //        sharedTests + "/core/expression",
        //        sharedTests + "/core/input1",
        //        sharedTests + "/core/input2",
        //        sharedTests + "/core/input3",
        //        sharedTests + "/core/json",
        //        sharedTests + "/core/nested",
        //        sharedTests + "/core/obj",
        sharedTests + "/core/pagination",
        //        sharedTests + "/core/ref",
        //        sharedTests + "/db/datomic/composite",
        //        sharedTests + "/db/datomic/entity",
        sharedTests + "/db/datomic/generic",
        //        sharedTests + "/db/datomic/schemaDef",
        //        sharedTests + "/db/datomic/time",
        //        sharedTests + "/db/datomic/txMetaData",
        //        sharedTests + "/examples/datomic/dayOfDatomic",
        //        sharedTests + "/examples/datomic/mbrainz",
        //        sharedTests + "/examples/datomic/seattle",
        //        sharedTests + "/examples/gremlin/gettingStarted",
        sharedTests + "/Adhoc.scala",
      )
      new SimpleFileFilter(f =>
        f.getCanonicalPath.startsWith(sharedTests) && !allowed.exists(p => f.getCanonicalPath.startsWith(p))
      )
    },

    // Allow resolving local dependencies if using Datomic proprietary dev-local or pro
    resolvers += Resolver.mavenLocal,

    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.1",
      "com.typesafe.akka" %% "akka-stream" % "2.6.17",
      "com.typesafe.akka" %% "akka-actor" % "2.6.17",
      "com.typesafe.akka" %% "akka-actor-typed" % "2.6.17",
      "ch.megard" %% "akka-http-cors" % "1.1.2",

      // Free, but proprietary Client dev-local dependency needed for testing client/dev-local
      // Please download from https://cognitect.com/dev-tools and install locally per included instructions
      "com.datomic" % "dev-local" % datomicDevLocalVersion,
    )
  ) ++ (if (datomicUseFree)
    Nil // Datomic free version is already default in `molecule` module
  else
    Seq(
      // To use Datomic Pro, please download from https://www.datomic.com/get-datomic.html
      // and install locally per included instructions
      libraryDependencies += "com.datomic" % "datomic-pro" % datomicProVersion,
      excludeDependencies += ExclusionRule("com.datomic", "datomic-free"),
      credentials += Credentials(Path.userHome / ".sbt" / ".credentials")
    ))
}
