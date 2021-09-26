package moleculeTests.setup

import molecule.datomic.base.marshalling.DatomicRpc


class MoleculeTestFramework extends utest.runner.Framework {
  override def setup() = {
    //    println("Setting up JVM CustomFramework")
  }
  override def teardown() = {
    //    println("Tearing down JVM CustomFramework")
  }
}
