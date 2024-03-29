
lazy val moleculeRoot = (project in file("."))
  .aggregate(
    molecule.js,
    molecule.jvm,
    moleculeTests.js,
    moleculeTests.jvm
  )
  .settings(
    name := "molecule",
    Publish.not
  )


lazy val molecule = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .settings(Settings.shared ++ Publish.docs)
  .jsSettings(Settings.js)
  .jvmSettings(Settings.jvm)


lazy val moleculeTests = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .dependsOn(molecule)
  .enablePlugins(BuildInfoPlugin, MoleculePlugin)
  .settings(Settings.shared ++ Settings.tests ++ Publish.not)
  .jsSettings(Settings.js)
  .jvmSettings(Settings.jvm)
