package moleculeTests.tests.core.obj

import molecule.core.util.Helpers
import molecule.datomic.api.out5._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import moleculeTests.tests.core.base.dsl.CoreTest._


object TxMetaData_ extends AsyncTestSuite with Helpers {

  lazy val tests = Tests {

    "Attr + meta" - core { implicit conn =>
      for {
        _ <- Ns.int(0).Tx(Ref1.int1(1)).save
        _ <- Ns.int.Tx(Ref1.int1).getObj.map { o =>
          o.int ==> 0
          o.Tx.Ref1.int1 ==> 1
        }
      } yield ()
    }

    "Ref + meta" - core { implicit conn =>
      for {
        _ <- Ns.int(0).Ref1.int1(1).Tx(Ref2.int2(2)).save
        _ <- Ns.int.Ref1.int1.Tx(Ref2.int2).getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          // Note how Tx attach to the base namespace (Ns)
          o.Tx.Ref2.int2 ==> 2
        }
      } yield ()
    }

    "Refs + meta" - core { implicit conn =>
      for {
        _ <- Ns.int(0).Ref1.int1(1).Ref2.int2(2).Tx(Ref3.int3(3)).save
        _ <- Ns.int.Ref1.int1.Ref2.int2.Tx(Ref3.int3).getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          o.Ref1.Ref2.int2 ==> 2
          o.Tx.Ref3.int3 ==> 3
        }
      } yield ()
    }

    "Ref + meta composites" - core { implicit conn =>
      for {
        _ <- Ns.int(0).Ref1.int1(1).Tx(Ref2.int2(2) + Ref3.int3(3)).save
        _ <- Ns.int.Ref1.int1.Tx(Ref2.int2 + Ref3.int3).getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          o.Tx.Ref2.int2 ==> 2
          o.Tx.Ref3.int3 ==> 3
        }
      } yield ()
    }

    "Refs + meta composites" - core { implicit conn =>
      for {
        _ <- Ns.int(0).Ref1.int1(1).Ref2.int2(2).Tx(Ref3.int3(3) + Ref4.int4(4)).save
        _ <- Ns.int.Ref1.int1.Ref2.int2.Tx(Ref3.int3 + Ref4.int4).getObj.map { o =>
          o.int ==> 0
          o.Ref1.int1 ==> 1
          o.Ref1.Ref2.int2 ==> 2
          o.Tx.Ref3.int3 ==> 3
          o.Tx.Ref4.int4 ==> 4
        }
      } yield ()
    }

    "Composite + meta" - core { implicit conn =>
      for {
        _ <- (Ns.int(0) + Ref1.int1(1).Tx(Ref2.int2(2))).save
        _ <- (Ns.int + Ref1.int1.Tx(Ref2.int2)).getObj.map { o =>
          // All properties are namespaced
          o.Ns.int ==> 0
          o.Ref1.int1 ==> 1
          // Note how Tx attach to the last composite
          o.Ref1.Tx.Ref2.int2 ==> 2
        }
      } yield ()
    }

    "Composite + meta ref" - core { implicit conn =>
      for {
        _ <- (Ns.int(0) + Ref1.int1(1).Tx(Ref2.int2(2).Ref3.int3(3))).save
        _ <- (Ns.int + Ref1.int1.Tx(Ref2.int2.Ref3.int3)).getObj.map { o =>
          o.Ns.int ==> 0
          o.Ref1.int1 ==> 1
          o.Ref1.Tx.Ref2.int2 ==> 2
          o.Ref1.Tx.Ref2.Ref3.int3 ==> 3
        }
      } yield ()
    }

    "Composite/ref + meta ref" - core { implicit conn =>
      for {
        _ <- (Ns.int(0) + Ref1.int1(1).Ref2.int2(2).Tx(Ref3.int3(3))).save
        _ <- (Ns.int + Ref1.int1.Ref2.int2.Tx(Ref3.int3)).getObj.map { o =>
          o.Ns.int ==> 0
          o.Ref1.int1 ==> 1
          o.Ref1.Ref2.int2 ==> 2
          o.Ref1.Tx.Ref3.int3 ==> 3
        }
      } yield ()
    }

    "Composite/refs + meta ref" - core { implicit conn =>
      for {
        _ <- (Ns.int(0) + Ref1.int1(1).Ref2.int2(2).Ref3.int3(3).Tx(Ref4.int4(4))).save
        _ <- (Ns.int + Ref1.int1.Ref2.int2.Ref3.int3.Tx(Ref4.int4)).getObj.map { o =>
          o.Ns.int ==> 0
          o.Ref1.int1 ==> 1
          o.Ref1.Ref2.int2 ==> 2
          o.Ref1.Ref2.Ref3.int3 ==> 3
          o.Ref1.Tx.Ref4.int4 ==> 4
        }
      } yield ()
    }

    "Composite/ref + meta composite" - core { implicit conn =>
      for {
        _ <- (Ns.int(0) + Ref1.int1(1).Ref2.int2(2).Tx(Ref3.int3(3) + Ref4.int4(4))).save
        _ <- (Ns.int + Ref1.int1.Ref2.int2.Tx(Ref3.int3 + Ref4.int4)).getObj.map { o =>
          o.Ns.int ==> 0
          o.Ref1.int1 ==> 1
          o.Ref1.Ref2.int2 ==> 2
          o.Ref1.Tx.Ref3.int3 ==> 3
          o.Ref1.Tx.Ref4.int4 ==> 4
        }
      } yield ()
    }
  }
}
