//package molecule.test
//
//import java.io.FileReader
//import datomic.{Connection, Peer, Util}
//import molecule.db.DatomicFacade
//import molecule.examples.seattle.dsl._
//import molecule.examples.seattle.schema.SeattleSchema
//import molecule.util.MoleculeSpec
//
//
//trait SchemaSpec extends MoleculeSpec with DatomicFacade {
//
//  def loadFromFiles(schemaFile: String, dataFile: String, version: Int) = {
//    val uri = "datomic:mem://seattle" + version
//    Peer.deleteDatabase(uri)
//    Peer.createDatabase(uri)
//    implicit val conn = Peer.connect(uri)
//
//    val dataDir = "examples/resources/seattle/"
//    val schema_rdr = new FileReader(dataDir + schemaFile)
//    val schema_tx = Util.readAll(schema_rdr).get(0).asInstanceOf[java.util.List[_]]
//    conn.transact(schema_tx).get()
//
//    val data_rdr = new FileReader(dataDir + dataFile)
//    val data_tx = Util.readAll(data_rdr).get(0).asInstanceOf[java.util.List[_]]
//    conn.transact(data_tx).get()
//
//    conn
//  }
//
//  def load(tx: java.util.List[_], version: Int): Connection = {
//    val uri = "datomic:mem://seattle" + version
//    Peer.deleteDatabase(uri)
//    Peer.createDatabase(uri)
//    implicit val conn = Peer.connect(uri)
//
//    // Save schema
//    conn.transact(tx).get()
//
//    // Load data
//    Community.name.url.`type`.orgtype.category.Neighborhood.name.District.name.region.insert(seattleData)
//
//    // Pass on connection
//    conn
//  }
//
//  implicit val conn = load(SeattleSchema.tx, 1)
//
//  lazy val seattleData = List(
//    ("15th Ave Community", "http://groups.yahoo.com/group/15thAve_Community/", "email_list", "community", Set("15th avenue residents"), "Capitol Hill", "East", "e")
//  )
//
//
//  //  // Extractor
//  //  val dataFromFile = Community.name.url.`type`.orgtype.category.Neighborhood.name.District.name.region.hls.map { rec =>
//  //    rec.toList.map {
//  //      case set: Set[_] => set.map("\"" + _.toString + "\"")
//  //      case other       => "\"" + other.toString + "\""
//  //    }
//  //  }.map(e => e.mkString("\n(", ", ", ")"))
//  //  println(dataFromFile)
//  //  println()
//  //  println(seattleData.map(_._1).sorted.mkString("\n"))
//}