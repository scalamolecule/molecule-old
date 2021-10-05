package molecule.core.api

import molecule.core.expression.{AggregateKeywords, AttrExpressions}

private[molecule] trait Keywords
  extends AggregateKeywords
  with AttrExpressions
{
  /** Unify attribute value in self-join.
    * <br><br>
    * Apply `unify` marker to attribute to unify its value with previous values of the same attribute in the molecule in a self-join.
    * {{{
    *   m(Person.age.name.Beverages * Beverage.name.rating) insert List(
    *       (23, "Joe", List(("Coffee", 3), ("Cola", 2), ("Pepsi", 3))),
    *       (25, "Ben", List(("Coffee", 2), ("Tea", 3))),
    *       (23, "Liz", List(("Coffee", 1), ("Tea", 3), ("Pepsi", 1))))
    *
    *   // What beverages do pairs of 23- AND 25-year-olds like in common?
    *   // Drink name is unified - Joe and Ben both drink coffee, etc..
    *   Person.age_(23).name.Beverages.name._Ns.Self
    *         .age_(25).name.Beverages.name_(unify).get.map(_.sorted ==> List()
    *     ("Joe", "Coffee", "Ben"),
    *     ("Liz", "Coffee", "Ben"),
    *     ("Liz", "Tea", "Ben")
    *   )
    * }}}
    *
    * @group attrMarker
    * */
  object unify extends AttrExpressions.unify_stable

  object count extends Keywords.count
  object countDistinct extends Keywords.countDistinct
  object distinct extends Keywords.distinct
  object max extends Keywords.max
  object min extends Keywords.min
  object rand extends Keywords.rand
  object sample extends Keywords.sample
  object avg extends Keywords.avg
  object median extends Keywords.median
  object stddev extends Keywords.stddev
  object sum extends Keywords.sum
  object variance extends Keywords.variance
}

object Keywords extends Keywords
