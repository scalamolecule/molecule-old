import sbt._
import sbt.Keys._
import sbtmolecule.MoleculePlugin.autoImport._

trait SettingsMolecule {

  val moleculeTests: Seq[Def.Setting[_]] = Seq(

    // Generate Molecule boilerplate code with `sbt clean compile -Dmolecule=true`
    moleculePluginActive := sys.props.get("molecule").contains("true"),

    // We need schema conversions for mBrainz
    moleculeSchemaConversions := true, // (default is false)

    //    moleculeMakeJars := true, // (default is true)
    //    moleculeGenericPkg := "molecule.core.generic",

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

      //      "moleculeTests/tests/core/generic"
    )
  )

  /*
To re-generate internal generic dsl code:

1. In SettingsMolecule, set:

    moleculeMakeJars := false,
    moleculeGenericPkg := "molecule.core.generic",
    moleculeDataModelPaths := Seq(
      // ...uncomment others to save compile time
      "molecule/tests/core/generic"
    )

2. `sbt clean compile -Dmolecule=true`
3. Copy 7 folders in src_managed.main.molecule.tests.core.generic.dsl to shared.src.main.scala.molecule.core.generic
   */
}
