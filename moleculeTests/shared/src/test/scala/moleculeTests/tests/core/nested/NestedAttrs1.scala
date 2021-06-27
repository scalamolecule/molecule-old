package moleculeTests.tests.core.nested

import molecule.datomic.api.in3_out11.m
import molecule.datomic.api.out6._
import moleculeTests.Adhoc.core
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object NestedAttrs1 extends AsyncTestSuite {

  lazy val tests = Tests {

    "String" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.str$) insert List(
          (1, List((1, Some("a")), (2, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss * Ns.int.str$).get.map(_ ==> List(
          (1, List((1, Some("a")), (2, None)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.str).get.map(_ ==> List(
          (1, List((1, "a")))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.str_).get.map(_ ==> List(
          (1, List(1))
        ))

        // Optional nested
        _ <- m(Ref1.int1.Nss *? Ns.int.str$).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Some("a")), (2, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.str).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, "a"))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.str_).get.map(_.sortBy(_._1) ==> List(
          (1, List(1)),
          (2, List())
        ))
      } yield ()
    }

    "Enum" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.enum$) insert List(
          (1, List((1, Some("enum1")), (2, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss * Ns.int.enum$).get.map(_ ==> List(
          (1, List((1, Some("enum1")), (2, None)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.enum).get.map(_ ==> List(
          (1, List((1, "enum1")))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.enum_).get.map(_ ==> List(
          (1, List(1))
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.enum$).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Some("enum1")), (2, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.enum).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, "enum1"))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.enum_).get.map(_.sortBy(_._1) ==> List(
          (1, List(1)),
          (2, List())
        ))
      } yield ()
    }

    "Long" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.long$) insert List(
          (1, List((1, Some(10L)), (2, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss * Ns.int.long$).get.map(_ ==> List(
          (1, List((1, Some(10L)), (2, None)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.long).get.map(_ ==> List(
          (1, List((1, 10L)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.long_).get.map(_ ==> List(
          (1, List(1))
        ))

        // Optional nested
        _ <- m(Ref1.int1.Nss *? Ns.int.long$).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Some(10L)), (2, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.long).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, 10L))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.long_).get.map(_.sortBy(_._1) ==> List(
          (1, List(1)),
          (2, List())
        ))
      } yield ()
    }

    "ref" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.ref1$) insert List(
          (1, List((1, Some(42L)), (2, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss * Ns.int.ref1$).get.map(_ ==> List(
          (1, List((1, Some(42L)), (2, None)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.ref1).get.map(_ ==> List(
          (1, List((1, 42L)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.ref1_).get.map(_ ==> List(
          (1, List(1))
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.ref1$).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Some(42L)), (2, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.ref1).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, 42L))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.ref1_).get.map(_.sortBy(_._1) ==> List(
          (1, List(1)),
          (2, List())
        ))
      } yield ()
    }

    "Double" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.double$) insert List(
          (1, List((1, Some(1.1)), (2, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss * Ns.int.double$).get.map(_ ==> List(
          (1, List((1, Some(1.1)), (2, None)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.double).get.map(_ ==> List(
          (1, List((1, 1.1)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.double_).get.map(_ ==> List(
          (1, List(1))
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.double$).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Some(1.1)), (2, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.double).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, 1.1))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.double_).get.map(_.sortBy(_._1) ==> List(
          (1, List(1)),
          (2, List())
        ))
      } yield ()
    }

    "Boolean" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.bool$) insert List(
          (1, List((1, Some(true)), (2, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss * Ns.int.bool$).get.map(_ ==> List(
          (1, List((1, Some(true)), (2, None)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bool).get.map(_ ==> List(
          (1, List((1, true)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bool_).get.map(_ ==> List(
          (1, List(1))
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.bool$).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Some(true)), (2, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.bool).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, true))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.bool_).get.map(_.sortBy(_._1) ==> List(
          (1, List(1)),
          (2, List())
        ))
      } yield ()
    }

    "Date" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.date$) insert List(
          (1, List((1, Some(date1)), (2, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss * Ns.int.date$).get.map(_ ==> List(
          (1, List((1, Some(date1)), (2, None)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.date).get.map(_ ==> List(
          (1, List((1, date1)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.date_).get.map(_ ==> List(
          (1, List(1))
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.date$).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Some(date1)), (2, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.date).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, date1))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.date_).get.map(_.sortBy(_._1) ==> List(
          (1, List(1)),
          (2, List())
        ))
      } yield ()
    }

    "UUID" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.uuid$) insert List(
          (1, List((1, Some(uuid1)), (2, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss * Ns.int.uuid$).get.map(_ ==> List(
          (1, List((1, Some(uuid1)), (2, None)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.uuid).get.map(_ ==> List(
          (1, List((1, uuid1)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.uuid_).get.map(_ ==> List(
          (1, List(1))
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.uuid$).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Some(uuid1)), (2, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.uuid).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, uuid1))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.uuid_).get.map(_.sortBy(_._1) ==> List(
          (1, List(1)),
          (2, List())
        ))
      } yield ()
    }

    "URI" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.uri$) insert List(
          (1, List((1, Some(uri1)), (2, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss * Ns.int.uri$).get.map(_ ==> List(
          (1, List((1, Some(uri1)), (2, None)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.uri).get.map(_ ==> List(
          (1, List((1, uri1)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.uri_).get.map(_ ==> List(
          (1, List(1))
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.uri$).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Some(uri1)), (2, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.uri).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, uri1))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.uri_).get.map(_.sortBy(_._1) ==> List(
          (1, List(1)),
          (2, List())
        ))
      } yield ()
    }

    "BigInt" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.bigInt$) insert List(
          (1, List((1, Some(bigInt1)), (2, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss * Ns.int.bigInt$).get.map(_ ==> List(
          (1, List((1, Some(bigInt1)), (2, None)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bigInt).get.map(_ ==> List(
          (1, List((1, bigInt1)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bigInt_).get.map(_ ==> List(
          (1, List(1))
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.bigInt$).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Some(bigInt1)), (2, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.bigInt).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, bigInt1))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.bigInt_).get.map(_.sortBy(_._1) ==> List(
          (1, List(1)),
          (2, List())
        ))
      } yield ()
    }

    "BigDecimal" - core { implicit conn =>
      for {
        _ <- m(Ref1.int1.Nss * Ns.int.bigDec$) insert List(
          (1, List((1, Some(bigDec1)), (2, None))),
          (2, List())
        )

        _ <- m(Ref1.int1.Nss * Ns.int.bigDec$).get.map(_ ==> List(
          (1, List((1, Some(bigDec1)), (2, None)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bigDec).get.map(_ ==> List(
          (1, List((1, bigDec1)))
        ))
        _ <- m(Ref1.int1.Nss * Ns.int.bigDec_).get.map(_ ==> List(
          (1, List(1))
        ))

        _ <- m(Ref1.int1.Nss *? Ns.int.bigDec$).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, Some(bigDec1)), (2, None))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.bigDec).get.map(_.sortBy(_._1) ==> List(
          (1, List((1, bigDec1))),
          (2, List())
        ))
        _ <- m(Ref1.int1.Nss *? Ns.int.bigDec_).get.map(_.sortBy(_._1) ==> List(
          (1, List(1)),
          (2, List())
        ))
      } yield ()
    }
  }
}