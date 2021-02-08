/*
* AUTO-GENERATED Molecule DSL for namespace `VAET`
*
* To change:
* 1. Edit data model in molecule.core.generic.dataModel/VAETDataModel
* 2. `sbt clean compile`
* 3. Re-compile project in IDE
*/
package molecule.core.generic.VAET

import java.util.Date
import molecule.core.dsl.attributes._
import molecule.core.generic.GenericNs

trait VAET extends GenericNs {
  final class e         [Stay, Next] extends OneLong   [Stay, Next] with Indexed
  final class a         [Stay, Next] extends OneString [Stay, Next] with Indexed
  final class v         [Stay, Next] extends OneAny    [Stay, Next] with Indexed
  final class t         [Stay, Next] extends OneLong   [Stay, Next] with Indexed
  final class tx        [Stay, Next] extends OneLong   [Stay, Next] with Indexed
  final class txInstant [Stay, Next] extends OneDate   [Stay, Next] with Indexed
  final class op        [Stay, Next] extends OneBoolean[Stay, Next] with Indexed
  
  final class e$        [Stay, Next] extends OneLong$   [Stay] with Indexed
  final class a$        [Stay, Next] extends OneString$ [Stay] with Indexed
  final class v$        [Stay, Next] extends OneAny$    [Stay] with Indexed
  final class t$        [Stay, Next] extends OneLong$   [Stay] with Indexed
  final class tx$       [Stay, Next] extends OneLong$   [Stay] with Indexed
  final class txInstant$[Stay, Next] extends OneDate$   [Stay] with Indexed
  final class op$       [Stay, Next] extends OneBoolean$[Stay] with Indexed
}

trait VAET_[props] { def VAET: props = ??? }

trait VAET_e          { lazy val e         : Long    = ??? }
trait VAET_a          { lazy val a         : String  = ??? }
trait VAET_v          { lazy val v         : Any     = ??? }
trait VAET_t          { lazy val t         : Long    = ??? }
trait VAET_tx         { lazy val tx        : Long    = ??? }
trait VAET_txInstant  { lazy val txInstant : Date    = ??? }
trait VAET_op         { lazy val op        : Boolean = ??? }

trait VAET_e$         { lazy val e$        : Option[Long   ] = ??? }
trait VAET_a$         { lazy val a$        : Option[String ] = ??? }
trait VAET_v$         { lazy val v$        : Option[Any    ] = ??? }
trait VAET_t$         { lazy val t$        : Option[Long   ] = ??? }
trait VAET_tx$        { lazy val tx$       : Option[Long   ] = ??? }
trait VAET_txInstant$ { lazy val txInstant$: Option[Date   ] = ??? }
trait VAET_op$        { lazy val op$       : Option[Boolean] = ??? }

