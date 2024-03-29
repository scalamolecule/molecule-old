package moleculeTests.tests.examples.gremlin.gettingStarted

import molecule.datomic.api.out10._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.examples.gremlin.gettingStarted.dsl.ModernGraph2._
import utest._
import scala.concurrent.{ExecutionContext, Future}

/*
  Bidirectional property edge - expanding on the Gremlin tutorial at:

  http://tinkerpop.apache.org/docs/current/tutorials/getting-started/

  In the tutorial, the weight property of the edges are not used. To see what we can use
  edge properties for we now implement the same tutorial with a bidirectional property
  edge `Knows` that has a weight property:

      Person --> Knows.weight --> Person
      Person <-- Knows.weight <-- Person

  For each edge/ref created, a reverse edge is created in the background by Molecule
  so that we can uniformly query in both directions.

  We now have a unidirectional edge from `Person` to `Software` that we call `Created`:

      Person --> Created.weight --> Software

  This allow us to assert a weight property of how much a person has been involved in
  a software project.

  See schema definition in
  examples/src/main/scala/molecule/examples/gremlin/schema/ModernGraphDefinition2.scala
*/

object Friends2 extends AsyncTestSuite {

  def testData(implicit conn: Future[Conn], ec: ExecutionContext) = {
    for {
      /*
        Sample bidirectional friendship graph (with extra vadas--peter friendship added for more fun examples)

          marko (29) --1.0-- josh (32)
            |
           0.5
            |
          vadas (27) --0.7-- peter (35)
      */

      // Software
      List(lop, ripple) <- Software.name.lang insert Seq(
        ("lop", "java"),
        ("ripple", "java")
      ) map (_.eids)

      // People and software created
      List(
      marko, markoLop,
      vadas,
      josh, joshLop, joshRipple,
      peter, peterLop
      ) <- m(Person.name.age.Created * Created.software.weight) insert Seq(
        ("marko", 29, Seq((lop, 0.4))),
        ("vadas", 27, Seq()),
        ("josh", 32, Seq((lop, 0.4), (ripple, 1.0))),
        ("peter", 35, Seq((lop, 0.2)))
      ) map (_.eids)

      // Weighed friendships (property edges)

      // Creating multiple edges
      markoKnows <- Knows.person.weight.insert(List((vadas, 0.5), (josh, 1.0))).map(_.eids)
      _ <- Person(marko).knows(markoKnows).update

      // Extra friendship not in the tutorial so that we can make fof queries
      _ <- Person(vadas).Knows.weight(0.7).person(peter).update
    } yield {
      (marko, markoLop, vadas, josh, joshLop, joshRipple, peter, peterLop)
    }
  }

  lazy val tests = Tests {
    import molecule.core.util.Executor._

    "The first 5 minutes" - modernGraph2 { implicit conn =>
      for {
        (marko, markoLop, vadas, josh, joshLop, joshRipple, peter, peterLop) <- testData

        // Marko's name
        // gremlin> g.V(1).values('name')
        _ <- Person(marko).name.get.map(_.head ==> "marko")

        // Marko knows (entity ids)
        // g.V(1).outE('knows')
        _ <- Person(marko).Knows.person.a1.get.map(_ ==> Seq(josh, vadas).sorted)

        // Marko knows (by name)
        // g.V(1).outE('knows').inV().values('name')
        // g.V(1).out('knows').values('name')
        _ <- Person(marko).Knows.Person.name.get.map(_ ==> List("vadas", "josh"))
        _ <- Person(marko).Knows.person.a1.get.map(_ ==> List(josh, vadas).sorted)

        // We can uniformly query in the other direction too
        // vadas and josh also know marko
        _ <- Person(vadas).Knows.person.a1.get.map(_ ==> List(peter, marko).sorted)
        _ <- Person(josh).Knows.person.get.map(_ ==> List(marko))

        // "Markos knows older than 30"
        // g.V(1).out('knows').has('age', gt(30)).values('name')
        _ <- Person(marko).Knows.Person.name.age_.>(30).get.map(_ ==> List("josh"))
      } yield ()
    }


    "Next 15 minutes" - modernGraph2 { implicit conn =>
      for {
        (marko, markoLop, vadas, josh, joshLop, joshRipple, peter, peterLop) <- testData

        // Find Marko id
        // g.V().has('name','marko')
        _ <- Person.e.name_("marko").get.map(_ ==> List(marko))

        // Marko created (refs to Created)
        // g.V().has('name','marko').out('created')
        _ <- Person.name_("marko").created.get.map(_.head ==> Set(markoLop))

        // Marko created software named...
        // g.V().has('name','marko').out('created').values('name')
        _ <- Person.name_("marko").Created.Software.name.get.map(_ ==> List("lop"))


        // Ages of marko and vadas
        // g.V().has('name',within('vadas','marko')).values('age')
        _ <- Person.name_("marko", "vadas").age.get.map(_ ==> List(27, 29))

        // Mean/average age of marko and vadas
        // g.V().has('name',within('vadas','marko')).values('age').mean()
        _ <- Person.name_("marko", "vadas").age(avg).get.map(_.head ==> 28.0)


        // Who are the people that marko develops software with?
        // g.V().has('name','marko').out('created').in('created').values('name')

        // It's idiomatic for Datomic to split such query and use the output of
        // one query as input for the next one.

        // First find ids of software projects that marko has participated in
        markoSoftware <- Person.name_("marko").Created.software.get

        // Then find names of persons that have participated in those projects
        _ <- Person.name.Created.software_(markoSoftware).get.map(_ ==> List("peter", "josh", "marko"))

        // Excluding marko from the result (re-using the first sub-query)
        // g.V().has('name','marko').as('exclude').out('created').in('created').where(neq('exclude')).values('name')
        _ <- Person.name.not("marko").Created.software_(markoSoftware).get.map(_ ==> List("peter", "josh"))

        // Untyped generic queries are not directly handled by Molecule.
        // With a namespace prefix we though can similar and type safe results
        // g.V().group().by(label).by('name')
        _ <- Person.name.get.map(_ ==> List("peter", "vadas", "josh", "marko"))
        _ <- Software.name.get.map(_ ==> List("ripple", "lop"))
      } yield ()
    }


    "What else could we ask?" - modernGraph2 { implicit conn =>
      for {
        (marko, markoLop, vadas, josh, joshLop, joshRipple, peter, peterLop) <- testData

        // What friends does each person have (nested lists)
        _ <- m(Person.name.a1.Knows * Person.name.a1).get.map(_ ==> List(
          ("josh", List("marko")),
          ("marko", List("josh", "vadas")),
          ("peter", List("vadas")),
          ("vadas", List("marko", "peter")),
        ))

        // Who has most friends
        _ <- Person.name.a2.Knows.person(count).d1.get.map(_ ==> List(
          ("marko", 2),
          ("vadas", 2),
          ("josh", 1),
          ("peter", 1),
        ))

        // Markos friends older than 30
        _ <- Person(marko).Knows.Person.name.age.>(30).get.map(_ ==> List(("josh", 32)))

        // Josh's friends older than 30 (none)
        _ <- Person(josh).Knows.Person.name.age.>(30).get.map(_ ==> List())

        // Who knows young people?
        // Since we save bidirectional references we get friendships in both directions:
        _ <- Person.name.Knows.Person.name.age.<(30).get.map(_ ==> List(
          ("vadas", "marko", 29), // vadas knows marko who is 29
          ("josh", "marko", 29), // josh knows marko who is 29
          ("marko", "vadas", 27), // marko knows vadas who is 27
          ("peter", "vadas", 27) // peter knows vadas who is 27
        ))

        // Which older people have young friends
        _ <- Person.name.age.>=(30).Knows.Person.name.age.<(30).get.map(_ ==> List(
          ("peter", 35, "vadas", 27), // peter (35) knows vadas (27)
          ("josh", 32, "marko", 29) // josh (32) knows marko (29)
        ))

        // How many young friends does the older people have?
        _ <- Person.name.age.>=(30).Knows.Person.e(count).age_.<(30).get.map(_ ==> List(
          ("josh", 32, 1), // josh (32) knows 1
          ("peter", 35, 1) // peter (35) knows 1
        ))

        // Marko's friends and their friends
        _ <- Person.name("marko").Knows.Person.name.Knows.Person.name.get.map(_ ==> List(
          ("marko", "vadas", "peter"),
          ("marko", "josh", "marko"),
          ("marko", "vadas", "marko")
        ))

        // Same, nested
        _ <- Person.name("marko").Knows.*(Person.name.a1.Knows.*(Person.name.a1)).get.map(_ ==> List(
          (
            "marko",
            List(
              ("josh", List("marko")),
              ("vadas", List("marko", "peter")),
            )
          )
        ))

        // Marko's friends and their friends (excluding marko)
        _ <- Person.name("marko").Knows.Person.name.Knows.Person.name.not("marko").get.map(_ ==> List(
          ("marko", "vadas", "peter")
        ))

        // Marko's friends' friends
        _ <- Person.name_("marko").Knows.Person.Knows.Person.name.not("marko").get.map(_ ==> List(
          "peter"
        ))

        // Marko's friends' friends that are not already marko's friends (or marko)
        res <- Person(marko).Knows.Person.name.get
        ownCircle = res :+ "marko"
        _ <- Person(marko).Knows.Person.Knows.Person.name.not(ownCircle).get.map(_ ==> List(
          "peter"
        ))
      } yield ()
    }


    "Using the edge properties" - modernGraph2 { implicit conn =>
      for {
        (marko, markoLop, vadas, josh, joshLop, joshRipple, peter, peterLop) <- testData

        // Well-known friends
        _ <- Person(marko).Knows.weight_.>(0.8).Person.name.get.map(_ ==> List("josh"))

        // Well-known friends heavily involved in projects
        _ <- Person(marko).Knows.weight_.>(0.8).Person.name.Created.weight_.>(0.8).Software.name.get.map(_ ==>
          List(("josh", "ripple")))

        // Friends of friends' side projects
        _ <- Person(marko).Knows.Person.Knows.Person.name.not("marko").Created.weight.<(0.5).Software.name.get.map(_ ==>
          List(("peter", 0.2, "lop")))

        // .. or elaborated:
        _ <- Person(marko) // marko entity
          .Knows.Person // friends of marko
          .Knows.Person.name.not("marko") // friends of friends of marko that are not marko
          .Created.weight.<(0.5) // Created (software) with a low weight property
          .Software.name // name of software created
          .get.map(_ ==> List((
          "peter", // peter is a friend of vadas who is a friend of marko
          0.2, // peter participated with a weight of 0.2 in creating
          "lop" // the software "lop"
        )))
      } yield ()
    }
  }
}