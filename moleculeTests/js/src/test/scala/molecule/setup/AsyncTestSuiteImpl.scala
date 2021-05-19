package molecule.setup

import molecule.datomic.base.facade.Conn
import molecule.core.marshalling.Conn_Js
import molecule.tests.core.base.schema.CoreTestSchema

trait AsyncTestSuiteImpl {

  def coreImpl[T](func: Conn => T): T = func(Conn_Js.inMem(CoreTestSchema))
}
