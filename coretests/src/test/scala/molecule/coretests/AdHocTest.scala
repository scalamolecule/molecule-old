package molecule.coretests

import molecule.api.in3_out22._
import molecule.ast.tempDb.History
import molecule.coretests.util.CoreSpec
import molecule.coretests.util.dsl.coreTest.{Ns, Ref1}
import molecule.coretests.util.schema.CoreTestSchema
import molecule.util.Helpers


class AdHocTest extends CoreSpec with Helpers {

  sequential

  implicit val conn = recreateDbFrom(CoreTestSchema)


  "adhoc" >> {

    ok
  }

}