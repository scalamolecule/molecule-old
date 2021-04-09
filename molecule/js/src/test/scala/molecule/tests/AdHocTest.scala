package molecule.tests
import molecule.datomic.peer.facade.DummyConn
import utest._


object AdHocTest extends TestSuite {

  val tests = Tests {

    implicit val conn = DummyConn



    test("first") {
      1 ==> 1

//      Ns.str.

    }
  }
}
