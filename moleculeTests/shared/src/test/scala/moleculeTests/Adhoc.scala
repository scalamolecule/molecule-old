package moleculeTests

import java.util
import molecule.datomic.api.in3_out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}
import java.util.{Collections, Date, UUID, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import molecule.core.ast.elements._
import molecule.core.util.Helpers
import molecule.datomic.base.facade.Conn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer
import molecule.core.util.testing.expectCompileError
import molecule.datomic.base.transform.Model2Query
import moleculeTests.tests.core.base.schema.CoreTestSchema
import scala.util.control.NonFatal

object Adhoc extends AsyncTestSuite with Helpers {


  lazy val tests = Tests {

    "core" - core { implicit conn =>
      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed

        //        _ <- m(Ref1.str1.Nss * Ns.int.str$) insert List(
        //          ("A", List((1, Some("a")), (2, None))),
        //          ("B", List())
        //        )
        //
        //        _ <- m(Ref1.str1.Nss * Ns.int.str$).get.map(_ ==> List(
        //          ("A", List((1, Some("a")), (2, None)))
        //        ))
        //        _ <- m(Ref1.str1.Nss * Ns.int.str).get.map(_ ==> List(
        //          ("A", List((1, "a")))
        //        ))
        //        _ <- m(Ref1.str1.Nss * Ns.int.str_).get.map(_ ==> List(
        //          ("A", List(1))
        //        ))

        //        _ <- m(Ref1.str1.Nss *? Ns.int.str$).get.map(_.sortBy(_._1) ==> List(
        //          ("A", List((1, Some("a")), (2, None))),
        //          ("B", List())
        //        ))
        //        _ <- m(Ref1.str1.Nss *? Ns.int.str).get.map(_.sortBy(_._1) ==> List(
        //          ("A", List((1, "a"))),
        //          ("B", List())
        //        ))
        //        _ <- m(Ref1.str1.Nss *? Ns.int.str_).get.map(_.sortBy(_._1) ==> List(
        //          ("A", List(1)),
        //          ("B", List())
        //        ))


        _ <- Ns.str.Refs1.*(Ref1.int1).Tx(Ref3.int3_(7)) insert List(
          ("A", List(1, 2)),
          ("B", Nil)
        )
        //        _ <- Ns.bool.Refs1.*(Ref1.str1).Tx(Ref3.int3).get.map(_ ==> List(
        //          (true, List("a", "b"), 1)
        //        ))

        _ <- Ns.str.Refs1.*?(Ref1.int1).Tx(Ref3.int3).inspectGet
        _ <- Ns.str.Refs1.*?(Ref1.int1).Tx(Ref3.int3).get.map(_ ==> List(
          ("A", List(1, 2), 7),
          ("B", Nil, 7)
        ))
        /*
        --------------------------------------------------------------------------
Model(List(
  Atom("Ns", "str", "String", 1, VarValue, None, Seq(), Seq()),
  Nested(
    Bond("Ns", "refs1$", "Ref1", 2, Seq()),
    List(
      Atom("Ref1", "int1", "Int", 1, VarValue, None, Seq(), Seq()))),
  TxMetaData(List(
    Atom("Ref3", "int3", "Int", 1, VarValue, None, Seq(), Seq())))))

Query(
  Find(List(
    Var("b"),
    PullNested("a__1",
      NestedAttrs(1, "Ns", "refs1", Seq(
        PullAttr("Ref1", "int1", false)))),
    Var("c"))),
  Where(List(
    Funct("identity", Seq(Var("a")), ScalarBinding(Var("a__1"))),
    DataClause(ImplDS, Var("a"), KW("Ns", "str", ""), Var("b"), Var("tx"), NoBinding),
    DataClause(ImplDS, Var("tx"), KW("Ref3", "int3", ""), Var("c"), Empty, NoBinding))))

[:find  ?b
        (pull ?a__1 [
          {(:Ns/refs1 :limit nil) [
            (:Ref1/int1 :limit nil)]}]) ?c
 :where [(identity ?a) ?a__1]
        [?a :Ns/str ?b ?tx]
        [?tx :Ref3/int3 ?c]]

RULES: none

INPUTS: none

OUTPUTS:
1: [A  {:Ns/refs1 [{:Ref1/int1 1} {:Ref1/int1 2}]}  7]
2: [B  null  7]
(showing up to 500 rows)
--------------------------------------------------------------------------
         */

        //        _ <- Ns.str.Refs1.*(Ref1.int1$.str1).Tx(Ref3.int3_(1)) insert List(
        //
        //          ("E", List())
        //        )


        //        _ <- Ns.str.Refs1.*(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3_(1)) insert List(
        //          ("A", List((Some(11), Some(12), "a"))),
        //          ("B", List((Some(13), None, "b"))),
        //          ("C", List((None, Some(14), "c"))),
        //          ("D", List((None, None, "d"))),
        //          ("E", List())
        //        )
        //                _ <- Ns.str.Refs1.*?(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3).inspectGet
        //        _ <- Ns.str.Refs1.*?(Ref1.int1$.Ref2.int2$.str2).Tx(Ref3.int3).get.map(_.sortBy(_._1) ==> List(
        //          ("A", List((Some(11), Some(12), "a")), 1),
        //          ("B", List((Some(13), None, "b")), 1),
        //          ("C", List((None, Some(14), "c")), 1),
        //          ("D", List((None, None, "d")), 1),
        //          ("E", List(), 1)
        //        ))
        /*
        --------------------------------------------------------------------------
Model(List(
  Atom("Ns", "str", "String", 1, VarValue, None, Seq(), Seq()),
  Nested(
    Bond("Ns", "refs1$", "Ref1", 2, Seq()),
    List(
      Atom("Ref1", "int1$", "Int", 1, VarValue, None, Seq(), Seq()),
      Bond("Ref1", "ref2", "Ref2", 1, Seq()),
      Atom("Ref2", "int2$", "Int", 1, VarValue, None, Seq(), Seq()),
      Atom("Ref2", "str2", "String", 1, VarValue, None, Seq(), Seq()))),
  TxMetaData(List(
    Atom("Ref3", "int3", "Int", 1, VarValue, None, Seq(), Seq())))))

Query(
  Find(List(
    Var("b"),
    PullNested("a__1",
      NestedAttrs(1, "Ns", "refs1", Seq(
        PullAttr("Ref1", "int1", true),
        NestedAttrs(2, "Ref1", "ref2", Seq(
          PullAttr("Ref2", "int2", true),
          PullAttr("Ref2", "str2", false)))))),
    Var("c"))),
  Where(List(
    Funct("identity", Seq(Var("a")), ScalarBinding(Var("a__1"))),
    DataClause(ImplDS, Var("a"), KW("Ns", "str", ""), Var("b"), Var("tx"), NoBinding),
    DataClause(ImplDS, Var("tx"), KW("Ref3", "int3", ""), Var("c"), Empty, NoBinding))))

[:find  ?b
        (pull ?a__1 [
          {(:Ns/refs1 :limit nil) [
            (:Ref1/int1 :limit nil :default "__none__")
            {(:Ref1/ref2 :limit nil :default "__none__") [
              (:Ref2/int2 :limit nil :default "__none__")
              (:Ref2/str2 :limit nil)]}]}]) ?c
 :where [(identity ?a) ?a__1]
        [?a :Ns/str ?b ?tx]
        [?tx :Ref3/int3 ?c]]

RULES: none

INPUTS: none

OUTPUTS:
1: [A  {:Ns/refs1 [{:Ref1/int1 11, :Ref1/ref2 {:Ref2/int2 12, :Ref2/str2 "a"}}]}  1]
2: [B  {:Ns/refs1 [{:Ref1/int1 13, :Ref1/ref2 {:Ref2/int2 "__none__", :Ref2/str2 "b"}}]}  1]
3: [C  {:Ns/refs1 [{:Ref1/int1 "__none__", :Ref1/ref2 {:Ref2/int2 14, :Ref2/str2 "c"}}]}  1]
4: [D  {:Ns/refs1 [{:Ref1/int1 "__none__", :Ref1/ref2 {:Ref2/int2 "__none__", :Ref2/str2 "d"}}]}  1]
5: [E  null  1]
(showing up to 500 rows)
--------------------------------------------------------------------------
         */


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
