
lazy val moleculeRoot = (project in file("."))
  .aggregate(
    molecule.js,
    molecule.jvm,
    moleculeTests.js,
    moleculeTests.jvm
  )
  .settings(Publish.not)


lazy val molecule = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .settings(Settings.shared ++
    Publish.withoutDocs // save time without doc creation for publishLocal
    //    Publish.withDocs // make docs for publishSigned
  )
  .jsSettings(Settings.js)
  .jvmSettings(Settings.jvm)


lazy val moleculeTests = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .dependsOn(molecule)
  .enablePlugins(BuildInfoPlugin, MoleculePlugin)
  .settings(Settings.shared ++
    Settings.moleculeTests ++
    Settings.tests ++
    Publish.not
  )
  .jsSettings(Settings.js)
  .jvmSettings(Settings.jvm)
