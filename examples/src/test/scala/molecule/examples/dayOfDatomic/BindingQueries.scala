package molecule.examples.dayOfDatomic

import molecule._
import molecule.examples.dayOfDatomic.dsl.socialNews._
import molecule.util.MoleculeSpec

class BindingQueries extends MoleculeSpec {

  "Binding queries" in new SocialNewsSetup {

    // Input molecules
    val first     = m(User.firstName_(?).lastName)
    val firstLast = m(User.firstName(?).lastName(?))

    // Bind vars / tuples (?)
    firstLast("Stu", "Halloway").one ===("Stu", "Halloway")

    // Bind collection
    first("Stu", "Ed").get === List("Halloway", "Itor")

    // Bind a relation
    firstLast(("Stu", "Halloway"), ("Ed", "Itor")).get === List(("Ed", "Itor"), ("Stu", "Halloway"))
    firstLast(("Stu", "Halloway"), ("Ed", "zzz")).get === List(("Stu", "Halloway"))
  }
}