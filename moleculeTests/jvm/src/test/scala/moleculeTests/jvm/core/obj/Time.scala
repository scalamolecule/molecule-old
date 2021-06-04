//package moleculeTests.jvm.core.obj
//
//import java.io.FileReader
//import java.util.{List => jList}
//import molecule.core.util.Helpers
//import molecule.datomic.api.out3._
//import moleculeTests.setup.AsyncTestSuite
//import moleculeTests.tests.core.base.dsl.CoreTest._
//import utest._
//import scala.concurrent.ExecutionContext.Implicits.global
//
//
//object Time extends AsyncTestSuite with Helpers {
//
//  lazy val tests = Tests {
//
//    "With" - core { implicit conn =>
//      // Tx data from edn file
//      // contains: "[{:Ns/int 2} {:Ns/int 3}]"
//      val data      = new FileReader("moleculeTests/jvm/resources/tests/core/time/save2-3.dtm")
//      val txData2_3 = datomic.Util.readAll(data).get(0).asInstanceOf[jList[jList[_]]]
//
//      for {
//        _ <- Ns.int(1).save
//
//        _ <- Ns.int.getObjListWith(txData2_3).map(_.map(_.int) ==> List(1, 2, 3))
//        _ <- Ns.int.getObjListWith(txData2_3, 2).map(_.map(_.int) ==> List(1, 2))
//
//        _ <- Ns.int.getObjArrayWith(txData2_3).map(_.map(_.int) ==> Array(1, 2, 3))
//        _ <- Ns.int.getObjArrayWith(txData2_3, 2).map(_.map(_.int) ==> Array(1, 2))
//
//        _ <- Ns.int.getObjIterableWith(txData2_3).map(_.iterator.toList.map(_.int) ==> Iterator(1, 2, 3).toList)
//      } yield ()
//    }
//  }
//}
