package moleculeTests.tests.db.datomic.generic

import molecule.core.util.Executor._
import molecule.core.util.testing.expectCompileError
import molecule.datomic.api.out13._
import molecule.datomic.base.util.{SystemDevLocal, SystemPeer, SystemPeerServer}
import moleculeTests.setup.AsyncTestSuite
import utest._
import scala.concurrent.Future


object SchemaMigration extends AsyncTestSuite {

  lazy val tests = Tests {

    "ident - New attribute" - core { implicit futConn =>
      for {
        conn <- futConn

        _ <- Schema.ns_("Ref4").a.a1.valueType.cardinality.get.map(_ ==> List(
          (":Ref4/int4", "long", "one"),
          (":Ref4/str4", "string", "one"),
        ))


        // Add new `foo` attribute
        _ <- conn.transact(
          """[
            |  {
            |   :db/ident         :Ref4/foo
            |   :db/valueType     :db.type/boolean
            |   :db/cardinality   :db.cardinality/many
            |  }
            |]""".stripMargin
        )

        _ <- Schema.ns_("Ref4").a.a1.valueType.cardinality.get.map(_ ==> List(
          (":Ref4/foo", "boolean", "many"),
          (":Ref4/int4", "long", "one"),
          (":Ref4/str4", "string", "one"),
        ))

      } yield ()
    }


    "ident - change attribute name" - core { implicit futConn =>
      for {
        conn <- futConn



        _ <- conn.transact(
          """[
            |  {
            |    :db/id        :Ns/int
            |    :db/ident     :Ns/int2
            |    :db/noHistory true
            |  }
            |]""".stripMargin
        )

      } yield ()
    }

  }
}