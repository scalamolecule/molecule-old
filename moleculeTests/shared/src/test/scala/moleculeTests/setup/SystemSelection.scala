package moleculeTests.setup

import molecule.datomic.base.util.{System, SystemPeerServer}

/*
To avoid recompiling all tests extending AsyncTestSuite, we grab the system to be tested from here.
 */
object SystemSelection {

  // Uncomment system to be tested:

  // SystemPeer (Peer library)
  // Run test suite with Peer connection
  // For tests against durable dbs like MBrainz, a transactor needs to be running. Not necessary for in-mem db tests.

  // SystemDevLocal (Client library)
  // Run test suite against Client api with local dev-local installation (no running transactor needed)

  // PeerServer (Client library)
  // Since we can't recreate dbs on PeerServer without restarting the Peer Server, we can only
  // test one test at a time so that we avoid asynchronous calls to the same db across tests.
  // So using SystemPeerServer is

  val system: System = {
    //    SystemPeer
    //    SystemDevLocal
    SystemPeerServer
  }
}
