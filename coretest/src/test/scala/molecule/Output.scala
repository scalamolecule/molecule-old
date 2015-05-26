package molecule

import molecule.util.dsl.coreTest._
import molecule.util.{CoreSetup, CoreSpec}


class Output extends CoreSpec {


  class Setup extends CoreSetup {
    // Persons with ages (except Ben)
    Ns.str.int insert List(
      ("John", 37),
      ("Lisa", 28),
      ("Mona", null.asInstanceOf[Int]))
  }


  "Tacet assertion" in new Setup {

    // All persons
    Ns.str.get === List("Mona", "John", "Lisa")

    // Tacet assertion
    // All persons _with an age_
    // Only return the age
    Ns.str.int_.get === List("John", "Lisa")

    // Same query with age returned too
    Ns.str.int.get === List(("Lisa", 28), ("John", 37))
  }


  "Tacet expression" in new Setup {

    // Tacet expression
    // Similarly we can add additional constraints to our query
    // without affecting which attributes to return

    Ns.str.int_(28).get === List("Lisa")
    Ns.str.int_.>(30).get === List("John")
    Ns.str.int_(37 or 30).get === List("John")
    Ns.str.int_(37 or 28).get === List("John", "Lisa")
    // etc...
  }
}