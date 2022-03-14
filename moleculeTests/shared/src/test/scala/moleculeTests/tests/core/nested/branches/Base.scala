package moleculeTests.tests.core.nested.branches

import moleculeTests.setup.AsyncTestSuite


// Nested branches base
trait Base extends AsyncTestSuite {

  def branchA[T](leafs: List[T]): (Int, String, List[T]) = (1, "a", leafs)
  def branchB[T](leafs: List[T]): (Int, String, List[T]) = (2, "b", leafs)
  val leaf  = List((3, "x"))
  val leafs = List((4, "y"), (5, "z"))

  def sort[T](leafs: List[T]): List[T] = {
    leafs.head match {
      case (_, _, _) =>
        leafs
          .sortBy { case (i: Int, _, _) => i }
          .map { case (i, s, l: List[_]) => (i, s, sort(l)).asInstanceOf[T] }

      case (_, _) =>
        leafs.sortBy { case (i: Int, _) => i }
    }
  }
}