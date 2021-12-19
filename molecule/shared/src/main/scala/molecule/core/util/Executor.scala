package molecule.core.util

import scala.concurrent.ExecutionContext

object Executor extends ExecutorImpl {
  implicit def global: ExecutionContext = globalImpl
}
