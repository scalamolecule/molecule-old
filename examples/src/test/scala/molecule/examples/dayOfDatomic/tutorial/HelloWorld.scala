//package molecule
//package examples.dayOfDatomic.tutorial
////import scala.language.reflectiveCalls
//import datomic.Peer
//import datomic.Util._
//import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
//
//class HelloWorld extends DayOfAtomicSpec {
//
//  "Hello World" >> {
//    implicit val conn = init("hello-world")
//
//    // Transaction input is data
//    val tempid = Peer.tempid(":db.part/user")
//    val txresult = conn.transact(list(list(":db/add", tempid, ":db/doc", "Hello world")))
//
//    // Transaction result is data
//    txresult.get().toString.take(26) === "{:db-before datomic.db.Db@" // etc...
//
//    val dbVal = conn.db()
//
//    // Query input is data
//    val qresult = Peer.q( """[:find ?e :where [?e :db/doc "Hello world"]]""", dbVal)
//
//    // Query result is data
//    dbVal.entity(qresult.toList.head.head).get(":db/id") === 17592186045417L
//
//    // Schema itself is data
//    dbVal.entity(":db/doc").get(":db/id") === 61
//  }
//}