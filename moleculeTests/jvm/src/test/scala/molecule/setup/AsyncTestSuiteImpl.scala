package molecule.setup

import molecule.datomic.base.facade.Conn
import molecule.datomic.peer.facade.Datomic_Peer
import molecule.tests.core.base.schema.CoreTestSchema

trait AsyncTestSuiteImpl {

  def coreImpl[T](func: Conn => T): T = func(Datomic_Peer.recreateDbFrom(CoreTestSchema))

}
