package molecule
package semantics
import molecule.semantics.dsl.coreTest._


class Expressions extends CoreSpec {


  class BaseSetup extends CoreSetup {
    Ns.str insert List("", " ", ",", ".", "?", "A", "B", "a", "b")
    Ns.int insert List(-2, -1, 0, 1, 2)
    Ns.long insert List(-2L, -1L, 0L, 1L, 2L)
    Ns.float insert List(-2f, -1f, 0f, 1f, 2f)
    Ns.double insert List(-2.0, -1.0, 0.0, 1.0, 2.0)
    Ns.bool insert List(true, false)
    Ns.date insert List(date1, date2)

  }

  "Apply value" in new BaseSetup {

    // strings

    // Empty strings are saved too
    Ns.str.get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")

    Ns.str("").get === List("") // same as Ns.str.apply("").get
    Ns.str(" ").get === List(" ")
    Ns.str(",").get === List(",")
    Ns.str(".").get === List(".")
    Ns.str("?").get === List("?")
    Ns.str("A").get === List("A")
    Ns.str("B").get === List("B")
    Ns.str("a").get === List("a")
    Ns.str("b").get === List("b")

    // numbers

    Ns.int(1).get === List(1)
    Ns.int(0).get === List(0)
    Ns.int(-1).get === List(-1)

    Ns.long(1L).get === List(1L)
    Ns.long(0L).get === List(0L)
    Ns.long(-1L).get === List(-1L)

    Ns.float(1f).get === List(1f)
    Ns.float(0f).get === List(0f)
    Ns.float(-1f).get === List(-1f)

    Ns.double(1.0).get === List(1.0)
    Ns.double(0.0).get === List(0.0)
    Ns.double(-1.0).get === List(-1.0)

    Ns.bool(true).get === List(true)
    Ns.bool(false).get === List(false)

    Ns.date(date1).get === List(date1)
    Ns.date(date2).get === List(date2)
  }

  "Range of strings" in new BaseSetup {

    Ns.str.<("").get.sorted === List()
    Ns.str.<(" ").get.sorted === List("")
    Ns.str.<(",").get.sorted === List("", " ")
    Ns.str.<(".").get.sorted === List("", " ", ",")
    Ns.str.<("?").get.sorted === List("", " ", ",", ".")
    Ns.str.<("A").get.sorted === List("", " ", ",", ".", "?")
    Ns.str.<("B").get.sorted === List("", " ", ",", ".", "?", "A")
    Ns.str.<("a").get.sorted === List("", " ", ",", ".", "?", "A", "B")
    Ns.str.<("b").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a")
    Ns.str.<("d").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")

    Ns.str.>("").get.sorted === List(" ", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.>(" ").get.sorted === List(",", ".", "?", "A", "B", "a", "b")
    Ns.str.>(",").get.sorted === List(".", "?", "A", "B", "a", "b")
    Ns.str.>(".").get.sorted === List("?", "A", "B", "a", "b")
    Ns.str.>("?").get.sorted === List("A", "B", "a", "b")
    Ns.str.>("A").get.sorted === List("B", "a", "b")
    Ns.str.>("B").get.sorted === List("a", "b")
    Ns.str.>("C").get.sorted === List("a", "b")
    Ns.str.>("a").get.sorted === List("b")
    Ns.str.>("b").get.sorted === List()


    Ns.str.<=("").get.sorted === List("")
    Ns.str.<=(" ").get.sorted === List("", " ")
    Ns.str.<=(",").get.sorted === List("", " ", ",")
    Ns.str.<=(".").get.sorted === List("", " ", ",", ".")
    Ns.str.<=("?").get.sorted === List("", " ", ",", ".", "?")
    Ns.str.<=("A").get.sorted === List("", " ", ",", ".", "?", "A")
    Ns.str.<=("B").get.sorted === List("", " ", ",", ".", "?", "A", "B")
    Ns.str.<=("a").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a")
    Ns.str.<=("b").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")

    Ns.str.>=("").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.>=(" ").get.sorted === List(" ", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.>=(",").get.sorted === List(",", ".", "?", "A", "B", "a", "b")
    Ns.str.>=(".").get.sorted === List(".", "?", "A", "B", "a", "b")
    Ns.str.>=("?").get.sorted === List("?", "A", "B", "a", "b")
    Ns.str.>=("A").get.sorted === List("A", "B", "a", "b")
    Ns.str.>=("B").get.sorted === List("B", "a", "b")
    Ns.str.>=("a").get.sorted === List("a", "b")
    Ns.str.>=("b").get.sorted === List("b")
    Ns.str.>=("c").get.sorted === List()
  }


  "Negate string" in new BaseSetup {

    Ns.str.not("").get.sorted === List(" ", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.not(" ").get.sorted === List("", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.not(",").get.sorted === List("", " ", ".", "?", "A", "B", "a", "b")
    Ns.str.not(".").get.sorted === List("", " ", ",", "?", "A", "B", "a", "b")
    Ns.str.not("?").get.sorted === List("", " ", ",", ".", "A", "B", "a", "b")
    Ns.str.not("A").get.sorted === List("", " ", ",", ".", "?", "B", "a", "b")
    Ns.str.not("B").get.sorted === List("", " ", ",", ".", "?", "A", "a", "b")
    Ns.str.not("a").get.sorted === List("", " ", ",", ".", "?", "A", "B", "b")
    Ns.str.not("b").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a")
    Ns.str.not("C").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.not("c").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")

    Ns.str.!=("").get.sorted === List(" ", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.!=(" ").get.sorted === List("", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.!=(",").get.sorted === List("", " ", ".", "?", "A", "B", "a", "b")
    Ns.str.!=(".").get.sorted === List("", " ", ",", "?", "A", "B", "a", "b")
    Ns.str.!=("?").get.sorted === List("", " ", ",", ".", "A", "B", "a", "b")
    Ns.str.!=("A").get.sorted === List("", " ", ",", ".", "?", "B", "a", "b")
    Ns.str.!=("B").get.sorted === List("", " ", ",", ".", "?", "A", "a", "b")
    Ns.str.!=("a").get.sorted === List("", " ", ",", ".", "?", "A", "B", "b")
    Ns.str.!=("b").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a")
    Ns.str.!=("C").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")
    Ns.str.!=("c").get.sorted === List("", " ", ",", ".", "?", "A", "B", "a", "b")
  }


  "Search string (fulltext search)" in new CoreSetup {

    Ns.str insert List("The quick fox jumps", "Ten slow monkeys")

    // Trivial words like "The" not indexed
    Ns.str.contains("The").get === List()
    Ns.str.contains("Ten").get === List("Ten slow monkeys")

    // Only full words counted
    Ns.str.contains("jumps").get === List("The quick fox jumps")
    Ns.str.contains("jump").get === List()

    // Empty spaces ignored
    Ns.str.contains("slow ").get === List("Ten slow monkeys")
    Ns.str.contains(" slow").get === List("Ten slow monkeys")
    Ns.str.contains(" slow ").get === List("Ten slow monkeys")
    Ns.str.contains("  slow  ").get === List("Ten slow monkeys")

    // Words are searched individually - order and spaces ignored
    Ns.str.contains("slow     monkeys").get === List("Ten slow monkeys")
    Ns.str.contains("monkeys slow").get === List("Ten slow monkeys")
    Ns.str.contains("monkeys quick").get === List("Ten slow monkeys", "The quick fox jumps")
    Ns.str.contains("quick monkeys").get === List("Ten slow monkeys", "The quick fox jumps")
  }
  //
  //
  //  "String logic" in new CoreSetup {
  //    Ns.str.int insert List(("John", 37), ("Lisa", 28), ("Ben", 28))
  //
  //
  //  }
  //
  //
  //    class ExpressionSetup extends CoreSetup {
  //      Ns.str.int insert List(("John", 37), ("Lisa", 28), ("Ben", 28))
  //    }
  //
  //    "Applying a value" in new ExpressionSetup {
  //
  //      Ns.str_("John").int.get === List(37)
  //      Ns.str("John").int.get === List(("John", 37))
  //
  //      Ns.str.int_(28).get === List("Ben", "Lisa")
  //      Ns.str.int(28).get === List(("Ben", 28), ("Lisa", 28))
  //    }
  //  "Applying a value (parameterized)" in new ExpressionSetup {
  //    val ageOf = m(Ns.str_(?).int)
  //    ageOf("John").one === 37
  //
  //    val personByName = m(Ns.str(?).int)
  //    personByName("John").one ===("John", 37)
  //
  //    val nameOfAge = m(Ns.str.int_(?))
  //    nameOfAge(37).get === List("John")
  //    nameOfAge(28).get === List("Lisa", "Ben")
  //    nameOfAge(10).get === List()
  //
  //    val personOfAge = m(Ns.str.int(?))
  //    personOfAge(37).get === List(("John", 37))
  //    personOfAge(28).get === List(("Lisa", 28), ("Ben", 28))
  //    personOfAge(10).get === List()
  //  }
  //
  //  "Applying a Set of values" in new ExpressionSetup {
  //
  //  }
  //
  //  "Applying a value" in new ExpressionSetup {
  //    Ns.str("Lisa" or "Ben").int.get === List(("Lisa", 28), ("Ben", 28))
  //    Ns.str_("Lisa" or "Ben").int.get === List(28, 28)
  //
  //    Ns.str.int(28 or 37).get === List(("John", 37), ("Lisa", 28), ("Ben", 28))
  //    Ns.str.int_(28 or 37).get === List("John", "Lisa", "Ben")
  //  }
  //
  ////  "Fulltext search" in new ExpressionSetup {}
  //
  //  "Compare String" in new ExpressionSetup {
  //    //
  //    Ns.str.<("C").one === "Ben"
  //    // same as
  //    m(Ns.str < "C").one === "Ben"
  //
  //    Ns.str.<("C").one === "Ben"
  //  }

  //  "OR expressions" in new ExpressionSetup {}

  //  "AND expressions" in new ExpressionSetup {}
}