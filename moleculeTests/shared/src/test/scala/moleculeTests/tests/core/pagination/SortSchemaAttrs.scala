package moleculeTests.tests.core.pagination

import molecule.core.util.Executor._
import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.in1_out7._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._


object SortSchemaAttrs extends AsyncTestSuite {

  lazy val tests = Tests {

    "doc$" - core { implicit conn =>
      for {
        _ <- Schema.id.a2.a.doc$.a1.get(5).map(_ ==> List(
          (74, ":Ns/long", None),
          (75, ":Ns/double", None),
          (76, ":Ns/bool", None),
          (77, ":Ns/date", None),
          (78, ":Ns/uuid", None),
        ))
        _ <- Schema.id.a2.a.doc$.d1.get(5).map(_ ==> List(
          (72, ":Ns/str", Some("Card one String attribute")),
          (73, ":Ns/int", Some("Card one Int attribute")),
          (74, ":Ns/long", None),
          (75, ":Ns/double", None),
          (76, ":Ns/bool", None),
        ))
      } yield ()
    }


    "index$" - core { implicit conn =>
      for {
        _ <- Schema.id.a2.a.index$.a1.get(3).map(_ ==> (
          if (isJsPlatform) {
            // No attributes indexed
            List(
              (72, ":Ns/str", None),
              (73, ":Ns/int", None),
              (74, ":Ns/long", None),
            )
          } else {
            // All attributes indexed
            List(
              (72, ":Ns/str", Some(true)),
              (73, ":Ns/int", Some(true)),
              (74, ":Ns/long", Some(true)),
            )
          })
        )
        _ <- Schema.id.a2.a.index$.d1.get(3).map(_ ==> (
          if (isJsPlatform) {
            // No attributes indexed
            List(
              (72, ":Ns/str", None),
              (73, ":Ns/int", None),
              (74, ":Ns/long", None),
            )
          } else {
            // All attributes indexed
            List(
              (72, ":Ns/str", Some(true)),
              (73, ":Ns/int", Some(true)),
              (74, ":Ns/long", Some(true)),
            )
          })
        )
      } yield ()
    }


    "unique$" - core { implicit conn =>
      for {
        _ <- Schema.ns_("Ref2").card_("one").a.a2.unique$.a1.get.map(_ ==> List(
          (":Ref2/enum2", None),
          (":Ref2/ref3", None),
          (":Ref2/str2", Some("identity")),
          (":Ref2/int2", Some("value")),
        ))
        _ <- Schema.ns_("Ref2").card_("one").a.a2.unique$.d1.get.map(_ ==> List(
          (":Ref2/int2", Some("value")),
          (":Ref2/str2", Some("identity")),
          (":Ref2/enum2", None),
          (":Ref2/ref3", None),
        ))
      } yield ()
    }


    "fulltext$" - core { implicit conn =>
      for {
        _ <- Schema.ns_("Ref2").card_("one").a.a2.fulltext$.a1.get.map(_ ==> List(
          (":Ref2/enum2", None),
          (":Ref2/int2", None),
          (":Ref2/ref3", None),
          (":Ref2/str2", Some(true)),
        ))
        _ <- Schema.ns_("Ref2").card_("one").a.a2.fulltext$.d1.get.map(_ ==> List(
          (":Ref2/str2", Some(true)),
          (":Ref2/enum2", None),
          (":Ref2/int2", None),
          (":Ref2/ref3", None),
        ))
      } yield ()
    }


    "isComponent$" - core { implicit conn =>
      for {
        _ <- Schema.ns_("Ref1").card_("one").a.a2.isComponent$.a1.get.map(_ ==> List(
          (":Ref1/enum1", None),
          (":Ref1/int1", None),
          (":Ref1/ref2", None),
          (":Ref1/str1", None),
          (":Ref1/refSub2", Some(true)),
        ))
        _ <- Schema.ns_("Ref1").card_("one").a.a2.isComponent$.d1.get.map(_ ==> List(
          (":Ref1/refSub2", Some(true)),
          (":Ref1/enum1", None),
          (":Ref1/int1", None),
          (":Ref1/ref2", None),
          (":Ref1/str1", None),
        ))
      } yield ()
    }

    
    "noHistory$" - core { implicit conn =>
      for {
        _ <- Schema.ns_("Ref2").card_("many").a.a2.noHistory$.a1.get.map(_ ==> List(
          (":Ref2/refs3", None),
          (":Ref2/strs2", None),
          (":Ref2/ints2", Some(true)),
        ))
        _ <- Schema.ns_("Ref2").card_("many").a.a2.noHistory$.d1.get.map(_ ==> List(
          (":Ref2/ints2", Some(true)),
          (":Ref2/refs3", None),
          (":Ref2/strs2", None),
        ))
      } yield ()
    }
  }
}
