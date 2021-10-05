package molecule.core.api

import molecule.core.expression.AttrExpressions

private[molecule] trait Qm {
  /** Turn molecule into input molecule awaiting input.
    * <br><br>
    * Apply input marker `?` to attribute to turn molecule into an 'input molecule'.
    * <br><br>
    * At runtime the input molecule expects input for the attribute in place of the `?` marker.
    * {{{
    *   // Input molecule created at compile time.
    *   val ageOfPersons = m(Person.name_(?).age) // awaiting name of type String
    *
    *   // At runtime, "Ben" is applied as input replacing the `?` placeholder and we can get the age.
    *   ageOfPersons("Ben").get.map(_ ==> List(42))
    * }}}
    *
    * @note Data can only be retrieved from input molecules once they have been resolved with input.<br>
    *       Input molecule queries are cached and optimized by Datomic.
    * @group attrMarker
    * */
  object ? extends AttrExpressions.qm
}


