package molecule.tests.core.attrMap

import molecule.tests.core.base.dsl.CoreTest._
import molecule.datomic.api.out2._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object Keyed extends Base {

  val en_da       = Seq("en", "da")
  val hi_he       = Seq("Hi there", "Hello")
  val _10_30      = Seq(10, 30)
  val date1_date3 = Seq(date1, date3)

  lazy val tests = Tests {

    "Key only" - core { implicit conn =>
      for {
        _ <- testData
        _ <- Ns.strMapK("fr").get === List("Bonjour")
        _ <- Ns.intMapK("fr").get === List(20)
        _ <- Ns.longMapK("fr").get === List(20L)
        _ <- Ns.doubleMapK("fr").get === List(20.0)
        _ <- Ns.boolMapK("fr").get === List(false)
        _ <- Ns.dateMapK("fr").get === List(date2)
        _ <- Ns.uuidMapK("fr").get === List(uuid2)
        _ <- Ns.uriMapK("fr").get === List(uri2)
      } yield ()
    }


    "Single value" - core { implicit conn =>
      for {
        _ <- testData

        // Whereas attribute maps return Maps
        _ <- Ns.int.strMap("en" -> "Hi there").get === List((1, Map("en" -> "Hi there")))
        _ <- Ns.int.strMap.k("en").apply("Hi there").get === List((1, Map("en" -> "Hi there")))

        // ..the special "K"-appended (for Keyed) attribute returns the value only:
        _ <- Ns.int.strMapK("en")("Hi there").get === List((1, "Hi there"))
      } yield ()
    }


    "Multiple values" - core { implicit conn =>
      val hi_hello = List(
        (1, "Hi there"),
        (3, "Hello")
      )
      val en_10_30 = List(
        (3, 30),
        (2, 10),
        (1, 10)
      )
      val en_date1_date3 = List(
        (3, date3),
        (2, date1),
        (1, date1),
      )
      for {
        _ <- testData

        _ <- Ns.int.strMapK("en")("Hi there" or "Hello").get === hi_hello
        _ <- Ns.int.strMapK("en")("Hi there", "Hello").get === hi_hello
        _ <- Ns.int.strMapK("en")(Seq("Hi there", "Hello")).get === hi_hello
        _ <- Ns.int.strMapK(en)(hi_he).get === hi_hello

        _ <- Ns.int.strMapK_("en")("Hi there" or "Hello").get === List(1, 3)
        _ <- Ns.int.strMapK_("en")("Hi there", "Hello").get === List(1, 3)
        _ <- Ns.int.strMapK_("en")(Seq("Hi there", "Hello")).get === List(1, 3)
        _ <- Ns.int.strMapK_(en)(hi_he).get === List(1, 3)

        _ <- Ns.int.intMapK("en")(10 or 30).get === en_10_30
        _ <- Ns.int.intMapK("en")(10, 30).get === en_10_30
        _ <- Ns.int.intMapK("en")(Seq(10, 30)).get === en_10_30
        _ <- Ns.int.intMapK("en")(_10_30).get === en_10_30

        _ <- Ns.int.intMapK_("en")(10 or 30).get === List(1, 2, 3)
        _ <- Ns.int.intMapK_("en")(10, 30).get === List(1, 2, 3)
        _ <- Ns.int.intMapK_("en")(Seq(10, 30)).get === List(1, 2, 3)
        _ <- Ns.int.intMapK_("en")(_10_30).get === List(1, 2, 3)

        _ <- Ns.int.dateMapK("en")(date1 or date3).get === en_date1_date3
        _ <- Ns.int.dateMapK("en")(date1, date3).get === en_date1_date3
        _ <- Ns.int.dateMapK("en")(Seq(date1, date3)).get === en_date1_date3
        _ <- Ns.int.dateMapK("en")(date1_date3).get === en_date1_date3

        _ <- Ns.int.dateMapK_("en")(date1 or date3).get === List(1, 2, 3)
        _ <- Ns.int.dateMapK_("en")(date1, date3).get === List(1, 2, 3)
        _ <- Ns.int.dateMapK_("en")(Seq(date1, date3)).get === List(1, 2, 3)
        _ <- Ns.int.dateMapK_("en")(date1_date3).get === List(1, 2, 3)
      } yield ()
    }


    "Regular expressions" - core { implicit conn =>
      for {
        _ <- testData

        // String attribute maps allow us to use regexes
        // Note that searches are case-sensitive ("Hi there" not matched)
        _ <- Ns.int.strMapK("en")(".*He.*").get === List((3, "Hello"))
        _ <- Ns.int.strMapK_("en")(".*He.*").get === List(3)
      } yield ()
    }


    "Multiple keys" - core { implicit conn =>
      val en_da_hi = List(
        (2, "Oh, Hi"),
        (2, "Hilser"),
        (1, "Hi there")
      )

      for {
        _ <- testData

        // We can get values with multiple keys by using a regex for the key
        _ <- Ns.int.strMapK("en|da")(".*Hi.*").get === en_da_hi
        _ <- Ns.int.strMapK_("en|da")(".*Hi.*").get === List(1, 2)

        // Freetext searches can also be done with `contains(needle)`
        _ <- Ns.int.strMapK("en|da").contains("Hi").get === en_da_hi
        _ <- Ns.int.strMapK_("en|da").contains("Hi").get === List(1, 2)

        _ <- Ns.int.intMapK("en|da")(30).get === List(
          (4, 30),
          (3, 30),
          (1, 30)
        )
        _ <- Ns.int.intMapK_("en|da")(30).get === List(1, 3, 4)

        _ <- Ns.int.dateMapK("en|da")(date3).get === List(
          (1, date3),
          (3, date3),
          (4, date3),
        )
        _ <- Ns.int.dateMapK_("en|da")(date3).get === List(1, 3, 4)
      } yield ()
    }


    "Multiple keys, multiple values" - core { implicit conn =>
      val en_da_oh_he = List(
        (2, "Oh, Hi"),
        (3, "Hello"),
        (3, "Hej"),
        (4, "Hej"),
        (1, "Hejsa")
      )
      val en_da_10_30 = List(
        (4, 30),
        (3, 30),
        (1, 30),
        (2, 10),
        (1, 10)
      )
      val en_da_date1_date3 = List(
        (1, date3),
        (3, date3),
        (4, date3),
        (2, date1),
        (1, date1),
      )

      for {
        _ <- testData

        _ <- Ns.int.strMapK("en|da")("Oh.*" or ".*He.*").get === en_da_oh_he
        _ <- Ns.int.strMapK_("en|da")("Oh.*" or ".*He.*").get === List(1, 2, 3, 4)

        _ <- Ns.int.intMapK("en|da")(10 or 30).get === en_da_10_30
        _ <- Ns.int.intMapK_("en|da")(10 or 30).get === List(1, 2, 3, 4)

        _ <- Ns.int.dateMapK("en|da")(date1 or date3).get === en_da_date1_date3
        _ <- Ns.int.dateMapK_("en|da")(date1 or date3).get === List(1, 2, 3, 4)
      } yield ()
    }


    "Value comparison" - core { implicit conn =>
      for {
        _ <- testData

        _ <- Ns.int.strMapK("en").<("Hi").get === List(
          (3, "Hello")
        )

        _ <- Ns.int.strMapK("en").>("Hi").get === List(
          (2, "Oh, Hi"),
          (1, "Hi there")
        )

        _ <- Ns.int.strMapK_("en").<("Hi").get === List(3)
        _ <- Ns.int.strMapK_("en").>("Hi").get === List(1, 2)

        // Multiple keys

        _ <- Ns.int.strMapK("en|da").<("Hi").get === List(
          (3, "Hello"),
          (3, "Hej"),
          (4, "Hej"),
          (1, "Hejsa")
        )

        _ <- Ns.int.strMapK("en|da").>("Hi").get === List(
          (2, "Oh, Hi"),
          (2, "Hilser"),
          (1, "Hi there")
        )

        _ <- Ns.int.intMapK_("en|da").>(20).get === List(1, 3, 4)
        _ <- Ns.int.intMapK_("en|da").>=(20).get === List(1, 3, 4)
        _ <- Ns.int.intMapK_("en|da").<=(20).get === List(1, 2)
        _ <- Ns.int.intMapK_("en|da").<(20).get === List(1, 2)
        _ <- Ns.int.intMapK_("en|da").>(-10).get === List(1, 2, 3, 4)


        _ <- Ns.int.dateMapK("en|da").>(date2).get === List(
          (1, date3),
          (3, date3),
          (4, date3),
        )
        _ <- Ns.int.dateMapK("en|da").>=(date2).get === List(
          (1, date3),
          (3, date3),
          (4, date3),
        )
        _ <- Ns.int.dateMapK("en|da").<=(date2).get === List(
          (2, date1),
          (1, date1),
        )
        _ <- Ns.int.dateMapK("en|da").<(date2).get === List(
          (2, date1),
          (1, date1),
        )

        _ <- Ns.int.dateMapK_("en|da").>(date2).get === List(1, 3, 4)
        _ <- Ns.int.dateMapK_("en|da").>=(date2).get === List(1, 3, 4)
        _ <- Ns.int.dateMapK_("en|da").<=(date2).get === List(1, 2)
        _ <- Ns.int.dateMapK_("en|da").<(date2).get === List(1, 2)
      } yield ()
    }
  }
}