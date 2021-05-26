//package moleculeTests
//
//import java.lang.RuntimeException
//import java.nio.ByteBuffer
//import java.util.UUID
//import boopickle.Default._
//import molecule.core.marshalling.{Conn_Js, DatomicInMemProxy, DbException}
//import molecule.datomic.api.in1_out13._
//import molecule.datomic.base.facade.Conn
//import molecule.core.attr.Attribute.{bool1, date1, dates1, dates2, double1, doubles1, doubles2, enum1, enums1, enums2, int1, ints1, ints2, long1, longs1, longs2, str1, strs1, strs2, uri1, uris1, uris2, uuid1, uuids1, uuids2}
//import moleculeTests.setup.AsyncTestSuite
//import moleculeTests.setup.core.CoreData
//import moleculeTests.tests.core.base.dsl.CoreTest._
//import moleculeTests.tests.core.base.schema.CoreTestSchema
//import moleculeTests.tests.examples.datomic.dayOfDatomic.schema.GraphSchema
//import org.scalajs.dom
//import org.scalajs.dom.ext.{Ajax, AjaxException}
//import utest._
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.{Future, Promise}
//import scala.scalajs.js.Date
//import scala.scalajs.js.timers._
//import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}
//import scala.util.{Failure, Success}
//import scala.util.control.Exception
//
//object AdhocTestJs extends AsyncTestSuite {
//
//  println("")
//
//  lazy val tests = Tests {
//
//    "hej" - core { implicit conn =>
//      for {
//        // insert single value for one cardinality-1 attribute
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
//        _ <- {
//          println("X")
//          Ns.str.get === List(str1)
//        }
//        _ <- Ns.int.get === List(int1)
//        _ <- Ns.long.get === List(long1)
//        _ <- Ns.double.get === List(double1)
//        _ <- Ns.bool.get === List(bool1)
//        _ <- Ns.date.get === List(date1)
//        _ <- Ns.uuid.get === List(uuid1)
//        _ <- Ns.uri.get === List(uri1)
//        _ <- Ns.enum.get === List(enum1)
//      } yield ()
//    }
//
//    "sa" - core { implicit conn =>
//      for {
//        // insert single value for one cardinality-1 attribute
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
//        _ <- Ns.str.get === List(str1)
//        _ <- Ns.int.get === List(int1)
//        _ <- Ns.long.get === List(long1)
//        _ <- Ns.double.get === List(double1)
//        _ <- Ns.bool.get === List(bool1)
//        _ <- Ns.date.get === List(date1)
//        _ <- Ns.uuid.get === List(uuid1)
//        _ <- Ns.uri.get === List(uri1)
//        _ <- Ns.enum.get === List(enum1)
//      } yield ()
//    }
//
//
//  }
//}
