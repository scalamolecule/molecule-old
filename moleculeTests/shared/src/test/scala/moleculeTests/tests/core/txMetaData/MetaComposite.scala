package moleculeTests.tests.core.txMetaData

import molecule.datomic.api.in3_out10._
import molecule.datomic.base.ops.QueryOps
import molecule.datomic.base.util.{SystemPeer, SystemPeerServer}
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MetaComposite extends AsyncTestSuite {

  lazy val tests = Tests {

    "Composite + tx meta data" - core { implicit conn =>
      for {
        _ <- (Ref1.int1(1).str1("a") + Ref2.int2(2).str2("b").Tx(Ref3.int3(3).str3("c"))).save

        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3.str3)).get.map(_ ==> List(((1, "a"), (2, "b", 3, "c"))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3.str3_)).get.map(_ ==> List(((1, "a"), (2, "b", 3))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3)).get.map(_ ==> List(((1, "a"), (2, "b", 3))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3_)).get.map(_ ==> List(((1, "a"), (2, "b"))))

        _ <- (Ref1.int1.str1 + Ref2.int2.str2_.Tx(Ref3.int3.str3)).get.map(_ ==> List(((1, "a"), (2, 3, "c"))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2_.Tx(Ref3.int3.str3_)).get.map(_ ==> List(((1, "a"), (2, 3))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2_.Tx(Ref3.int3)).get.map(_ ==> List(((1, "a"), (2, 3))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2_.Tx(Ref3.int3_)).get.map(_ ==> List(((1, "a"), 2)))

        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3.str3)).get.map(_ ==> List(((1, "a"), (2, 3, "c"))))
        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3.str3_)).get.map(_ ==> List(((1, "a"), (2, 3))))
        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3)).get.map(_ ==> List(((1, "a"), (2, 3))))
        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3_)).get.map(_ ==> List(((1, "a"), 2)))

        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3.str3)).get.map(_ ==> List(((1, "a"), (3, "c"))))
        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3.str3_)).get.map(_ ==> List(((1, "a"), 3)))
        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3)).get.map(_ ==> List(((1, "a"), 3)))
        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3_)).get.map(_ ==> List((1, "a")))

        _ <- Ref1.int1.str1.Tx(Ref3.int3.str3).get.map(_ ==> List((1, "a", 3, "c")))
        _ <- Ref1.int1.str1.Tx(Ref3.int3.str3_).get.map(_ ==> List((1, "a", 3)))
        _ <- Ref1.int1.str1.Tx(Ref3.int3).get.map(_ ==> List((1, "a", 3)))
        _ <- Ref1.int1.str1.Tx(Ref3.int3_).get.map(_ ==> List((1, "a")))


        _ <- (Ref1.int1.str1_ + Ref2.int2.str2.Tx(Ref3.int3.str3)).get.map(_ ==> List((1, (2, "b", 3, "c"))))
        _ <- (Ref1.int1.str1_ + Ref2.int2.str2.Tx(Ref3.int3.str3_)).get.map(_ ==> List((1, (2, "b", 3))))
        _ <- (Ref1.int1.str1_ + Ref2.int2.str2.Tx(Ref3.int3)).get.map(_ ==> List((1, (2, "b", 3))))
        _ <- (Ref1.int1.str1_ + Ref2.int2.str2.Tx(Ref3.int3_)).get.map(_ ==> List((1, (2, "b"))))

        _ <- (Ref1.int1.str1_ + Ref2.int2.str2_.Tx(Ref3.int3.str3)).get.map(_ ==> List((1, (2, 3, "c"))))
        _ <- (Ref1.int1.str1_ + Ref2.int2.str2_.Tx(Ref3.int3.str3_)).get.map(_ ==> List((1, (2, 3))))
        _ <- (Ref1.int1.str1_ + Ref2.int2.str2_.Tx(Ref3.int3)).get.map(_ ==> List((1, (2, 3))))
        _ <- (Ref1.int1.str1_ + Ref2.int2.str2_.Tx(Ref3.int3_)).get.map(_ ==> List((1, 2)))

        _ <- (Ref1.int1.str1_ + Ref2.int2.Tx(Ref3.int3.str3)).get.map(_ ==> List((1, (2, 3, "c"))))
        _ <- (Ref1.int1.str1_ + Ref2.int2.Tx(Ref3.int3.str3_)).get.map(_ ==> List((1, (2, 3))))
        _ <- (Ref1.int1.str1_ + Ref2.int2.Tx(Ref3.int3)).get.map(_ ==> List((1, (2, 3))))
        _ <- (Ref1.int1.str1_ + Ref2.int2.Tx(Ref3.int3_)).get.map(_ ==> List((1, 2)))

        _ <- (Ref1.int1.str1_ + Ref2.int2_.Tx(Ref3.int3.str3)).get.map(_ ==> List((1, (3, "c"))))
        _ <- (Ref1.int1.str1_ + Ref2.int2_.Tx(Ref3.int3.str3_)).get.map(_ ==> List((1, 3)))
        _ <- (Ref1.int1.str1_ + Ref2.int2_.Tx(Ref3.int3)).get.map(_ ==> List((1, 3)))
        _ <- (Ref1.int1.str1_ + Ref2.int2_.Tx(Ref3.int3_)).get.map(_ ==> List(1))

        _ <- Ref1.int1.str1_.Tx(Ref3.int3.str3).get.map(_ ==> List((1, 3, "c")))
        _ <- Ref1.int1.str1_.Tx(Ref3.int3.str3_).get.map(_ ==> List((1, 3)))
        _ <- Ref1.int1.str1_.Tx(Ref3.int3).get.map(_ ==> List((1, 3)))
        _ <- Ref1.int1.str1_.Tx(Ref3.int3_).get.map(_ ==> List(1))


        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3.str3)).get.map(_ ==> List((1, (2, "b", 3, "c"))))
        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3.str3_)).get.map(_ ==> List((1, (2, "b", 3))))
        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3)).get.map(_ ==> List((1, (2, "b", 3))))
        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3_)).get.map(_ ==> List((1, (2, "b"))))

        _ <- (Ref1.int1 + Ref2.int2.str2_.Tx(Ref3.int3.str3)).get.map(_ ==> List((1, (2, 3, "c"))))
        _ <- (Ref1.int1 + Ref2.int2.str2_.Tx(Ref3.int3.str3_)).get.map(_ ==> List((1, (2, 3))))
        _ <- (Ref1.int1 + Ref2.int2.str2_.Tx(Ref3.int3)).get.map(_ ==> List((1, (2, 3))))
        _ <- (Ref1.int1 + Ref2.int2.str2_.Tx(Ref3.int3_)).get.map(_ ==> List((1, 2)))

        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3.str3)).get.map(_ ==> List((1, (2, 3, "c"))))
        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3.str3_)).get.map(_ ==> List((1, (2, 3))))
        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3)).get.map(_ ==> List((1, (2, 3))))
        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3_)).get.map(_ ==> List((1, 2)))

        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3.str3)).get.map(_ ==> List((1, (3, "c"))))
        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3.str3_)).get.map(_ ==> List((1, 3)))
        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3)).get.map(_ ==> List((1, 3)))
        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3_)).get.map(_ ==> List(1))

        _ <- Ref1.int1.Tx(Ref3.int3.str3).get.map(_ ==> List((1, 3, "c")))
        _ <- Ref1.int1.Tx(Ref3.int3.str3_).get.map(_ ==> List((1, 3)))
        _ <- Ref1.int1.Tx(Ref3.int3).get.map(_ ==> List((1, 3)))
        _ <- Ref1.int1.Tx(Ref3.int3_).get.map(_ ==> List(1))


        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3.str3)).get.map(_ ==> List((2, "b", 3, "c")))
        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3.str3_)).get.map(_ ==> List((2, "b", 3)))
        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3)).get.map(_ ==> List((2, "b", 3)))
        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3_)).get.map(_ ==> List((2, "b")))

        _ <- (Ref1.int1_ + Ref2.int2.str2_.Tx(Ref3.int3.str3)).get.map(_ ==> List((2, 3, "c")))
        _ <- (Ref1.int1_ + Ref2.int2.str2_.Tx(Ref3.int3.str3_)).get.map(_ ==> List((2, 3)))
        _ <- (Ref1.int1_ + Ref2.int2.str2_.Tx(Ref3.int3)).get.map(_ ==> List((2, 3)))
        _ <- (Ref1.int1_ + Ref2.int2.str2_.Tx(Ref3.int3_)).get.map(_ ==> List(2))

        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3.str3)).get.map(_ ==> List((2, 3, "c")))
        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3.str3_)).get.map(_ ==> List((2, 3)))
        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3)).get.map(_ ==> List((2, 3)))
        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3_)).get.map(_ ==> List(2))

        _ <- (Ref1.int1_ + Ref2.int2_.Tx(Ref3.int3.str3)).get.map(_ ==> List((3, "c")))
        _ <- (Ref1.int1_ + Ref2.int2_.Tx(Ref3.int3.str3_)).get.map(_ ==> List(3))
        _ <- (Ref1.int1_ + Ref2.int2_.Tx(Ref3.int3)).get.map(_ ==> List(3))

        _ <- Ref1.int1_.Tx(Ref3.int3.str3).get.map(_ ==> List((3, "c")))
        _ <- Ref1.int1_.Tx(Ref3.int3.str3_).get.map(_ ==> List(3))
        _ <- Ref1.int1_.Tx(Ref3.int3).get.map(_ ==> List(3))
      } yield ()
    }


    "Tx composite meta data" - core { implicit conn =>
      for {
        _ <- Ref1.int1(1).str1("a").Tx(Ref2.int2(2).str2("b") + Ref3.int3(3).str3("c")).save

        _ <- Ref1.int1.str1.Tx(Ref2.int2.str2 + Ref3.int3.str3).get.map(_ ==> List((1, "a", (2, "b"), (3, "c"))))
        _ <- Ref1.int1.str1.Tx(Ref2.int2.str2 + Ref3.int3.str3_).get.map(_ ==> List((1, "a", (2, "b"), 3)))
        _ <- Ref1.int1.str1.Tx(Ref2.int2.str2 + Ref3.int3).get.map(_ ==> List((1, "a", (2, "b"), 3)))
        // Note how composite enforces (2, b) tuple
        _ <- Ref1.int1.str1.Tx(Ref2.int2.str2 + Ref3.int3_).get.map(_ ==> List((1, "a", (2, "b"))))
        // Without composite, 2 and b are treated on same level as preceding attributes
        _ <- Ref1.int1.str1.Tx(Ref2.int2.str2).get.map(_ ==> List((1, "a", 2, "b")))

        _ <- Ref1.int1.str1.Tx(Ref2.int2.str2_ + Ref3.int3.str3).get.map(_ ==> List((1, "a", 2, (3, "c"))))
        _ <- Ref1.int1.str1.Tx(Ref2.int2.str2_ + Ref3.int3.str3_).get.map(_ ==> List((1, "a", 2, 3)))
        _ <- Ref1.int1.str1.Tx(Ref2.int2.str2_ + Ref3.int3).get.map(_ ==> List((1, "a", 2, 3)))
        _ <- Ref1.int1.str1.Tx(Ref2.int2.str2_ + Ref3.int3_).get.map(_ ==> List((1, "a", 2)))
        _ <- Ref1.int1.str1.Tx(Ref2.int2.str2_).get.map(_ ==> List((1, "a", 2)))

        _ <- Ref1.int1.str1.Tx(Ref2.int2 + Ref3.int3.str3).get.map(_ ==> List((1, "a", 2, (3, "c"))))
        _ <- Ref1.int1.str1.Tx(Ref2.int2 + Ref3.int3.str3_).get.map(_ ==> List((1, "a", 2, 3)))
        _ <- Ref1.int1.str1.Tx(Ref2.int2 + Ref3.int3).get.map(_ ==> List((1, "a", 2, 3)))
        _ <- Ref1.int1.str1.Tx(Ref2.int2 + Ref3.int3_).get.map(_ ==> List((1, "a", 2)))
        _ <- Ref1.int1.str1.Tx(Ref2.int2).get.map(_ ==> List((1, "a", 2)))

        _ <- Ref1.int1.str1.Tx(Ref2.int2_ + Ref3.int3.str3).get.map(_ ==> List((1, "a", (3, "c"))))
        _ <- Ref1.int1.str1.Tx(Ref2.int2_ + Ref3.int3.str3_).get.map(_ ==> List((1, "a", 3)))
        _ <- Ref1.int1.str1.Tx(Ref2.int2_ + Ref3.int3).get.map(_ ==> List((1, "a", 3)))
        _ <- Ref1.int1.str1.Tx(Ref2.int2_ + Ref3.int3_).get.map(_ ==> List((1, "a")))
        _ <- Ref1.int1.str1.Tx(Ref2.int2_).get.map(_ ==> List((1, "a")))
        _ <- Ref1.int1.str1.get.map(_ ==> List((1, "a")))


        _ <- Ref1.int1.str1_.Tx(Ref2.int2.str2 + Ref3.int3.str3).get.map(_ ==> List((1, (2, "b"), (3, "c"))))
        _ <- Ref1.int1.str1_.Tx(Ref2.int2.str2 + Ref3.int3.str3_).get.map(_ ==> List((1, (2, "b"), 3)))
        _ <- Ref1.int1.str1_.Tx(Ref2.int2.str2 + Ref3.int3).get.map(_ ==> List((1, (2, "b"), 3)))
        _ <- Ref1.int1.str1_.Tx(Ref2.int2.str2 + Ref3.int3_).get.map(_ ==> List((1, (2, "b"))))
        _ <- Ref1.int1.str1_.Tx(Ref2.int2.str2).get.map(_ ==> List((1, 2, "b")))

        _ <- Ref1.int1.str1_.Tx(Ref2.int2.str2_ + Ref3.int3.str3).get.map(_ ==> List((1, 2, (3, "c"))))
        _ <- Ref1.int1.str1_.Tx(Ref2.int2.str2_ + Ref3.int3.str3_).get.map(_ ==> List((1, 2, 3)))
        _ <- Ref1.int1.str1_.Tx(Ref2.int2.str2_ + Ref3.int3).get.map(_ ==> List((1, 2, 3)))
        _ <- Ref1.int1.str1_.Tx(Ref2.int2.str2_ + Ref3.int3_).get.map(_ ==> List((1, 2)))
        _ <- Ref1.int1.str1_.Tx(Ref2.int2.str2_).get.map(_ ==> List((1, 2)))

        _ <- Ref1.int1.str1_.Tx(Ref2.int2 + Ref3.int3.str3).get.map(_ ==> List((1, 2, (3, "c"))))
        _ <- Ref1.int1.str1_.Tx(Ref2.int2 + Ref3.int3.str3_).get.map(_ ==> List((1, 2, 3)))
        _ <- Ref1.int1.str1_.Tx(Ref2.int2 + Ref3.int3).get.map(_ ==> List((1, 2, 3)))
        _ <- Ref1.int1.str1_.Tx(Ref2.int2 + Ref3.int3_).get.map(_ ==> List((1, 2)))
        _ <- Ref1.int1.str1_.Tx(Ref2.int2).get.map(_ ==> List((1, 2)))

        _ <- Ref1.int1.str1_.Tx(Ref2.int2_ + Ref3.int3.str3).get.map(_ ==> List((1, (3, "c"))))
        _ <- Ref1.int1.str1_.Tx(Ref2.int2_ + Ref3.int3.str3_).get.map(_ ==> List((1, 3)))
        _ <- Ref1.int1.str1_.Tx(Ref2.int2_ + Ref3.int3).get.map(_ ==> List((1, 3)))
        _ <- Ref1.int1.str1_.Tx(Ref2.int2_ + Ref3.int3_).get.map(_ ==> List(1))
        _ <- Ref1.int1.str1_.Tx(Ref2.int2_).get.map(_ ==> List(1))
        _ <- Ref1.int1.str1_.get.map(_ ==> List(1))


        _ <- Ref1.int1.Tx(Ref2.int2.str2 + Ref3.int3.str3).get.map(_ ==> List((1, (2, "b"), (3, "c"))))
        _ <- Ref1.int1.Tx(Ref2.int2.str2 + Ref3.int3.str3_).get.map(_ ==> List((1, (2, "b"), 3)))
        _ <- Ref1.int1.Tx(Ref2.int2.str2 + Ref3.int3).get.map(_ ==> List((1, (2, "b"), 3)))
        _ <- Ref1.int1.Tx(Ref2.int2.str2 + Ref3.int3_).get.map(_ ==> List((1, (2, "b"))))
        _ <- Ref1.int1.Tx(Ref2.int2.str2).get.map(_ ==> List((1, 2, "b")))

        _ <- Ref1.int1.Tx(Ref2.int2.str2_ + Ref3.int3.str3).get.map(_ ==> List((1, 2, (3, "c"))))
        _ <- Ref1.int1.Tx(Ref2.int2.str2_ + Ref3.int3.str3_).get.map(_ ==> List((1, 2, 3)))
        _ <- Ref1.int1.Tx(Ref2.int2.str2_ + Ref3.int3).get.map(_ ==> List((1, 2, 3)))
        _ <- Ref1.int1.Tx(Ref2.int2.str2_ + Ref3.int3_).get.map(_ ==> List((1, 2)))
        _ <- Ref1.int1.Tx(Ref2.int2.str2_).get.map(_ ==> List((1, 2)))

        _ <- Ref1.int1.Tx(Ref2.int2 + Ref3.int3.str3).get.map(_ ==> List((1, 2, (3, "c"))))
        _ <- Ref1.int1.Tx(Ref2.int2 + Ref3.int3.str3_).get.map(_ ==> List((1, 2, 3)))
        _ <- Ref1.int1.Tx(Ref2.int2 + Ref3.int3).get.map(_ ==> List((1, 2, 3)))
        _ <- Ref1.int1.Tx(Ref2.int2 + Ref3.int3_).get.map(_ ==> List((1, 2)))
        _ <- Ref1.int1.Tx(Ref2.int2).get.map(_ ==> List((1, 2)))

        _ <- Ref1.int1.Tx(Ref2.int2_ + Ref3.int3.str3).get.map(_ ==> List((1, (3, "c"))))
        _ <- Ref1.int1.Tx(Ref2.int2_ + Ref3.int3.str3_).get.map(_ ==> List((1, 3)))
        _ <- Ref1.int1.Tx(Ref2.int2_ + Ref3.int3).get.map(_ ==> List((1, 3)))
        _ <- Ref1.int1.Tx(Ref2.int2_ + Ref3.int3_).get.map(_ ==> List(1))
        _ <- Ref1.int1.Tx(Ref2.int2_).get.map(_ ==> List(1))
        _ <- Ref1.int1.get.map(_ ==> List(1))


        _ <- Ref1.int1_.Tx(Ref2.int2.str2 + Ref3.int3.str3).get.map(_ ==> List(((2, "b"), (3, "c"))))
        _ <- Ref1.int1_.Tx(Ref2.int2.str2 + Ref3.int3.str3_).get.map(_ ==> List(((2, "b"), 3)))
        _ <- Ref1.int1_.Tx(Ref2.int2.str2 + Ref3.int3).get.map(_ ==> List(((2, "b"), 3)))
        _ <- Ref1.int1_.Tx(Ref2.int2.str2 + Ref3.int3_).get.map(_ ==> List(((2, "b"))))
        _ <- Ref1.int1_.Tx(Ref2.int2.str2).get.map(_ ==> List((2, "b")))

        _ <- Ref1.int1_.Tx(Ref2.int2.str2_ + Ref3.int3.str3).get.map(_ ==> List((2, (3, "c"))))
        _ <- Ref1.int1_.Tx(Ref2.int2.str2_ + Ref3.int3.str3_).get.map(_ ==> List((2, 3)))
        _ <- Ref1.int1_.Tx(Ref2.int2.str2_ + Ref3.int3).get.map(_ ==> List((2, 3)))
        _ <- Ref1.int1_.Tx(Ref2.int2.str2_ + Ref3.int3_).get.map(_ ==> List(2))
        _ <- Ref1.int1_.Tx(Ref2.int2.str2_).get.map(_ ==> List(2))

        _ <- Ref1.int1_.Tx(Ref2.int2 + Ref3.int3.str3).get.map(_ ==> List((2, (3, "c"))))
        _ <- Ref1.int1_.Tx(Ref2.int2 + Ref3.int3.str3_).get.map(_ ==> List((2, 3)))
        _ <- Ref1.int1_.Tx(Ref2.int2 + Ref3.int3).get.map(_ ==> List((2, 3)))
        _ <- Ref1.int1_.Tx(Ref2.int2 + Ref3.int3_).get.map(_ ==> List(2))
        _ <- Ref1.int1_.Tx(Ref2.int2).get.map(_ ==> List(2))

        _ <- Ref1.int1_.Tx(Ref2.int2_ + Ref3.int3.str3).get.map(_ ==> List((3, "c")))
        _ <- Ref1.int1_.Tx(Ref2.int2_ + Ref3.int3.str3_).get.map(_ ==> List(3))
        _ <- Ref1.int1_.Tx(Ref2.int2_ + Ref3.int3).get.map(_ ==> List(3))
      } yield ()
    }


    "Composite + tx composite meta data" - core { implicit conn =>
      for {
        _ <- (Ref1.int1(1).str1("a") + Ref2.int2(2).str2("b").Tx(Ref3.int3(3).str3("c") + Ref4.int4(4).str4("d"))).save

        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3.str3 + Ref4.int4.str4)).get.map(_ ==> List(((1, "a"), (2, "b", (3, "c"), (4, "d")))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3.str3 + Ref4.int4.str4_)).get.map(_ ==> List(((1, "a"), (2, "b", (3, "c"), 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3.str3 + Ref4.int4)).get.map(_ ==> List(((1, "a"), (2, "b", (3, "c"), 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3.str3 + Ref4.int4_)).get.map(_ ==> List(((1, "a"), (2, "b", (3, "c")))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3.str3_ + Ref4.int4.str4)).get.map(_ ==> List(((1, "a"), (2, "b", 3, (4, "d")))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3.str3_ + Ref4.int4.str4_)).get.map(_ ==> List(((1, "a"), (2, "b", 3, 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3.str3_ + Ref4.int4)).get.map(_ ==> List(((1, "a"), (2, "b", 3, 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3.str3_ + Ref4.int4_)).get.map(_ ==> List(((1, "a"), (2, "b", 3))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3 + Ref4.int4.str4)).get.map(_ ==> List(((1, "a"), (2, "b", 3, (4, "d")))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3 + Ref4.int4.str4_)).get.map(_ ==> List(((1, "a"), (2, "b", 3, 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3 + Ref4.int4)).get.map(_ ==> List(((1, "a"), (2, "b", 3, 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3 + Ref4.int4_)).get.map(_ ==> List(((1, "a"), (2, "b", 3))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3_ + Ref4.int4.str4)).get.map(_ ==> List(((1, "a"), (2, "b", (4, "d")))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3_ + Ref4.int4.str4_)).get.map(_ ==> List(((1, "a"), (2, "b", 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3_ + Ref4.int4)).get.map(_ ==> List(((1, "a"), (2, "b", 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3_ + Ref4.int4_)).get.map(_ ==> List(((1, "a"), (2, "b"))))

        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3.str3 + Ref4.int4.str4)).get.map(_ ==> List(((1, "a"), (2, (3, "c"), (4, "d")))))
        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3.str3 + Ref4.int4.str4_)).get.map(_ ==> List(((1, "a"), (2, (3, "c"), 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3.str3 + Ref4.int4)).get.map(_ ==> List(((1, "a"), (2, (3, "c"), 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3.str3 + Ref4.int4_)).get.map(_ ==> List(((1, "a"), (2, (3, "c")))))
        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3.str3_ + Ref4.int4.str4)).get.map(_ ==> List(((1, "a"), (2, 3, (4, "d")))))
        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3.str3_ + Ref4.int4.str4_)).get.map(_ ==> List(((1, "a"), (2, 3, 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3.str3_ + Ref4.int4)).get.map(_ ==> List(((1, "a"), (2, 3, 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3.str3_ + Ref4.int4_)).get.map(_ ==> List(((1, "a"), (2, 3))))
        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3 + Ref4.int4.str4)).get.map(_ ==> List(((1, "a"), (2, 3, (4, "d")))))
        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3 + Ref4.int4.str4_)).get.map(_ ==> List(((1, "a"), (2, 3, 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3 + Ref4.int4)).get.map(_ ==> List(((1, "a"), (2, 3, 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3 + Ref4.int4_)).get.map(_ ==> List(((1, "a"), (2, 3))))
        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3_ + Ref4.int4.str4)).get.map(_ ==> List(((1, "a"), (2, (4, "d")))))
        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3_ + Ref4.int4.str4_)).get.map(_ ==> List(((1, "a"), (2, 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3_ + Ref4.int4)).get.map(_ ==> List(((1, "a"), (2, 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2.Tx(Ref3.int3_ + Ref4.int4_)).get.map(_ ==> List(((1, "a"), 2)))

        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3.str3 + Ref4.int4.str4)).get.map(_ ==> List(((1, "a"), ((3, "c"), (4, "d")))))
        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3.str3 + Ref4.int4.str4_)).get.map(_ ==> List(((1, "a"), ((3, "c"), 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3.str3 + Ref4.int4)).get.map(_ ==> List(((1, "a"), ((3, "c"), 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3.str3 + Ref4.int4_)).get.map(_ ==> List(((1, "a"), ((3, "c")))))
        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3.str3_ + Ref4.int4.str4)).get.map(_ ==> List(((1, "a"), (3, (4, "d")))))
        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3.str3_ + Ref4.int4.str4_)).get.map(_ ==> List(((1, "a"), (3, 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3.str3_ + Ref4.int4)).get.map(_ ==> List(((1, "a"), (3, 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3.str3_ + Ref4.int4_)).get.map(_ ==> List(((1, "a"), (3))))
        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3 + Ref4.int4.str4)).get.map(_ ==> List(((1, "a"), (3, (4, "d")))))
        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3 + Ref4.int4.str4_)).get.map(_ ==> List(((1, "a"), (3, 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3 + Ref4.int4)).get.map(_ ==> List(((1, "a"), (3, 4))))
        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3 + Ref4.int4_)).get.map(_ ==> List(((1, "a"), (3))))
        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3_ + Ref4.int4.str4)).get.map(_ ==> List(((1, "a"), ((4, "d")))))
        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3_ + Ref4.int4.str4_)).get.map(_ ==> List(((1, "a"), (4))))
        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3_ + Ref4.int4)).get.map(_ ==> List(((1, "a"), (4))))
        _ <- (Ref1.int1.str1 + Ref2.int2_.Tx(Ref3.int3_ + Ref4.int4_)).get.map(_ ==> List(((1, "a"))))

        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3.str3 + Ref4.int4.str4)).get.map(_ ==> List((1, (2, "b", (3, "c"), (4, "d")))))
        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3.str3 + Ref4.int4.str4_)).get.map(_ ==> List((1, (2, "b", (3, "c"), 4))))
        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3.str3 + Ref4.int4)).get.map(_ ==> List((1, (2, "b", (3, "c"), 4))))
        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3.str3 + Ref4.int4_)).get.map(_ ==> List((1, (2, "b", (3, "c")))))
        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3.str3_ + Ref4.int4.str4)).get.map(_ ==> List((1, (2, "b", 3, (4, "d")))))
        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3.str3_ + Ref4.int4.str4_)).get.map(_ ==> List((1, (2, "b", 3, 4))))
        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3.str3_ + Ref4.int4)).get.map(_ ==> List((1, (2, "b", 3, 4))))
        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3.str3_ + Ref4.int4_)).get.map(_ ==> List((1, (2, "b", 3))))
        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3 + Ref4.int4.str4)).get.map(_ ==> List((1, (2, "b", 3, (4, "d")))))
        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3 + Ref4.int4.str4_)).get.map(_ ==> List((1, (2, "b", 3, 4))))
        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3 + Ref4.int4)).get.map(_ ==> List((1, (2, "b", 3, 4))))
        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3 + Ref4.int4_)).get.map(_ ==> List((1, (2, "b", 3))))
        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3_ + Ref4.int4.str4)).get.map(_ ==> List((1, (2, "b", (4, "d")))))
        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3_ + Ref4.int4.str4_)).get.map(_ ==> List((1, (2, "b", 4))))
        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3_ + Ref4.int4)).get.map(_ ==> List((1, (2, "b", 4))))
        _ <- (Ref1.int1 + Ref2.int2.str2.Tx(Ref3.int3_ + Ref4.int4_)).get.map(_ ==> List((1, (2, "b"))))

        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3.str3 + Ref4.int4.str4)).get.map(_ ==> List((1, (2, (3, "c"), (4, "d")))))
        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3.str3 + Ref4.int4.str4_)).get.map(_ ==> List((1, (2, (3, "c"), 4))))
        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3.str3 + Ref4.int4)).get.map(_ ==> List((1, (2, (3, "c"), 4))))
        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3.str3 + Ref4.int4_)).get.map(_ ==> List((1, (2, (3, "c")))))
        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3.str3_ + Ref4.int4.str4)).get.map(_ ==> List((1, (2, 3, (4, "d")))))
        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3.str3_ + Ref4.int4.str4_)).get.map(_ ==> List((1, (2, 3, 4))))
        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3.str3_ + Ref4.int4)).get.map(_ ==> List((1, (2, 3, 4))))
        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3.str3_ + Ref4.int4_)).get.map(_ ==> List((1, (2, 3))))
        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3 + Ref4.int4.str4)).get.map(_ ==> List((1, (2, 3, (4, "d")))))
        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3 + Ref4.int4.str4_)).get.map(_ ==> List((1, (2, 3, 4))))
        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3 + Ref4.int4)).get.map(_ ==> List((1, (2, 3, 4))))
        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3 + Ref4.int4_)).get.map(_ ==> List((1, (2, 3))))
        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3_ + Ref4.int4.str4)).get.map(_ ==> List((1, (2, (4, "d")))))
        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3_ + Ref4.int4.str4_)).get.map(_ ==> List((1, (2, 4))))
        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3_ + Ref4.int4)).get.map(_ ==> List((1, (2, 4))))
        _ <- (Ref1.int1 + Ref2.int2.Tx(Ref3.int3_ + Ref4.int4_)).get.map(_ ==> List((1, 2)))

        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3.str3 + Ref4.int4.str4)).get.map(_ ==> List((1, ((3, "c"), (4, "d")))))
        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3.str3 + Ref4.int4.str4_)).get.map(_ ==> List((1, ((3, "c"), 4))))
        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3.str3 + Ref4.int4)).get.map(_ ==> List((1, ((3, "c"), 4))))
        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3.str3 + Ref4.int4_)).get.map(_ ==> List((1, (3, "c"))))
        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3.str3_ + Ref4.int4.str4)).get.map(_ ==> List((1, (3, (4, "d")))))
        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3.str3_ + Ref4.int4.str4_)).get.map(_ ==> List((1, (3, 4))))
        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3.str3_ + Ref4.int4)).get.map(_ ==> List((1, (3, 4))))
        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3.str3_ + Ref4.int4_)).get.map(_ ==> List((1, 3)))
        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3 + Ref4.int4.str4)).get.map(_ ==> List((1, (3, (4, "d")))))
        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3 + Ref4.int4.str4_)).get.map(_ ==> List((1, (3, 4))))
        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3 + Ref4.int4)).get.map(_ ==> List((1, (3, 4))))
        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3 + Ref4.int4_)).get.map(_ ==> List((1, 3)))
        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3_ + Ref4.int4.str4)).get.map(_ ==> List((1, (4, "d"))))
        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3_ + Ref4.int4.str4_)).get.map(_ ==> List((1, 4)))
        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3_ + Ref4.int4)).get.map(_ ==> List((1, 4)))
        _ <- (Ref1.int1 + Ref2.int2_.Tx(Ref3.int3_ + Ref4.int4_)).get.map(_ ==> List(1))

        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3.str3 + Ref4.int4.str4)).get.map(_ ==> List((2, "b", (3, "c"), (4, "d"))))
        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3.str3 + Ref4.int4.str4_)).get.map(_ ==> List((2, "b", (3, "c"), 4)))
        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3.str3 + Ref4.int4)).get.map(_ ==> List((2, "b", (3, "c"), 4)))
        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3.str3 + Ref4.int4_)).get.map(_ ==> List((2, "b", (3, "c"))))
        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3.str3_ + Ref4.int4.str4)).get.map(_ ==> List((2, "b", 3, (4, "d"))))
        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3.str3_ + Ref4.int4.str4_)).get.map(_ ==> List((2, "b", 3, 4)))
        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3.str3_ + Ref4.int4)).get.map(_ ==> List((2, "b", 3, 4)))
        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3.str3_ + Ref4.int4_)).get.map(_ ==> List((2, "b", 3)))
        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3 + Ref4.int4.str4)).get.map(_ ==> List((2, "b", 3, (4, "d"))))
        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3 + Ref4.int4.str4_)).get.map(_ ==> List((2, "b", 3, 4)))
        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3 + Ref4.int4)).get.map(_ ==> List((2, "b", 3, 4)))
        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3 + Ref4.int4_)).get.map(_ ==> List((2, "b", 3)))
        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3_ + Ref4.int4.str4)).get.map(_ ==> List((2, "b", (4, "d"))))
        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3_ + Ref4.int4.str4_)).get.map(_ ==> List((2, "b", 4)))
        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3_ + Ref4.int4)).get.map(_ ==> List((2, "b", 4)))
        _ <- (Ref1.int1_ + Ref2.int2.str2.Tx(Ref3.int3_ + Ref4.int4_)).get.map(_ ==> List((2, "b")))

        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3.str3 + Ref4.int4.str4)).get.map(_ ==> List((2, (3, "c"), (4, "d"))))
        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3.str3 + Ref4.int4.str4_)).get.map(_ ==> List((2, (3, "c"), 4)))
        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3.str3 + Ref4.int4)).get.map(_ ==> List((2, (3, "c"), 4)))
        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3.str3 + Ref4.int4_)).get.map(_ ==> List((2, (3, "c"))))
        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3.str3_ + Ref4.int4.str4)).get.map(_ ==> List((2, 3, (4, "d"))))
        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3.str3_ + Ref4.int4.str4_)).get.map(_ ==> List((2, 3, 4)))
        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3.str3_ + Ref4.int4)).get.map(_ ==> List((2, 3, 4)))
        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3.str3_ + Ref4.int4_)).get.map(_ ==> List((2, 3)))
        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3 + Ref4.int4.str4)).get.map(_ ==> List((2, 3, (4, "d"))))
        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3 + Ref4.int4.str4_)).get.map(_ ==> List((2, 3, 4)))
        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3 + Ref4.int4)).get.map(_ ==> List((2, 3, 4)))
        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3 + Ref4.int4_)).get.map(_ ==> List((2, 3)))
        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3_ + Ref4.int4.str4)).get.map(_ ==> List((2, (4, "d"))))
        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3_ + Ref4.int4.str4_)).get.map(_ ==> List((2, 4)))
        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3_ + Ref4.int4)).get.map(_ ==> List((2, 4)))
        _ <- (Ref1.int1_ + Ref2.int2.Tx(Ref3.int3_ + Ref4.int4_)).get.map(_ ==> List(2))

        _ <- (Ref1.int1_ + Ref2.int2_.Tx(Ref3.int3.str3 + Ref4.int4.str4)).get.map(_ ==> List(((3, "c"), (4, "d"))))
        _ <- (Ref1.int1_ + Ref2.int2_.Tx(Ref3.int3.str3 + Ref4.int4.str4_)).get.map(_ ==> List(((3, "c"), 4)))
        _ <- (Ref1.int1_ + Ref2.int2_.Tx(Ref3.int3.str3 + Ref4.int4)).get.map(_ ==> List(((3, "c"), 4)))
        _ <- (Ref1.int1_ + Ref2.int2_.Tx(Ref3.int3.str3 + Ref4.int4_)).get.map(_ ==> List((3, "c")))
        _ <- (Ref1.int1_ + Ref2.int2_.Tx(Ref3.int3.str3_ + Ref4.int4.str4)).get.map(_ ==> List((3, (4, "d"))))
        _ <- (Ref1.int1_ + Ref2.int2_.Tx(Ref3.int3.str3_ + Ref4.int4.str4_)).get.map(_ ==> List((3, 4)))
        _ <- (Ref1.int1_ + Ref2.int2_.Tx(Ref3.int3.str3_ + Ref4.int4)).get.map(_ ==> List((3, 4)))
        _ <- (Ref1.int1_ + Ref2.int2_.Tx(Ref3.int3.str3_ + Ref4.int4_)).get.map(_ ==> List(3))
        _ <- (Ref1.int1_ + Ref2.int2_.Tx(Ref3.int3 + Ref4.int4.str4)).get.map(_ ==> List((3, (4, "d"))))
        _ <- (Ref1.int1_ + Ref2.int2_.Tx(Ref3.int3 + Ref4.int4.str4_)).get.map(_ ==> List((3, 4)))
        _ <- (Ref1.int1_ + Ref2.int2_.Tx(Ref3.int3 + Ref4.int4)).get.map(_ ==> List((3, 4)))
        _ <- (Ref1.int1_ + Ref2.int2_.Tx(Ref3.int3 + Ref4.int4_)).get.map(_ ==> List(3))
        _ <- (Ref1.int1_ + Ref2.int2_.Tx(Ref3.int3_ + Ref4.int4.str4)).get.map(_ ==> List((4, "d")))
        _ <- (Ref1.int1_ + Ref2.int2_.Tx(Ref3.int3_ + Ref4.int4.str4_)).get.map(_ ==> List(4))
        _ <- (Ref1.int1_ + Ref2.int2_.Tx(Ref3.int3_ + Ref4.int4)).get.map(_ ==> List(4))
      } yield ()
    }


    "Composite + tx composite meta data + input" - core { implicit conn =>
      for {
        _ <- (Ref1.int1(1).str1("a") + Ref2.int2(2).str2("b").Tx(Ref3.int3(3).str3("c") + Ref4.int4(4).str4("d"))).save

        m1 = m(Ref1.int1(?).str1 + Ref2.int2.str2.Tx(Ref3.int3.str3 + Ref4.int4.str4))
        m2 = m(Ref1.int1.str1 + Ref2.int2(?).str2.Tx(Ref3.int3.str3 + Ref4.int4.str4))
        m3 = m(Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3(?).str3 + Ref4.int4.str4))
        m4 = m(Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3.str3 + Ref4.int4(?).str4))

        _ <- m1(1).get.map(_ ==> List(((1, "a"), (2, "b", (3, "c"), (4, "d")))))
        _ <- m2(2).get.map(_ ==> List(((1, "a"), (2, "b", (3, "c"), (4, "d")))))
        _ <- m3(3).get.map(_ ==> List(((1, "a"), (2, "b", (3, "c"), (4, "d")))))
        _ <- m4(4).get.map(_ ==> List(((1, "a"), (2, "b", (3, "c"), (4, "d")))))

        m12 = m(Ref1.int1(?).str1 + Ref2.int2(?).str2.Tx(Ref3.int3.str3 + Ref4.int4.str4))
        m13 = m(Ref1.int1(?).str1 + Ref2.int2.str2.Tx(Ref3.int3(?).str3 + Ref4.int4.str4))
        m14 = m(Ref1.int1(?).str1 + Ref2.int2.str2.Tx(Ref3.int3.str3 + Ref4.int4(?).str4))
        m23 = m(Ref1.int1.str1 + Ref2.int2(?).str2.Tx(Ref3.int3(?).str3 + Ref4.int4.str4))
        m24 = m(Ref1.int1.str1 + Ref2.int2(?).str2.Tx(Ref3.int3.str3 + Ref4.int4(?).str4))
        m34 = m(Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3(?).str3 + Ref4.int4(?).str4))

        _ <- m12(1, 2).get.map(_ ==> List(((1, "a"), (2, "b", (3, "c"), (4, "d")))))
        _ <- m13(1, 3).get.map(_ ==> List(((1, "a"), (2, "b", (3, "c"), (4, "d")))))
        _ <- m14(1, 4).get.map(_ ==> List(((1, "a"), (2, "b", (3, "c"), (4, "d")))))
        _ <- m23(2, 3).get.map(_ ==> List(((1, "a"), (2, "b", (3, "c"), (4, "d")))))
        _ <- m24(2, 4).get.map(_ ==> List(((1, "a"), (2, "b", (3, "c"), (4, "d")))))
        _ <- m34(3, 4).get.map(_ ==> List(((1, "a"), (2, "b", (3, "c"), (4, "d")))))

        m123 = m(Ref1.int1(?).str1 + Ref2.int2(?).str2.Tx(Ref3.int3(?).str3 + Ref4.int4.str4))
        m124 = m(Ref1.int1(?).str1 + Ref2.int2(?).str2.Tx(Ref3.int3.str3 + Ref4.int4(?).str4))
        m134 = m(Ref1.int1(?).str1 + Ref2.int2.str2.Tx(Ref3.int3(?).str3 + Ref4.int4(?).str4))
        m234 = m(Ref1.int1.str1 + Ref2.int2(?).str2.Tx(Ref3.int3(?).str3 + Ref4.int4(?).str4))
        m334 = m(Ref1.int1.str1 + Ref2.int2.str2.Tx(Ref3.int3(?).str3(?) + Ref4.int4(?).str4))

        _ <- m123(1, 2, 3).get.map(_ ==> List(((1, "a"), (2, "b", (3, "c"), (4, "d")))))
        _ <- m124(1, 2, 4).get.map(_ ==> List(((1, "a"), (2, "b", (3, "c"), (4, "d")))))
        _ <- m134(1, 3, 4).get.map(_ ==> List(((1, "a"), (2, "b", (3, "c"), (4, "d")))))
        _ <- m234(2, 3, 4).get.map(_ ==> List(((1, "a"), (2, "b", (3, "c"), (4, "d")))))
        _ <- m334(3, "c", 4).get.map(_ ==> List(((1, "a"), (2, "b", (3, "c"), (4, "d")))))
      } yield ()
    }


    "Composite with multiple tx meta data molecules" - core { implicit conn =>
      for {
        _ <- m(Ns.str + Ns.int
          .Tx(
            Ns
              .bool_(true)
              .bools_(Set(false))
              .date_(date7)
              .dates_(Set(date8, date9))
              .double_(7.7)
              .doubles_(Set(8.8, 9.9))
              .enum_(enum7)
              .enums_(Set(enum8, enum9))
              +
              Ns
                .long_(7L)
                .longs_(Set(8L, 9L))
                .ref1_(701L)
                .refSub1_(702L)
                .uuid_(uuid7)
                .uuids_(Set(uuid8))
          )
        ) insert Seq(
          ("with tx meta data", 1)
        )

        _ <- Ns.str.int.insert("without tx meta data", 2)

        // Since both attributes are from the same namespace
        // the two following queries will return both entities
        _ <- m(Ns.str.int).get.map(_.sorted ==> List(
          ("with tx meta data", 1),
          ("without tx meta data", 2)
        ))
        _ <- m(Ns.str + Ns.int).get.map(_.sorted ==> List(
          ("with tx meta data", 1),
          ("without tx meta data", 2)
        ))

        // Find by some meta data
        _ <- m(Ns.str.int.Tx(Ns.double_(7.7))).get.map(_.sorted ==> List(("with tx meta data", 1)))
        _ <- m(Ns.str + Ns.int.Tx(Ns.double_(7.7))).get.map(_.sorted ==> List(("with tx meta data", 1)))

        // Find by other meta data
        _ <- m(Ns.str.int.Tx(Ns.long_(7L))).get.map(_.sorted ==> List(("with tx meta data", 1)))
        _ <- m(Ns.str + Ns.int.Tx(Ns.long_(7L))).get.map(_.sorted ==> List(("with tx meta data", 1)))

        // Find by two meta values
        _ <- m(Ns.str.int.Tx(Ns.double_(7.7).long_(7L))).get.map(_ ==> List(("with tx meta data", 1)))
        _ <- m(Ns.str + Ns.int.Tx(Ns.double_(7.7).long_(7L))).get.map(_ ==> List(("with tx meta data", 1)))

        // Entities _without_ meta data
        _ <- m(Ns.str.int.Tx(Ns.long_(Nil))).get.map(_.sorted ==> List(("without tx meta data", 2)))
        _ <- m(Ns.str + Ns.int.Tx(Ns.long_(Nil))).get.map(_.sorted ==> List(("without tx meta data", 2)))
      } yield ()
    }
  }
}