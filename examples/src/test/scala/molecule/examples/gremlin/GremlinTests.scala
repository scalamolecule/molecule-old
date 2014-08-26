//package molecule
//package examples.gremlin
//import molecule.util.MoleculeSpec
//
//trait GremlinSpec extends MoleculeSpec {
//
//
//  val List(marko, vadas, josh, peter) = Person.name.age insert List(
//    ("marko", 29),
//    ("vadas", 27),
//    ("josh", 32),
//    ("peter", 35)
//  )
//
//  val List(lop, riple) = Project.name insert List("lop", "riple")
//
//  Person.Knows.weight.Person insert List(
//    //  Person.knows.Person insert List(
//    (marko, 0.5f, vadas),
//    (marko, 1.0f, josh)
//  )
//
//  Person.Created.weight.Project insert List(
//    (marko, 0.4f, lop),
//    (josh, 1.0f, riple),
//    (josh, 0.4f, lop),
//    (peter, 0.2f, lop)
//  )
//
//  def > (i: Int) = 7
//
//  val xx = >(8)
//}
//
//
//class GremlinTests extends GremlinSpec {
//
//
//  "Stubs..." >> {
//
//    // "Markos friends older than 30"
//    Person(marko).Knows.Person.name.age.>(30).get.head === "josh"
//
//    // "Age of Markos friends older than 21, and whose names are 4 characters and start with a ‘jo’ or ‘JO’"
//    Person(marko).Knows.Person.age.name(r"jo.{2}|JO.{2}").age.>(30).get.head === 32
//
//    // “What are the ages of the people that know people that are 30+ years old?”
//    Person.age.Knows.Person.age.>(30) === 29
//
//    // https://github.com/tinkerpop/gremlin/wiki/Backtrack-Pattern
//    // "get the names of those songs that follow Dark Star and are sung by Jerry Garcia"
//    Song.id(89).FollowedBy.name.Singer.name("Jerry Garcia").take(3) === List("Eyes of the World", "Sing me back home", "Morning Dew")
//
//    // https://github.com/tinkerpop/gremlin/wiki/Except-Retain-Pattern
//    // “Who are my friends friends that are not already my friends?”
//    Person(marko).Knows.Person('friend).Knows.Person.!=('friend).getFirst === 5
//
//    // https://github.com/tinkerpop/gremlin/wiki/Flow-Rank-Pattern
//    // "Who created projects"
//    Person.eid.Created.Project.eid === List(
//      (marko, lop),
//      (josh, lop),
//      (peter, lop),
//      (josh, riple)
//    )
//    // "Who created most projects"
//    Person.eid(count).Created.Project === List(
//      marko -> 1,
//      josh -> 2,
//      peter -> 1
//    )
//
//    // https://github.com/thinkaurelius/titan/wiki/Gremlin-Query-Language
//
//    Being.name("hercules").Father.Father.name.getFirst === "saturn"
//
//    Being.name("hercules").eid === 123
//
//    Being.name("hercules").Father.`type`.Mother.`type` === List("god", "human")
//
//    // "Who did hercules battle?"
//    Being.name("hercules").Battled.Being.name.`type` === List(
//      ("nemean", "monster"),
//      ("hydra", "monster"),
//      ("cererus", "monster")
//    )
//
//
//
//    ok
//  }
//}
