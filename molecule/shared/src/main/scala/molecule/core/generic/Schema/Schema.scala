/*
* AUTO-GENERATED Molecule DSL for namespace `Schema`
*
* To change:
* 1. Edit data model in molecule.core.generic.dataModel/SchemaDataModel
* 2. `sbt clean compile`
* 3. Re-compile project in IDE
*/
package molecule.core.generic.Schema

import java.util.Date
import molecule.core.dsl.attributes._
import molecule.core.generic.GenericNs
import scala.language.higherKinds

trait Schema extends GenericNs {
  final class attrId     [Stay, Next] extends OneLong   [Stay, Next] with Indexed
  final class a          [Stay, Next] extends OneString [Stay, Next] with Indexed
  final class part       [Stay, Next] extends OneString [Stay, Next] with Indexed
  final class nsFull     [Stay, Next] extends OneString [Stay, Next] with Indexed
  final class ns         [Stay, Next] extends OneString [Stay, Next] with Indexed
  final class attr       [Stay, Next] extends OneString [Stay, Next] with Indexed
  final class enumm      [Stay, Next] extends OneString [Stay, Next] with Indexed
  final class ident      [Stay, Next] extends OneString [Stay, Next] with Indexed
  final class valueType  [Stay, Next] extends OneString [Stay, Next] with Indexed
  final class cardinality[Stay, Next] extends OneString [Stay, Next] with Indexed
  final class doc        [Stay, Next] extends OneString [Stay, Next] with Indexed with Fulltext[Stay, Next]
  final class unique     [Stay, Next] extends OneString [Stay, Next] with Indexed
  final class isComponent[Stay, Next] extends OneBoolean[Stay, Next] with Indexed
  final class noHistory  [Stay, Next] extends OneBoolean[Stay, Next] with Indexed
  final class index      [Stay, Next] extends OneBoolean[Stay, Next] with Indexed
  final class fulltext   [Stay, Next] extends OneBoolean[Stay, Next] with Indexed
  final class t          [Stay, Next] extends OneLong   [Stay, Next] with Indexed
  final class tx         [Stay, Next] extends OneLong   [Stay, Next] with Indexed
  final class txInstant  [Stay, Next] extends OneDate   [Stay, Next] with Indexed

  final class ident$      [Stay, Next] extends OneString$ [Stay] with Indexed
  final class valueType$  [Stay, Next] extends OneString$ [Stay] with Indexed
  final class cardinality$[Stay, Next] extends OneString$ [Stay] with Indexed
  final class doc$        [Stay, Next] extends OneString$ [Stay] with Indexed with Fulltext[Stay, Next]
  final class unique$     [Stay, Next] extends OneString$ [Stay] with Indexed
  final class isComponent$[Stay, Next] extends OneBoolean$[Stay] with Indexed
  final class noHistory$  [Stay, Next] extends OneBoolean$[Stay] with Indexed
  final class index$      [Stay, Next] extends OneBoolean$[Stay] with Indexed
  final class fulltext$   [Stay, Next] extends OneBoolean$[Stay] with Indexed
}

trait Schema_[props] { def Schema: props = ??? }

trait Schema_attrId      { lazy val attrId     : Long    = ??? }
trait Schema_a           { lazy val a          : String  = ??? }
trait Schema_part        { lazy val part       : String  = ??? }
trait Schema_nsFull      { lazy val nsFull     : String  = ??? }
trait Schema_ns          { lazy val ns         : String  = ??? }
trait Schema_attr        { lazy val attr       : String  = ??? }
trait Schema_enumm       { lazy val enumm      : String  = ??? }
trait Schema_ident       { lazy val ident      : String  = ??? }
trait Schema_valueType   { lazy val valueType  : String  = ??? }
trait Schema_cardinality { lazy val cardinality: String  = ??? }
trait Schema_doc         { lazy val doc        : String  = ??? }
trait Schema_unique      { lazy val unique     : String  = ??? }
trait Schema_isComponent { lazy val isComponent: Boolean = ??? }
trait Schema_noHistory   { lazy val noHistory  : Boolean = ??? }
trait Schema_index       { lazy val index      : Boolean = ??? }
trait Schema_fulltext    { lazy val fulltext   : Boolean = ??? }
trait Schema_t           { lazy val t          : Long    = ??? }
trait Schema_tx          { lazy val tx         : Long    = ??? }
trait Schema_txInstant   { lazy val txInstant  : Date    = ??? }

// Please note that `$` has been subsituted with `_` to allow packaging to jars.
// To be interpreted as optional and not tacit
trait Schema_ident_       { lazy val ident$      : Option[String ] = ??? }
trait Schema_valueType_   { lazy val valueType$  : Option[String ] = ??? }
trait Schema_cardinality_ { lazy val cardinality$: Option[String ] = ??? }
trait Schema_doc_         { lazy val doc$        : Option[String ] = ??? }
trait Schema_unique_      { lazy val unique$     : Option[String ] = ??? }
trait Schema_isComponent_ { lazy val isComponent$: Option[Boolean] = ??? }
trait Schema_noHistory_   { lazy val noHistory$  : Option[Boolean] = ??? }
trait Schema_index_       { lazy val index$      : Option[Boolean] = ??? }
trait Schema_fulltext_    { lazy val fulltext$   : Option[Boolean] = ??? }

