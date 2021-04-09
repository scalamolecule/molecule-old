package molecule.core.macros

import scala.reflect.macros.blackbox

case class Platform(val c: blackbox.Context) {
  import c.universe._

  def foo: Tree = q"""override def foo: String = "jvm""""

}
