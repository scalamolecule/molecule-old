package moleculeTests

import java.util
import molecule.datomic.api.in3_out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}
import java.util.{Collections, Date, UUID, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import molecule.core.ast.elements._
import molecule.core.dsl.base.Init
import molecule.core.exceptions.MoleculeException
import molecule.core.macros.rowAttr.{CastOptNested, CastTypes, JsonBase}
import molecule.datomic.base.marshalling._
import molecule.core.util.Helpers
import molecule.datomic.base.facade.Conn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer
import molecule.core.util.testing.expectCompileError
import molecule.datomic.base.transform.Model2Query
import moleculeTests.dataModels.core.base.schema.CoreTestSchema
import scala.util.control.NonFatal
import molecule.core.marshalling.nodes._
import molecule.core.marshalling.unpackAttr.String2cast
import molecule.core.marshalling.unpackers.Packed2EntityMap
import molecule.core.ops.exception.VerifyModelException
import molecule.datomic.base.marshalling.packers.PackEntityMap
import scala.jdk.CollectionConverters._


object AdhocJvm extends AsyncTestSuite with Helpers
  with String2cast with CastTypes with CastOptNested with JsonBase
  with PackEntityMap {


  lazy val tests = Tests {

    "adhoc jvm" - core { implicit futConn =>
      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
        conn <- futConn



        //        _ = {
        //          val entityMaps = Seq(
        //            Map(":db/id" -> 1L, ":Ns/ref1" -> ":db/add"),
        //            //            Map(":db/id" -> 1L, ":Ns/ints" -> List(2, 3)),
        //            //            Map(":db/id" -> 1L, ":Ns/ref1" -> Map(":db/id" -> 2L, ":Ns/str" -> "data")),
        //            //            Map(":db/id" -> 1L, ":Ns/ref1" -> Map(":db/id" -> 2L, ":Ns/ints" -> List(2, 3))),
        //            //            Map(
        //            //              ":db/id" -> 10,
        //            //              ":Ns/refs1" -> List(
        //            //                Map(
        //            //                  ":db/id" -> 11,
        //            //                  ":Ref1/int1" -> 1
        //            //                ),
        //            //                Map(
        //            //                  ":db/id" -> 12,
        //            //                  ":Ref1/int1" -> 2
        //            //                )
        //            //              ),
        //            //              ":Ns/str" -> "a"
        //            //            ),
        //            //            Map(
        //            //              ":db/id" -> 10,
        //            //              ":Ns/refs1" -> List(
        //            //                Map(
        //            //                  ":db/id" -> 11,
        //            //                  ":Ns/ints" -> List(2, 3)
        //            //                ),
        //            //                Map(
        //            //                  ":db/id" -> 12,
        //            //                  ":Ns/ints" -> List(4, 5)
        //            //                )
        //            //              ),
        //            //              ":Ns/str" -> "a"
        //            //            ),
        //            //            Map(
        //            //              ":db/id" -> 1,
        //            //              ":Ns/int" -> 42,
        //            //              ":Ns/ref1" -> Map((":db/id", 2), (":Ref1/str1", "Hollywood Rd")),
        //            //              ":Ns/str" -> "Ben"
        //            //            )
        //          )
        //
        //          entityMaps.foreach { entityMap =>
        //            val packedMap2 = entityMap2packed2(entityMap)
        //            new Packed2EntityMap(conn).packed2entityMap(packedMap2) ==> entityMap
        //            println("======================================")
        //          }
        //
        //          val entityLists = List(
        //            List(":db/id" -> 1L, ":Ns/ref1" -> ":db/add"),
        //            //            List(":db/id" -> 1L, ":Ns/ints" -> List(2, 3)),
        //            //            List(":db/id" -> 1L, ":Ns/ref1" -> List(":db/id" -> 2L, ":Ns/str" -> "data")),
        //            //            List(":db/id" -> 1L, ":Ns/ref1" -> List(":db/id" -> 2L, ":Ns/ints" -> List(2, 3))),
        //            //            List(
        //            //              ":db/id" -> 10,
        //            //              ":Ns/refs1" -> List(
        //            //                List(
        //            //                  ":db/id" -> 11,
        //            //                  ":Ref1/int1" -> 1
        //            //                ),
        //            //                List(
        //            //                  ":db/id" -> 12,
        //            //                  ":Ref1/int1" -> 2
        //            //                )
        //            //              ),
        //            //              ":Ns/str" -> "a"
        //            //            ),
        //            //            List(
        //            //              ":db/id" -> 10,
        //            //              ":Ns/refs1" -> List(
        //            //                List(
        //            //                  ":db/id" -> 11,
        //            //                  ":Ns/ints" -> List(2, 3)
        //            //                ),
        //            //                List(
        //            //                  ":db/id" -> 12,
        //            //                  ":Ns/ints" -> List(4, 5)
        //            //                )
        //            //              ),
        //            //              ":Ns/str" -> "a"
        //            //            ),
        //            //            List(
        //            //              ":db/id" -> 1,
        //            //              ":Ns/int" -> 42,
        //            //              ":Ns/ref1" -> List((":db/id", 2), (":Ref1/str1", "Hollywood Rd")),
        //            //              ":Ns/str" -> "Ben"
        //            //            )
        //          )
        //
        //          entityLists.foreach { entityList =>
        //            val packedList2 = entityList2packed2(entityList)
        //            new Packed2EntityMap(conn).packed2entityList(packedList2) ==> entityList
        //            println("======================================")
        //          }
        //        }


        //        obj = Obj("", "Ns", false, List(
        //          Prop("Ns_str", "str", "String", 1, "One", None),
        //          Obj("Ns__Refs1", "Refs1", true, List(
        //            Prop("Ref1_int1", "int1", "Int", 1, "One", None),
        //            Obj("Ref1__Ref2", "Ref2", false, List(
        //              Prop("Ref2_int2", "int2", "Int", 1, "One", None),
        //              Prop("Ref2_str2", "str2", "String", 1, "One", None),
        //              Obj("Ref2__Refs3", "Refs3", true, List(
        //                Prop("Ref3_int3", "int3", "Int", 1, "One", None))))))),
        //          Obj("Tx_", "Tx", false, List(
        //            Obj("Ref2_", "Ref2", false, List(
        //              Prop("Ref2_str2", "str2", "String", 1, "One", None),
        //              Prop("Ref2_int2", "int2", "Int", 1, "One", None),
        //              Obj("Ref2__Ref3", "Ref3", false, List(
        //                Prop("Ref3_str3", "str3", "String", 1, "One", None))))),
        //            Obj("Ns_", "Ns", false, Nil)))))
        //
        //        rows <- conn.qRaw(
        //          """[:find  ?b
        //            |        (pull ?a__1 [
        //            |          {(:Ns/refs1 :limit nil) [
        //            |            (:Ref1/int1 :limit nil)
        //            |            {(:Ref1/ref2 :limit nil :default "__none__") [
        //            |              (:Ref2/int2 :limit nil)
        //            |              (:Ref2/str2 :limit nil)
        //            |              {(:Ref2/refs3 :limit nil :default "__none__") [
        //            |                (:Ref3/int3 :limit nil)]}]}]}]) ?c ?d ?f
        //            | :where [(identity ?a) ?a__1]
        //            |        [?a :Ns/str ?b ?tx]
        //            |        [?tx :Ref2/str2 ?c]
        //            |        [?tx :Ref2/int2 ?d]
        //            |        [?tx :Ref2/ref3 ?e]
        //            |        [?e :Ref3/str3 ?f]
        //            |        [?tx :Ns/int ?g]
        //            |        [?tx :Ns/bool ?h]]""".stripMargin)
        //
        //        _ = rows.forEach { row =>
        //          println(row)
        //          //          val last = row.get(1)
        //          //          val list = last.asInstanceOf[jMap[Any, Any]].values().iterator().next.asInstanceOf[jList[Any]]
        //          //          val it = extractFlatValues(2, Nil, List(1))(list)
        //          //          println("----------- " + row.get(0))
        //          //          println(list)
        //          //          println(it.asScala.toList)
        //        }
        //
        ////        packed = OptNested2packed(obj, rows, -1, List(Nil, Nil), List(Nil, List(1))).getPacked
        ////        packed = OptNested2packed(obj, rows, -1, List(Nil, Nil, Nil), List(Nil, Nil, Nil)).getPacked
        ////        packed = OptNested2packed(obj, rows, -1, List(Nil, List(1)), List(Nil, Nil)).getPacked
        //
        //        packed = Nested2packed(obj, rows, 1).getPacked
        //        _ = println(packed)
        //
        //
        //        vs = packed.linesIterator
        //        _ = vs.next()
        //        res = {
        //          def nested1 = {
        //            v = vs.next()
        //            if (v == "◄◄") {
        //              Nil
        //            } else {
        //              val buf = new ListBuffer[(Option[Int], String)]()
        //              do {
        //                buf.append((
        //                  unpackOptOneInt(v),
        //                  unpackOneString(vs.next(), vs)
        //                ))
        //                v = vs.next()
        //              } while (v != "►")
        //              buf.toList
        //            }
        //          }
        //          (
        //            unpackOneString(vs.next(), vs),
        //            nested1
        //          )
        //        }
        //        _ = println(res)


      } yield ()
    }

    //    "adhoc" - products { implicit conn =>
    //      import moleculeTests.dataModels.examples.datomic.dayOfDatomic.dsl.ProductsOrder._
    //
    //      for {
    //
    //      } yield ()
    //    }


    //    "mbrainz" - mbrainz { implicit conn =>
    //      import moleculeTests.dataModels.examples.datomic.mbrainz.dsl.MBrainz._
    //      val ledZeppelinUUID = UUID.fromString("678d88b2-87b0-403b-b63d-5da7465aecc3")
    //
    //      for {
    //        ledZeppelin <- Artist.e.gid_(ledZeppelinUUID).get
    //        mccartney <- Artist.e.gid_(UUID.fromString("ba550d0e-adac-4864-b88b-407cab5e76af")).get
    //        darkSideOfTheMoon <- Release.e.gid_(UUID.fromString("24824319-9bb8-3d1e-a2c5-b8b864dafd1b")).get
    //        dylanHarrisonSessions <- Release.e.gid_(UUID.fromString("67bbc160-ac45-4caf-baae-a7e9f5180429")).get
    //        concertForBangladesh <- Release.e.gid_(UUID.fromString("f3bdff34-9a85-4adc-a014-922eef9cdaa5")).get
    //        dylanHarrisonCd <- Release(dylanHarrisonSessions).media.get
    //        ghosotRiders <- Release(dylanHarrisonSessions).Media.Tracks.e.position_(11).get
    //        gb <- Country.e.name_("United Kingdom").get
    //        georgeHarrison <- Artist.e.name_("George Harrison").get
    //        bobDylan <- Artist.e.name_("Bob Dylan").get
    //
    //
    //      } yield ()
    //    }

    //
    //
    //    "adhoc" - bidirectional { implicit conn =>
    //      import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
    //
    //      for {
    //        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
    //
    //
    //
    //
    //      } yield ()
    //    }

  }
}
