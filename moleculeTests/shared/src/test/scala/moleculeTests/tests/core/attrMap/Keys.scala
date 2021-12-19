package moleculeTests.tests.core.attrMap

import molecule.datomic.api.out2._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import molecule.core.util.Executor._


object Keys extends Base {

  lazy val tests = Tests {

    "One key" - core { implicit conn =>
      for {
        _ <- testData

        _ <- Ns.int.strMap.k("en").get.map(_ ==> List(
          (1, Map("en" -> "Hi there")),
          (2, Map("en" -> "Oh, Hi")),
          (3, Map("en" -> "Hello"))
        ))
        _ <- Ns.int.strMap_.k("en").get.map(_ ==> List(1, 2, 3))

        _ <- Ns.int.intMap.k("en").get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("en" -> 10)),
          (3, Map("en" -> 30))
        ))
        _ <- Ns.int.intMap_.k("en").get.map(_ ==> List(1, 2, 3))

        // OBS: Since a map attribute returns a map, we have to beware that
        // key/value pairs are coalesced if there are no other attributes and
        // we end up with one random pair:
        _ <- Ns.strMap.k("en").get.map(_ ==> List(
          Map("en" -> "Oh, Hi") // random pair...
        ))

        // A workaround to get all the mapped values only is to add the `e`
        // entity attribute (which always has a value) and then filter it out
        // from the result set:
        _ <- Ns.e.strMap.k("en").get.map(_.map(_._2) ==> List(
          Map("en" -> "Hi there"),
          Map("en" -> "Oh, Hi"),
          Map("en" -> "Hello")
        ))
        _ <- Ns.e.intMap.k("en").get.map(_.map(_._2) ==> List(
          Map("en" -> 10),
          Map("en" -> 10),
          Map("en" -> 30)
        ))

        // Then we might as well return only the values
        _ <- Ns.e.strMap.k("en").get.map(_.map(_._2("en")) ==> List(
          "Hi there",
          "Oh, Hi",
          "Hello"
        ))

        _ <- Ns.e.intMap.k("en").get.map(_.map(_._2("en")) ==> List(
          10,
          10,
          30
        ))
      } yield ()
    }


    "Multiple keys (OR semantics)" - core { implicit conn =>
      // Variables
      val seq1 = Seq("en", "fr")
      val seq2 = Seq(en, fr)

      val en_fr_str  = List(
        (1, Map("en" -> "Hi there")),
        (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
        (3, Map("en" -> "Hello"))
      )
      val en_fr_int  = List(
        (1, Map("en" -> 10)),
        (2, Map("fr" -> 20, "en" -> 10)),
        (3, Map("en" -> 30))
      )
      val en_fr_date = List(
        (1, Map("en" -> date1)),
        (2, Map("fr" -> date2, "en" -> date1)),
        (3, Map("en" -> date3))
      )

      for {
        _ <- testData

        _ <- Ns.int.strMap.k("en" or "fr").get.map(_ ==> en_fr_str)
        _ <- Ns.int.strMap.k("en", "fr").get.map(_ ==> en_fr_str)
        _ <- Ns.int.strMap.k(Seq("en", "fr")).get.map(_ ==> en_fr_str)
        _ <- Ns.int.strMap.k(Seq(en, fr)).get.map(_ ==> en_fr_str)
        _ <- Ns.int.strMap.k(seq1).get.map(_ ==> en_fr_str)
        _ <- Ns.int.strMap.k(seq2).get.map(_ ==> en_fr_str)

        _ <- Ns.int.strMap_.k("en" or "fr").get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.strMap_.k("en", "fr").get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.strMap_.k(Seq("en", "fr")).get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.strMap_.k(Seq(en, fr)).get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.strMap_.k(seq1).get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.strMap_.k(seq2).get.map(_ ==> List(1, 2, 3))


        _ <- Ns.int.intMap.k("en" or "fr").get.map(_ ==> en_fr_int)
        _ <- Ns.int.intMap.k("en", "fr").get.map(_ ==> en_fr_int)
        _ <- Ns.int.intMap.k(Seq("en", "fr")).get.map(_ ==> en_fr_int)
        _ <- Ns.int.intMap.k(Seq(en, fr)).get.map(_ ==> en_fr_int)
        _ <- Ns.int.intMap.k(seq1).get.map(_ ==> en_fr_int)
        _ <- Ns.int.intMap.k(seq2).get.map(_ ==> en_fr_int)

        _ <- Ns.int.intMap_.k("en" or "fr").get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.intMap_.k("en", "fr").get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.intMap_.k(Seq("en", "fr")).get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.intMap_.k(Seq(en, fr)).get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.intMap_.k(seq1).get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.intMap_.k(seq2).get.map(_ ==> List(1, 2, 3))


        _ <- Ns.int.dateMap.k("en" or "fr").get.map(_ ==> en_fr_date)
        _ <- Ns.int.dateMap.k("en", "fr").get.map(_ ==> en_fr_date)
        _ <- Ns.int.dateMap.k(Seq("en", "fr")).get.map(_ ==> en_fr_date)
        _ <- Ns.int.dateMap.k(Seq(en, fr)).get.map(_ ==> en_fr_date)
        _ <- Ns.int.dateMap.k(seq1).get.map(_ ==> en_fr_date)
        _ <- Ns.int.dateMap.k(seq2).get.map(_ ==> en_fr_date)

        _ <- Ns.int.dateMap_.k("en" or "fr").get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.dateMap_.k("en", "fr").get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.dateMap_.k(Seq("en", "fr")).get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.dateMap_.k(Seq(en, fr)).get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.dateMap_.k(seq1).get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.dateMap_.k(seq2).get.map(_ ==> List(1, 2, 3))
      } yield ()

      // AND semantics aren't relevant for attribute maps since we should
      // consider them as card-one containers of one "value with variation".
      // When we need to work with those variations we can narrow those
      // by OR semantics.
    }
  }
}