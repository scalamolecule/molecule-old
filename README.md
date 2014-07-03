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

Molecule 0.1.0 for Scala 2.11.1 will soon be available at 
[Sonatype](https://oss.sonatype.org/index.html#nexus-search;quick%7Escaladci)
 so that you can

1. Add `"org.scaladatomic" % "molecule_2.11.1" % "0.1.0"` to your sbt build file.
2. Define your domain in a [schema definition file][setup]
3. `sbt compile`
4. `sbt`
5. `gen-idea` to create your project // if you're using IntelliJ
6. [Setup your database][setup]
7. [Populate your database][populate] with data
8. [Make molecule queries][tutorial]


## Resources
- [ScalaDatomic google group][scaladatomicgroup] for questions and discussions about Molecule and other Scala-based Datomic resources
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
[scaladatomicgroup]: https://groups.google.com/forum/#!forum/molecule-dsl
[pullrequests]: https://github.com/marcgrue/molecule/pulls
[issues]: https://github.com/marcgrue/molecule/issues

[intro]: https://github.com/marcgrue/molecule/wiki/Quick-introduction-to-Datomic-and-Molecule
[setup]: https://github.com/marcgrue/molecule/wiki/Setup-a-Datomic-database
[populate]: https://github.com/marcgrue/molecule/wiki/Populate-the-database
[tutorial]: https://github.com/marcgrue/molecule/wiki/Molecule-Seattle-tutorial
[tutorialcode]: https://github.com/marcgrue/molecule/blob/master/examples/src/test/scala/molecule/examples/seattle/SeattleTests.scala
[tutorialqueries]: https://github.com/marcgrue/molecule/blob/master/examples/src/test/scala/molecule/examples/seattle/SeattleQueryTests.scala
[tutorialtransformations]: https://github.com/marcgrue/molecule/blob/master/examples/src/test/scala/molecule/examples/seattle/SeattleTransformationTests.scala
