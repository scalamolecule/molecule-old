package moleculeTests

import java.util
import molecule.datomic.api.in3_out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}
import java.util.{Collections, Date, UUID, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import molecule.core.ast.elements._
import molecule.core.dsl.base.Init
import molecule.datomic.base.marshalling.{Flat2packed, Nested2packed, NestedOpt2packed}
import molecule.core.marshalling.unpack.UnpackTypes
import molecule.core.marshalling.attrIndexes._
import molecule.core.util.Helpers
import molecule.datomic.base.facade.Conn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer
import molecule.core.util.testing.expectCompileError
import molecule.datomic.base.transform.Model2Query
import moleculeTests.tests.core.base.schema.CoreTestSchema
import scala.util.control.NonFatal
import molecule.core.macros.attrResolvers.CastTypes

object AdhocJvm extends AsyncTestSuite with Helpers with UnpackTypes with CastTypes {


  lazy val tests = Tests {


    "core" - core { implicit futConn =>
      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
        conn <- futConn

        //        _ <- Ns.int(0).str("x").Ref1.int1(1).save
        //
        //        //        _ <- Ns.int.str.Ref1.int1.inspectGet
        //        _ <- Ns.int.str.Ref1.int1.getObj.map { o =>
        //
        //          // The above molecule and object getter generates the following code:
        //          val obj = new Ns_int with Ns_str with Ns__Ref1[Ref1_int1] {
        //            override lazy val int: Int    = 0
        //            override lazy val str: String = "x"
        //            override def Ref1: Ref1_int1 = new Ref1_int1 {
        //              override lazy val int1: Int = 1
        //            }
        //          }
        //
        //          // This way, we get type inference in the IDE and can access the data
        //          // as named object properties, even in referenced namespaces:
        //
        //
        //          // todo: check that all lines are checked
        //          o.int ==> 0
        //          o.str ==> "x"
        //          o.Ref1.int1 ==> 1
        //        }
        //
        //        indexes = Indexes("Ns", "Ns", false, List(
        //          AttrIndex("Ns_int", "int", 1, true),
        //          AttrIndex("Ns_str", "str", 0, true),
        //          Indexes("Ref1", "Ref1", false, List(
        //            AttrIndex("Ref1_int1", "int1", 1, true)))))
        //
        //        rows <- conn.qRaw(
        //          """[:find  ?b ?c ?e
        //            | :where [?a :Ns/int ?b]
        //            |        [?a :Ns/str ?c]
        //            |        [?a :Ns/ref1 ?d]
        //            |        [?d :Ref1/int1 ?e]]""".stripMargin
        //        )
        //        packed = Flat2packed(indexes, rows, 1).getPacked
        //        _ = println(packed)
        //        _ = {
        //          def row2obj(row: jList[AnyRef]): Init with Ns_int with Ns_str with Ns__Ref1[Init with Ref1_int1] = {
        //            new Init with Ns_int with Ns_str with Ns__Ref1[Init with Ref1_int1] {
        //              final override lazy val int: Int    = castOneInt(row, 0)
        //              final override lazy val str: String = castOne[String](row, 1)
        //              final override def Ref1: Init with Ref1_int1 =
        //                new Init with Ref1_int1 {
        //                  final override lazy val int1: Int = castOneInt(row, 2)
        //                }
        //            }
        //          }
        //        }


        //
        //                _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3_(7777).Ref4.int4_(8888)) insert List(
        //                  ("A", List((Some(11), Some(12), "a"), (Some(110), Some(120), "aa"))),
        //                  ("B", List((Some(13), None, "b"))),
        //                  ("C", List((None, Some(14), "c"))),
        //                  ("D", List((None, None, "d"))),
        //                  ("E", List())
        //                )
        //
        //                _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3.Ref4.int4).get.map(_.sortBy(_._1) ==> List(
        //                  ("A", List((Some(11), Some(12), "a"), (Some(110), Some(120), "aa")), 7777, 8888),
        //                  ("B", List((Some(13), None, "b")), 7777, 8888),
        //                  ("C", List((None, Some(14), "c")), 7777, 8888),
        //                  ("D", List((None, None, "d")), 7777, 8888),
        //                ))
        //
        //                _ <- Ns.str.Refs1.*?(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3.Ref4.int4).get.map(_.sortBy(_._1) ==> List(
        //                  ("A", List((Some(11), Some(12), "a"), (Some(110), Some(120), "aa")), 7777, 8888),
        //                  ("B", List((Some(13), None, "b")), 7777, 8888),
        //                  ("C", List((None, Some(14), "c")), 7777, 8888),
        //                  ("D", List((None, None, "d")), 7777, 8888),
        //                  ("E", List(), 7777, 8888)
        //                ))

        //        indexes = Indexes("Ns", 2, List(
        //          AttrIndex("X", "str", 0, 0, 1, false),
        //          Indexes("Refs1", 2, List(
        //            AttrIndex("X", "int1$", 14, 11, 1, false),
        //            Indexes("Ref2", 1, List(
        //              AttrIndex("X", "int2$", 14, 11, 0, false),
        //              AttrIndex("X", "str2", 0, 0, 0, false))))),
        //          Indexes("Tx", 1, List(
        //            Indexes("Ref3", 1, List(
        //              AttrIndex("X", "int3", 1, 1, 0, true),
        //              Indexes("Ref4", 1, List(
        //                AttrIndex("X", "int4", 1, 1, 0, true)))))))))
        //
        //        rows <- conn.qRaw(
        //          """[:find  ?sort0 ?sort1 ?b
        //            |        (pull ?c__1 [(limit :Ref1/int1 nil)])
        //            |        (pull ?e__2 [(limit :Ref2/int2 nil)])
        //            |        ?g ?i ?k
        //            | :where [?a :Ns/str ?b]
        //            |        [?a :Ns/refs1 ?c]
        //            |        [(identity ?c) ?c__1]
        //            |        [?c :Ref1/ref2 ?e]
        //            |        [(identity ?e) ?e__2]
        //            |        [?e :Ref2/str2 ?g ?tx]
        //            |        [?tx :Ref3/int3 ?i]
        //            |        [?tx :Ref3/ref4 ?j]
        //            |        [?j :Ref4/int4 ?k]
        //            |        [(identity ?a) ?sort0]
        //            |        [(identity ?c) ?sort1]]""".stripMargin
        //        )
        //        packed = Nested2packed(indexes, rows, 1).getPacked
        //
        //        rowsOpt <- conn.qRaw(
        //          """[:find  ?b
        //            |        (pull ?a__1 [
        //            |          {(:Ns/refs1 :limit nil) [
        //            |            (:Ref1/int1 :limit nil :default "__none__")
        //            |            {(:Ref1/ref2 :limit nil :default "__none__") [
        //            |              (:Ref2/int2 :limit nil :default "__none__")
        //            |              (:Ref2/str2 :limit nil)]}]}]) ?c ?e
        //            | :where [(identity ?a) ?a__1]
        //            |        [?a :Ns/str ?b ?tx]
        //            |        [?tx :Ref3/int3 ?c]
        //            |        [?tx :Ref3/ref4 ?d]
        //            |        [?d :Ref4/int4 ?e]]""".stripMargin
        //        )
        //        packedOpt = NestedOpt2packed(indexes, rowsOpt, 7).getPacked
        //
        //        _ = println("=========================================\n" + packed)
        //        _ = println("=========================================\n" + packedOpt)
        //        _ = packed ==> packedOpt


        //        _ <- Ns.str.Refs1.int1.str1$.Ref2.int2.str2$.Tx(Ref3.int3_(7777)) insert List(
        //          ("A", 1, Some("a"), 11, Some("aa")),
        //          ("B", 2, None, 22, None)
        //        )
        //
        //        _ <- Ns.str.Refs1.int1.str1$.Ref2.int2.str2$.Tx(Ref3.int3).inspectGet
        //
        //        //        _ <- Ns.str.Refs1.int1.str1$.Refs2.int2.str2$.Tx(Ref3.int3).get.map(_.sortBy(_._1) ==> List(
        //        //          ("A", 1, Some("a"), 11, Some("aa")),
        //        //          ("B", 2, None, 22, None)
        //        //        ))
        //
        //        indexes = Indexes("Ns", false, List(
        //          AttrIndex("Ns_str", "str", 0, true),
        //          Indexes("Refs1", false, List(
        //            AttrIndex("Ref1_int1", "int1", 1, true),
        //            AttrIndex("Ref1_str1", "str1$", 13, true),
        //            Indexes("Ref2", false, List(
        //              AttrIndex("Ref2_int2", "int2", 1, true),
        //              AttrIndex("Ref2_str2", "str2$", 13, true))))),
        //          Indexes("Tx", false, List(
        //            Indexes("Ref3", false, List(
        //              AttrIndex("Ref3_int3", "int3", 1, true)))))))
        //
        //        rows <- conn.qRaw(
        //          """[:find  ?b ?d
        //            |        (pull ?c__2 [(limit :Ref1/str1 nil)])
        //            |        ?g
        //            |        (pull ?f__4 [(limit :Ref2/str2 nil)])
        //            |        ?i
        //            | :where [(identity ?c) ?c__2]
        //            |        [(identity ?f) ?f__4]
        //            |        [?a :Ns/str ?b]
        //            |        [?a :Ns/refs1 ?c]
        //            |        [?c :Ref1/int1 ?d]
        //            |        [?c :Ref1/ref2 ?f]
        //            |        [?f :Ref2/int2 ?g ?tx]
        //            |        [?tx :Ref3/int3 ?i]]""".stripMargin
        //        )
        //        packed = Flat2packed(indexes, rows, 7).getPacked
        //
        //        _ = println(packed)

        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2_("b").int2_(5).Ref3.str3_("c") + Ns.int_(6).bool_(true)) insert List(
          ("A", List((1, 2, "a", List(3, 4)), (11, 22, "aa", Nil))),
          ("B", Nil)
        )
        /*
        List(
  list(
    Add(TempId(":db.part/user", 1),:Ns/str,A,Card(1)),

    Add(TempId(":db.part/user", 1),:Ns/refs1,TempId(":db.part/user", 5),Card(2)),
    Add(TempId(":db.part/user", 5),:Ref1/int1,1,Card(1)),
    Add(TempId(":db.part/user", 5),:Ref1/ref2,TempId(":db.part/user", 7),Card(1)),
    Add(TempId(":db.part/user", 7),:Ref2/int2,2,Card(1)),
    Add(TempId(":db.part/user", 7),:Ref2/str2,a,Card(1)),
    Add(TempId(":db.part/user", 7),:Ref2/refs3,TempId(":db.part/user", 8),Card(2)),
    Add(TempId(":db.part/user", 8),:Ref3/int3,3,Card(1)),
    Add(TempId(":db.part/user", 7),:Ref2/refs3,TempId(":db.part/user", 9),Card(2)),
    Add(TempId(":db.part/user", 9),:Ref3/int3,4,Card(1)),

    Add(TempId(":db.part/user", 1),:Ns/refs1,TempId(":db.part/user", 6),Card(2)),
    Add(TempId(":db.part/user", 6),:Ref1/int1,11,Card(1)),
    Add(TempId(":db.part/user", 6),:Ref1/ref2,TempId(":db.part/user", 10),Card(1)),
    Add(TempId(":db.part/user", 10),:Ref2/int2,22,Card(1)),
    Add(TempId(":db.part/user", 10),:Ref2/str2,aa,Card(1)),

    Add(datomic.tx,:Ref2/str2,b,Card(1)),
    Add(datomic.tx,:Ref2/int2,5,Card(1)),
    Add(datomic.tx,:Ref2/ref3,TempId(":db.part/user", 4),Card(1)),
    Add(TempId(":db.part/user", 4),:Ref3/str3,c,Card(1)),
    Add(datomic.tx,:Ns/int,6,Card(1)),
    Add(datomic.tx,:Ns/bool,true,Card(1))),

  list(
    Add(TempId(":db.part/user", 2),:Ns/str,B,Card(1)),
    Add(datomic.tx,:Ref2/str2,b,Card(1)),
    Add(datomic.tx,:Ref2/int2,5,Card(1)),
    Add(datomic.tx,:Ref2/ref3,TempId(":db.part/user", 3),Card(1)),
    Add(TempId(":db.part/user", 3),:Ref3/str3,c,Card(1)),
    Add(datomic.tx,:Ns/int,6,Card(1)),
    Add(datomic.tx,:Ns/bool,true,Card(1))))
         */
        //        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.int2.str2.Refs3.*?(Ref3.int3)).Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
        //          ("A", List((1, 2, "a", List(3, 4)), (11, 22, "aa", Nil)), ("b", 5, "c"), (6, true)),
        //          ("B", Nil)
        //        ))
        //        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3)).Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).inspectGet


        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3))
          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
          ("A", List((1, 2, "a", List(3, 4))), ("b", 5, "c"), (6, true))
        ))

      } yield ()
    }

    //    "adhoc" - products { implicit conn =>
    //      import moleculeTests.tests.examples.datomic.dayOfDatomic.dsl.ProductsOrder._
    //
    //      for {
    //
    //      } yield ()
    //    }


    //    "mbrainz" - mbrainz { implicit conn =>
    //      import moleculeTests.tests.examples.datomic.mbrainz.dsl.MBrainz._
    //      val ledZeppelinUUID = UUID.fromString("678d88b2-87b0-403b-b63d-5da7465aecc3")
    //
    //      for {
    //        ledZeppelin <- Artist.e.gid_(ledZeppelinUUID).get
    //        mccartney <- Artist.e.gid_(UUID.fromString("ba550d0e-adac-4864-b88b-407cab5e76af")).get
    //        darkSideOfTheMoon <- Release.e.gid_(UUID.fromString("24824319-9bb8-3d1e-a2c5-b8b864dafd1b")).get
    //        dylanHarrisonSessions <- Release.e.gid_(UUID.fromString("67bbc160-ac45-4caf-baae-a7e9f5180429")).get
    //        concertForBangladesh <- Release.e.gid_(UUID.fromString("f3bdff34-9a85-4adc-a014-922eef9cdaa5")).get
    //        dylanHarrisonCd <- Release(dylanHarrisonSessions).media.get
    //        ghostRiders <- Release(dylanHarrisonSessions).Media.Tracks.e.position_(11).get
    //        gb <- Country.e.name_("United Kingdom").get
    //        georgeHarrison <- Artist.e.name_("George Harrison").get
    //        bobDylan <- Artist.e.name_("Bob Dylan").get
    //
    //
    //      } yield ()
    //    }


    //    "adhoc" - bidirectional { implicit conn =>
    //    for {
    //      _ <- Future(1 ==> 1) // dummy to start monad chain if needed
    //
    //    } yield ()
    //  }
  }
}
