package molecule.core.util

import scala.concurrent.ExecutionContext

trait ExecutorImpl {
  def globalImpl: ExecutionContext = ExecutionContext.global
}
