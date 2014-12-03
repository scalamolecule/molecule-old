package molecule
package examples.dayOfDatomic.tutorial
import molecule.examples.dayOfDatomic.dsl.socialNews._
import molecule.examples.dayOfDatomic.schema._
import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec

class Binding extends DayOfAtomicSpec {

  implicit val conn = load(SocialNewsSchema.tx, "Binding")

  val List(stewartBrand, johnStewart, stuartSmalley, stuartHalloway) = User.firstName.lastName insert List(
    ("Stewart", "Brand"),
    ("John", "Stewart"),
    ("Stuart", "Smalley"),
    ("Stuart", "Halloway")
  ) ids


  "Binding" >> {

    // Input molecules returning only the entity id (`e`)
    val personFirst = m(User.e.firstName_(?))
    val person = m(User.e.firstName_(?).lastName_(?))

//    // Bind vars
//    personFirstLast("John", "Doe").one ===("John", "Doe")
//
//    // Bind tuple
//    person(("John", "Doe")).one ===("John", "Doe")
//
//    // Bind collection
//    personFirst(List("John", "Jane", "Phineas")).get === List("John", "Jane", "Phineas")
//
//    // Bind relation
//    person(List(("John", "Doe"), ("Jane", "Doe"))).get === List(("John", "Doe"), ("Jane", "Doe"))
//
//
    // Binding queries

    // Find all the Stewart first names
    personFirst("Stewart").one === stewartBrand

    // Find all the Stewart or Stuart first names
    personFirst("Stewart", "Stuart").get === List(stewartBrand, stuartSmalley, stuartHalloway)

    // Find all the Stewart/Stuart as either first name or last name

    User.a.v.debug
    /*
    [:find ?b2 ?b
     :where
       [?a ?attr ?b ?tx]
       [?attr :db/ident ?b1]
       [(.toString ^clojure.lang.Keyword ?b1) ?b2]]
     */
    User.a.v("Stewart").debug
    /*
    [:find ?b2
     :where
       [?a ?attr ?b]
       [?attr :db/ident ?b1]
       [(.toString ^clojure.lang.Keyword ?b1) ?b2]]
     */

//    User.e.a.v("John").debug
//    User.e.a(":user/firstName").v("John").debug
//    val personFirstOrLast = m(User.e.a(?).v(?))
//    personFirstOrLast(("Stewart", "Stuart"), ("Stewart", "Stuart")).get === List()
//    personFirstOrLast(("Stewart", "Stuart"), ("Stewart", "Stuart")).get === List()
//
//    person(("Stewart", "Stuart"), ("Stewart", "Stuart")).get === List()
//
//    // Find only the Smalley Stuarts
//    person("Stewart", "Stuart").get === List()
//
//    // Same query above, but with map (tuple) form
//    person(("Stewart", "Stuart")).get === List()

    ok
  }
}