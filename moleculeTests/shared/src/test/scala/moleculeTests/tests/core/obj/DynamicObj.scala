//package moleculeTests.tests.core.obj
//
//import molecule.core.util.Helpers
//import molecule.core.util.testing.expectCompileError
//import molecule.datomic.api.in1_out5._
//import molecule.datomic.base.facade.Conn
//import moleculeTests.setup.AsyncTestSuite
//import utest._
//import scala.concurrent.ExecutionContext.Implicits.global
//import moleculeTests.tests.core.base.dsl.CoreTest._
//import moleculeTests.tests.core.ref.dsl.SelfJoin._
//
//
///**
//  * Testing dynamic molecule objects.
//  *
//  * Dynamically applying a body of method(s) to a molecule allows it to
//  * resemble a case class definition body as though you instantiated a domain class
//  * with already populated data according to the molecule structure - plus your
//  * methods.
//  *
//  * Properties are available inside the body through the `self` reference to the
//  * object (you can give it other names too).
//  *
//  * Note that the molecule needs to be created explicitly with `m` and that the
//  * self reference to the molecule object can't be named the same as any of the
//  * top level attributes of the molecule.
//  */
//object DynamicObj extends AsyncTestSuite with Helpers {
//
//  "Local logic" - selfJoin { implicit conn =>
//      for {
//      _ <- Person.name("Ben").age(23).save
//
//    _ <- m(Person.name.age).apply { person =>
//      // Local business logic using the molecule object properties
//      def nextAge: Int = person.age + 1
//    }
//
//    person.name === "Ben"
//    person.age === 23
//    person.nextAge === 24
//  }
//
//
//  val baz = 2
//
//  "Dynamic type tests" - core { implicit conn =>
//
//    val foo = 5
//
//    _ <- Ns.int(42).Ref1.int1(43).save
//
//    // Molecule needs to be created explicitly with `m`
//    val obj = m(Ns.int.Ref1.int1) { self =>
//      // External references
//      val bar = foo + baz
//
//      // val
//      val v1 = bar + bar
//
//      // def
//      def a1: Int = v1 + 1
//      def a2(): Int = a1 + 1
//      def a3(i: Int): Int = a2() + i
//      def a4[A](): Int = a3(2)
//      def a5[A](v: A): String = v.toString
//      def a6(i: Int): Int = i + a1
//      def a7(i: Int, s: String): String = s + (i + self.int)
//      def a8(ii: List[Int]): Int = ii.length
//      def a9(ii: List[Conn]): Int = ii.length
//    }
//
//    // Dynamic calls
//    obj.v1 === 14
//    obj.a1 === 15
//    obj.a2() === 16
//    obj.a3(1) === 17
//    obj.a4() === 18
//    obj.a5(1) === "1"
//    obj.a5(true) === "true"
//    obj.a6(1) === 16
//    obj.a7(1, "hi") === "hi43"
//    obj.a8(List(1, 2, 3)) === 3
//    obj.a9(List(conn)) === 1
//
//    // Object properties
//    obj.int === 42
//    obj.Ref1.int1 === 43
//    } yield ()
//    }
//
//"Mandatory implementation" - core {   implicit conn =>
//      for {
//    expectCompileError(
//      "m(Ns.int) {self => }",
//      "molecule.core.exceptions.package$MoleculeException: " +
//        "Body of dynamic molecule can't be empty. Please define some val or def"
//    )
//    } yield ()
//    }
//
//"Not for input molecules" - core {   implicit conn =>
//      for {
//    val inputMolecule = m(Ns.str.int(?))
//    expectCompileError(
//      """inputMolecule(42).apply { self =>
//        def x = 7
//      }""",
//      "molecule.core.exceptions.package$MoleculeException: " +
//        "Can't use input molecules as dynamic molecules"
//    )
//  }
//  }
//}