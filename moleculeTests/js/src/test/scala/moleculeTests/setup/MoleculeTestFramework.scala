package moleculeTests.setup

import boopickle.Default._
import molecule.core.marshalling.{BooPicklers, MoleculeRpc, WebClient}

class MoleculeTestFramework extends utest.runner.Framework with WebClient with BooPicklers {

  //  override def setup(): Unit = {
  //    //    println("Setting up JS MoleculeTestFramework")
  //  }

  override def teardown(): Unit = {
    //    println("Tearing down JS MoleculeTestFramework")

    // Clear connection pool after each test suite run
    moleculeAjax("localhost", 8080).wire[MoleculeRpc].clearConnPool()
  }
}
