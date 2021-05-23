package molecule.tests.examples.gremlin.gettingStarted

import molecule.datomic.api.out4._
import molecule.datomic.base.facade.Conn
import molecule.setup.AsyncTestSuite
import utest._
import molecule.tests.examples.gremlin.gettingStarted.dsl.ModernGraph1._
import scala.concurrent.ExecutionContext

/*
  Bidirectional references - comparing with Gremlin tutorial at:

  http://tinkerpop.apache.org/docs/current/tutorials/getting-started/

  In the tutorial, the weight property is not used at all and we therefor here use a
  bidirectional self-reference `Friends` without the weight property:

      Person <--> Friends[Person]

  We also have a normal uni-directional relationship from `Person` to `Software`:

      Person --> Software

  See schema definition in
  examples/src/main/scala/molecule/examples/gremlin/schema/ModernGraphDefinition1.scala
*/

object Friends extends AsyncTestSuite {

  def testData(implicit conn: Conn, ec: ExecutionContext) = {
    for {
      /*
        Sample bidirectional friendships graph (with extra vadas--peter friendship added for more fun examples)

          marko (29) ---- josh (32)
            |
            |
          vadas (27) ---- peter (35)
      */

      // Software
      tx <- Software.name.lang insert Seq(
        ("lop", "java"),
        ("ripple", "java")
      )
      List(lop, ripple) = tx.eids

      // People and software created
      tx2 <- Person.name.age.software insert Seq(
        ("marko", 29, Set(lop)),
        ("vadas", 27, Set[Long]()),
        ("josh", 32, Set(lop, ripple)),
        ("peter", 35, Set(lop))
      )
      List(marko, vadas, josh, peter) = tx2.eids

      // Friendships
      _ <- Person(marko).friends(vadas, josh).update

      // Extra friendship not in the tutorial so that we can make fof queries
      _ <- Person(vadas).friends.assert(peter).update
    } yield {
        (lop, ripple, marko, vadas, josh, peter)
    }
  }


  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "The first 5 minutes" - modernGraph1 { implicit conn =>
      for {
        (lop, ripple, marko, vadas, josh, peter) <- testData

        // Marko's name
        // gremlin> g.V(1).values('name')
        _ <- Person(marko).name.get.map(_.head ==> "marko")

        // Marko knows (entity ids)
        // g.V(1).outE('knows')
        _ <- Person(marko).friends.get.map(_.head ==> Set(vadas, josh))

        // Marko knows (by name)
        // g.V(1).outE('knows').inV().values('name')
        // g.V(1).out('knows').values('name')
        _ <- Person(marko).Friends.name.get === List("vadas", "josh")
        _ <- Person(marko).friends.get.map(_.head ==> Set(vadas, josh))

        // We can uniformly query in the other direction too
        // vadas and josh also know marko
        _ <- Person(vadas).friends.get.map(_.head ==> Set(peter, marko))
        _ <- Person(josh).friends.get.map(_.head ==> Set(marko))

        // "Markos friends older than 30"
        // g.V(1).out('knows').has('age', gt(30)).values('name')
        _ <- Person(marko).Friends.name.age_.>(30).get === List("josh")
      } yield ()
    }


    "Next 15 minutes" - modernGraph1 { implicit conn =>
      for {
        (lop, ripple, marko, vadas, josh, peter) <- testData

        // Find Marko id
        // g.V().has('name','marko')
        _ <- Person.e.name_("marko").get === List(marko)

        // Marko created software named...
        // g.V().has('name','marko').out('created')
        // g.V().has('name','marko').out('created').values('name')
        _ <- Person.name_("marko").Software.name.get === List("lop")


        // Ages of marko and vadas
        // g.V().has('name',within('vadas','marko')).values('age')
        _ <- Person.name_("marko", "vadas").age.get === List(27, 29)

        // Mean/average age of marko and vadas
        // g.V().has('name',within('vadas','marko')).values('age').mean()
        _ <- Person.name_("marko", "vadas").age(avg).get.map(_.head ==> 28.0)


        // Who are the people that marko develops software with?
        // g.V().has('name','marko').out('created').in('created').values('name')

        // It's idiomatic for Datomic to split such query and use the output of
        // one query as input for the next one.

        // First find ids of software projects that marko has participated in
        r1 <- Person.name_("marko").software.get
        markoSoftware = r1.head.toList

        // Then find names of persons that have participated in those projects
        _ <- Person.name.software_(markoSoftware).get === List("peter", "josh", "marko")

        // Excluding marko from the result (re-using the first sub-query)
        // g.V().has('name','marko').as('exclude').out('created').in('created').where(neq('exclude')).values('name')
        _ <- Person.name.not("marko").software_(markoSoftware).get === List("peter", "josh")

        // Untyped generic queries are not directly handled by Molecule.
        // With a namespace prefix we though can similar and type safe results
        // g.V().group().by(label).by('name')
        _ <- Person.name.get === List("peter", "vadas", "josh", "marko")
        _ <- Software.name.get === List("ripple", "lop")
      } yield ()
    }


    "What else could we ask?" - modernGraph1 { implicit conn =>
      for {
        (lop, ripple, marko, vadas, josh, peter) <- testData

        // What friends does each person have (nested lists)
        _ <- m(Person.name.Friends * Person.name).get === List(
          ("marko", List("vadas", "josh")),
          ("vadas", List("marko", "peter")),
          ("josh", List("marko")),
          ("peter", List("vadas"))
        )

        // Who has most friends
        _ <- Person.name.friends(count).get.map(_.sortBy(_._2).reverse ==> List(
          ("vadas", 2),
          ("marko", 2),
          ("peter", 1),
          ("josh", 1)
        ))

        // Markos friends older than 30
        _ <- Person(marko).Friends.name.age.>(30).get === List(("josh", 32))

        // Josh's friends older than 30 (none)
        _ <- Person(josh).Friends.name.age.>(30).get === List()

        // Who knows young people?
        // Since we save bidirectional references we get friendships in both directions:
        _ <- Person.name.Friends.name.age.<(30).get === List(
          ("vadas", "marko", 29), // vadas knows marko who is 29
          ("josh", "marko", 29), // josh knows marko who is 29
          ("marko", "vadas", 27), // marko knows vadas who is 27
          ("peter", "vadas", 27) // peter knows vadas who is 27
        )

        // Which older people have young friends
        _ <- Person.name.age.>=(30).Friends.name.age.<(30).get === List(
          ("peter", 35, "vadas", 27), // peter (35) knows vadas (27)
          ("josh", 32, "marko", 29) // josh (32) knows marko (29)
        )

        // Older friends of Vadas + Vadas age
        _ <- Person.name.age.>=(30).Friends.e_(vadas).age.get === List(
          ("peter", 35, 27)
        )

        // How many young friends does the older people have?
        _ <- Person.name.age.>=(30).Friends.e(count).age_.<(30).get === List(
          ("josh", 32, 1), // josh (32) knows 1
          ("peter", 35, 1) // peter (35) knows 1
        )

        // Marko's friends and their friends
        _ <- Person.name("marko").Friends.name.Friends.name.get === List(
          ("marko", "vadas", "peter"),
          ("marko", "josh", "marko"),
          ("marko", "vadas", "marko")
        )

        // Marko's friends and their friends, nested
        _ <- m(
          Person.name("marko").Friends.*(
            Person.name.Friends.*(
              Person.name))).get === List(
          (
            "marko",
            List(
              ("vadas", List("marko", "peter")),
              ("josh", List("marko")))
          )
        )

        // Marko's friends and their friends (excluding marko)
        _ <- Person.name("marko").Friends.name.Friends.name.not("marko").get === List(
          ("marko", "vadas", "peter")
        )

        // Marko's friends' friends
        _ <- Person.name_("marko").Friends.Friends.name.not("marko").get === List(
          "peter"
        )

        // Marko's friends' friends that are not already marko's friends (or marko)
        r1 <- Person(marko).Friends.name.get
        markoFriends = r1 :+ "marko"
        _ <- Person(marko).Friends.Friends.name.not(markoFriends).get === List(
          "peter"
        )
      } yield ()
    }
  }
}