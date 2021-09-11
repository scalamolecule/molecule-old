//package moleculeTests
//
//import molecule.core.macros.rowAttr.{CastOptNested, CastTypes, JsonBase}
//import molecule.core.marshalling.unpackAttr.String2cast
//import molecule.core.marshalling.unpackers.Packed2EntityMap
//import molecule.core.util.Helpers
//import molecule.datomic.base.marshalling.packers.PackEntityMap
//import moleculeTests.setup.AsyncTestSuite
//import utest._
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future
//
//
//object EntityMapListPacking extends AsyncTestSuite with Helpers
//  with String2cast with CastTypes with CastOptNested with JsonBase
//  with PackEntityMap {
//
//
//  lazy val tests = Tests {
//
//    "entity map/list" - core { implicit futConn =>
//      for {
//        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
//        conn <- futConn
//
//        _ = {
//          val entityMaps = Seq(
//            Map(":db/id" -> 1L, ":Ns/ref1" -> ":db/add"),
//            Map(":db/id" -> 1L, ":Ns/ints" -> List(2, 3)),
//            Map(":db/id" -> 1L, ":Ns/ref1" -> Map(":db/id" -> 2L, ":Ns/str" -> "data")),
//            Map(":db/id" -> 1L, ":Ns/ref1" -> Map(":db/id" -> 2L, ":Ns/ints" -> List(2, 3))),
//            Map(
//              ":db/id" -> 10,
//              ":Ns/refs1" -> List(
//                Map(
//                  ":db/id" -> 11,
//                  ":Ref1/int1" -> 1
//                ),
//                Map(
//                  ":db/id" -> 12,
//                  ":Ref1/int1" -> 2
//                )
//              ),
//              ":Ns/str" -> "a"
//            ),
//            Map(
//              ":db/id" -> 10,
//              ":Ns/refs1" -> List(
//                Map(
//                  ":db/id" -> 11,
//                  ":Ns/ints" -> List(2, 3)
//                ),
//                Map(
//                  ":db/id" -> 12,
//                  ":Ns/ints" -> List(4, 5)
//                )
//              ),
//              ":Ns/str" -> "a"
//            ),
//            Map(
//              ":db/id" -> 1,
//              ":Ns/int" -> 42,
//              ":Ns/ref1" -> Map((":db/id", 2), (":Ref1/str1", "Hollywood Rd")),
//              ":Ns/str" -> "Ben"
//            )
//          )
//
//          entityMaps.foreach { entityMap =>
//            val packedMap2 = entityMap2packed2(entityMap)
//            new Packed2EntityMap(conn).packed2entityMap(packedMap2) ==> entityMap
//            println("======================================")
//          }
//
//
//          val entityLists = List(
//            List(":db/id" -> 1L, ":Ns/ref1" -> ":db/add"),
//            List(":db/id" -> 1L, ":Ns/ints" -> List(2, 3)),
//            List(":db/id" -> 1L, ":Ns/ref1" -> List(":db/id" -> 2L, ":Ns/str" -> "data")),
//            List(":db/id" -> 1L, ":Ns/ref1" -> List(":db/id" -> 2L, ":Ns/ints" -> List(2, 3))),
//            List(
//              ":db/id" -> 10,
//              ":Ns/refs1" -> List(
//                List(
//                  ":db/id" -> 11,
//                  ":Ref1/int1" -> 1
//                ),
//                List(
//                  ":db/id" -> 12,
//                  ":Ref1/int1" -> 2
//                )
//              ),
//              ":Ns/str" -> "a"
//            ),
//            List(
//              ":db/id" -> 10,
//              ":Ns/refs1" -> List(
//                List(
//                  ":db/id" -> 11,
//                  ":Ns/ints" -> List(2, 3)
//                ),
//                List(
//                  ":db/id" -> 12,
//                  ":Ns/ints" -> List(4, 5)
//                )
//              ),
//              ":Ns/str" -> "a"
//            ),
//            List(
//              ":db/id" -> 1,
//              ":Ns/int" -> 42,
//              ":Ns/ref1" -> List((":db/id", 2), (":Ref1/str1", "Hollywood Rd")),
//              ":Ns/str" -> "Ben"
//            )
//          )
//
//          entityLists.foreach { entityList =>
//            val packedList2 = entityList2packed2(entityList)
//            new Packed2EntityMap(conn).packed2entityList(packedList2) ==> entityList
//            println("======================================")
//          }
//        }
//
//      } yield ()
//    }
//  }
//}
