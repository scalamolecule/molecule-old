package moleculeTests

import java.util.Collections
import molecule.core.dsl.base
import molecule.core.exceptions.MoleculeException
import molecule.datomic.api.in3_out11._
import molecule.core.macros.rowAttr.{CastOptNested, CastTypes, JsonBase}
import molecule.core.marshalling.nodes.{Node, Obj, Prop}
import molecule.core.marshalling.unpackAttr.String2cast
import molecule.core.marshalling.unpackers.Packed2EntityMap
import molecule.core.util.Helpers
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object AdhocJs extends AsyncTestSuite with Helpers
  with String2cast with CastTypes with CastOptNested with JsonBase {


  lazy val tests = Tests {

    "adhoc js" - core { implicit futConn =>
      for {
        _ <- Future(1 ==> 1) // dummy to start monad chain if needed
        conn <- futConn


//        _ <- (Ns.int.str + Ref1.str1.int1).insert((1, "a"), ("b", 2))
//        _ <- (Ns.int.str + Ref1.str1.int1).get.map(_ ==> List(((1, "a"), ("b", 2))))
        _ <- (Ns.int.str + Ref1.str1.int1).getObj.map { o =>
          o.Ns.int ==> 1
          o.Ns.str ==> "a"
          o.Ref1.int1 ==> 2
          o.Ref1.str1 ==> "b"
        }

        //        _ <- (Ns.int + Ref1.str1.int1).get


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
