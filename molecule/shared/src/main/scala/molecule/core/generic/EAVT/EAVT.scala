/*
* AUTO-GENERATED Molecule DSL for namespace `EAVT`
*
* To change:
* 1. Edit data model in molecule.core.generic.dataModel/EAVTDataModel
* 2. `sbt clean compile`
* 3. Re-compile project in IDE
*/
package molecule.core.generic.EAVT

import java.util.Date
import molecule.core.dsl.attributes._
import molecule.core.generic.GenericNs
import scala.language.higherKinds

trait EAVT extends GenericNs {
  final class e         [Stay, Next] extends OneLong   [Stay, Next] with Index
  final class a         [Stay, Next] extends OneString [Stay, Next] with Index
  final class v         [Stay, Next] extends OneAny    [Stay, Next] with Index
  final class t         [Stay, Next] extends OneLong   [Stay, Next] with Index
  final class tx        [Stay, Next] extends OneLong   [Stay, Next] with Index
  final class txInstant [Stay, Next] extends OneDate   [Stay, Next] with Index
  final class op        [Stay, Next] extends OneBoolean[Stay, Next] with Index
}

trait EAVT_[props] { def EAVT: props = ??? }

trait EAVT_e          { lazy val e         : Long    = ??? }
trait EAVT_a          { lazy val a         : String  = ??? }
trait EAVT_v          { lazy val v         : Any     = ??? }
trait EAVT_t          { lazy val t         : Long    = ??? }
trait EAVT_tx         { lazy val tx        : Long    = ??? }
trait EAVT_txInstant  { lazy val txInstant : Date    = ??? }
trait EAVT_op         { lazy val op        : Boolean = ??? }

