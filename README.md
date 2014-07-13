# ☍ Molecule

Molecule is a type safe and intuitive Scala query/modelling DSL for 
[Datomic][datomic] - the immutable database of facts. 

As an example: to find

_Names of twitter/facebook_page communities in neighborhoods of southern districts_
 
we can compose a "molecule query" that is very close to our
human sentence:

```scala
Community.name.`type`("twitter" or "facebook_page")
  .Neighborhood.District.region("sw" or "s" or "se")
```

Molecule transforms this at compile time (with macros) to a little more elaborate Datalog query string and
 input rules that finds those communities in the Datomic database:

<pre>
[:find ?a
 :in $ %
 :where
   [?ent :community/name ?a]
   (rule1 ?ent)
   [?ent :community/neighborhood ?c]
   [?c :neighborhood/district ?d]
   (rule2 ?d)]

INPUTS:
List(
  datomic.db.Db@xxx,
  [[[rule1 ?ent] [?ent :community/type ":community.type/twitter"]]
   [[rule1 ?ent] [?ent :community/type ":community.type/facebook_page"]]
   [[rule2 ?d] [?d :district/region ":district.region/sw"]]
   [[rule2 ?d] [?d :district/region ":district.region/s"]]
   [[rule2 ?d] [?d :district/region ":district.region/se"]]]
)
</pre>

#### Benefits

By not having to write such complex Datalog queries and rules "by hand", Molecule 
allows you to

- Type less
- Make type safe queries with inferred return types
- Use your domain terms directly as query building blocks
- Model complex queries intuitively (easier to understand and maintain)
- Reduce syntactic noise
- Focus more on your domain and less on queries

#### Possible drawbacks

We still need to explore how far Molecule can match the expressive powers
 of Datalog. So far, all 
 examples in the
[Seattle tutorial][seattle] have been 
"molecularized" succesfully (see the 
[Molecule Seattle tutorial][tutorial] and 
[code][tutorialcode]). So as a proof-of-concept it looks promising...

## Getting started

- [Quick introduction][intro] to Datomic/Molecule
- [Setup Database][setup]: initiate a Datomic database and create a database schema with Molecule
- [Populate Database][populate]: populate a Datomic database with Molecule
- [Molecule Seattle tutorial][tutorial] examples of using Molecule (based on the 
[Datomic Seattle tutorial][seattle])
- Tests in [SeattleQueries][tutorialqueries] shows the queries produced by the molecules in the tutorial
- Tests in [SeattleTransformations][tutorialtransformations] shows the full dsl -> model -> query -> query string 
transformations of molecules

## Using Molecule

### Download code

1. `git clone https://github.com/marcgrue/molecule.git`
2. `sbt compile`
3. Import into your IDE
4. Poke around...


### Dependency in your project

Molecule 0.1.1 for Scala 2.11.1 is available at 
[Sonatype](https://oss.sonatype.org/content/repositories/releases/com/marcgrue/molecule_2.11/)
 so that you can add a dependency in your sbt file to `"com.marcgrue" % "molecule_2.11.1" % "0.1.1"`.

Since Molecule generates boilerplate code from your definitions it also needs to have the `DslBoilerplate.scala` file in your project folder. Please have a look at how the sbt build file
 of the Molecule project itself puts things together and simply copy that to your own project:

2. Setup your sbt build file [as in Molecule][moleculesbt]: 
    - Add library dependency `"com.marcgrue" % "molecule_2.11.1" % "0.1.1"`
    - List directories where you have your [definition file(s)][setup]
3. Define your domain schema in a [schema definition file][setup]
4. `sbt compile`
5. Import into your IDE
6. [Setup your database][setup]
7. [Populate your database][populate] with data
8. [Make molecule queries][tutorial]


## Resources
- [Molecule google list][moleculegroup] for questions and discussions about Molecule and other Scala-based Datomic resources
- [Issues][issues] - please feel free to raise issues/report bugs
- [Pull requests][pullrequests] are welcome
- [Datomic][datomic] website
- [Datomisca](https://github.com/pellucidanalytics/datomisca), a Scala API for Datomic


#### Author
[Marc Grue](http://marcgrue.com)

#### Credits/inspiration/acknowledgements
- [Rogue](https://github.com/foursquare/rogue), type-safe Scala DSL for MongoDB
- [Datomisca](https://github.com/pellucidanalytics/datomisca), a Scala API for Datomic
- [SqlTyped](https://github.com/jonifreeman/sqltyped), Embedding SQL as an external DSL into Scala
- [JScala](https://github.com/nau/jscala), Scala macro that produces JavaScript from Scala code
- [Parboiled2](https://github.com/sirthias/parboiled2), A macro-based PEG parser generator for Scala

#### License
Molecule is licensed under the [Apache License 2.0](http://en.wikipedia.org/wiki/Apache_license)

[datomic]: http://www.datomic.com
[seattle]: http://docs.datomic.com/tutorial.html
[moleculegroup]: https://groups.google.com/forum/#!forum/molecule-dsl
[pullrequests]: https://github.com/marcgrue/molecule/pulls
[issues]: https://github.com/marcgrue/molecule/issues
[moleculesbt]: https://github.com/marcgrue/molecule/blob/master/project/build.scala

[intro]: https://github.com/marcgrue/molecule/wiki/Quick-introduction-to-Datomic-and-Molecule
[setup]: https://github.com/marcgrue/molecule/wiki/Setup-a-Datomic-database
[populate]: https://github.com/marcgrue/molecule/wiki/Populate-the-database
[tutorial]: https://github.com/marcgrue/molecule/wiki/Molecule-Seattle-tutorial
[tutorialcode]: https://github.com/marcgrue/molecule/blob/master/examples/src/test/scala/molecule/examples/seattle/SeattleTests.scala
[tutorialqueries]: https://github.com/marcgrue/molecule/blob/master/examples/src/test/scala/molecule/examples/seattle/SeattleQueryTests.scala
[tutorialtransformations]: https://github.com/marcgrue/molecule/blob/master/examples/src/test/scala/molecule/examples/seattle/SeattleTransformationTests.scala
