package moleculeTests

import molecule.core.util.Executor._
import molecule.core.util.{Helpers, JavaUtil}
import molecule.datomic.api.out12._
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import moleculeTests.setup.AsyncTestSuite
import utest._

object Adhoc extends AsyncTestSuite with Helpers with JavaUtil {


  lazy val tests = Tests {

    "core" - core { implicit futConn =>

      for {
        conn <- futConn


        /*
moleculeTests.tests.core.input1.expression.Input1Enum
moleculeTests.tests.core.input1.resolution.IntCard2coalesce
moleculeTests.tests.core.input1.resolution.EnumCard2
moleculeTests.tests.core.bidirectionals.edgeSelf.EdgeOneSelfUpdateProps
moleculeTests.tests.core.input2.OneMany
moleculeTests.tests.core.input2.ManyMany
moleculeTests.tests.core.input1.expression.Input1BigDecimal
moleculeTests.tests.core.input1.resolution.IntCard1
moleculeTests.tests.core.input2.Input2syntax
moleculeTests.tests.db.datomic.time.TestDbWith
moleculeTests.tests.core.input1.expression.Input1URI
moleculeTests.tests.core.expression.Eid
moleculeTests.tests.core.input1.expression.Input1StringIntro
moleculeTests.tests.core.bidirectionals.edgeOther.EdgeOneOtherUpdateProps
moleculeTests.tests.core.input1.resolution.EnumCard1
moleculeTests.tests.core.input1.resolution.EnumCard2coalesce
moleculeTests.tests.core.pagination.OffsetPagination
moleculeTests.tests.core.ref.SelfJoin
moleculeTests.tests.core.input1.expression.Input1Date
moleculeTests.tests.core.input1.resolution.IntCard2
moleculeTests.tests.core.input3.Input3examples
moleculeTests.tests.core.bidirectionals.other.OneOther
moleculeTests.tests.core.input1.expression.Input1UUID
moleculeTests.tests.core.input1.expression.Input1Boolean
moleculeTests.tests.db.datomic.generic.Datom
moleculeTests.tests.core.bidirectionals.self.ManySelf
moleculeTests.tests.core.input1.expression.Input1Int
moleculeTests.tests.core.input1.expression.Input1String
moleculeTests.tests.core.input1.resolution.IntCard2tacit
moleculeTests.tests.core.bidirectionals.other.ManyOther
moleculeTests.tests.core.input1.expression.Input1BigInt
moleculeTests.tests.db.datomic.generic.Schema_Attr
moleculeTests.tests.core.input1.resolution.EnumCard2tacit
moleculeTests.tests.core.input1.expression.Input1Long
moleculeTests.tests.core.input1.expression.Input1Double
         */

      } yield ()
    }
  }
}
