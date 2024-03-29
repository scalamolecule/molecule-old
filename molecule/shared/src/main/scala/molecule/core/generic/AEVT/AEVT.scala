/*
* AUTO-GENERATED Molecule DSL for namespace `AEVT`
*
* To change:
* 1. Edit data model in molecule.core.generic.dataModel/AEVTDataModel
* 2. `sbt clean compile`
* 3. Re-compile project in IDE
*/
package molecule.core.generic.AEVT

import java.util.Date
import molecule.core.dsl.attributes._
import molecule.core.generic.GenericNs
import scala.language.higherKinds

trait AEVT extends GenericNs {
  final class e         [Stay, Next] extends OneLong   [Stay, Next] with Index
  final class a         [Stay, Next] extends OneString [Stay, Next] with Index
  final class v         [Stay, Next] extends OneAny    [Stay, Next] with Index
  final class t         [Stay, Next] extends OneLong   [Stay, Next] with Index
  final class tx        [Stay, Next] extends OneLong   [Stay, Next] with Index
  final class txInstant [Stay, Next] extends OneDate   [Stay, Next] with Index
  final class op        [Stay, Next] extends OneBoolean[Stay, Next] with Index
}

trait AEVT_[props] { def AEVT: props = ??? }

trait AEVT_e          { lazy val e         : Long    = ??? }
trait AEVT_a          { lazy val a         : String  = ??? }
trait AEVT_v          { lazy val v         : Any     = ??? }
trait AEVT_t          { lazy val t         : Long    = ??? }
trait AEVT_tx         { lazy val tx        : Long    = ??? }
trait AEVT_txInstant  { lazy val txInstant : Date    = ??? }
trait AEVT_op         { lazy val op        : Boolean = ??? }

