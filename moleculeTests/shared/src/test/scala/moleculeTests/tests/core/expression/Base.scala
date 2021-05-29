package moleculeTests.tests.core.expression

import molecule.datomic.api.out2._
import molecule.datomic.base.facade.Conn
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.tests.core.base.dsl.CoreTest._
import scala.concurrent.{ExecutionContext, Future}

trait Base extends AsyncTestSuite {

  def oneData(implicit conn: Future[Conn], ec: ExecutionContext): Future[Unit] = {
    for {
      _ <- Ns.str insert List("", " ", ",", ".", "?", "A", "B", "a", "b")
      _ <- Ns.int insert List(-2, -1, 0, 1, 2)
      _ <- Ns.long insert List(-2L, -1L, 0L, 1L, 2L)
      _ <- Ns.double insert List(-2.0, -1.0, 0.0, 1.0, 2.0)
      _ <- Ns.bool insert List(true, false)
      _ <- Ns.date insert List(date0, date1, date2)
      _ <- Ns.bigInt insert List(bigInt0, bigInt1, bigInt2)
      _ <- Ns.bigDec insert List(bigDec0, bigDec1, bigDec2)
      _ <- Ns.uuid insert List(uuid0, uuid1, uuid2)
      _ <- Ns.uri insert List(uri0, uri1, uri2)
      _ <- Ns.enum insert List("enum0", "enum1", "enum2")
    } yield ()
  }

  def manyData(implicit conn: Future[Conn], ec: ExecutionContext): Future[Unit] = {
    for {
      // We pair cardinality many attribute values with card-one's too to be able to group by cardinality one values
      _ <- Ns.str.strs insert List(
        ("str1", Set("a", "b")),
        ("str2", Set("b", "c")),
        ("str3", Set("ba", "d")),
        ("str4", Set[String]())
      )

      _ <- Ns.int.ints insert List(
        (1, Set(1, 2)),
        (2, Set(2, 3)),
        (3, Set(2, 4)))

      _ <- Ns.long.longs insert List(
        (1L, Set(1L, 2L)),
        (2L, Set(2L, 3L)),
        (3L, Set(2L, 4L)))

      _ <- Ns.double.doubles insert List(
        (1.0, Set(1.0, 2.0)),
        (2.0, Set(2.0, 3.0)),
        (3.0, Set(2.5, 4.0)))

      // Set of boolean values maybe not so useful
      _ <- Ns.bool.bools insert List(
        (false, Set(false)),
        (true, Set(false, true)))

      _ <- Ns.date.dates insert List(
        (date1, Set(date1, date2)),
        (date2, Set(date2, date3)),
        (date3, Set(date2, date4)))

      _ <- Ns.bigInt.bigInts insert List(
        (bigInt1, Set(bigInt1, bigInt2)),
        (bigInt2, Set(bigInt2, bigInt3)),
        (bigInt3, Set(bigInt2, bigInt4)))

      _ <- Ns.bigDec.bigDecs insert List(
        (bigDec1, Set(bigDec1, bigDec2)),
        (bigDec2, Set(bigDec2, bigDec3)),
        (bigDec3, Set(bigDec2, bigDec4)))

      _ <- Ns.uuid.uuids insert List(
        (uuid1, Set(uuid1, uuid2)),
        (uuid2, Set(uuid2, uuid3)),
        (uuid3, Set(uuid2, uuid4)))

      _ <- Ns.uri.uris insert List(
        (uri1, Set(uri1, uri2)),
        (uri2, Set(uri2, uri3)),
        (uri3, Set(uri2, uri4)))

      _ <- Ns.enum.enums insert List(
        ("enum1", Set("enum1", "enum2")),
        ("enum2", Set("enum2", "enum3")),
        ("enum3", Set("enum2", "enum4")))
    } yield ()
  }


  //  class OneRefSetup extends CoreSetup {
  //
  //    m(Ns.str.Ref1.int1) insert List(
  //      ("en", 1),
  //      ("fr", 2)
  //    )
  //  }
  //
  //  class ManyRefSetup extends CoreSetup {
  //
  //    m(Ns.str.Refs1 * Ref1.int1) insert List(
  //      ("en", List(1, 2)),
  //      ("fr", List(1, 2)),
  //      ("da", List(3, 4))
  //    )
  //  }

  // Todo
  //  class OneRefSetup extends CoreSetup {
  //
  //    m(Ns.strMap.Ref1.int1) insert List(
  //      (Map("en" -> "Hi there"), 1),
  //      (Map("fr" -> "Bonjour", "en" -> "Oh, Hi"), 1)
  //    )
  //  }
  //
  //  class ManyRefSetup extends CoreSetup {
  //
  //    m(Ns.strMap.Refs1 * Ref1.int1) insert List(
  //      (Map("en" -> "Hi there"), List(1, 2)),
  //      (Map("fr" -> "Bonjour", "en" -> "Oh, Hi"), List(1, 2)),
  //      (Map("en" -> "Hello"), List(2, 3)),
  //      (Map("da" -> "Hej"), List(3, 4))
  //    )
  //  }
  //  }
}