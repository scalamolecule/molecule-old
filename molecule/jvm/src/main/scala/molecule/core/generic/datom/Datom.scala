/*
* AUTO-GENERATED Molecule DSL boilerplate code for namespace `Datom`
*
* To change:
* 1. edit schema definition file in `molecule.tests.core.generic.schema/`
* 2. `sbt compile` in terminal
* 3. Refresh and re-compile project in IDE
*/
package molecule.core.generic.datom

import molecule.core.boilerplate.attributes._
import scala.language.higherKinds

/** Base trait with generic attribute trait types shared by all arity interfaces */
trait Datom {

  /** Entity id (Long) */
  final class e        [Ns, In] extends OneLong   [Ns, In] with Indexed

  /** (Partition)-Namespace-prefixed attribute name (":part_Ns/attr") */
  final class a        [Ns, In] extends OneString [Ns, In] with Indexed

  /** Datom value (Any)*/
  final class v        [Ns, In] extends OneAny    [Ns, In] with Indexed

  /** Transaction point in time `t` (Long/Int) */
  final class t        [Ns, In] extends OneLong   [Ns, In] with Indexed

  /** Transaction entity id (Long) */
  final class tx       [Ns, In] extends OneLong   [Ns, In] with Indexed

  /** Transaction wall-clock time (Date) */
  final class txInstant[Ns, In] extends OneDate   [Ns, In] with Indexed

  /** Transaction operation: assertion (true) or retraction (false) */
  final class op       [Ns, In] extends OneBoolean[Ns, In] with Indexed
}