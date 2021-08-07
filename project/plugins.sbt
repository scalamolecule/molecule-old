addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.10.0")

addSbtPlugin("org.scalamolecule" % "sbt-molecule" % "0.14.0-SNAPSHOT")

addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "1.7.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0")

libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0"
