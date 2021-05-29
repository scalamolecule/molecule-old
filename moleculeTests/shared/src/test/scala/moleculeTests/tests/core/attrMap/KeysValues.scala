package moleculeTests.tests.core.attrMap

import molecule.datomic.api.out2._
import moleculeTests.tests.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global

object KeysValues extends Base {

  val en_da       = Seq("en", "da")
  val hi_he       = Seq("Hi there", "Hello")
  val _10_30      = Seq(10, 30)
  val date1_date3 = Seq(date1, date3)

  lazy val tests = Tests {

    "(key)(value)" - core { implicit conn =>
      val hi_hello       = List(
        (1, Map("en" -> "Hi there")),
        (3, Map("en" -> "Hello"))
      )
      val en_10_30       = List(
        (1, Map("en" -> 10)),
        (2, Map("en" -> 10)),
        (3, Map("en" -> 30))
      )
      val en_date1_date3 = List(
        (1, Map("en" -> date1)),
        (2, Map("en" -> date1)),
        (3, Map("en" -> date3))
      )

      for {
        _ <- testData

        // As an alternative to pair notation we can use key/value notation.
        // The following two molecules accomplish the same:
        _ <- Ns.int.strMap("en" -> "Hi there").get.map(_ ==> List((1, Map("en" -> "Hi there"))))
        _ <- Ns.int.strMap.k("en")("Hi there").get.map(_ ==> List((1, Map("en" -> "Hi there"))))


        // Multiple values

        // The k/apply syntax is suitable when we want to treat
        // key and value independently. So, instead of repeating the key:
        _ <- Ns.int.strMap("en" -> "Hi there", "en" -> "Hello").get.map(_ ==> hi_hello)

        // ..we can let the key "en" be applied to both values:
        _ <- Ns.int.strMap.k("en")("Hi there" or "Hello").get.map(_ ==> hi_hello)
        _ <- Ns.int.strMap.k("en")("Hi there", "Hello").get.map(_ ==> hi_hello)
        _ <- Ns.int.strMap.k("en")(Seq("Hi there", "Hello")).get.map(_ ==> hi_hello)
        _ <- Ns.int.strMap.k(en)(hi_he).get.map(_ ==> hi_hello)

        _ <- Ns.int.strMap_.k("en")("Hi there" or "Hello").get.map(_ ==> List(1, 3))
        _ <- Ns.int.strMap_.k("en")("Hi there", "Hello").get.map(_ ==> List(1, 3))
        _ <- Ns.int.strMap_.k("en")(Seq("Hi there", "Hello")).get.map(_ ==> List(1, 3))
        _ <- Ns.int.strMap_.k(en)(hi_he).get.map(_ ==> List(1, 3))


        _ <- Ns.int.intMap.k("en")(10 or 30).get.map(_ ==> en_10_30)
        _ <- Ns.int.intMap.k("en")(10, 30).get.map(_ ==> en_10_30)
        _ <- Ns.int.intMap.k("en")(Seq(10, 30)).get.map(_ ==> en_10_30)
        _ <- Ns.int.intMap.k("en")(_10_30).get.map(_ ==> en_10_30)

        _ <- Ns.int.intMap_.k("en")(10 or 30).get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.intMap_.k("en")(10, 30).get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.intMap_.k("en")(Seq(10, 30)).get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.intMap_.k("en")(_10_30).get.map(_ ==> List(1, 2, 3))


        _ <- Ns.int.dateMap.k("en")(date1 or date3).get.map(_ ==> en_date1_date3)
        _ <- Ns.int.dateMap.k("en")(date1, date3).get.map(_ ==> en_date1_date3)
        _ <- Ns.int.dateMap.k("en")(Seq(date1, date3)).get.map(_ ==> en_date1_date3)
        _ <- Ns.int.dateMap.k("en")(date1_date3).get.map(_ ==> en_date1_date3)

        _ <- Ns.int.dateMap_.k("en")(date1 or date3).get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.dateMap_.k("en")(date1, date3).get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.dateMap_.k("en")(Seq(date1, date3)).get.map(_ ==> List(1, 2, 3))
        _ <- Ns.int.dateMap_.k("en")(date1_date3).get.map(_ ==> List(1, 2, 3))
      } yield ()
    }


    "Regular expressions" - core { implicit conn =>
      for {
        _ <- testData

        // String attribute maps allow us to use regexes
        // Note that searches are case-insensitive

        _ <- Ns.int.strMap.k("en")(".*Hi.*").get.map(_ ==> List(
          (1, Map("en" -> "Hi there")),
          (2, Map("en" -> "Oh, Hi"))
        ))
        _ <- Ns.int.strMap_.k("en")(".*Hi.*").get.map(_ ==> List(1, 2))
      } yield ()
    }


    "Multiple keys" - core { implicit conn =>
      val en_da_hi    = List(
        (1, Map("en" -> "Hi there")),
        (2, Map("en" -> "Oh, Hi", "da" -> "Hilser"))
      )
      val reg_ex      = ".*Hi.*"
      val en_da_30    = List(
        (1, Map("da" -> 30)),
        (3, Map("en" -> 30, "da" -> 30)),
        (4, Map("da" -> 30))
      )
      val en_da_date3 = List(
        (1, Map("da" -> date3)),
        (3, Map("en" -> date3, "da" -> date3)),
        (4, Map("da" -> date3))
      )

      for {
        _ <- testData

        _ <- Ns.int.strMap.k("en" or "da")(".*Hi.*").get.map(_ ==> en_da_hi)
        _ <- Ns.int.strMap.k("en", "da")(".*Hi.*").get.map(_ ==> en_da_hi)
        _ <- Ns.int.strMap.k(Seq("en", "da"))(".*Hi.*").get.map(_ ==> en_da_hi)
        _ <- Ns.int.strMap.k(en_da)(reg_ex).get.map(_ ==> en_da_hi)

        _ <- Ns.int.strMap_.k("en" or "da")(".*Hi.*").get.map(_ ==> List(1, 2))
        _ <- Ns.int.strMap_.k("en", "da")(".*Hi.*").get.map(_ ==> List(1, 2))
        _ <- Ns.int.strMap_.k(Seq("en", "da"))(".*Hi.*").get.map(_ ==> List(1, 2))
        _ <- Ns.int.strMap_.k(en_da)(reg_ex).get.map(_ ==> List(1, 2))

        // Freetext searches can also be done with `contains(needle)`
        _ <- Ns.int.strMap.k("en" or "da").contains("Hi").get.map(_ ==> en_da_hi)
        _ <- Ns.int.strMap_.k("en" or "da").contains("Hi").get.map(_ ==> List(1, 2))


        _ <- Ns.int.intMap.k("en" or "da")(30).get.map(_ ==> en_da_30)
        _ <- Ns.int.intMap.k("en", "da")(30).get.map(_ ==> en_da_30)
        _ <- Ns.int.intMap.k(Seq("en", "da"))(30).get.map(_ ==> en_da_30)
        _ <- Ns.int.intMap.k(en_da)(30).get.map(_ ==> en_da_30)

        _ <- Ns.int.intMap_.k("en" or "da")(30).get.map(_ ==> List(1, 3, 4))
        _ <- Ns.int.intMap_.k("en", "da")(30).get.map(_ ==> List(1, 3, 4))
        _ <- Ns.int.intMap_.k(Seq("en", "da"))(30).get.map(_ ==> List(1, 3, 4))
        _ <- Ns.int.intMap_.k(en_da)(30).get.map(_ ==> List(1, 3, 4))


        _ <- Ns.int.dateMap.k("en" or "da")(date3).get.map(_ ==> en_da_date3)
        _ <- Ns.int.dateMap.k("en", "da")(date3).get.map(_ ==> en_da_date3)
        _ <- Ns.int.dateMap.k(Seq("en", "da"))(date3).get.map(_ ==> en_da_date3)
        _ <- Ns.int.dateMap.k(en_da)(date3).get.map(_ ==> en_da_date3)

        _ <- Ns.int.dateMap_.k("en" or "da")(date3).get.map(_ ==> List(1, 3, 4))
        _ <- Ns.int.dateMap_.k("en", "da")(date3).get.map(_ ==> List(1, 3, 4))
        _ <- Ns.int.dateMap_.k(Seq("en", "da"))(date3).get.map(_ ==> List(1, 3, 4))
        _ <- Ns.int.dateMap_.k(en_da)(date3).get.map(_ ==> List(1, 3, 4))
      } yield ()
    }


    "Multiple keys, multiple values" - core { implicit conn =>
      val oh_he             = Seq("Oh.*", ".*He.*")
      val en_da_oh_he       = List(
        (1, Map("da" -> "Hejsa")),
        (2, Map("en" -> "Oh, Hi")),
        (3, Map("en" -> "Hello", "da" -> "Hej")),
        (4, Map("da" -> "Hej"))
      )
      val en_da_10_30       = List(
        (1, Map("en" -> 10, "da" -> 30)),
        (2, Map("en" -> 10, "da" -> 10)),
        (3, Map("en" -> 30, "da" -> 30)),
        (4, Map("da" -> 30))
      )
      val en_da_date1_date3 = List(
        (1, Map("en" -> date1, "da" -> date3)),
        (2, Map("en" -> date1, "da" -> date1)),
        (3, Map("en" -> date3, "da" -> date3)),
        (4, Map("da" -> date3))
      )

      for {
        _ <- testData

        _ <- Ns.int.strMap.k("en" or "da")("Oh.*" or ".*He.*").get.map(_ ==> en_da_oh_he)
        _ <- Ns.int.strMap.k("en", "da")("Oh.*", ".*He.*").get.map(_ ==> en_da_oh_he)
        _ <- Ns.int.strMap.k(en_da)(Seq("Oh.*", ".*He.*")).get.map(_ ==> en_da_oh_he)
        _ <- Ns.int.strMap.k(en_da)(oh_he).get.map(_ ==> en_da_oh_he)

        _ <- Ns.int.strMap_.k("en" or "da")("Oh.*" or ".*He.*").get.map(_ ==> List(1, 2, 3, 4))
        _ <- Ns.int.strMap_.k("en", "da")("Oh.*", ".*He.*").get.map(_ ==> List(1, 2, 3, 4))
        _ <- Ns.int.strMap_.k(en_da)(Seq("Oh.*", ".*He.*")).get.map(_ ==> List(1, 2, 3, 4))
        _ <- Ns.int.strMap_.k(en_da)(oh_he).get.map(_ ==> List(1, 2, 3, 4))

        _ <- Ns.int.intMap.k("en" or "da")(10 or 30).get.map(_ ==> en_da_10_30)
        _ <- Ns.int.intMap.k("en", "da")(10, 30).get.map(_ ==> en_da_10_30)
        _ <- Ns.int.intMap.k(Seq("en", "da"))(Seq(10, 30)).get.map(_ ==> en_da_10_30)
        _ <- Ns.int.intMap.k(en_da)(_10_30).get.map(_ ==> en_da_10_30)

        _ <- Ns.int.intMap_.k("en" or "da")(10 or 30).get.map(_ ==> List(1, 2, 3, 4))
        _ <- Ns.int.intMap_.k("en", "da")(10, 30).get.map(_ ==> List(1, 2, 3, 4))
        _ <- Ns.int.intMap_.k(Seq("en", "da"))(Seq(10, 30)).get.map(_ ==> List(1, 2, 3, 4))
        _ <- Ns.int.intMap_.k(en_da)(_10_30).get.map(_ ==> List(1, 2, 3, 4))

        _ <- Ns.int.dateMap.k("en" or "da")(date1 or date3).get.map(_ ==> en_da_date1_date3)
        _ <- Ns.int.dateMap.k("en", "da")(date1, date3).get.map(_ ==> en_da_date1_date3)
        _ <- Ns.int.dateMap.k(Seq("en", "da"))(Seq(date1, date3)).get.map(_ ==> en_da_date1_date3)
        _ <- Ns.int.dateMap.k(en_da)(date1_date3).get.map(_ ==> en_da_date1_date3)

        _ <- Ns.int.dateMap_.k("en" or "da")(date1 or date3).get.map(_ ==> List(1, 2, 3, 4))
        _ <- Ns.int.dateMap_.k("en", "da")(date1, date3).get.map(_ ==> List(1, 2, 3, 4))
        _ <- Ns.int.dateMap_.k(Seq("en", "da"))(Seq(date1, date3)).get.map(_ ==> List(1, 2, 3, 4))
        _ <- Ns.int.dateMap_.k(en_da)(date1_date3).get.map(_ ==> List(1, 2, 3, 4))
      } yield ()
    }


    "Value negation" - core { implicit conn =>
      for {
        _ <- testData

        _ <- Ns.int.strMap_.k("en").not("Hello").get.map(_ ==> List(1, 2))
        _ <- Ns.int.strMap.k("en").not("Hello").get.map(_ ==> List(
          (1, Map("en" -> "Hi there")),
          (2, Map("en" -> "Oh, Hi"))
        ))

        // Multiple value filters (OR semantics)
        _ <- Ns.int.strMap_.k("en").not("Hello", "Hi there").get.map(_ ==> List(2))


        // Same as

        _ <- Ns.int.strMap_.k("en").!=("Hello").get.map(_ ==> List(1, 2))
        _ <- Ns.int.strMap.k("en").!=("Hello").get.map(_ ==> List(
          (1, Map("en" -> "Hi there")),
          (2, Map("en" -> "Oh, Hi"))
        ))

        // Multiple value filters (OR semantics)
        _ <- Ns.int.strMap_.k("en").!=("Hello", "Hi there").get.map(_ ==> List(2))
      } yield ()
    }


    "Value comparison" - core { implicit conn =>
      for {
        _ <- testData

        _ <- Ns.int.strMap.k("en").<("Hi").get.map(_ ==> List(
          (3, Map("en" -> "Hello"))
        ))

        _ <- Ns.int.strMap.k("en").>("Hi").get.map(_ ==> List(
          (1, Map("en" -> "Hi there")),
          (2, Map("en" -> "Oh, Hi"))
        ))

        _ <- Ns.int.strMap_.k("en").<("Hi").get.map(_ ==> List(3))
        _ <- Ns.int.strMap_.k("en").>("Hi").get.map(_ ==> List(1, 2))


        // Multiple keys

        _ <- Ns.int.strMap.k("en" or "da").<("Hi").get.map(_ ==> List(
          (1, Map("da" -> "Hejsa")),
          (3, Map("en" -> "Hello", "da" -> "Hej")),
          (4, Map("da" -> "Hej"))
        ))

        _ <- Ns.int.strMap.k("en" or "da").>("Hi").get.map(_ ==> List(
          (1, Map("en" -> "Hi there")),
          (2, Map("en" -> "Oh, Hi", "da" -> "Hilser"))
        ))

        _ <- Ns.int.strMap_.k("en" or "da").<("Hi").get.map(_ ==> List(1, 3, 4))
        _ <- Ns.int.strMap_.k("en" or "da").>("Hi").get.map(_ ==> List(1, 2))


        _ <- Ns.int.intMap.k("en" or "da").>(20).get.map(_ ==> List(
          (1, Map("da" -> 30)),
          (3, Map("en" -> 30, "da" -> 30)),
          (4, Map("da" -> 30))
        ))
        _ <- Ns.int.intMap.k("en" or "da").>=(20).get.map(_ ==> List(
          (1, Map("da" -> 30)),
          (3, Map("en" -> 30, "da" -> 30)),
          (4, Map("da" -> 30))
        ))
        _ <- Ns.int.intMap.k("en" or "da").<=(20).get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("en" -> 10, "da" -> 10))
        ))
        _ <- Ns.int.intMap.k("en" or "da").<(20).get.map(_ ==> List(
          (1, Map("en" -> 10)),
          (2, Map("en" -> 10, "da" -> 10))
        ))

        _ <- Ns.int.intMap.k("en" or "da").>(-10).get.map(_ ==> List(
          (1, Map("en" -> 10, "da" -> 30)),
          (2, Map("en" -> 10, "da" -> 10)),
          (3, Map("en" -> 30, "da" -> 30)),
          (4, Map("da" -> 30))
        ))

        _ <- Ns.int.intMap_.k("en" or "da").>(20).get.map(_ ==> List(1, 3, 4))
        _ <- Ns.int.intMap_.k("en" or "da").>=(20).get.map(_ ==> List(1, 3, 4))
        _ <- Ns.int.intMap_.k("en" or "da").<=(20).get.map(_ ==> List(1, 2))
        _ <- Ns.int.intMap_.k("en" or "da").<(20).get.map(_ ==> List(1, 2))
        _ <- Ns.int.intMap_.k("en" or "da").>(-10).get.map(_ ==> List(1, 2, 3, 4))


        _ <- Ns.int.dateMap.k("en" or "da").>(date2).get.map(_ ==> List(
          (1, Map("da" -> date3)),
          (3, Map("en" -> date3, "da" -> date3)),
          (4, Map("da" -> date3))
        ))
        _ <- Ns.int.dateMap.k("en" or "da").>=(date2).get.map(_ ==> List(
          (1, Map("da" -> date3)),
          (3, Map("en" -> date3, "da" -> date3)),
          (4, Map("da" -> date3))
        ))
        _ <- Ns.int.dateMap.k("en" or "da").<=(date2).get.map(_ ==> List(
          (1, Map("en" -> date1)),
          (2, Map("en" -> date1, "da" -> date1))
        ))
        _ <- Ns.int.dateMap.k("en" or "da").<(date2).get.map(_ ==> List(
          (1, Map("en" -> date1)),
          (2, Map("en" -> date1, "da" -> date1))
        ))

        _ <- Ns.int.dateMap_.k("en" or "da").>(date2).get.map(_ ==> List(1, 3, 4))
        _ <- Ns.int.dateMap_.k("en" or "da").>=(date2).get.map(_ ==> List(1, 3, 4))
        _ <- Ns.int.dateMap_.k("en" or "da").<=(date2).get.map(_ ==> List(1, 2))
        _ <- Ns.int.dateMap_.k("en" or "da").<(date2).get.map(_ ==> List(1, 2))
      } yield ()
    }
  }
}