package molecule.core.expression

import molecule.core.ast.elements
import molecule.core.ast.elements._
import molecule.core.dsl.attributes.Attr


/** Attribute expression markers and methods.
 * {{{
 * Person.age(42)                           // equality
 * Person.name.contains("John")             // fulltext search
 * Person.age.!=(42)                        // negation (or `not`)
 * Person.age.<(42)                         // comparison (< > <= >=)
 * Person.age().get                         // match non-asserted datoms (null) in query
 * Person(benId).age().update               // apply empty value to retract value(s) in updates
 * Person.hobbies.assert("golf")            // assert card-many value(s)
 * Person.hobbies.replace("golf", "diving") // replace card-many attribute value(s)
 * Person.hobbies.retract("golf")           // retract card-many attribute value(s)
 * Person.tags.k("en")                      // match values of map attributes by key
 * Person.name(?)                           // initiate input molecules awaiting input at runtime
 * Person.name(unify)                       // Unify attributes in self-joins
 * }}}
 *
 * @groupname attrMarker Attribute markers
 * @groupdesc attrMarker Markers applied to attributes that change the semantics of the attribute/molecule.
 * @groupprio attrMarker 20
 * @groupname attrExprImplicits Expression implicits
 * @groupdesc attrExprImplicits Turns basic types into `TermValue`'s that can be used in [[elements.Expression Expression]]
 * @groupprio attrExprImplicits 21
 */
trait AttrExpressions {

  trait qm
  trait unify_stable

  /** Expression methods common for all attributes. */
  trait AttrExpr[Ns, T] {

    /** Apply empty value to retract datom in an update.
     * {{{
     * for {
     *   benId <- Person.name("Ben").age(42).save.map(_.eid)
     *   _ <- Person.name.age$.get.map(_ ==> List(("Ben", Some(42))))
     *
     *   // Retract Ben's age
     *   _ <- Person(benId).age().update
     *   _ <- Person.name.age$.get.map(_ ==> List(("Ben", None)))
     * } yield ()
     * }}}
     * For cardinality-many attributes, ''all'' values of the attribute are retracted.
     */
    def apply(): Ns with Attr = ???

    /** Match one or more attribute values.
     * <br><br>
     * Applying value(s) to an attribute has different semantics depending on what operation is performed:
     * {{{
     * for {
     *   // Querying with `get` - Ben is 42
     *   _ <- Person.name_("Ben").age.get.map(_ ==> List(42))
     *
     *   // OR-semantics when multiple values are queried
     *   _ <- Person.name_("Ben", "Liz").age.get.map(_ ==> List(42, 37))
     *
     *   // Saving new value (any old value is retracted)
     *   _ <- Person.name("Joe").save
     *
     *   // Saving multiple new card-many attribute values (all old values are retracted).
     *   // (Saving multiple new values not allowed for card-one attributes)
     *   _ <- Person.hobbies("golf", "diving").save
     *
     *   // Replacing value when updating (old value is retracted).
     *   _ <- Person(benId).age(43).update
     *
     *   // Replacing multiple values for card-many attributes (all old values are retracted).
     *   // (Replacing multiple values not allowed for card-one attributes)
     *   _ <- Person(benId).hobbies("reading", "walking").update
     * } yield ()
     * }}}
     *
     * @param value      Attribute values to be matched
     * @param moreValues Optional additional attribute values to be matched
     * @return Filtered molecule
     */
    def apply(value: T, moreValues: T*): Ns with Attr = ???

    /** Match one or more Iterables of attribute values.
     * <br><br>
     * Multiple Iterables are concatenated into one Iterable of values to be matched.
     * <br><br>
     * Applying value(s) to an attribute has different semantics depending on what operation is performed:
     * {{{
     * for {
     *   // Querying with `get` - Ben is 42
     *   _ <- Person.name_(Set("Ben")).age.get.map(_ ==> List(42))
     *
     *   members = List("Ben", "Liz")
     *   associates = List("Don", "Ann")
     *
     *   // OR-semantics when multiple values are queried
     *   _ <- Person.name_(members).age.get.map(_ ==> List(42, 37))
     *   // Multiple Iterables concatenated
     *   _ <- Person.name_(members, associates).age.get.map(_ ==> List(42, 37, 71, 28))
     *
     *   // Single value in Iterable can be added when saving
     *   // (although easier to apply the value directly)
     *   _ <- Person.name(List("Joe")).save
     *
     *   // Saving multiple new card-many attribute values (all old values are retracted).
     *   // (Saving multiple new values not allowed for card-one attributes)
     *   sports = Set("golf", "diving")
     *   _ <- Person.hobbies(sports).save
     *
     *   // Replacing value when updating (old value is retracted).
     *   _ <- Person(benId).age(List(43)).update
     *
     *   // Replacing multiple values for card-many attributes (all old values are retracted).
     *   // (Replacing multiple values not allowed for card-one attributes)
     *   _ <- Person(benId).hobbies(Seq("reading", "walking")).update
     *
     *   // Multiple Iterables can be applied
     *   _ <- Person(benId).hobbies(Seq("reading", "walking"), Set("stamps")).update
     * } yield ()
     * }}}
     *
     * @param values     Iterable of attribute values to be matched
     * @param moreValues Optional additional Iterables of attribute values to be matched
     * @return Filtered molecule
     */
    def apply(values: Iterable[T], moreValues: Iterable[T]*): Ns with Attr = ???


    /** Mark tacit attribute to be unified in self-join.
     * <br><br>
     * Attributes before '''`Self`''' are joined with attributes added after '''`Self`''' by values that can unify:
     * <br><br>
     * Find 23-year olds liking the same beverage as 25-year olds (unifying by beverage):
     * {{{
     * for {
     *   _ <- Person.name.age(23).Drinks.beverage._Person.Self // create self join
     *         .name.age(25).Drinks.beverage_(unify)           // unify by beverage
     *         .get.map(_ ==> List(
     *           ("Joe", 23, "Coffee", "Ben", 25),  // Joe (23) and Ben(25) both like coffee
     *           ("Liz", 23, "Coffee", "Ben", 25),  // Liz (23) and Ben(25) both like coffee
     *           ("Liz", 23, "Tea", "Ben", 25)      // Liz (23) and Ben(25) both like tea
     *         ))
     * } yield ()
     * }}}
     * `unify` marker can only be applied to tacit attribute (with underscore).
     *
     * @param unifyer `unify` marker to unify self-join by this attribute values
     * @return Self-join molecule
     */
    def apply(unifyer: unify_stable): Ns with Attr = ???


    /** Match attribute values different from one or more applied values.
     * {{{
     * for {
     *   _ <- Person.name.get.map(_ ==> List("Ben", "Liz", "Joe"))
     *
     *   // Negate one value
     *   _ <- Person.name.not("Ben").get.map(_ ==> List("Liz", "Joe"))
     *
     *   // Negate multiple values
     *   _ <- Person.name.not("Ben", "Liz").get.map(_ ==> List("Joe"))
     *
     *   // same as
     *   _ <- Person.name.!=("Ben", "Liz").get.map(_ ==> List("Joe"))
     * } yield ()
     * }}}
     *
     * @param value      Negated attribute value
     * @param moreValues Optional additional negated attribute values
     * @return Filtered molecule
     */
    def not(value: T, moreValues: T*): Ns with Attr = ???


    /** Match attribute values different from applied Iterable of values.
     * {{{
     * for {
     *   _ <- Person.name.get.map(_ ==> List("Ben", "Liz", "Joe"))
     *
     *   // Negate Iterable of values
     *   _ <- Person.name.not(List("Ben", "Joe")).get.map(_ ==> List("Liz"))
     *
     *   // same as
     *   _ <- Person.name.!=(List("Ben", "Joe")).get.map(_ ==> List("Liz"))
     * } yield ()
     * }}}
     *
     * @param values Iterable of negated attribute values
     * @return Filtered molecule
     */
    def not(values: Iterable[T]): Ns with Attr = ???


    /** Match attribute values different from one or more applied values.
     * {{{
     * for {
     *   _ <- Person.name.get.map(_ ==> List("Ben", "Liz", "Joe"))
     *
     *   // Negate one value
     *   _ <- Person.name.!=("Ben").get.map(_ ==> List("Liz", "Joe"))
     *
     *   // Negate multiple values
     *   _ <- Person.name.!=("Ben", "Liz").get.map(_ ==> List("Joe"))
     *
     *   // same as
     *   _ <- Person.name.not("Ben", "Liz").get.map(_ ==> List("Joe"))
     * } yield ()
     * }}}
     *
     * @param value      Negated attribute value
     * @param moreValues Optional additional negated attribute values
     * @return Filtered molecule
     */
    def !=(value: T, moreValues: T*): Ns with Attr = ???


    /** Match attribute values different from applied Iterable of values.
     * {{{
     * for {
     *   _ <- Person.name.get.map(_ ==> List("Ben", "Liz", "Joe"))
     *
     *   // Negate Iterable of values
     *   _ <- Person.name.!=(List("Ben", "Joe")).get.map(_ ==> List("Liz"))
     *
     *   // same as
     *   _ <- Person.name.not(List("Ben", "Joe")).get.map(_ ==> List("Liz"))
     * } yield ()
     * }}}
     *
     * @param values Iterable of negated attribute values
     * @return Filtered molecule
     */
    def !=(values: Seq[T]): Ns with Attr = ???
  }


  /** Value update methods for card-many attributes. */
  trait ManyAttrExpr[Ns, Add, OldNew, Rem] {

    /** Assert one or more card-many attribute values.
     * {{{
     * for {
     *   _ <- Person.hobbies.get.map(_ ==> List(Set("golf", "diving")))
     *
     *   // Assert/add value
     *   _ <- Person(benId).hobbies.assert("stamps").update
     *
     *   // Assert multiple values
     *   _ <- Person(benId).hobbies.assert("walking", "theater").update
     *
     *   _ <- Person.hobbies.get.map(_ ==> List(Set("golf", "diving", "stamps", "walking", "theater")))
     * } yield ()
     * }}}
     *
     * @param value      New attribute value
     * @param moreValues Optional additional new attribute values
     * @return Molecule to be updated
     */
    def assert(value: Add, moreValues: Add*): Ns with Attr = ???


    /** Assert Iterable of card-many attribute values.
     * {{{
     * for {
     *   _ <- Person.hobbies.get.map(_ ==> List(Set("golf", "diving")))
     *
     *   // Assert/add values of Iterable
     *   _ <- Person(benId).hobbies.assert(Seq("stamps", "walking", "theater")).update
     *
     *   _ <- Person.hobbies.get.map(_ ==> List(Set("golf", "diving", "stamps", "walking", "theater")))
     * } yield ()
     * }}}
     *
     * @param values Iterable of attribute values
     * @return Molecule to be updated
     */
    def assert(values: Iterable[Add]): Ns with Attr = ???


    /** Replace one or more card-many attribute values.
     * <br><br>
     * Retracts old value and asserts new value.
     * {{{
     * for {
     *   _ <- Person.hobbies.get.map(_ ==> List(Set("golf", "diving")))
     *
     *   // Replace value by applying old/new value pair
     *   _ <- Person(benId).hobbies.replace("golf" -> "theater").update
     *
     *   // Replace multiple values by applying multiple old/new value pairs
     *   _ <- Person(benId).hobbies.replace("theater" -> "concerts", "diving" -> "football").update
     *
     *   _ <- Person.hobbies.get.map(_ ==> List(Set("concerts", "football")))
     * } yield ()
     * }}}
     *
     * @param oldNew  Pair of old/new value
     * @param oldNews Optional additional pairs of old/new value
     * @return Molecule to be updated
     */
    def replace(oldNew: OldNew, oldNews: OldNew*): Ns with Attr = ???


    /** Replace Iterable of card-many attribute values.
     * <br><br>
     * Retracts old value and asserts new value.
     * {{{
     * for {
     *   _ <- Person.hobbies.get.map(_ ==> List(Set("golf", "diving")))
     *
     *   // Replace values by applying Iterable of old/new value pairs
     *   _ <- Person(benId).hobbies.replace("theater" -> "concerts", "diving" -> "football").update
     *
     *   _ <- Person.hobbies.get.map(_ ==> List(Set("concerts", "football")))
     * } yield ()
     * }}}
     *
     * @param oldNews Iterable of old/new attribute values. For map attributes it's key/value pairs.
     * @return Molecule to be updated
     */
    def replace(oldNews: Iterable[OldNew]): Ns with Attr = ???


    /** Retract one or more card-many attribute values.
     * {{{
     * for {
     *   _ <- Person.hobbies.get.map(_ ==> List(Set("golf", "diving", "stamps", "walking", "theater")))
     *
     *   // Retract value
     *   _ <- Person(benId).hobbies.retract("theater").update
     *
     *   // Retract multiple values
     *   _ <- Person(benId).hobbies.retract("stamps", "walking").update
     *
     *   _ <- Person.hobbies.get.map(_ ==> List(Set("golf", "diving")))
     * } yield ()
     * }}}
     *
     * @param value      Attribute value to be retracted
     * @param moreValues Optional additional attribute values to be retracted
     * @return Molecule to be updated
     */
    def retract(value: Rem, moreValues: Rem*): Ns with Attr = ???


    /** Retract Iterable of card-many attribute values.
     * {{{
     * for {
     *   _ <- Person.hobbies.get.map(_ ==> List(Set("golf", "diving", "stamps", "walking", "theater")))
     *
     *   // Retract multiple values
     *   _ <- Person(benId).hobbies.retract(List("walking", "theater")).update
     *
     *   _ <- Person.hobbies.get.map(_ ==> List(Set("golf", "diving", "stamps")))
     * } yield ()
     * }}}
     *
     * @param values Iterable of attribute values to be retracted
     * @return Molecule to be updated
     */
    def retract(values: Iterable[Rem]): Ns with Attr = ???
  }


  /** Expression methods of value attributes. */
  trait ValueAttrExpr[Ns, In, T] extends AttrExpr[Ns, T] {

    /** Match attribute values less than upper value.
     * {{{
     * for {
     *   _ <- Person.age.get.map(_ ==> List(5, 12, 28))
     *   _ <- Person.age.<(12).get.map(_ ==> List(5))
     * } yield ()
     * }}}
     *
     * @param upper Upper value
     * @return Molecule
     */
    def <(upper: T): Ns with Attr = ???


    /** Match attribute values less than or equal to upper value.
     * {{{
     * for {
     *   _ <- Person.age.get.map(_ ==> List(5, 12, 28))
     *   _ <- Person.age.<=(12).get.map(_ ==> List(5, 12))
     * } yield ()
     * }}}
     *
     * @param upper Upper value
     * @return Molecule
     */
    def <=(upper: T): Ns with Attr = ???


    /** Match attribute values bigger than lower value
     * {{{
     * for {
     *   _ <- Person.age.get.map(_ ==> List(5, 12, 28))
     *   _ <- Person.age.>(12).get.map(_ ==> List(28))
     * } yield ()
     * }}}
     *
     * @param lower Lower value
     * @return Molecule
     */
    def >(lower: T): Ns with Attr = ???


    /** Match attribute values bigger than or equal to lower value.
     * {{{
     * for {
     *   _ <- Person.age.get.map(_ ==> List(5, 12, 28))
     *   _ <- Person.age.>=(12).get.map(_ ==> List(12, 28))
     * } yield ()
     * }}}
     *
     * @param lower Lower value
     * @return Molecule
     */
    def >=(lower: T): Ns with Attr = ???

    // Input

    /** Mark molecule as input molecule awaiting attribute value(s) to be matched.
     * {{{
     * for {
     *   _ <- Person.name.age.get.map(_ ==> List(("Ben", 42), ("Liz", 37)))
     *
     *   // Create input molecule at compile time by applying `?` marker to attribute
     *   ageOfPerson = m(Person.name_(?).age)
     *
     *   // Apply `name` attribute value at runtime to get age
     *   _ <- ageOfPerson("Ben").get.map(_ ==> List(42))
     * } yield ()
     * }}}
     *
     * @param value Input marker `?` for equality match
     * @return Input molecule
     */
    def apply(value: qm): In with Attr = ???


    /** Mark molecule as input molecule awaiting attribute negation value(s).
     * {{{
     * for {
     *   _ <- Person.name.age.get.map(_ ==> List(("Ben", 42), ("Liz", 37)))
     *
     *   // Create input molecule at compile time by applying `?` marker to attribute
     *   ageOfPersonsOtherThan = m(Person.name_.not(?).age)
     *
     *   // Apply `name` attribute value at runtime to get ages of all other than Ben
     *   _ <- ageOfPersonsOtherThan("Ben").get.map(_ ==> List(37)) // Liz' age
     * } yield ()
     * }}}
     *
     * @param value Input marker `?` for negation value
     * @return Input molecule
     */
    def not(value: qm): In with Attr = ???


    /** Mark molecule as input molecule awaiting attribute negation value(s).
     * {{{
     * for {
     *   _ <- Person.name.age.get.map(_ ==> List(("Ben", 42), ("Liz", 37)))
     *
     *   // Create input molecule at compile time by applying `?` marker to attribute
     *   ageOfPersonsOtherThan = m(Person.name_.!=(?).age)
     *
     *   // Apply `name` attribute value at runtime to get ages of all other than Ben
     *   _ <- ageOfPersonsOtherThan("Ben").get.map(_ ==> List(37)) // Liz' age
     * } yield ()
     * }}}
     *
     * @param value Input marker `?` for negation value
     * @return Input molecule
     */
    def !=(value: qm): In with Attr = ???


    /** Mark molecule as input molecule awaiting attribute upper value.
     * {{{
     * for {
     *   _ <- Person.name.age.get.map(_ ==> List(("Liz", 37), ("Ben", 42), ("Don", 71)))
     *
     *   // Create input molecule at compile time by applying `?` marker to attribute
     *   personsUnder = m(Person.name.age_.<(?))
     *
     *   // Apply upper value at runtime to get names of all under 42
     *   _ <- personsUnder(42).get.map(_ ==> List("Liz"))
     * } yield ()
     * }}}
     *
     * @param upper Input marker `?` for upper value
     * @return Input molecule
     */
    def <(upper: qm): In with Attr = ???


    /** Mark molecule as input molecule awaiting attribute upper value.
     * {{{
     * for {
     *   _ <- Person.name.age.get.map(_ ==> List(("Liz", 37), ("Ben", 42), ("Don", 71)))
     *
     *   // Create input molecule at compile time by applying `?` marker to attribute
     *   personsUnderOrExactly = m(Person.name.age_.<=(?))
     *
     *   // Apply upper value at runtime to get names of all under or exactly 42
     *   _ <- personsUnderOrExactly(42).get.map(_ ==> List("Liz", "Ben"))
     * } yield ()
     * }}}
     *
     * @param upper Input marker `?` for upper value
     * @return Input molecule
     */
    def <=(upper: qm): In with Attr = ???


    /** Mark molecule as input molecule awaiting attribute lower value.
     * {{{
     * for {
     *   _ <- Person.name.age.get.map(_ ==> List(("Liz", 37), ("Ben", 42), ("Don", 71)))
     *
     *   // Create input molecule at compile time by applying `?` marker to attribute
     *   personsOver = m(Person.name.age_.>(?))
     *
     *   // Apply lower value at runtime to get names of all over 42
     *   _ <- personsOver(42).get.map(_ ==> List("Don"))
     * } yield ()
     * }}}
     *
     * @param lower Input marker `?` for lower value
     * @return Input molecule
     */
    def >(lower: qm): In with Attr = ???


    /** Mark molecule as input molecule awaiting attribute lower value.
     * {{{
     * for {
     *   _ <- Person.name.age.get.map(_ ==> List(("Liz", 37), ("Ben", 42), ("Don", 71)))
     *
     *   // Create input molecule at compile time by applying `?` marker to attribute
     *   personsOverOrExactly = m(Person.name.age_.>=(?))
     *
     *   // Apply lower value at runtime to get names of all over or exactly 42
     *   _ <- personsOverOrExactly(42).get.map(_ ==> List("Ben", "Don"))
     * } yield ()
     * }}}
     *
     * @param lower Input marker `?` for lower value
     * @return Input molecule
     */
    def >=(lower: qm): In with Attr = ???


    /** Filter attribute values with logical expression.
     * {{{
     * for {
     *   _ <- Person.name.age.get.map(_ ==> List(
     *     ("Liz", 37),
     *     ("Ben", 42),
     *     ("Don", 71)
     *   ))
     *
     *   // Apply OR expression
     *   // Match all entities with `name` attribute having value "Liz" or "Ben"
     *   _ <- Person.name_("Liz" or "Ben").age.get.map(_ ==> List(37, 42))
     * } yield ()
     * }}}
     *
     * @param expr1 OR expression
     * @return Molecule
     */
    def apply(expr1: Exp1[T]): Ns with Attr = ???


    /** Filter attribute values with logical expression.
     * {{{
     * for {
     *   _ <- Person.name.age.hobbies.get.map(_ ==> List(
     *     ("Joe", 42, Set("golf", "reading")),
     *     ("Ben", 42, Set("golf", "diving", "reading")),
     *     ("Liz", 37, Set("golf", "diving"))
     *   ))
     *
     *   // Apply AND expression for card-many attributes
     *   _ <- Person.name.hobbies_("golf" and "diving").get.map(_ ==> List("Ben", "Liz"))
     *
     *   // Given an input molecule awaiting 2 inputs, we can apply AND-pairs to OR expression:
     *   persons = m(Person.name_(?).age(?))
     *   _ <- persons(("Ben" and 42) or ("Liz" and 37)).get.map(_ ==> List(42, 37))
     * } yield ()
     * }}}
     *
     * @param expr2 OR/AND expression
     * @return Molecule
     */
    def apply(expr2: Exp2[T, T]): Ns with Attr = ???


    /** Expression AST for building OR/AND expressions.
     * {{{
     * for {
     *   _ <- Person.name.age.hobbies.get.map(_ ==> List(
     *     ("Liz", 37, Set("golf", "diving")),
     *     ("Ben", 42, Set("golf", "diving", "reading")),
     *     ("Joe", 42, Set("golf", "reading"))
     *   ))
     *
     *   // Apply two AND expression for card-many attributes
     *   _ <- Person.name.hobbies_("golf" and "diving" and "reading").get.map(_ ==> List("Ben"))
     * } yield ()
     * }}}
     * With input molecules we can apply logic to multiple attributes at once.
     * {{{
     * for {
     *   _ <- Person.name.age.noOfCars.noOfKids.get.map(_ ==> List(
     *     ("Joe", 42, 1, 2),
     *     ("Ben", 42, 1, 1),
     *     ("Liz", 37, 2, 3)
     *   ))
     *   // Apply AND-triples to OR expression:
     *   persons = m(Person.name.age_(?).noOfCars(?).noOfKids_(?))
     *   _ <- persons((42 and 1 and 1) or (37 and 2 and 3)).get.map(_ ==> List("Ben", "Liz"))
     * } yield ()
     * }}}
     *
     * @param expr3 OR/AND expression
     * @return Molecule
     */
    def apply(expr3: Exp3[T, T, T]): Ns with Attr = ???
  }


  /** Expression methods of card-one attributes. */
  trait OneExpr[Ns, In, T] extends ValueAttrExpr[Ns, In, T]


  /** Expression methods of card-many attributes. */
  trait ManyExpr[Ns, In, T] extends ValueAttrExpr[Ns, In, T] with ManyAttrExpr[Ns, T, (T, T), T] {

    def apply(set: Set[T], moreSets: Set[T]*): Ns with Attr = ???

    def not(set: Set[T]): Ns with Attr = ???
    def not(set: Set[T], set2: Set[T], moreSets: Set[T]*): Ns with Attr = ???
    def not(sets: Seq[Set[T]]): Ns with Attr = ???

    def !=(set: Set[T]): Ns with Attr = ???
    def !=(set: Set[T], set2: Set[T], moreSets: Set[T]*): Ns with Attr = ???
    def !=(sets: Seq[Set[T]]): Ns with Attr = ???
  }


  /** Expression methods of map attributes. */
  trait MapAttrExpr[Ns, In, T] extends ValueAttrExpr[Ns, In, T] with ManyAttrExpr[Ns, (String, T), (String, T), String] {

    /** Match map attribute key/value pair(s).
     * {{{
     * for {
     *   _ <- Greeting.id.strMap insert List(
     *     (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
     *     (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
     *     (3, Map("en" -> "Hello", "da" -> "Hej")),
     *     (4, Map("da" -> "Hej"))
     *   )
     *
     *   // Apply key/value pair
     *   _ <- Greeting.id.strMap_("da" -> "Hej").get.map(_ ==> List(3, 4))
     *
     *   // Apply multiple key/value pairs (OR semantics)
     *   _ <- Greeting.id.strMap_("da" -> "Hej", "en" -> "Hi there").get.map(_ ==> List(1, 3, 4))
     * } yield ()
     * }}}
     *
     * @param keyValue      Key/value pair
     * @param moreKeyValues Optional further key/value pairs
     * @return Filtered molecule
     */
    def apply(keyValue: (String, T), moreKeyValues: (String, T)*): Ns with Attr = ???


    /** Match Iterable of map attribute key/value pair(s).
     * {{{
     * for {
     *   _ <- Greeting.id.strMap insert List(
     *     (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
     *     (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
     *     (3, Map("en" -> "Hello", "da" -> "Hej")),
     *     (4, Map("da" -> "Hej"))
     *   )
     *
     *   // Apply Iterable of key/value pairs (OR semantics)
     *   _ <- Greeting.id.strMap_(List("da" -> "Hej", "en" -> "Hi there")).get.map(_ ==> List(1, 3, 4))
     * } yield ()
     * }}}
     *
     * @param keyValues Iterable of key/value pairs
     * @return Filtered molecule
     */
    def apply(keyValues: Iterable[(String, T)]): Ns with Attr = ???


    /** Match OR expression of map attribute key/value pairs.
     * {{{
     * for {
     *   _ <- Greeting.id.strMap insert List(
     *     (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
     *     (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
     *     (3, Map("en" -> "Hello", "da" -> "Hej")),
     *     (4, Map("da" -> "Hej"))
     *   )
     *
     *   // Apply OR expression(s) of key/value pairs
     *   _ <- Greeting.id.strMap_("en" -> "Hi there" or "fr" -> "Bonjour").get.map(_ ==> List(1, 2))
     *   _ <- Greeting.id.strMap_("en" -> "Hi there" or "fr" -> "Bonjour" or "en" -> "Hello").get.map(_ ==> List(1, 2, 3))
     * } yield ()
     * }}}
     *
     * @param keyValues One or more OR expressions of key/value pairs
     * @return Filtered molecule
     */
    def apply(keyValues: Or[(String, T)]): Ns with Attr = ???


    /** Match Map of map attribute key/value pair(s).
     * {{{
     * for {
     *   _ <- Greeting.id.strMap insert List(
     *     (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
     *     (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
     *     (3, Map("en" -> "Hello", "da" -> "Hej")),
     *     (4, Map("da" -> "Hej"))
     *   )
     *
     *   // Apply Map of key/value pairs (OR semantics)
     *   _ <- Greeting.id.strMap_(Map("da" -> "Hej", "en" -> "Hi there")).get.map(_ ==> List(1, 3, 4))
     * } yield ()
     * }}}
     *
     * @param map Map of key/value pairs
     * @return Filtered molecule
     */
    def apply(map: Map[String, T]): Ns with Attr = ???


    /** Match map attribute key to retrieve corresponding key/value pairs.
     * {{{
     * for {
     *   _ <- Greeting.id.strMap insert List(
     *     (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
     *     (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
     *     (3, Map("en" -> "Hello", "da" -> "Hej")),
     *     (4, Map("da" -> "Hej"))
     *   )
     *
     *   // Apply key
     *   _ <- Greeting.id.strMap_.k("en").get.map(_ ==> List(1, 2, 3))
     *   _ <- Greeting.id.strMap.k("en").get.map(_ ==> List(
     *     (1, Map("en" -> "Hi there")),
     *     (2, Map("en" -> "Oh, Hi")),
     *     (3, Map("en" -> "Hello"))
     *   ))
     * } yield ()
     * }}}
     *
     * @param key Map attribute key (String)
     * @return Filtered molecule
     */
    def k(key: String): Values with Ns with Attr = ??? // Hack to satisfy intellij inference


    /** Match map attribute keys to retrieve corresponding key/value pairs.
     * {{{
     * for {
     *   _ <- Greeting.id.strMap insert List(
     *     (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
     *     (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
     *     (3, Map("en" -> "Hello", "da" -> "Hej")),
     *     (4, Map("da" -> "Hej"))
     *   )
     *
     *   // Apply keys
     *   _ <- Greeting.id.strMap_.k("en", "fr").get.map(_ ==> List(1, 2, 3))
     *   _ <- Greeting.id.strMap.k("en", "fr").get.map(_ ==> List(
     *     (1, Map("en" -> "Hi there")),
     *     (2, Map("en" -> "Oh, Hi", "fr" -> "Bonjour")),
     *     (3, Map("en" -> "Hello"))
     *   ))
     * } yield ()
     * }}}
     *
     * @param key      Map attribute key (String)
     * @param moreKeys Optional additional map attribute keys
     * @return Filtered molecule
     */
    def k(key: String, moreKeys: String*): Values with Ns with Attr = ???


    /** Match Iterable of map attribute keys to retrieve corresponding key/value pairs.
     * {{{
     * for {
     *   _ <- Greeting.id.strMap insert List(
     *     (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
     *     (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
     *     (3, Map("en" -> "Hello", "da" -> "Hej")),
     *     (4, Map("da" -> "Hej"))
     *   )
     *
     *   // Apply Iterable of keys
     *   _ <- Greeting.id.strMap_.k(List("en", "fr")).get.map(_ ==> List(1, 2, 3))
     *   _ <- Greeting.id.strMap.k(List("en", "fr")).get.map(_ ==> List(
     *     (1, Map("en" -> "Hi there")),
     *     (2, Map("en" -> "Oh, Hi", "fr" -> "Bonjour")),
     *     (3, Map("en" -> "Hello"))
     *   ))
     * } yield ()
     * }}}
     *
     * @param keys Iterable of attribute keys
     * @return Filtered molecule
     */
    def k(keys: Iterable[String]): Values with Ns with Attr = ???


    /** Match OR expression(s) of map attribute keys to retrieve corresponding key/value pairs.
     * {{{
     * for {
     *   _ <- Greeting.id.strMap insert List(
     *     (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
     *     (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
     *     (3, Map("en" -> "Hello", "da" -> "Hej")),
     *     (4, Map("da" -> "Hej"))
     *   )
     *
     *   // Apply OR expression of keys
     *   _ <- Greeting.id.strMap_.k("en" or "fr").get.map(_ ==> List(1, 2, 3))
     *   _ <- Greeting.id.strMap.k("en" or "fr").get.map(_ ==> List(
     *     (1, Map("en" -> "Hi there")),
     *     (2, Map("en" -> "Oh, Hi", "fr" -> "Bonjour")),
     *     (3, Map("en" -> "Hello"))
     *   ))
     *
     *   // Apply multiple OR expressions of keys
     *   _ <- Greeting.id.strMap_.k("en" or "fr" or "da").get.map(_ ==> List(1, 2, 3, 4))
     * } yield ()
     * }}}
     *
     * @param or OR expression(s) of attribute keys
     * @return Filtered molecule
     */
    def k(or: Or[String]): Values with Ns with Attr = ???


    // Keyed attribute value methods
    trait Values {

      /** Match value(s) of filtered map attribute.
       * {{{
       * for {
       *   _ <- Greeting.id.strMap insert List(
       *     (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
       *     (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
       *     (3, Map("en" -> "Hello", "da" -> "Hej")),
       *     (4, Map("da" -> "Hej"))
       *   )
       *
       *   // Apply key filter only
       *   _ <- Greeting.id.strMap_.k("da").get.map(_ ==> List(1, 2, 3, 4))
       *
       *   // Apply additional value filter
       *   _ <- Greeting.id.strMap_.k("da")("Hej").get.map(_ ==> List(3, 4))
       *
       *   // Apply additional value filters as OR expression
       *   _ <- Greeting.id.strMap_.k("da")("Hej", "Hejsa").get.map(_ ==> List(1, 3, 4))
       * } yield ()
       * }}}
       *
       * @param value      Filtering value
       * @param moreValues Optional additional filtering values
       * @return Filtered molecule
       */
      def apply(value: T, moreValues: T*): Ns with Attr = ???


      /** Match Iterable of value(s) of filtered map attribute.
       * {{{
       * for {
       *   _ <- Greeting.id.strMap insert List(
       *     (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
       *     (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
       *     (3, Map("en" -> "Hello", "da" -> "Hej")),
       *     (4, Map("da" -> "Hej"))
       *   )
       *
       *   // Apply key filter only
       *   _ <- Greeting.id.strMap_.k("da").get.map(_ ==> List(1, 2, 3, 4))
       *
       *   // Apply Seq of additional value filters (OR semantics)
       *   _ <- Greeting.id.strMap_.k("da")(Seq("Hej", "Hejsa")).get.map(_ ==> List(1, 3, 4))
       * } yield ()
       * }}}
       *
       * @param values Iterable of filtering value(s)
       * @return Filtered molecule
       */
      def apply(values: Iterable[T]): Ns with Attr = ???


      /** Match OR expression of value(s) of filtered map attribute.
       * {{{
       * for {
       *   _ <- Greeting.id.strMap insert List(
       *     (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
       *     (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
       *     (3, Map("en" -> "Hello", "da" -> "Hej")),
       *     (4, Map("da" -> "Hej"))
       *   )
       *
       *   // Apply key filter only
       *   _ <- Greeting.id.strMap_.k("da").get.map(_ ==> List(1, 2, 3, 4))
       *
       *   // Apply additional value filters as OR expression
       *   _ <- Greeting.id.strMap_.k("da")("Hej" or "Hejsa").get.map(_ ==> List(1, 3, 4))
       *   _ <- Greeting.id.strMap_.k("da")("Hej" or "Hejsa" or "Hilser").get.map(_ ==> List(1, 2, 3, 4))
       * } yield ()
       * }}}
       *
       * @param or OR expression of filtering values
       * @return Filtered molecule
       */
      def apply(or: Or[T]): Ns with Attr = ???


      /** Match negated value(s) of filtered map attribute.
       * {{{
       * for {
       *   _ <- Greeting.id.strMap insert List(
       *     (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
       *     (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
       *     (3, Map("en" -> "Hello", "da" -> "Hej")),
       *     (4, Map("da" -> "Hej"))
       *   )
       *
       *   // Apply key filter only
       *   _ <- Greeting.id.strMap_.k("en").get.map(_ ==> List(1, 2, 3))
       *
       *   // Apply additional negation value filter
       *   _ <- Greeting.id.strMap_.k("en").not("Hello").get.map(_ ==> List(1, 2))
       *   _ <- Greeting.id.strMap.k("en").not("Hello").get.map(_ ==> List(
       *     (1, Map("en" -> "Hi there")),
       *     (2, Map("en" -> "Oh, Hi"))
       *   ))
       *
       *   // Apply multiple negation value filters (OR semantics)
       *   _ <- Greeting.id.strMap_.k("en").not("Hello", "Hi there").get.map(_ ==> List(2))
       *
       *   // Same as
       *   _ <- Greeting.id.strMap_.k("en").!=("Hello", "Hi there").get.map(_ ==> List(2))
       * } yield ()
       * }}}
       *
       * @param value      Filter value
       * @param moreValues Optional additional filter values
       * @return Filtered molecule
       */
      def not(value: T, moreValues: T*): Ns with Attr = ???


      /** Match negated value(s) of filtered map attribute.
       * {{{
       * for {
       *   _ <- Greeting.id.strMap insert List(
       *     (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
       *     (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
       *     (3, Map("en" -> "Hello", "da" -> "Hej")),
       *     (4, Map("da" -> "Hej"))
       *   )
       *
       *   // Apply key filter only
       *   _ <- Greeting.id.strMap_.k("en").get.map(_ ==> List(1, 2, 3))
       *
       *   // Apply additional negation value filter
       *   _ <- Greeting.id.strMap_.k("en").!=("Hello").get.map(_ ==> List(1, 2))
       *   _ <- Greeting.id.strMap.k("en").!=("Hello").get.map(_ ==> List(
       *     (1, Map("en" -> "Hi there")),
       *     (2, Map("en" -> "Oh, Hi"))
       *   ))
       *
       *   // Apply multiple negation value filters (OR semantics)
       *   _ <- Greeting.id.strMap_.k("en").!=("Hello", "Hi there").get.map(_ ==> List(2))
       *
       *   // Same as
       *   _ <- Greeting.id.strMap_.k("en").not("Hello", "Hi there").get.map(_ ==> List(2))
       * } yield ()
       * }}}
       *
       * @param value      Filter value
       * @param moreValues Optional additional filter values
       * @return Filtered molecule
       */
      def !=(value: T, moreValues: T*): Ns with Attr = ???


      /** Match values of filtered map attribute less than upper value.
       * {{{
       * for {
       *   _ <- Greeting.id.strMap insert List(
       *     (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
       *     (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
       *     (3, Map("en" -> "Hello", "da" -> "Hej")),
       *     (4, Map("da" -> "Hej"))
       *   )
       *
       *   // Apply key filter only
       *   _ <- Greeting.id.strMap_.k("en").get.map(_ ==> List(1, 2, 3))
       *
       *   // Apply additional less-than value filter
       *   _ <- Greeting.id.strMap_.k("en").<("Hi").get.map(_ ==> List(3))
       *   _ <- Greeting.id.strMap.k("en").<("Hi").get.map(_ ==> List(
       *     (3, Map("en" -> "Hello"))
       *   ))
       * } yield ()
       * }}}
       *
       * @param upper Upper value
       * @return Filtered molecule
       */
      def <(upper: T): Ns with Attr = ???


      /** Match values of filtered map attribute less than or equal to upper value.
       * {{{
       * for {
       *   _ <- Greeting.id.strMap insert List(
       *     (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
       *     (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
       *     (3, Map("en" -> "Hello", "da" -> "Hej")),
       *     (4, Map("da" -> "Hej"))
       *   )
       *
       *   // Apply key filter only
       *   _ <- Greeting.id.strMap_.k("en").get.map(_ ==> List(1, 2, 3))
       *
       *   // Apply additional less-than-or-equal-to value filter
       *   _ <- Greeting.id.strMap_.k("en").<=("Hi").get.map(_ ==> List(1, 3))
       *   _ <- Greeting.id.strMap.k("en").<=("Hi").get.map(_ ==> List(
       *     (1, Map("en" -> "Hi there")),
       *     (3, Map("en" -> "Hello"))
       *   ))
       * } yield ()
       * }}}
       *
       * @param upper Upper value
       * @return Filtered molecule
       */
      def <=(upper: T): Ns with Attr = ???


      /** Match values of filtered map attribute bigger than lower value.
       * {{{
       * for {
       *   _ <- Greeting.id.strMap insert List(
       *     (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
       *     (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
       *     (3, Map("en" -> "Hello", "da" -> "Hej")),
       *     (4, Map("da" -> "Hej"))
       *   )
       *
       *   // Apply key filter only
       *   _ <- Greeting.id.strMap_.k("en").get.map(_ ==> List(1, 2, 3))
       *
       *   // Apply additional bigger-than value filter
       *   _ <- Greeting.id.strMap_.k("en").>("Hi").get.map(_ ==> List(2))
       *   _ <- Greeting.id.strMap.k("en").>("Hi").get.map(_ ==> List(
       *     (2, Map("en" -> "Oh, Hi"))
       *   ))
       * } yield ()
       * }}}
       *
       * @param lower Lower value
       * @return Filtered molecule
       */
      def >(lower: T): Ns with Attr = ???


      /** Match values of filtered map attribute bigger than or equal to lower value.
       * {{{
       * for {
       *   _ <- Greeting.id.strMap insert List(
       *     (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
       *     (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
       *     (3, Map("en" -> "Hello", "da" -> "Hej")),
       *     (4, Map("da" -> "Hej"))
       *   )
       *
       *   // Apply key filter only
       *   _ <- Greeting.id.strMap_.k("en").get.map(_ ==> List(1, 2, 3))
       *
       *   // Apply additional bigger-than-or-equal-to value filter
       *   _ <- Greeting.id.strMap_.k("en").>=("Hi").get.map(_ ==> List(1, 2))
       *   _ <- Greeting.id.strMap.k("en").>=("Hi").get.map(_ ==> List(
       *     (1, Map("en" -> "Hi there")),
       *     (2, Map("en" -> "Oh, Hi"))
       *   ))
       * } yield ()
       * }}}
       *
       * @param lower Lower value
       * @return Filtered molecule
       */
      def >=(lower: T): Ns with Attr = ???


      /** Match word values of filtered String map attribute.
       * <br><br>
       * Fulltext searches are case-insensitive and only searches for whole words.
       * {{{
       * for {
       *   _ <- Greeting.id.strMap insert List(
       *     (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
       *     (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
       *     (3, Map("en" -> "Hello", "da" -> "Hej")),
       *     (4, Map("da" -> "Hej"))
       *   )
       *
       *   // Apply key filter only
       *   _ <- Greeting.id.strMap_.k("en").get.map(_ ==> List(1, 2, 3))
       *
       *   // Apply additional less-than-or-equal-to value filter
       *   _ <- Greeting.id.strMap_.k("en").contains("Hi").get.map(_ ==> List(1, 2))
       *   _ <- Greeting.id.strMap.k("en").contains("Hi").get.map(_ ==> List(
       *     (1, Map("en" -> "Hi there")),
       *     (2, Map("en" -> "Oh, Hi"))
       *   ))
       *
       *   // Regex can be used
       *   _ <- Greeting.id.strMap_.k("en").contains("Hi|Hello").get.map(_ ==> List(1, 2, 3))
       * } yield ()
       * }}}
       *
       * @note Fulltext search is constrained by several defaults (which cannot be altered):
       *       searches are case insensitive, remove apostrophe or apostrophe and s sequences,
       *       and filter out the following common English stop words:
       *       "a", "an", "and", "are", "as", "at", "be", "but", "by",
       *       "for", "if", "in", "into", "is", "it",
       *       "no", "not", "of", "on", "or", "such",
       *       "that", "the", "their", "then", "there", "these",
       *       "they", "this", "to", "was", "will", "with"
       * @param word Search word or regex
       * @return Filtered molecule
       */
      def contains(word: T): Ns with Attr = ???
      // Todo: How can we make `contains` available to String type only?
    }
  }

  /** Expression methods of optional attributes. */
  trait OptionalExpr[Ns, T] {

    /** Apply optional attribute value in save molecule.
     * {{{
     * for {
     *   // Save optional `age` values
     *   _ <- Person.name("Ben").age$(Some(42)).save
     *   _ <- Person.name("Liz").age$(None).save
     *
     *   _ <- Person.name.age$.get.map(_ ==> List(
     *     ("Ben", Some(42)),
     *     ("Liz", None),
     *   ))
     * } yield ()
     * }}}
     *
     * @param some Optional attribute value to be saved
     * @return Save-molecule
     */
    def apply(some: Option[T]): Ns with Attr = ???
  }

  /** Expression methods of String attributes with fulltext search. */
  trait FulltextExpr[Ns, In] {

    /** Match words of String attribute.
     * <br><br>
     * Fulltext searches are case-insensitive and only searches for whole words.
     * {{{
     * for {
     *   _ <- Phrase.id.txt.get.map(_ ==> List(
     *     (1, "The quick fox jumps"),
     *     (2, "Ten slow monkeys")
     *   ))
     *
     *   _ <- Phrase.id.txt_.contains("jumps").get.map(_ ==> List(1))
     *
     *   // Only whole words matched
     *   _ <- Phrase.id.txt_.contains("jump").get.map(_ ==> Nil)
     *
     *   // Searches are case-insensitive
     *   _ <- Phrase.id.txt_.contains("JuMpS").get.map(_ ==> List(1))
     *
     *   // Empty spaces ignored
     *   _ <- Phrase.id.txt_.contains("   jumps   ").get.map(_ ==> List(1))
     *
     *   // Multiple search words have OR semantics
     *   _ <- Phrase.id.txt_.contains("jumps", "slow").get.map(_ ==> List(1, 2))
     *
     *   // Common words ignored
     *   _ <- Phrase.id.txt_.contains("The").get.map(_ ==> Nil)
     * } yield ()
     * }}}
     *
     * @note Fulltext search is constrained by several defaults (which cannot be altered):
     *       searches are case insensitive, remove apostrophe or apostrophe and s sequences,
     *       and filter out the following common English stop words:
     *       "a", "an", "and", "are", "as", "at", "be", "but", "by",
     *       "for", "if", "in", "into", "is", "it",
     *       "no", "not", "of", "on", "or", "such",
     *       "that", "the", "their", "then", "there", "these",
     *       "they", "this", "to", "was", "will", "with"
     * @param word      Search word
     * @param moreWords Optional additional search words
     * @return Filtered molecule
     */
    def contains(word: String, moreWords: String*): Ns with Attr = ???

    // todo: this set of String searchers instead - check Django ORM and other ORMs too
    // def startsWith(str: String): Ns with Attr = ???
    // def endsWith(str: String): Ns with Attr = ???
    // def containsWord(str: String): Ns with Attr = ??? // current `contains`
    // def contains(needle: String): Ns with Attr = ???     // *$needle*


    /** Mark as input molecule with String attribute word search.
     * <br><br>
     * Fulltext searches are case-insensitive and only searches for whole words.
     * {{{
     * for {
     *   _ <- Phrase.id.txt.get.map(_ ==> List(
     *     (1, "The quick fox jumps"),
     *     (2, "Ten slow monkeys")
     *   ))
     *
     *   // Mark as input molecule awaiting word(s) to search for
     *   phraseFinder = m(Phrase.id.txt_.contains(?))
     *
     *   // Then we can apply words to the input molecule at runtime:
     *
     *   _ <- phraseFinder("jumps").get.map(_ ==> List(1))
     *
     *   // Only whole words matched
     *   _ <- phraseFinder("jump").get.map(_ ==> Nil)
     *
     *   // Searches are case-insensitive
     *   _ <- phraseFinder("JuMpS").get.map(_ ==> List(1))
     *
     *   // Empty spaces ignored
     *   _ <- phraseFinder("   jumps   ").get.map(_ ==> List(1))
     *
     *   // Multiple search words have OR semantics
     *   _ <- phraseFinder("jumps", "slow").get.map(_ ==> List(1, 2))
     *
     *   // Common words ignored
     *   _ <- phraseFinder("The").get.map(_ ==> Nil)
     * } yield ()
     * }}}
     *
     * @note Fulltext search is constrained by several defaults (which cannot be altered):
     *       searches are case insensitive, remove apostrophe or apostrophe and s sequences,
     *       and filter out the following common English stop words:
     *       "a", "an", "and", "are", "as", "at", "be", "but", "by",
     *       "for", "if", "in", "into", "is", "it",
     *       "no", "not", "of", "on", "or", "such",
     *       "that", "the", "their", "then", "there", "these",
     *       "they", "this", "to", "was", "will", "with"
     * @param words Search words
     * @return Input molecule awaiting search word(s)
     */
    def contains(words: qm): In with Attr = ???
  }
}

object AttrExpressions extends AttrExpressions