package moleculeTests

import java.util
import molecule.datomic.api.in3_out11._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.{ExecutionContext, Future}
import java.util.{Collections, Date, UUID, Iterator => jIterator, List => jList, Map => jMap, Set => jSet}
import molecule.core.util.Helpers
import molecule.datomic.base.facade.Conn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer

object Adhoc extends AsyncTestSuite with Helpers {


  lazy val tests = Tests {

    //    "adhoc" - bidirectional { implicit conn =>
    "adhoc" - core { implicit conn =>

      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed


//        _ <- Ns.str.Refs1.*(Ref1.int1).bool inspectInsert List(("a", List(10, 11), true))

        _ <- Ns.str.Refs1.*(Ref1.int1).bool insert List(("a", List(10, 11), true))
//        _ <- Ns.str.Refs1.*(Ref1.int1).bool.inspectGet
        _ <- Ns.str.Refs1.*(Ref1.int1).bool.get.map(_ ==> List(
          ("a", List(10, 11), true)
        ))


        _ <- Ns.str.Refs1.*(Ref1.int1).Parent.int.Ref1.str1 insert List(("a", List(10, 11), 1, "aa"))
//        _ <- Ns.str.Refs1.*(Ref1.int1).Parent.int.Ref1.str1 inspectInsert List(("a", List(10, 11), 1, "aa"))
//        _ <- Ns.str.Refs1.*(Ref1.int1).Parent.int.Ref1.str1.inspectGet
//
        _ <- Ns.str.Refs1.*(Ref1.int1).Parent.int.Ref1.str1.get.map(_ ==> List(
          ("a", List(10, 11), 1, "aa")
        ))

//        _ <- Ns.str.Refs1.*(Ref1.int1).Parent.int.Ref1.str1.inspectGet

//        _ <- e.touch.map(_ ==> Map(
//          ":db/id" -> e,
//          ":Ns/refs1" -> List(
//            Map(":db/id" -> r1, ":Ref1/str1" -> "r1"),
//            Map(":db/id" -> r2, ":Ref1/str1" -> "r2")
//          )))

//        _ <- Ns.str.Refs1.*(Ref1.int1).bool insert List(
//          ("a", List(10, 11), true)
//        )
//        _ <- Ns.str.Refs1.*(Ref1.int1).bool.get.map(_ ==> List(
//          ("a", true, List(10, 11))
//        ))

//        _ <- m(Ns.str.Refs1.*(Ref1.int1)) insert List(
//          ("a", List(1)),
//          ("b", List(2, 3))
//        )
//
//        _ <- Ns.str.Refs1.*(Ref1.int1).getObjs.collect { case List(o1, o2) =>
//          o1.str ==> "a"
//          val List(r1) = o1.Refs1
//          r1.int1 ==> 1
//
//          o2.str ==> "b"
//          val List(r2, r3) = o2.Refs1
//          r2.int1 ==> 2
//          r3.int1 ==> 3
//        }



//        _ <- m(Ref1.str1.Nss * Ns.int.enum$) insert List(
//          ("A", List((1, Some("enum1")), (2, None))),
//          ("B", List())
//        )
//
//        _ <- m(Ref1.str1.Nss * Ns.int.enum$).get.map(_ ==> List(
//          ("A", List((1, Some("enum1")), (2, None)))
//        ))
//        _ <- m(Ref1.str1.Nss * Ns.int.enum).get.map(_ ==> List(
//          ("A", List((1, "enum1")))
//        ))
//        _ <- m(Ref1.str1.Nss * Ns.int.enum_).get.map(_ ==> List(
//          ("A", List(1))
//        ))
//
//        _ <- m(Ref1.str1.Nss *? Ns.int.enum$).get.map(_.sortBy(_._1) ==> List(
//          ("A", List((1, Some("enum1")), (2, None))),
//          ("B", List())
//        ))
//        _ <- m(Ref1.str1.Nss *? Ns.int.enum).get.map(_.sortBy(_._1) ==> List(
//          ("A", List((1, "enum1"))),
//          ("B", List())
//        ))
//        _ <- m(Ref1.str1.Nss *? Ns.int.enum_).get.map(_.sortBy(_._1) ==> List(
//          ("A", List(1)),
//          ("B", List())
//        ))


      } yield ()
    }
  }
}
