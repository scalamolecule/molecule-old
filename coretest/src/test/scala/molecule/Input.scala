package molecule

import molecule.util.dsl.coreTest._
import molecule.util.{CoreSetup, CoreSpec}


class Input extends CoreSpec {

  class Setup extends CoreSetup {
    Ns.str.int insert List(("John", 37), ("Lisa", 28), ("Ben", 28), ("Ann", 14))
  }

  // Parameterized molecules have a `?` placeholder for an expected input value
  // and we call them "Input-molecules".

  // Input-molecules have the benefit that you can assign them to a variable
  // and then re-use them with various input values. This also allows Datomic
  // to optimize the query and thereby increase runtime performance.


  "Awaiting equality match" in new Setup {

    // "Input-molecule" with a `?` placeholder for an expected input value
    // Often we are not interested in returning the input value so
    // we make the integer attribute tacet by adding an underscore
    val personOfAge = m(Ns.str.int_(?))

    // We can now use the input-molecule as a query template

    // Equality
    personOfAge(37).get === List("John")
    personOfAge(28).get === List("Ben", "Lisa")
    personOfAge(10).get === List()
  }


  "Awaiting expression input" in new Setup {

    val personsYoungerThan = m(Ns.str.int_.<(?))

    // Apply expression value
    personsYoungerThan(30).get === List("Ben", "Ann", "Lisa")

    // We don't have to assign an input-molecule to a variable
    m(Ns.str.int_.<(?))(30).get === List("Ben", "Ann", "Lisa")
    // Although then it would be easier to just say
    Ns.str.int_.<(30).get === List("Ben", "Ann", "Lisa")

    // For brevity we test some more expressions in the short form
    m(Ns.str.int_.>(?))(30).get === List("John")
    m(Ns.str.int_.<=(?))(28).get === List("Ben", "Ann", "Lisa")
    m(Ns.str.int_.>=(?))(28).get === List("Ben", "John", "Lisa")
    m(Ns.str.int_.!=(?))(30).get === List("Ben", "John", "Ann", "Lisa")
    m(Ns.str.int_.!=(?))(28).get === List("John", "Ann")
    m(Ns.str.int_.not(?))(28).get === List("John", "Ann")
  }
}