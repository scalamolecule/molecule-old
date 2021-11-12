package moleculeTests.tests.core.input2


import molecule.datomic.api.in2_out3._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object ManyMany extends AsyncTestSuite {


  lazy val tests = Tests {

    "Enum/URI" - core { implicit conn =>
      for {
        _ <- Ns.int.enums$.uris$ insert List(
          (1, Some(Set(enum1, enum2, enum3)), Some(Set(uri1, uri2, uri3))),
          (2, Some(Set(enum2, enum3, enum4)), Some(Set(uri2, uri3, uri4))),
          (3, Some(Set(enum3, enum4, enum5)), Some(Set(uri3, uri4, uri5))),

          (4, Some(Set(enum1, enum2, enum3)), None),
          (5, None, Some(Set(uri1, uri2, uri3))),
          (6, None, None)
        )
        _ <- m(Ns.int.enums_(?).uris_(?)).apply(List(Set("enum2")), List(Set(uri3))).get.map(_ ==> List(1, 2))
        _ <- m(Ns.int.enums_(?).uris_.not(?)).apply(List(Set("enum2")), List(Set(uri3))).get.map(_ ==> Nil)
        _ <- m(Ns.int.enums_(?).uris_.>(?)).apply(List(Set("enum2")), List(Set(uri3))).get.map(_ ==> List(2))
        _ <- m(Ns.int.enums_.not(?).uris_(?)).apply(List(Set("enum2")), List(Set(uri3))).get.map(_ ==> List(3))
        _ <- m(Ns.int.enums_.not(?).uris_.not(?)).apply(List(Set("enum2")), List(Set(uri3))).get.map(_ ==> Nil)
        _ <- m(Ns.int.enums_.not(?).uris_.>(?)).apply(List(Set("enum2")), List(Set(uri3))).get.map(_ ==> List(3))
        _ <- m(Ns.int.enums_.<(?).uris_(?)).apply(List(Set("enum2")), List(Set(uri3))).get.map(_ ==> List(1))
        _ <- m(Ns.int.enums_.<(?).uris_.not(?)).apply(List(Set("enum2")), List(Set(uri3))).get.map(_ ==> Nil)
        _ <- m(Ns.int.enums_.<(?).uris_.>(?)).apply(List(Set("enum2")), List(Set(uri3))).get.map(_ ==> Nil)
      } yield ()
    }

    "`and` syntax" - core { implicit conn =>
      val im = m(Ns.int.ints_(?).longs_(?))
      for {
        _ <- Ns.int.ints$.longs$ insert List(
          (1, Some(Set(1, 2, 3)), Some(Set(1L, 2L, 3L))),
          (2, Some(Set(2, 3, 4)), Some(Set(2L, 3L, 4L))),
          (3, Some(Set(3, 4, 5)), Some(Set(3L, 4L, 5L))),

          (4, Some(Set(1, 2, 3)), None),
          (5, None, Some(Set(1L, 2L, 3L))),
          (6, None, None)
        )
        _ <- im(Set(1) and Set(2L)).get.map(_ ==> List(1))
        _ <- im(List(Set(1)), List(Set(2L))).get.map(_ ==> List(1))

        _ <- im(Set(1) and Set(2L, 3L)).get.map(_ ==> List(1))
        _ <- im(List(Set(1)), List(Set(2L, 3L))).get.map(_ ==> List(1))

        _ <- im(Set(1) and (Set(2L) or Set(3L))).get.map(_ ==> List(1))
        _ <- im(List(Set(1)), List(Set(2L), Set(3L))).get.map(_ ==> List(1))

        _ <- im(Set(1) and (Set(1L, 2L) or Set(3L))).get.map(_ ==> List(1))
        _ <- im(List(Set(1)), List(Set(1L, 2L), Set(3L))).get.map(_ ==> List(1))


        _ <- im(Set(2, 3) and Set(2L)).get.map(_ ==> List(1, 2))
        _ <- im(List(Set(2, 3)), List(Set(2L))).get.map(_ ==> List(1, 2))

        _ <- im(Set(2, 3) and Set(2L, 3L)).get.map(_ ==> List(1, 2))
        _ <- im(List(Set(2, 3)), List(Set(2L, 3L))).get.map(_ ==> List(1, 2))

        _ <- im(Set(2, 3) and (Set(2L) or Set(3L))).get.map(_ ==> List(1, 2))
        _ <- im(List(Set(2, 3)), List(Set(2L), Set(3L))).get.map(_ ==> List(1, 2))

        _ <- im(Set(2, 3) and (Set(1L, 2L) or Set(3L))).get.map(_ ==> List(1, 2))
        _ <- im(List(Set(2, 3)), List(Set(1L, 2L), Set(3L))).get.map(_ ==> List(1, 2))


        _ <- im((Set(2) or Set(3)) and Set(2L)).get.map(_ ==> List(1, 2))
        _ <- im(List(Set(2), Set(3)), List(Set(2L))).get.map(_ ==> List(1, 2))

        _ <- im((Set(2) or Set(3)) and Set(2L, 3L)).get.map(_ ==> List(1, 2))
        _ <- im(List(Set(2), Set(3)), List(Set(2L, 3L))).get.map(_ ==> List(1, 2))

        _ <- im((Set(2) or Set(3)) and (Set(2L) or Set(3L))).get.map(_ ==> List(1, 2, 3))
        _ <- im(List(Set(2), Set(3)), List(Set(2L), Set(3L))).get.map(_ ==> List(1, 2, 3))

        _ <- im((Set(2) or Set(3)) and (Set(1L, 2L) or Set(3L))).get.map(_ ==> List(1, 2, 3))
        _ <- im(List(Set(2), Set(3)), List(Set(1L, 2L), Set(3L))).get.map(_ ==> List(1, 2, 3))
      } yield ()
    }
  }
}
