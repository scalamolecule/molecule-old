package molecule

import java.lang.RuntimeException
import java.nio.ByteBuffer
import java.util.UUID
import boopickle.Default._
import molecule.core.marshalling.{Conn_Js, DatomicInMemProxy, DbException}
import molecule.datomic.api.in1_out13._
import molecule.datomic.base.facade.Conn
import molecule.core.attr.Attribute.{bool1, date1, dates1, dates2, double1, doubles1, doubles2, enum1, enums1, enums2, int1, ints1, ints2, long1, longs1, longs2, str1, strs1, strs2, uri1, uris1, uris2, uuid1, uuids1, uuids2}
import molecule.setup.AsyncTestSuite
import molecule.setup.core.CoreData
import molecule.tests.core.base.dsl.CoreTest._
import molecule.tests.core.base.schema.CoreTestSchema
import molecule.tests.examples.datomic.dayOfDatomic.schema.GraphSchema
import org.scalajs.dom
import org.scalajs.dom.ext.{Ajax, AjaxException}
import utest._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.scalajs.js.Date
import scala.scalajs.js.timers._
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}
import scala.util.{Failure, Success}
import scala.util.control.Exception

object AdhocTestJs extends AsyncTestSuite {

  println("")

  lazy val tests = Tests {

    def myTest[T](func: Conn => T) = {
      func {
        println("A")
        val conn = Conn_Js.inMem(CoreTestSchema)
        println(conn.isJsPlatform)
        conn
      }
      func {
        println("B")
        Conn_Js.inMem(CoreTestSchema)
      }
    }


    "hej" - myTest { implicit conn =>
      for {
        // insertAsync single value for one cardinality-1 attribute
        _ <- Ns.str insertAsync str1
        _ <- Ns.int insertAsync int1
        _ <- Ns.long insertAsync long1
        _ <- Ns.double insertAsync double1
        _ <- Ns.bool insertAsync bool1
        _ <- Ns.date insertAsync date1
        _ <- Ns.uuid insertAsync uuid1
        _ <- Ns.uri insertAsync uri1
        _ <- Ns.enum insertAsync enum1

        // Get one value (RuntimeException if no value)
        _ <- {
          println("X")
          Ns.str.getAsync === List(str1)
        }
        _ <- Ns.int.getAsync === List(int1)
        _ <- Ns.long.getAsync === List(long1)
        _ <- Ns.double.getAsync === List(double1)
        _ <- Ns.bool.getAsync === List(bool1)
        _ <- Ns.date.getAsync === List(date1)
        _ <- Ns.uuid.getAsync === List(uuid1)
        _ <- Ns.uri.getAsync === List(uri1)
        _ <- Ns.enum.getAsync === List(enum1)
      } yield ()
    }

    "sa" - myTest { implicit conn =>
      for {
        // insertAsync single value for one cardinality-1 attribute
        _ <- Ns.str insertAsync str1
        _ <- Ns.int insertAsync int1
        _ <- Ns.long insertAsync long1
        _ <- Ns.double insertAsync double1
        _ <- Ns.bool insertAsync bool1
        _ <- Ns.date insertAsync date1
        _ <- Ns.uuid insertAsync uuid1
        _ <- Ns.uri insertAsync uri1
        _ <- Ns.enum insertAsync enum1

        // Get one value (RuntimeException if no value)
        _ <- Ns.str.getAsync === List(str1)
        _ <- Ns.int.getAsync === List(int1)
        _ <- Ns.long.getAsync === List(long1)
        _ <- Ns.double.getAsync === List(double1)
        _ <- Ns.bool.getAsync === List(bool1)
        _ <- Ns.date.getAsync === List(date1)
        _ <- Ns.uuid.getAsync === List(uuid1)
        _ <- Ns.uri.getAsync === List(uri1)
        _ <- Ns.enum.getAsync === List(enum1)
      } yield ()
    }


  }
}
