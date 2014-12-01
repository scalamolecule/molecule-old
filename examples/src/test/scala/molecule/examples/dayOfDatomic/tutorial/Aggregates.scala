package molecule.examples.dayOfDatomic.tutorial
import molecule._
import molecule.examples.dayOfDatomic.schema.AggregatesSchema
import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
//import molecule.examples.dayOfDatomic.samples._
import molecule.examples.dayOfDatomic.dsl.aggregates._


class Aggregates extends DayOfAtomicSpec {

  "Aggregates" >> {
    implicit val conn = load(AggregatesSchema.tx, "Aggregates")

    // Insert data
    Obj.name.meanRadius
      .tx(Data.source_("http://en.wikipedia.org/wiki/List_of_Solar_System_objects_by_size")) insert Seq(
      ("Sun", 696000.0),
      ("Jupiter", 69911.0),
      ("Saturn", 58232.0),
      ("Uranus", 25362.0),
      ("Neptune", 24622.0),
      ("Earth", 6371.0),
      ("Venus", 6051.8),
      ("Mars", 3390.0),
      ("Ganymede", 2631.2),
      ("Titan", 2576.0),
      ("Mercury", 2439.7),
      ("Callisto", 2410.3),
      ("Io", 1821.5),
      ("Moon", 1737.1),
      ("Europa", 1561.0),
      ("Triton", 1353.4),
      ("Eris", 1163.0)
    )

    // Aggregated Attributes ..........................

    // Maximum value(s)
    Obj.meanRadius(max).get.head === 696000.0
//    Obj.meanRadius(max(3)).get.head === List(1, 2, 3)
//
//    // Minimum value(s)
//    Obj.meanRadius(min).get.head === 1163.0
//    Obj.meanRadius(min(3)).get.head === List(1, 2, 3)
//
//    // Random value(s) - duplicates possible
//    Obj.meanRadius(rand).get.head === 1163.0
//    Obj.meanRadius(rand(5)).get.head === List(1, 2, 3, 4, 5)
//
//    // Sample value(s) - no duplicates
//    Obj.meanRadius(sample).get.head === 1163.0
//    Obj.meanRadius(sample(5)).get.head === List(1, 2, 3, 4, 5)
//
//
//    // Aggregate Calculations .............................
//
//    // Count
//    Obj.name(count).get.head === 17
//    // (or)
//    Obj.name.get.size === 17
//
//    // Count distinct
//    Obj.name(countDistinct).get.head === 17
//
//    // Sum
//    Obj.meanRadius(sum).get.head === 696000.0
//
//    // Average
//    Obj.meanRadius(avg).get.head === 696000.0
//
//    // Median
//    Obj.meanRadius(median).get.head === 696000.0
//
//    // Variance
//    Obj.meanRadius(variance).get.head === 696000.0
//
//    // Standard deviation
//    Obj.meanRadius(stddev).get.head === 696000.0
//
//
//    // Schema aggregations .............................
//    import molecule.schemas.Db
//
//    // What is the average length of a schema name?
//    Db.a(avg(count)).get.head === 5.1
//
//    // Todo Custom aggregates
//    // ...and the mode(s) -
//
//    // How many attributes and value types does this; schema use ?
//    Db.e(count).valueType(countDistinct).get.head ===(3, 2)

    ok
  }

//  "Aggregates" >> {
//    implicit val conn = init("aggregates", "bigger-than-pluto.edn")
//
//    // how many objects are there?
//    m(Obj).count
//
//    // largest radius?
//    m(Obj.meanRadius(max))
//
//    // Smallest radius
//    m(Obj.meanRadius(min))
//
//
//    // Average radius
//    m(Obj.meanRadius(avg))
//
//    // Median radius
//    m(Obj.meanRadius(median))
//
//    // stddev
//    m(Obj.meanRadius(stddev))
//
//    // random solar system object
//    m(Obj(rand))
//    // or
//    m(Obj).rand
//
//    // smallest 3
//    m(Obj.meanRadius min 3)
//    m(Obj.meanRadius).asc.get(3)
//
//    // largest 3
//    m(Obj.meanRadius max 3)
//    m(Obj.meanRadius).desc.get(3)
//
//    // 5 random (duplicates possible)
//    m(Obj(rand, 5))
//
//    // Choose 5, no duplicates
//    m(Obj(sample, 5))
//
//    // What is the average length of a schema name?
//    m(e.ident.name(count)).avg
//
//    // ...and the mode(s) ?
//    m(e.ident.name(count)).modes
//
//    // How many attributes and value types does this; schema use ?
//    m(e(count).ident(countDistinct))
//  }
}