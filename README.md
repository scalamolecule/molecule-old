This is an older version of the Molecule Scala library based on macros. It only targets the [Datomic][datomic] database whereas the new Molecule library living in the new [molecule](https://github.com/scalamolecule/molecule) repo targets both Datomic and JDBC-compliant databases.


## Old Molecule...

Molecule is a non-blocking asynchronous domain-customizable database query language for Scala and Scala.js against the [Datomic][datomic] database.

Transactions and queries are type-safely built with the terms of your domain and works on both the jvm and js platform.

Visit [ScalaMolecule.org](http://ScalaMolecule.org) to learn more or visit the [Molecule forum](https://groups.google.com/forum/#!forum/molecule-dsl)

[![Gitter](https://badges.gitter.im/scalamolecule/Lobby.svg)](https://gitter.im/scalamolecule/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)


## Your domain data model == your query language

Instead of squeezing your domain terms in between commands like `SELECT name, age FROM Person` etc, or other query constructs in other database languages, you directly use your domain terms as tokens to fetch data from the database:

```scala
Person.name.age.get
```

This is possible since a schema is initially defined based on your domain data model:

```scala
trait Person {
  val name = oneString
  val age  = oneInt
}
```
When you compile your project with `sbt compile`, Molecule generates the necessary boilerplate code to allow writing the more intuitive query with type-inferred attributes. Since the types of each attribute `name` and `age` is encoded in the schema definition we'll also get typed data back from our query.

```scala
val personsWithAge: Future[List[(String, Int)]] = Person.name.age.get
```


## No runtime overhead
Molecule transform molecules like `Person.name.age.get` to [Datalog](https://docs.datomic.com/on-prem/query.html) queries for Datomic. The returned untyped data from Datomic is then cast by Molecule to the expected Scala type.

All queries are prepared at compile time by macros. So there is no overhead at runtime when running the queries.


## Client transactions and queries

Molecule is fully implemented on both the Scala jvm and Scala.js platforms. This means that you can transact and query data directly from the client side too with the same molecules that you would use on the server side. Molecule transparently handles marshalling data back and forth with one simple [configuration](). 

Client molecules relieve you from having to manually define shared apis and make server implementations for shuttling data back and forth between client and server. Like QraphQL clients, just fully type-inferred.


## Getting started

- [Introduction](http://www.scalamolecule.org/intro/) to Datomic/Molecule
- [Setup][setup]: populate a Datomic database with Molecule
- [Molecule Seattle tutorial](http://www.scalamolecule.org/community/seattle/) examples of using Molecule


## Sample projects

1. `git clone https://github.com/scalamolecule/molecule-samples.git`
2. `cd molecule-samples/molecule-basic`
3. `sbt compile`
4. Open in your IDE
5. Run tests and try adding more attributes...

Various sample projects show sbt setups for different contexts. You can also read the [Setup][setup] section on the Molecule website. 


## Basic sbt setup (server side)

Add the following to your build files:

`project/build.properties`:

```scala
sbt.version=1.6.1
```

`project/buildinfo.sbt`:

```scala
addSbtPlugin("org.scalamolecule" % "sbt-molecule" % "1.0.3")
```

`build.sbt`:

```scala
lazy val yourProject = project.in(file("app"))
  .enablePlugins(MoleculePlugin)
  .settings(
    resolvers ++= Seq(
      "datomic" at "http://files.datomic.com/maven",
      "clojars" at "http://clojars.org/repo",
      Resolver.sonatypeRepo("releases")
    ),
    libraryDependencies ++= Seq(
      "org.scalamolecule" %% "molecule" % "1.1.0",
      "com.datomic" % "datomic-free" % "0.9.5697" // or pro (see README_pro)
    ),
    moleculeSchemas := Seq("app") // paths to your schema definition files...
  )
```

For Scala.js setups, please check the [sample rpc projects]() or read the [Setup][setup] section on the Molecule website.

Molecule is available at [maven]((https://repo1.maven.org/maven2/org/scalamolecule/)).


#### Author
Marc Grue


#### License
Molecule is licensed under the [Apache License 2.0](http://en.wikipedia.org/wiki/Apache_license)

[datomic]: http://www.datomic.com
[setup]: http://www.scalamolecule.org/setup/
