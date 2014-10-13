package molecule.out
import scala.language.higherKinds

trait Number_0[Ns[_]]  {
  def count   : Ns[Int] = ???
  def avg     : Ns[Float] = ???
  def median  : Ns[Int] = ???
  def variance: Ns[Int] = ???
  def stddev  : Ns[Int] = ???
}

trait Number_1[Ns[_, _], A]{
  def count   : Ns[A, Int] = ???
  def avg     : Ns[A, Float] = ???
  def median  : Ns[A, Int] = ???
  def variance: Ns[A, Int] = ???
  def stddev  : Ns[A, Int] = ???
}

trait Number_2[Ns[_, _, _], A, B] {
  def count   : Ns[A, B, Int] = ???
  def avg     : Ns[A, B, Float] = ???
  def median  : Ns[A, B, Int] = ???
  def variance: Ns[A, B, Int] = ???
  def stddev  : Ns[A, B, Int] = ???
}