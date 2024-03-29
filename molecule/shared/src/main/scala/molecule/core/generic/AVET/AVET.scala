/*
* AUTO-GENERATED Molecule DSL for namespace `AVET`
*
* To change:
* 1. Edit data model in molecule.core.generic.dataModel/AVETDataModel
* 2. `sbt clean compile`
* 3. Re-compile project in IDE
*/
package molecule.core.generic.AVET

import java.util.Date
import molecule.core.dsl.attributes._
import molecule.core.generic.GenericNs
import scala.language.higherKinds

trait AVET extends GenericNs {
  final class e         [Stay, Next] extends OneLong   [Stay, Next] with Index
  final class a         [Stay, Next] extends OneString [Stay, Next] with Index
  final class v         [Stay, Next] extends OneAny    [Stay, Next] with Index
  final class t         [Stay, Next] extends OneLong   [Stay, Next] with Index
  final class tx        [Stay, Next] extends OneLong   [Stay, Next] with Index
  final class txInstant [Stay, Next] extends OneDate   [Stay, Next] with Index
  final class op        [Stay, Next] extends OneBoolean[Stay, Next] with Index
}

trait AVET_[props] { def AVET: props = ??? }

trait AVET_e          { lazy val e         : Long    = ??? }
trait AVET_a          { lazy val a         : String  = ??? }
trait AVET_v          { lazy val v         : Any     = ??? }
trait AVET_t          { lazy val t         : Long    = ??? }
trait AVET_tx         { lazy val tx        : Long    = ??? }
trait AVET_txInstant  { lazy val txInstant : Date    = ??? }
trait AVET_op         { lazy val op        : Boolean = ??? }

