

/*

  Copy this trait to your build file and let your build trait extend it.

  This will allow you to have your sbt build create the necessary boilerplate
  code to run Molecule queries with your own domain.

  Remember to add the directories where you have schema definition files!

  See how it's done in the Molecule project build file:
  https://github.com/marcgrue/molecule/blob/master/project/build.scala

  Todo: there must be a smarter way than this... :-)

*/

//trait Boilerplate {
//
//  def boilerplate(inputDirs: String*) = sourceGenerators in Compile += Def.task[Seq[File]] {
//    val sourceDir = (sourceManaged in Compile).value
//
//    // generate source files
//    val sourceFiles = DslBoilerplate.generate(sourceDir, inputDirs.toSeq)
//
//    // Avoid re-generating boilerplate if nothing has changed when running `sbt compile`
//    val cache = FileFunction.cached(
//      streams.value.cacheDirectory / "filesCache",
//      inStyle = FilesInfo.lastModified,
//      outStyle = FilesInfo.hash
//    ) {
//      in: Set[File] => sourceFiles.toSet
//    }
//    cache(sourceFiles.toSet).toSeq
//  }.taskValue
//
//  // Format file data for jar creation
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
//}