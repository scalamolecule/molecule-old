package moleculeTests.tests.examples.datomic.dayOfDatomic

import molecule.datomic.api.out5._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.examples.datomic.dayOfDatomic.dsl.Aggregates._
import utest._
import molecule.core.util.Executor._
import scala.language.postfixOps

object Aggregates extends AsyncTestSuite {

  val planets  = Seq("Sun", "Jupiter", "Saturn", "Uranus", "Neptune", "Earth", "Venus", "Mars", "Ganymede", "Titan", "Mercury", "Callisto", "Io", "Moon", "Europa", "Triton", "Eris")
  val radiuses = Seq(696000.0, 69911.0, 58232.0, 25362.0, 24622.0, 6371.0, 6051.8, 3390.0, 2631.2, 2576.0, 2439.7, 2410.3, 1821.5, 1737.1, 1561.0, 1353.4, 1163.0)
  val url      = "http://en.wikipedia.org/wiki/List_of_Solar_System_objects_by_size"

  lazy val tests = Tests {

    "Aggregated Attributes" - aggregate { implicit conn =>
      for {
        _ <- Obj.name.meanRadius.Tx(Data.source_(url)) insert (planets zip radiuses)

        // Maximum value(s)
        _ <- Obj.meanRadius(max).get.map(_.head ==> 696000.0)
        _ <- Obj.meanRadius(max(3)).get.map(_.head ==> List(696000.0, 69911.0, 58232.0))


        // Minimum value(s)
        _ <- Obj.meanRadius(min).get.map(_.head ==> 1163.0)
        _ <- Obj.meanRadius(min(3)).get.map(_.head ==> List(1163.0, 1353.4, 1561.0))


        // Random value(s) - duplicates possible

        // Single random value
        _ <- Obj.meanRadius(rand).get.map(rr => radiuses.contains(rr.head) ==> true)

        // 5 random values
        _ <- Obj.meanRadius(rand(5)).get.map { rr =>
          rr.head.size ==> 5
          rr.head.map(radiuses.contains(_)).reduce(_ && _) ==> true
        }

        // Sample values - no duplicates

        // Single sample value
        _ <- Obj.meanRadius(sample).get.map(rr => radiuses.contains(rr.head) ==> true)

        // 5 sample values
        _ <- Obj.meanRadius(sample(5)).get.map { samples =>
          samples.head.size ==> 5
          samples.head.distinct.size ==> 5
          samples.head.map(radiuses.contains(_)).reduce(_ && _) ==> true
        }
      } yield ()
    }


    "Aggregate Calculations" - aggregate { implicit conn =>
      for {
        _ <- Obj.name.meanRadius.Tx(Data.source_(url)) insert (planets zip radiuses)
        _ <- Obj.name(count).get.map(_.head ==> 17)
        _ <- Obj.name(countDistinct).get.map(_.head ==> 17)
        _ <- Obj.meanRadius(sum).get.map(_.head ==> 907633.0)
        _ <- Obj.meanRadius(avg).get.map(_.head ==> 53390.17647058824)
        _ <- Obj.meanRadius(median).get.map(_.head ==> 2631.2)
        _ <- Obj.meanRadius(variance).get.map(_.head ==> 2.6212428511091213E10)
        _ <- Obj.meanRadius(stddev).get.map(_.head ==> 161902.52780945456)

        // We can even fetch multiple aggregates in one query:
        _ <- Obj.meanRadius(sum).meanRadius(avg).meanRadius(median).get
          .map(_.head ==> (907633.0, 53390.17647058824, 2631.2))
      } yield ()
    }


    "Schema aggregations" - aggregate { implicit conn =>
      for {
        // What is the average length of a schema name?
        _ <- Schema.a.get.map(attrs =>
          attrs.map(_.length).sum / attrs.size ==> 12
        )

        // How many attributes does this schema use?
        _ <- Schema.a.get.map(_.sorted ==> List(
          ":Data/source",
          ":Monster/heads",
          ":Monster/name",
          ":Obj/meanRadius",
          ":Obj/name",
        ))

        // How many (Datomic) types does this schema use?
        _ <- Schema.tpe.get.map(_ ==> List(
          "string", // String
          "double", // Double
          "long", // Int
        ))
      } yield ()
    }


    "Monsters" - aggregate { implicit conn =>
      for {
        _ <- Monster.name.heads insert List(
          ("Cerberus", 3),
          ("Medusa", 1),
          ("Cyclops", 1),
          ("Chimera", 1))

        _ <- Monster.heads(sum).get.map(_.head ==> 6) // 3 + 1 + 1 + 1

        // Other aggregations
        _ <- Monster.heads(avg).get.map(_.head ==> 1.5) // 6 / 4
        _ <- Monster.heads(median).get.map(_.head ==> 1)
        _ <- Monster.heads(variance).get.map(_.head ==> 0.75) // ?
        _ <- Monster.heads(stddev).get.map(_.head ==> 0.8660254037844386) // ?


        // Add a twin Cyclop with 4 heads (not factual!)
        _ <- Monster.name.heads.insert("Cyclops", 4)

        // Now we have a different set to aggregate over and compare
        _ <- Monster.name.heads.get.map(_.sortBy(t => (t._1, t._2)) ==> List(
          ("Cerberus", 3),
          ("Chimera", 1),
          ("Cyclops", 1),
          ("Cyclops", 4),
          ("Medusa", 1)))

        _ <- Monster.heads(sum).get.map(_.head ==> 10) // 3 + 1 + 1 + 1 + 4
        _ <- Monster.heads(avg).get.map(_.head ==> 2.0) // 10 / 5
        _ <- Monster.heads(median).get.map(_.head ==> 1)
        _ <- Monster.heads(variance).get.map(_.head ==> 1.6)
        _ <- Monster.heads(stddev).get.map(_.head ==> 1.2649110640673518)

        // Note how the query group by name so that we get 1 + 4 = 5 Cyclopes heads
        _ <- Monster.name.heads(sum).get.map(_.sortBy(t => (t._1, t._2)) ==> List(
          ("Cerberus", 3),
          ("Chimera", 1),
          ("Cyclops", 5), // <-- 1 + 4
          ("Medusa", 1)))
      } yield ()
    }
  }
}