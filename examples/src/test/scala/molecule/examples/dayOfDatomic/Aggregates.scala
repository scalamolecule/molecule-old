package molecule.examples.dayOfDatomic
import molecule._
import molecule.examples.dayOfDatomic.dsl.aggregates._
import molecule.examples.dayOfDatomic.schema.AggregatesSchema
import molecule.util.MoleculeSpec
import scala.language.postfixOps


class Aggregates extends MoleculeSpec {

  implicit val conn = load(AggregatesSchema.tx, "Aggregates")

  val planets  = Seq("Sun", "Jupiter", "Saturn", "Uranus", "Neptune", "Earth", "Venus", "Mars", "Ganymede", "Titan", "Mercury", "Callisto", "Io", "Moon", "Europa", "Triton", "Eris")
  val radiuses = Seq(696000.0, 69911.0, 58232.0, 25362.0, 24622.0, 6371.0, 6051.8, 3390.0, 2631.2, 2576.0, 2439.7, 2410.3, 1821.5, 1737.1, 1561.0, 1353.4, 1163.0)
  val url      = "http://en.wikipedia.org/wiki/List_of_Solar_System_objects_by_size"

  // Insert data with tx meta data
  Obj.name.meanRadius.tx_(Data.source_(url)) insert (planets zip radiuses)


  "Aggregated Attributes" >> {

    // Maximum value(s)
    Obj.meanRadius(max).one === 696000.0
    Obj.meanRadius(max(3)).one === List(696000.0, 69911.0, 58232.0)


    // Minimum value(s)
    Obj.meanRadius(min).one === 1163.0
    Obj.meanRadius(min(3)).one === List(1163.0, 1353.4, 1561.0)


    // Random value(s) - duplicates possible
    val random = Obj.meanRadius(rand).one
    radiuses must contain(random)

    val randoms = Obj.meanRadius(rand(5)).one
    randoms.size === 5
    randoms.map(radiuses.contains(_)).reduce(_ && _) === true


    // Sample values - no duplicates
    val samples = Obj.meanRadius(sample(5)).one
    samples.size === 5
    samples.distinct.size === 5
    samples.map(radiuses.contains(_)).reduce(_ && _) === true


    println("Random:  " + random)
    println("Randoms: " + randoms)
    println("Samples: " + samples + "\n")
    ok
  }


  "Aggregate Calculations" >> {

    Obj.name.apply(count).one === 17

    Obj.name(countDistinct).one === 17

    Obj.meanRadius(sum).one === 907633.0

    Obj.meanRadius(avg).one === 53390.17647058824

    Obj.meanRadius(median).one === 2631.2

    Obj.meanRadius(variance).one === 2.6212428511091217E10

    Obj.meanRadius(stddev).one === 161902.5278094546
  }


  "Schema aggregations" >> {
    import molecule.schema.Db

    // What is the average length of a schema name?
    Db.a.length(avg).one === 12.9

    // How many attributes and value types does this schema use?
    Db.a(count).valueType(countDistinct).one ===(37, 8)
  }


  "Monsters" >> {

    Monster.name.heads insert List(
      ("Cerberus", 3),
      ("Medusa", 1),
      ("Cyclops", 1),
      ("Chimera", 1))

    Monster.heads(sum).one === 6

    Monster.name.heads(sum).get === List(
      ("Cerberus", 3),
      ("Chimera", 1),
      ("Cyclops", 1),
      ("Medusa", 1))


    // Testing group by...

    // Adding a twin Cyclop with 4 heads (not factual!)
    Monster.name.heads insert ("Cyclops", 4)

    Monster.heads(sum).one === 10

    // Note how the query group by name so that we get 1 + 4 = 5 Cyclopes heads
    Monster.name.heads(sum).get === List(
      ("Cerberus", 3),
      ("Chimera", 1),
      ("Cyclops", 5),
      ("Medusa", 1))
  }
}