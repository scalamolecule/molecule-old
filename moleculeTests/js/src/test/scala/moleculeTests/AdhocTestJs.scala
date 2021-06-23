//package moleculeTests
//
//import molecule.core.util.JavaUtil
//import molecule.datomic.api.in1_out13._
//import moleculeTests.setup.AsyncTestSuite
//import moleculeTests.tests.core.base.dsl.CoreTest._
//import utest._
//import scala.concurrent.ExecutionContext.Implicits.global
//
//
//object AdhocTestJs extends AsyncTestSuite with JavaUtil {
//
//
//  lazy val tests = Tests {
//
//    "AsOf" - core { implicit conn =>
//      for {
//        _ <- Ns.str insert str1
//        _ <- Ns.int insert int1
//        _ <- Ns.long insert long1
//        _ <- Ns.double insert double1
//        _ <- Ns.bool insert bool1
//        _ <- Ns.date insert date1
//        _ <- Ns.uuid insert uuid1
//        _ <- Ns.uri insert uri1
//        _ <- Ns.enum insert enum1
//
//        // Get one value (RuntimeException if no value)
//        _ <- Ns.str.get.map(_ ==> List(str1))
//        _ <- Ns.int.get.map(_ ==> List(int1))
//        _ <- Ns.long.get.map(_ ==> List(long1))
//        _ <- Ns.double.get.map(_ ==> List(double1))
//        _ <- Ns.bool.get.map(_ ==> List(bool1))
//        _ <- Ns.date.get.map(_ ==> List(date1))
//        _ <- Ns.uuid.get.map(_ ==> List(uuid1))
//        _ <- Ns.uri.get.map(_ ==> List(uri1))
//        _ <- Ns.enum.get.map(_ ==> List(enum1))
//      } yield ()
//    }
//  }
//}
