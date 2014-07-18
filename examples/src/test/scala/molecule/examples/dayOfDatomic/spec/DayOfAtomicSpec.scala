package molecule.examples.dayOfDatomic.spec
import molecule.util.MoleculeSpec
import org.specs2.mutable._
import datomic.Peer
import datomic.Util.readAll
import java.io.FileReader

trait DayOfAtomicSpec extends MoleculeSpec {

  def init(name: String, files: String*) = {
    val uri = "datomic:mem://" + name
    Peer.deleteDatabase(uri)
    Peer.createDatabase(uri)
    val conn = Peer.connect(uri)
    if (!files.isEmpty) {
      files.map(file =>
        conn.transact(readAll(
          new FileReader("examples/resources/day-of-datomic/edn/" + file)
        )))
    }
    conn
  }
}