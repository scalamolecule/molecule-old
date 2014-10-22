//package molecule.examples.dayOfDatomic.tutorial
//
//import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
//import molecule.examples.dayOfDatomic.samples._
//
//class Aggregates extends DayOfAtomicSpec with Generators {
//
//
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
//}