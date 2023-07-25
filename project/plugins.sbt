

resolvers ++= Seq(
  "clojars" at "https://clojars.org/repo", // sbt-molecule depends on molecule that depends on datomic-free
)

libraryDependencies ++= Seq(
  "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0"
)

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.11.0")

addSbtPlugin("org.scalamolecule" % "sbt-molecule" % "1.0.3")

addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "1.9.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.2.0")

