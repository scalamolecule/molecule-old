package moleculeTests

import molecule.core.exceptions.MoleculeException
import molecule.core.macros.rowAttr.{CastOptNested, CastTypes, JsonBase}
import molecule.core.marshalling.unpackAttr.String2cast
import molecule.core.util.Helpers
import molecule.datomic.api.in3_out12._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import molecule.core.util.Executor._
import scala.concurrent.Future


object AdhocJs extends AsyncTestSuite with Helpers
  with CastTypes with CastOptNested with JsonBase {


  lazy val tests = Tests {

    "adhocJs" - core { implicit futConn =>
      for {
        conn <- futConn


        _ <- Ns.int.insert(1, 2, 3)
        _ <- Ns.int.d1.get.map(_ ==> List(3, 2, 1))



      } yield ()
    }


    //    "adhoc" - bidirectional { implicit conn =>
    //      import moleculeTests.dataModels.core.bidirectionals.dsl.Bidirectional._
    //
    //      for {
    //        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
    //
    //        adam <- Person.name.insert("Adam").map(_.eid)
    //
    //        // Create and reference Lisa to Adam
    //        lisa <- Person(adam).Spouse.name("Lisa").update.map(_.eid)
    //
    //        _ <- Person(adam).Spouse.name.get.map(_ ==> List("Lisa"))
    //        _ <- Person(lisa).Spouse.name.get.map(_ ==> List("Adam"))
    //
    ////        _ <- Person.name.Spouse.name.get.map(_.sorted ==> List(
    ////          ("Adam", "Lisa"),
    ////          ("Lisa", "Adam")
    ////        ))
    ////
    ////        // Retract Adam and all references to/from Adam
    ////        _ <- adam.retract
    ////
    ////        // Lisa remains and both references retracted
    ////        _ <- Person.name.get.map(_ ==> List("Lisa"))
    ////        _ <- Person(adam).Spouse.name.get.map(_ ==> List())
    ////        _ <- Person(lisa).Spouse.name.get.map(_ ==> List())
    ////        _ <- Person.name.Spouse.name.get.map(_.sorted ==> List())
    //
    //      } yield ()
    //    }

  }
}
