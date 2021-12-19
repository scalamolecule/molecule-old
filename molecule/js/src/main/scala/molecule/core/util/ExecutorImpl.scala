package molecule.core.util

import org.scalajs.macrotaskexecutor.MacrotaskExecutor
import scala.concurrent.ExecutionContext

trait ExecutorImpl {
  def globalImpl: ExecutionContext = MacrotaskExecutor.Implicits.global
}
