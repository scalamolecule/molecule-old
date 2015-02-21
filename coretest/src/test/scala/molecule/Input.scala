package molecule

import molecule.util.dsl.coreTest._
import molecule.util.{CoreSetup, CoreSpec}


class Input extends CoreSpec {

  class Setup extends CoreSetup {
    Ns.str.int insert List(("John", 37), ("Lisa", 28), ("Ben", 28))
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

    // Expressions
//    personsYoungerThan.apply(30).debug
//    personsYoungerThan.apply(30).get === List("Ben", "Lisa")
//
//    // We don't have to assign an input-molecule to a variable
//    m(Ns.str.int_.<(?))(30).get === List("Lisa", "Ben")
//    // Although then it would be easier to just say
//    Ns.str.int_.<(30).get === List("Lisa", "Ben")

    ok
  }


}