//package molecule
//package examples.dayOfDatomic.tutorial
//import datomic.Peer
//import datomic.Util._
//import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
//import scala.collection.JavaConversions._
//
//class HelloWorld extends DayOfAtomicSpec {
//
//  "Hello World" >> {
//
//    // Transaction input is data
//    val tempid = Peer.tempid(":db.part/user")
//    val txData = list(list(
//      ":db/add", tempid,
//      ":db/doc", "Hello world"))
//    val conn = load(txData, "hello-world")
//
//    // Transaction result is data
//    val txresult = conn.transact(txData)
//    txresult.get().toString.take(26) === "{:db-before datomic.db.Db@" // etc...
//
//    // Database is a value
//    val dbValue = conn.db()
//
//    // Query input is data
//    val qresult = Peer.q( """[:find ?e :where [?e :db/doc "Hello world"]]""", dbValue)
//
//    // Query result is data
//    val id = qresult.toList.head.head
//
//    // Entity is a navigable view over data
//    dbValue.entity(id).get(":db/id") === 17592186045417L
//
//    // Schema itself is data
//    dbValue.entity(":db/doc").get(":db/id") === 62
//  }
//}