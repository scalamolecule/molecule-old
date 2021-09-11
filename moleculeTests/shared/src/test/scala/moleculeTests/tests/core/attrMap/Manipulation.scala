package moleculeTests.tests.core.attrMap

import moleculeTests.dataModels.core.base.dsl.CoreTest._
import molecule.datomic.api.out1._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object Manipulation extends Base {

  lazy val tests = Tests {

    "Manipulate" - core { implicit conn =>
      for {
        // Insert
        eid <- Ns.strMap.insert(Map("en" -> "Hi")).map(_.eid)
        _ <- Ns.strMap.get.map(_.head ==> Map("en" -> "Hi"))

        // Update + Add

        // When a previous populated key is encountered the old fact is
        // retracted and the new one asserted (like an update).
        _ <- Ns(eid).strMap.assert("en" -> "Hi there", "fr" -> "Bonjour").update
        _ <- Ns.strMap.get.map(_.head ==> Map("en" -> "Hi there", "fr" -> "Bonjour"))


        // Remove pair (by key)

        _ <- Ns(eid).strMap.retract("en").update
        _ <- Ns.strMap.get.map(_.head ==> Map("fr" -> "Bonjour"))


        // Applying nothing (empty parenthesises)
        // finds and retract all values of an attribute

        _ <- Ns(eid).strMap().update
        _ <- Ns.strMap.get.map(_ ==> Nil)
      } yield ()
    }
  }
}