package moleculeTests.restore

import molecule.datomic.api.out1._
import molecule.datomic.peer.facade.Datomic_Peer
import moleculeTests.dataModels.examples.datomic.mbrainz.schema.MBrainzSchemaLowerToUpper
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object CapitalizeMBrainzSchema extends App {

  implicit val futConn = Datomic_Peer.connect("dev", "localhost:4334/mbrainz-1968-1973")

  for {
    _ <- Schema.a.get.map(attrs => attrs.sorted foreach println)
    _ = println("--------------------")

    upper <- Schema.a(":Artist/name").get
    conn <- futConn
    _ <- if (upper.isEmpty) {
      // Add uppercase-namespaced attribute names so that we can access the externally
      // transacted lowercase names with uppercase names of the molecule code.
      println("Transacting nss from lower to upper..")
      conn.transact(MBrainzSchemaLowerToUpper.edn)
    } else Future.unit
  } yield ()
}
