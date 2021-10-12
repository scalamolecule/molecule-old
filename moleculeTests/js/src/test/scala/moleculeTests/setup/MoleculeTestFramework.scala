package moleculeTests.setup

import molecule.core.marshalling.MoleculeWebClient


class MoleculeTestFramework extends utest.runner.Framework {

  //  override def setup(): Unit = {
  //    //    println("Setting up JS MoleculeTestFramework")
  //  }

  override def teardown(): Unit = {
    //    println("Tearing down JS MoleculeTestFramework")

    // Clear connection pool after each test suite run
    MoleculeWebClient.rpc.clearConnPool()
  }
}
