package molecule.core

/** Attribute expressions and operations.
  * <br><br>
  * Refine attribute matches with various [[AttrExpressions attribute expressions]]:
  * {{{
  * Person.age(42)                           // equality
  * Person.name.contains("John")             // fulltext search
  * Person.age.not(42)                       // negation
  * Person.age.<(42)                         // comparison (< > <= >=)
  * Person.name("John" or "Jonas")           // OR-logic
  * Person.age()                             // apply empty value to retract value(s) in updates
  * Person.hobbies.assert("golf")            // add value(s) to card-many attributes
  * Person.hobbies.retract("golf")           // retract value(s) of card-many attributes
  * Person.hobbies.replace("golf", "diving") // replace value(s) of card-many attributes
  * Person.tags.k("en")                      // match values of map attributes by key
  * Person.age(Nil)                          // match non-asserted datoms (null)
  * Person.name(?)                           // initiate input molecules awaiting input at runtime
  * Person.name(unify)                       // Unify attributes in self-joins
  * }}}
  * Apply [[AggregateKeywords aggregate keywords]] to aggregate attribute value(s):
  * {{{
  * // Aggregates on any attribute type
  * Person.age(count).get.map(_.head ==> 3)         // count of asserted `age` attribute values
  * Person.age(countDistinct).get.map(_.head ==> 3) // count of asserted distinct `age` attribute values
  * Person.age(max).get.map(_.head ==> 38)          // maximum `age` value (using `compare`)
  * Person.age(min).get.map(_.head ==> 5)           // maximum `age` value (using `compare`)
  * Person.age(rand).get.map(_.head ==> 25)         // single random `age` value
  * Person.age(sample).get.map(_.head ==> 27)       // single sample `age` value (when single value, same as random)
  *
  * // Aggregates on any attribute type, returning multiple values
  * Person.age(distinct).get.map(_.head ==> Vector(5, 7, 38)) // distinct `age` values
  * Person.age(max(2)).get.map(_.head ==> Vector(38, 7))      // 2 maximum `age` values
  * Person.age(min(2)).get.map(_.head ==> Vector(5, 7))       // 2 minimum `age` values
  * Person.age(rand(2)).get.map(_.head ==> Stream(5, ?))      // 2 random `age` values (values can re-occur)
  * Person.age(sample(2)).get.map(_.head ==> Vector(7, 38))   // 2 sample `age` values
  *
  * // Aggregates on number attributes
  * Person.age(sum).get.map(_.head ==> 50)                  // sum of all `age` numbers
  * Person.age(avg).get.map(_.head ==> 16.66666667)         // average of all `age` numbers
  * Person.age(median).get.map(_.head ==> 7)                // median of all `age` numbers
  * Person.age(stddev).get.map(_.head ==> 15.107025591499)  // standard deviation of all `age` numbers
  * Person.age(variance).get.map(_.head ==> 228.2222222222) // variance of all `age` numbers
  * }}}
  * */
package object expression
