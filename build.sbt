//lazy val root = (project in file("."))
////  .aggregate(molecule.js, molecule.jvm)
//  .settings(publish / skip := true)


lazy val molecule = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .settings(
    //    Settings.base ++
    Settings.shared ++
      Publish.withoutDocs // save time without doc creation for publishLocal
    //      Publish.withDocs // make docs for publishSigned
  )
  .jsSettings(Settings.client)
  //  .jsConfigure(_.enablePlugins(TzdbPlugin))
  .jvmSettings(Settings.server)


lazy val moleculeTests = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .dependsOn(molecule)
  .enablePlugins(BuildInfoPlugin, MoleculePlugin)
  .settings(Settings.shared ++
    Settings.moleculeTests ++
    Settings.tests ++
    Publish.not
  )
  .jsSettings(Settings.client)
  .jvmSettings(Settings.server)

