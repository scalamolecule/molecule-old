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
import molecule.core.macros.rowAttr.{CastOptNested, CastTypes, JsonBase}
import molecule.datomic.base.marshalling._
import molecule.core.util.Helpers
import molecule.datomic.base.facade.Conn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer
import molecule.core.util.testing.expectCompileError
import molecule.datomic.base.transform.Model2Query
import moleculeTests.tests.core.base.schema.CoreTestSchema
import scala.util.control.NonFatal
import molecule.core.marshalling.nodes._
import molecule.core.marshalling.unpackAttr.String2cast
import scala.jdk.CollectionConverters._


object AdhocJvm extends AsyncTestSuite with Helpers
  with String2cast with CastTypes with CastOptNested with JsonBase {


  lazy val tests = Tests {

    "core" - core { implicit futConn =>
      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
        conn <- futConn

        _ <- Ns.int(1).save
        _ <- m(Ns.int(?))(1).get.map(_ ==> List(1))


        //        obj = Obj("", "Ns", false, List(
        //          Prop("Ns_str", "str", "String", 1, "One", None),
        //          Obj("Ns__Refs1", "Refs1", true, List(
        //            Obj("Ref1__Ref2", "Ref2", false, List(
        //              Prop("Ref2_int2_", "int2$", "Int", 1, "OptOne", None),
        //              Prop("Ref2_str2", "str2", "String", 1, "One", None)))))))
        //
        //        rows <- conn.qRaw(
        //          """[:find  ?sort0 ?sort1 ?b
        //            |        (pull ?e__1 [(limit :Ref2/int2 nil)])
        //            |        ?g
        //            | :where [(identity ?e) ?e__1]
        //            |        [(identity ?a) ?sort0]
        //            |        [(identity ?c) ?sort1]
        //            |        [?a :Ns/str ?b]
        //            |        [?a :Ns/refs1 ?c]
        //            |        [?c :Ref1/int1 ?d]
        //            |        [?c :Ref1/ref2 ?e]
        //            |        [?e :Ref2/str2 ?g]]""".stripMargin)
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
        //        //        packed = OptNested2packed(obj, rows, -1, List(Nil, Nil), List(Nil, List(1))).getPacked
        //        //        packed = OptNested2packed(obj, rows, -1, List(Nil, Nil, Nil), List(Nil, Nil, Nil)).getPacked
        //        //        packed = OptNested2packed(obj, rows, -1, List(Nil, List(1)), List(Nil, Nil)).getPacked
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
        //              val buf    = new ListBuffer[(Option[Int], String)]()
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
        //        packedOpt = OptNested2packed(indexes, rowsOpt, 7).getPacked
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


        //        _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3_(1)) insert List(
        //          ("A", List((Some(11), Some(12), "a"))),
        //          ("B", List((Some(13), None, "b"))),
        //          ("C", List((None, Some(14), "c"))),
        //          ("D", List((None, None, "d"))),
        //          ("E", List())
        //        )

        //        _ <- Ns.str.Refs1.*?(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3).inspectGet

        //        rows <- conn.qRaw(
        //          """[:find  ?b
        //            |        (pull ?a__1 [
        //            |          {(:Ns/refs1 :limit nil) [
        //            |            (:Ref1/int1 :limit nil :default "__none__")
        //            |            {(:Ref1/ref2 :limit nil :default "__none__") [
        //            |              (:Ref2/int2 :limit nil :default "__none__")
        //            |              (:Ref2/str2 :limit nil)]}]}]) ?c
        //            | :where [?a :Ns/str ?b ?tx]
        //            |        [(identity ?a) ?a__1]
        //            |        [?tx :Ref3/int3 ?c]]""".stripMargin)
        //
        //        row = rows.iterator.next
        //        sub = row.get(1)
        //        list = sub.asInstanceOf[jMap[_, _]].values.iterator.next.asInstanceOf[jList[Any]]
        //        it = extractFlatValues(list, 3, List(1), List(), false)
        //        _ = {
        //          println(rows)
        //          println("---")
        //          println(sub)
        //          println("---")
        //          println(list)
        //          println("---")
        //          println(it.hasNext)
        //          while(it.hasNext){
        //            println(it.next)
        //          }
        ////          it.forEachRemaining(v => println(v))
        ////          println(it.next)
        //        }


        //        _ <- Ns.str.Refs1.*?(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3).get.map(_.sortBy(_._1) ==> List(
        //          ("A", List((Some(11), Some(12), "a")), 1),
        //          ("B", List((Some(13), None, "b")), 1),
        //          ("C", List((None, Some(14), "c")), 1),
        //          ("D", List((None, None, "d")), 1),
        //          ("E", List(), 1)
        //        ))


        //        _ <- m(Ns.bool.Refs1.*(Ref1.str1).Tx(Ref3.int3_(1))) insert List(
        //          (true, List("a", "b")),
        //          (false, Nil)
        //        )

        //        _ <- Ns.bool.Refs1.*(Ref1.str1).Tx(Ref3.int3).get.map(_ ==> List(
        //          (true, List("a", "b"), 1)
        //        ))
        //
        //        _ <- Ns.bool.Refs1.*?(Ref1.str1).Tx(Ref3.int3).get.map(_ ==> List(
        //          (true, List("a", "b"), 1),
        //          (false, Nil, 1)
        //        ))


        //        _ <- m(Ns.str.Refs1.*(Ref1.int1)
        //          .Tx(Ref2.str2_("b").int2_(5).Ref3.str3_("c") + Ns.int_(6).bool_(true))) insert List(
        //          ("A", List(1, 11)),
        //          ("B", Nil)
        //        )

        //        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.int2)
        //          .Tx(Ref2.str2_("b").int2_(5).Ref3.str3_("c") + Ns.int_(6).bool_(true))) insert List(
        //          ("A", List((1, 2), (11, 22))),
        //          ("B", Nil)
        //        )

        //        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3))) insert List(
        //          ("A", List((1, 2, "a", List(3, 4)), (11, 22, "aa", Nil))),
        //          ("B", Nil)
        //        )

        //        _ <- m(Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3))
        //          .Tx(Ref2.str2_("b").int2_(5).Ref3.str3_("c") + Ns.int_(6).bool_(true))) insert List(
        //          ("A", List((1, 2, "a", List(3, 4)), (11, 22, "aa", Nil))),
        //          ("B", Nil)
        //        )

        //        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.int2.str2.Refs3.*?(Ref3.int3))
        //          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
        //          ("A", List((1, 2, "a", List(3, 4)), (11, 22, "aa", Nil)), ("b", 5, "c"), (6, true)),
        //          ("B", Nil, ("b", 5, "c"), (6, true))
        //        ))
        //        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3))
        //          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool).get.map(_ ==> List(
        //          ("A", List((1, 2, "a", List(3, 4))), ("b", 5, "c"), (6, true))
        //        ))
        //
        //        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.int2.str2.Refs3.*?(Ref3.int3))
        //          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool_).get.map(_ ==> List(
        //          ("A", List((1, 2, "a", List(3, 4)), (11, 22, "aa", Nil)), ("b", 5, "c"), 6),
        //          ("B", Nil, ("b", 5, "c"), 6)
        //        ))
        //        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3))
        //          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int.bool_).get.map(_ ==> List(
        //          ("A", List((1, 2, "a", List(3, 4))), ("b", 5, "c"), 6)
        //        ))
        //
        //        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.int2.str2.Refs3.*?(Ref3.int3))
        //          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int_.bool_).get.map(_ ==> List(
        //          ("A", List((1, 2, "a", List(3, 4)), (11, 22, "aa", Nil)), ("b", 5, "c")),
        //          ("B", Nil, ("b", 5, "c"))
        //        ))
        //        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3))
        //          .Tx(Ref2.str2.int2.Ref3.str3 + Ns.int_.bool_).get.map(_ ==> List(
        //          ("A", List((1, 2, "a", List(3, 4))), ("b", 5, "c"))
        //        ))
        //
        //        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.int2.str2.Refs3.*?(Ref3.int3))
        //          .Tx(Ref2.str2.int2.Ref3.str3_ + Ns.int.bool_).get.map(_ ==> List(
        //          ("A", List((1, 2, "a", List(3, 4)), (11, 22, "aa", Nil)), ("b", 5), 6),
        //          ("B", Nil, ("b", 5), 6)
        //        ))
        //        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3))
        //          .Tx(Ref2.str2.int2.Ref3.str3_ + Ns.int.bool_).get.map(_ ==> List(
        //          ("A", List((1, 2, "a", List(3, 4))), ("b", 5), 6)
        //        ))
        //
        //        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.int2.str2.Refs3.*?(Ref3.int3))
        //          .Tx(Ref2.str2.int2_.Ref3.str3 + Ns.int.bool_).get.map(_ ==> List(
        //          ("A", List((1, 2, "a", List(3, 4)), (11, 22, "aa", Nil)), ("b", "c"), 6),
        //          ("B", Nil, ("b", "c"), 6)
        //        ))
        //        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3))
        //          .Tx(Ref2.str2.int2_.Ref3.str3 + Ns.int.bool_).get.map(_ ==> List(
        //          ("A", List((1, 2, "a", List(3, 4))), ("b", "c"), 6)
        //        ))
        //
        //        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.int2.str2.Refs3.*?(Ref3.int3))
        //          .Tx(Ref2.str2.int2.Ref3.str3).get.map(_ ==> List(
        //          ("A", List((1, 2, "a", List(3, 4)), (11, 22, "aa", Nil)), "b", 5, "c"),
        //          ("B", Nil, "b", 5, "c")
        //        ))
        //        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.int2.str2.Refs3.*(Ref3.int3))
        //          .Tx(Ref2.str2.int2.Ref3.str3).get.map(_ ==> List(
        //          ("A", List((1, 2, "a", List(3, 4))), "b", 5, "c")
        //        ))
        //
        //                _ <- Ns.str.Refs1.*(Ref1.int1.str1.Refs2.*(Ref2.int2)) insert List(
        //                  ("A", List((1, "a", List(3, 4)), (11, "aa", Nil))),
        //                  ("B", Nil)
        //                )
        //                rows <- conn.qRaw(
        //                  """[:find  ?b
        //                    |        (pull ?a__1 [
        //                    |          {(:Ns/refs1 :limit nil) [
        //                    |            (:Ref1/int1 :limit nil)
        //                    |            (:Ref1/str1 :limit nil)
        //                    |            {(:Ref1/refs2 :limit nil :default "__none__") [
        //                    |              (:Ref2/int2 :limit nil)]}]}])
        //                    | :where [?a :Ns/str ?b]
        //                    |        [(identity ?a) ?a__1]]""".stripMargin
        //                )
        //
        //                _ <- Ns.str.Refs1.*?(Ref1.int1.str1.Refs2.*?(Ref2.int2)).inspectGet

        //
        //        _ <- Ns.str.Refs1.*(Ref1.int1.Ref2.str2.Refs3.*(Ref3.int3)) insert List(
        //          ("A", List((1, "a", List(3, 4)), (11, "aa", Nil))),
        //          ("B", Nil)
        //        )
        //
        //        rows <- conn.qRaw(
        //          """[:find  ?b
        //            |        (pull ?a__1 [
        //            |          {(:Ns/refs1 :limit nil) [
        //            |            (:Ref1/int1 :limit nil)
        //            |            {(:Ref1/ref2 :limit nil :default "__none__") [
        //            |              (:Ref2/str2 :limit nil)
        //            |              {(:Ref2/refs3 :limit nil :default "__none__") [
        //            |                (:Ref3/int3 :limit nil)]}]}]}])
        //            | :where [?a :Ns/str ?b]
        //            |        [(identity ?a) ?a__1]]""".stripMargin
        //        )
        ////        _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.str2.Refs3.*?(Ref3.int3)).inspectGet
        //
        //                _ <- Ns.str.Refs1.*?(Ref1.int1.Ref2.str2.Refs3.*?(Ref3.int3)).get.map(_ ==> List(
        //                  ("A", List((1, "a", List(3, 4)), (11, "aa", Nil))),
        //                  ("B", Nil)
        //                ))


        //        it = rows.iterator
        //        //        _ = it.next
        //        row = it.next
        //        _ = println(row)
        //        _ = {
        //          val it  = row.iterator();
        //          //          val res = scala.Tuple2(
        //          //            castOptNestedOne[String](it),
        //          //            it.next match {
        //          //              case null     =>
        //          //                Nil
        //          //              case (last@_) =>
        //          //                val list = last.asInstanceOf[jMap[Any, Any]].values().iterator().next.asInstanceOf[jList[Any]];
        //          //                val it   = extractFlatValues(list, 2, List(), List(), true);
        //          //                //                val it   = extractFlatValues(list, 2, List(1), List(), false);
        //          //                val buf  = new scala.collection.mutable.ListBuffer[Any]();
        //          //                while (it.hasNext) {
        //          //                  buf.addOne(scala.Tuple3(
        //          //                    castOptNestedOneInt(it),
        //          //                    castOptNestedOne[String](it),
        //          //                    it.next match {
        //          //                      case "__none__" => Nil
        //          //                      case last       =>
        //          //                        val list = last.asInstanceOf[jList[Any]];
        //          //                        val it   = extractFlatValues(list, 1, List(), List(), false);
        //          //                        val buf  = new scala.collection.mutable.ListBuffer[Any]();
        //          //                        while (it.hasNext) {
        //          //                          buf.addOne(castOptNestedOneInt(it));
        //          //                        }
        //          //                        buf.toList
        //          //                    }
        //          //                  ))
        //          //                }
        //          //                buf.toList
        //          //            }
        //          //          )
        //          //          val res0 = scala.Tuple2(
        //          //            castOptNestedOne[String](it),
        //          //            it.next match {
        //          //              case null     =>
        //          //                Nil
        //          //              case (last@_) =>
        //          //                val list = last.asInstanceOf[jMap[Any, Any]].values().iterator().next.asInstanceOf[jList[Any]];
        //          //                val it   = extractFlatValues(list, 2, List(1), List(), false);
        //          //                val buf  = new scala.collection.mutable.ListBuffer[Any]();
        //          //                while (it.hasNext) {
        //          //                  buf.addOne(scala.Tuple3(
        //          //                    castOptNestedOneInt(it),
        //          //                    castOptNestedOne[String](it),
        //          //                    it.next match {
        //          //                      case null             => Nil
        //          //                      case last: jMap[_, _] =>
        //          //                        val list = last.asInstanceOf[jMap[Any, Any]].values().iterator().next.asInstanceOf[jList[Any]];
        //          //                        val it   = extractFlatValues(list, 1, List(), List(), false);
        //          //                        val buf  = new scala.collection.mutable.ListBuffer[Any]();
        //          //                        while (it.hasNext) {
        //          //                          buf.addOne(castOptNestedOneInt(it));
        //          //                        }
        //          //                        buf.toList
        //          //                    }
        //          //                  ))
        //          //                }
        //          //                buf.toList
        //          //            }
        //          //          )
        //          val res = scala.Tuple2(
        //            castOptNestedOne[String](it),
        //            it.next match {
        //              case null     =>
        //                Nil
        //              case (last@_) =>
        //                val list = last.asInstanceOf[jMap[Any, Any]].values().iterator().next.asInstanceOf[jList[Any]];
        //                val it   = extractFlatValues(list, 2, List(1), List(), true);
        //                val buf  = new scala.collection.mutable.ListBuffer[Any]();
        //                while (it.hasNext) {
        //                  buf.addOne(scala.Tuple3(
        //                    castOptNestedOneInt(it),
        //                    castOptNestedOne[String](it),
        //                    it.next match {
        //                      case "__none__" => Nil
        //                      case last       =>
        //                        val list = last.asInstanceOf[jList[Any]];
        //                        val it   = extractFlatValues(list, 1, List(), List(), false);
        //                        val buf  = new scala.collection.mutable.ListBuffer[Any]();
        //                        while (it.hasNext) {
        //                          buf.addOne(castOptNestedOneInt(it));
        //                        }
        //                        buf.toList
        //
        //                      //                      case null => Nil
        //                      //                      case last: jMap[_, _] =>
        //                      //                        val list = last.asInstanceOf[jMap[Any, Any]].values().iterator().next.asInstanceOf[jList[Any]];
        //                      //                        val it   = extractFlatValues(list, 1, List(), List(), false);
        //                      //                        val buf  = new scala.collection.mutable.ListBuffer[Any]();
        //                      //                        while (it.hasNext) {
        //                      //                          buf.addOne(castOptNestedOneInt(it));
        //                      //                        }
        //                      //                        buf.toList
        //                    }
        //                  ))
        //                }
        //                buf.toList
        //            }
        //          )
        //          println("RESULT ############################\n" + res)
        //        }

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
