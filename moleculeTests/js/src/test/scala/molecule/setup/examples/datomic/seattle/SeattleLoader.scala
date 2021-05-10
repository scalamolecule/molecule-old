package molecule.setup.examples.datomic.seattle

import molecule.datomic.api.out8._
import molecule.datomic.base.facade.Conn
import molecule.setup.SpecHelpers
import molecule.setup.examples.seattle.SeattleData
import molecule.tests.examples.datomic.seattle.dsl.Seattle._

case class SeattleLoader(conn0: Conn) extends SpecHelpers with SeattleData {

  implicit val conn = conn0

  // Insert data
  Community.name.url.tpe.orgtype$.category$.Neighborhood.name.District.name.region$ insert seattleData

  //    // Extractor
  //    val dataFromFile = Community.name.url.`type`.orgtype.category.Neighborhood.name.District.name.region.hl.map { rec =>
  //      rec.toList.map {
  //        case set: Set[_] => set.map("\"" + _.toString + "\"")
  //        case other       => "\"" + other.toString + "\""
  //      }
  //    }.map(e => e.mkString("\n(", ", ", ")"))
  //    println(dataFromFile)
  //    println()
  //    println(seattleData.map(_._1).sorted.mkString("\n"))
}