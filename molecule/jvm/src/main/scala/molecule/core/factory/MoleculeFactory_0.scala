package molecule.core.factory

import molecule.core.api.Molecule_0._
import molecule.core.dsl.base._
import molecule.core.macros.MakeMolecule
import scala.language.experimental.macros


/** Implicit molecule factory methods of arity 1-22.
  * <br><br>
  * Molecules are type-safe custom Scala models of data structures in a Datomic database.
  * <br><br>
  * Molecules are build with your custom meta-DSL that is auto-generated from your Schema Definition file.
  * Calling `m` on your modelled DSL structure lets Molecule macros create a custom molecule,
  * ready for retrieving or manipulating data in the Datomic database.
  * <br><br>
  * Each molecule consists of one or more attributes that can have values or expressions applied.
  * The arity of a molecule is determined by the number of attributes that will return data when the
  * molecule is queried against the Datomic database. Attributes returning data are called "output attributes".
  * <br><br>
  * For brevity, only arity 1 and 2 method signatures are shown. Arity 3-22 follow the same pattern.
  *
  * @groupname molecule Implicit factory methods to create molecules.
  * @groupprio molecule 50
  */
trait Molecule_Factory2 {

  /** Macro creation of molecule from user-defined DSL structure with 1 output attribute.
    * <br><br>
    * Molecules can be created explicitly or implicitly by building a DSL structure using boilerplate
    * code generated from the schema definition file.
    * <br><br>
    * The builder pattern is used to add one or more attributes to an initial namespace
    * like `Person` from the example below. Once the molecule models the desired data structure
    * we can call various actions on it, like `get` that retrieves matching data from the database.
    * {{{
    *   // Explicitly calling `m` to create Person molecule with 1 output attribute
    *   m(Person.name).get === List("Ben")
    *
    *   // Molecule implicitly created so we can call `get`
    *   Person.name.get.head === "Ben"
    * }}}
    * For arity-many molecules, data structures are returned as tuples. But for arity-1
    * molecules (like the example having only 1 output attribute, `name`) there's no need for
    * a tuple, so values type-safely matching the attribute are returned directly in the list.
    *
    * @group molecule
    * @param dsl User-defined DSL structure modelling the molecule
    * @tparam A Type of output attribute 1 (`name`: String)
    * @return Molecule of arity-1 typed to first attribute (Molecule_0_01[props, A])
    */
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]


  /** Macro creation of molecule from user-defined DSL structure with 2 output attributes.
    * <br><br>
    * Molecules can be created explicitly or implicitly by building a DSL structure using boilerplate
    * code generated from the schema definition file.
    * <br><br>
    * The builder pattern is used to add one or more attributes to an initial namespace
    * like `Person` from the example below. Once the molecule models the desired data structure
    * we can call various actions on it, like `get` that retrieves matching data from the database.
    * <br><br>
    * Data structures are returned as tuples of values type-safely matching the molecule attribute types
    * {{{
    *   // Explicitly calling `m` to create Person molecule with 2 attributes
    *   m(Person.name.age).get.head === ("Ben", 42)
    *
    *   // Molecule implicitly created so we can call `get`
    *   Person.name.age.get.head === ("Ben", 42)
    * }}}
    *
    * @group molecule
    * @param dsl User-defined DSL structure modelling the molecule
    * @tparam A Type of output attribute 1 (`name`: String)
    * @tparam B Type of output attribute 2 (`age`: Int)
    * @return Molecule of arity-2 typed to two attributes (Molecule_0_02[props, A, B])
    */
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
}

trait Molecule_Factory1 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
}

trait Molecule_Factory3 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
}

trait Molecule_Factory4 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
  implicit final def m[obj[_], props, A, B, C, D](dsl: NS_0_04[obj, props, A, B, C, D]): Molecule_0_04[props, A, B, C, D] = macro MakeMolecule.from4attr[props, A, B, C, D]
}

trait Molecule_Factory5 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
  implicit final def m[obj[_], props, A, B, C, D](dsl: NS_0_04[obj, props, A, B, C, D]): Molecule_0_04[props, A, B, C, D] = macro MakeMolecule.from4attr[props, A, B, C, D]
  implicit final def m[obj[_], props, A, B, C, D, E](dsl: NS_0_05[obj, props, A, B, C, D, E]): Molecule_0_05[props, A, B, C, D, E] = macro MakeMolecule.from5attr[props, A, B, C, D, E]
}

trait Molecule_Factory6 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
  implicit final def m[obj[_], props, A, B, C, D](dsl: NS_0_04[obj, props, A, B, C, D]): Molecule_0_04[props, A, B, C, D] = macro MakeMolecule.from4attr[props, A, B, C, D]
  implicit final def m[obj[_], props, A, B, C, D, E](dsl: NS_0_05[obj, props, A, B, C, D, E]): Molecule_0_05[props, A, B, C, D, E] = macro MakeMolecule.from5attr[props, A, B, C, D, E]
  implicit final def m[obj[_], props, A, B, C, D, E, F](dsl: NS_0_06[obj, props, A, B, C, D, E, F]): Molecule_0_06[props, A, B, C, D, E, F] = macro MakeMolecule.from6attr[props, A, B, C, D, E, F]
}

trait Molecule_Factory7 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
  implicit final def m[obj[_], props, A, B, C, D](dsl: NS_0_04[obj, props, A, B, C, D]): Molecule_0_04[props, A, B, C, D] = macro MakeMolecule.from4attr[props, A, B, C, D]
  implicit final def m[obj[_], props, A, B, C, D, E](dsl: NS_0_05[obj, props, A, B, C, D, E]): Molecule_0_05[props, A, B, C, D, E] = macro MakeMolecule.from5attr[props, A, B, C, D, E]
  implicit final def m[obj[_], props, A, B, C, D, E, F](dsl: NS_0_06[obj, props, A, B, C, D, E, F]): Molecule_0_06[props, A, B, C, D, E, F] = macro MakeMolecule.from6attr[props, A, B, C, D, E, F]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G](dsl: NS_0_07[obj, props, A, B, C, D, E, F, G]): Molecule_0_07[props, A, B, C, D, E, F, G] = macro MakeMolecule.from7attr[props, A, B, C, D, E, F, G]
}

trait Molecule_Factory8 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
  implicit final def m[obj[_], props, A, B, C, D](dsl: NS_0_04[obj, props, A, B, C, D]): Molecule_0_04[props, A, B, C, D] = macro MakeMolecule.from4attr[props, A, B, C, D]
  implicit final def m[obj[_], props, A, B, C, D, E](dsl: NS_0_05[obj, props, A, B, C, D, E]): Molecule_0_05[props, A, B, C, D, E] = macro MakeMolecule.from5attr[props, A, B, C, D, E]
  implicit final def m[obj[_], props, A, B, C, D, E, F](dsl: NS_0_06[obj, props, A, B, C, D, E, F]): Molecule_0_06[props, A, B, C, D, E, F] = macro MakeMolecule.from6attr[props, A, B, C, D, E, F]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G](dsl: NS_0_07[obj, props, A, B, C, D, E, F, G]): Molecule_0_07[props, A, B, C, D, E, F, G] = macro MakeMolecule.from7attr[props, A, B, C, D, E, F, G]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H](dsl: NS_0_08[obj, props, A, B, C, D, E, F, G, H]): Molecule_0_08[props, A, B, C, D, E, F, G, H] = macro MakeMolecule.from8attr[props, A, B, C, D, E, F, G, H]
}

trait Molecule_Factory9 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
  implicit final def m[obj[_], props, A, B, C, D](dsl: NS_0_04[obj, props, A, B, C, D]): Molecule_0_04[props, A, B, C, D] = macro MakeMolecule.from4attr[props, A, B, C, D]
  implicit final def m[obj[_], props, A, B, C, D, E](dsl: NS_0_05[obj, props, A, B, C, D, E]): Molecule_0_05[props, A, B, C, D, E] = macro MakeMolecule.from5attr[props, A, B, C, D, E]
  implicit final def m[obj[_], props, A, B, C, D, E, F](dsl: NS_0_06[obj, props, A, B, C, D, E, F]): Molecule_0_06[props, A, B, C, D, E, F] = macro MakeMolecule.from6attr[props, A, B, C, D, E, F]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G](dsl: NS_0_07[obj, props, A, B, C, D, E, F, G]): Molecule_0_07[props, A, B, C, D, E, F, G] = macro MakeMolecule.from7attr[props, A, B, C, D, E, F, G]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H](dsl: NS_0_08[obj, props, A, B, C, D, E, F, G, H]): Molecule_0_08[props, A, B, C, D, E, F, G, H] = macro MakeMolecule.from8attr[props, A, B, C, D, E, F, G, H]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I](dsl: NS_0_09[obj, props, A, B, C, D, E, F, G, H, I]): Molecule_0_09[props, A, B, C, D, E, F, G, H, I] = macro MakeMolecule.from9attr[props, A, B, C, D, E, F, G, H, I]
}

trait Molecule_Factory10 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
  implicit final def m[obj[_], props, A, B, C, D](dsl: NS_0_04[obj, props, A, B, C, D]): Molecule_0_04[props, A, B, C, D] = macro MakeMolecule.from4attr[props, A, B, C, D]
  implicit final def m[obj[_], props, A, B, C, D, E](dsl: NS_0_05[obj, props, A, B, C, D, E]): Molecule_0_05[props, A, B, C, D, E] = macro MakeMolecule.from5attr[props, A, B, C, D, E]
  implicit final def m[obj[_], props, A, B, C, D, E, F](dsl: NS_0_06[obj, props, A, B, C, D, E, F]): Molecule_0_06[props, A, B, C, D, E, F] = macro MakeMolecule.from6attr[props, A, B, C, D, E, F]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G](dsl: NS_0_07[obj, props, A, B, C, D, E, F, G]): Molecule_0_07[props, A, B, C, D, E, F, G] = macro MakeMolecule.from7attr[props, A, B, C, D, E, F, G]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H](dsl: NS_0_08[obj, props, A, B, C, D, E, F, G, H]): Molecule_0_08[props, A, B, C, D, E, F, G, H] = macro MakeMolecule.from8attr[props, A, B, C, D, E, F, G, H]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I](dsl: NS_0_09[obj, props, A, B, C, D, E, F, G, H, I]): Molecule_0_09[props, A, B, C, D, E, F, G, H, I] = macro MakeMolecule.from9attr[props, A, B, C, D, E, F, G, H, I]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J](dsl: NS_0_10[obj, props, A, B, C, D, E, F, G, H, I, J]): Molecule_0_10[props, A, B, C, D, E, F, G, H, I, J] = macro MakeMolecule.from10attr[props, A, B, C, D, E, F, G, H, I, J]
}

trait Molecule_Factory11 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
  implicit final def m[obj[_], props, A, B, C, D](dsl: NS_0_04[obj, props, A, B, C, D]): Molecule_0_04[props, A, B, C, D] = macro MakeMolecule.from4attr[props, A, B, C, D]
  implicit final def m[obj[_], props, A, B, C, D, E](dsl: NS_0_05[obj, props, A, B, C, D, E]): Molecule_0_05[props, A, B, C, D, E] = macro MakeMolecule.from5attr[props, A, B, C, D, E]
  implicit final def m[obj[_], props, A, B, C, D, E, F](dsl: NS_0_06[obj, props, A, B, C, D, E, F]): Molecule_0_06[props, A, B, C, D, E, F] = macro MakeMolecule.from6attr[props, A, B, C, D, E, F]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G](dsl: NS_0_07[obj, props, A, B, C, D, E, F, G]): Molecule_0_07[props, A, B, C, D, E, F, G] = macro MakeMolecule.from7attr[props, A, B, C, D, E, F, G]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H](dsl: NS_0_08[obj, props, A, B, C, D, E, F, G, H]): Molecule_0_08[props, A, B, C, D, E, F, G, H] = macro MakeMolecule.from8attr[props, A, B, C, D, E, F, G, H]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I](dsl: NS_0_09[obj, props, A, B, C, D, E, F, G, H, I]): Molecule_0_09[props, A, B, C, D, E, F, G, H, I] = macro MakeMolecule.from9attr[props, A, B, C, D, E, F, G, H, I]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J](dsl: NS_0_10[obj, props, A, B, C, D, E, F, G, H, I, J]): Molecule_0_10[props, A, B, C, D, E, F, G, H, I, J] = macro MakeMolecule.from10attr[props, A, B, C, D, E, F, G, H, I, J]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K](dsl: NS_0_11[obj, props, A, B, C, D, E, F, G, H, I, J, K]): Molecule_0_11[props, A, B, C, D, E, F, G, H, I, J, K] = macro MakeMolecule.from11attr[props, A, B, C, D, E, F, G, H, I, J, K]
}

trait Molecule_Factory12 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
  implicit final def m[obj[_], props, A, B, C, D](dsl: NS_0_04[obj, props, A, B, C, D]): Molecule_0_04[props, A, B, C, D] = macro MakeMolecule.from4attr[props, A, B, C, D]
  implicit final def m[obj[_], props, A, B, C, D, E](dsl: NS_0_05[obj, props, A, B, C, D, E]): Molecule_0_05[props, A, B, C, D, E] = macro MakeMolecule.from5attr[props, A, B, C, D, E]
  implicit final def m[obj[_], props, A, B, C, D, E, F](dsl: NS_0_06[obj, props, A, B, C, D, E, F]): Molecule_0_06[props, A, B, C, D, E, F] = macro MakeMolecule.from6attr[props, A, B, C, D, E, F]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G](dsl: NS_0_07[obj, props, A, B, C, D, E, F, G]): Molecule_0_07[props, A, B, C, D, E, F, G] = macro MakeMolecule.from7attr[props, A, B, C, D, E, F, G]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H](dsl: NS_0_08[obj, props, A, B, C, D, E, F, G, H]): Molecule_0_08[props, A, B, C, D, E, F, G, H] = macro MakeMolecule.from8attr[props, A, B, C, D, E, F, G, H]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I](dsl: NS_0_09[obj, props, A, B, C, D, E, F, G, H, I]): Molecule_0_09[props, A, B, C, D, E, F, G, H, I] = macro MakeMolecule.from9attr[props, A, B, C, D, E, F, G, H, I]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J](dsl: NS_0_10[obj, props, A, B, C, D, E, F, G, H, I, J]): Molecule_0_10[props, A, B, C, D, E, F, G, H, I, J] = macro MakeMolecule.from10attr[props, A, B, C, D, E, F, G, H, I, J]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K](dsl: NS_0_11[obj, props, A, B, C, D, E, F, G, H, I, J, K]): Molecule_0_11[props, A, B, C, D, E, F, G, H, I, J, K] = macro MakeMolecule.from11attr[props, A, B, C, D, E, F, G, H, I, J, K]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L](dsl: NS_0_12[obj, props, A, B, C, D, E, F, G, H, I, J, K, L]): Molecule_0_12[props, A, B, C, D, E, F, G, H, I, J, K, L] = macro MakeMolecule.from12attr[props, A, B, C, D, E, F, G, H, I, J, K, L]
}

trait Molecule_Factory13 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
  implicit final def m[obj[_], props, A, B, C, D](dsl: NS_0_04[obj, props, A, B, C, D]): Molecule_0_04[props, A, B, C, D] = macro MakeMolecule.from4attr[props, A, B, C, D]
  implicit final def m[obj[_], props, A, B, C, D, E](dsl: NS_0_05[obj, props, A, B, C, D, E]): Molecule_0_05[props, A, B, C, D, E] = macro MakeMolecule.from5attr[props, A, B, C, D, E]
  implicit final def m[obj[_], props, A, B, C, D, E, F](dsl: NS_0_06[obj, props, A, B, C, D, E, F]): Molecule_0_06[props, A, B, C, D, E, F] = macro MakeMolecule.from6attr[props, A, B, C, D, E, F]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G](dsl: NS_0_07[obj, props, A, B, C, D, E, F, G]): Molecule_0_07[props, A, B, C, D, E, F, G] = macro MakeMolecule.from7attr[props, A, B, C, D, E, F, G]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H](dsl: NS_0_08[obj, props, A, B, C, D, E, F, G, H]): Molecule_0_08[props, A, B, C, D, E, F, G, H] = macro MakeMolecule.from8attr[props, A, B, C, D, E, F, G, H]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I](dsl: NS_0_09[obj, props, A, B, C, D, E, F, G, H, I]): Molecule_0_09[props, A, B, C, D, E, F, G, H, I] = macro MakeMolecule.from9attr[props, A, B, C, D, E, F, G, H, I]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J](dsl: NS_0_10[obj, props, A, B, C, D, E, F, G, H, I, J]): Molecule_0_10[props, A, B, C, D, E, F, G, H, I, J] = macro MakeMolecule.from10attr[props, A, B, C, D, E, F, G, H, I, J]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K](dsl: NS_0_11[obj, props, A, B, C, D, E, F, G, H, I, J, K]): Molecule_0_11[props, A, B, C, D, E, F, G, H, I, J, K] = macro MakeMolecule.from11attr[props, A, B, C, D, E, F, G, H, I, J, K]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L](dsl: NS_0_12[obj, props, A, B, C, D, E, F, G, H, I, J, K, L]): Molecule_0_12[props, A, B, C, D, E, F, G, H, I, J, K, L] = macro MakeMolecule.from12attr[props, A, B, C, D, E, F, G, H, I, J, K, L]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M](dsl: NS_0_13[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M]): Molecule_0_13[props, A, B, C, D, E, F, G, H, I, J, K, L, M] = macro MakeMolecule.from13attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M]
}

trait Molecule_Factory14 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
  implicit final def m[obj[_], props, A, B, C, D](dsl: NS_0_04[obj, props, A, B, C, D]): Molecule_0_04[props, A, B, C, D] = macro MakeMolecule.from4attr[props, A, B, C, D]
  implicit final def m[obj[_], props, A, B, C, D, E](dsl: NS_0_05[obj, props, A, B, C, D, E]): Molecule_0_05[props, A, B, C, D, E] = macro MakeMolecule.from5attr[props, A, B, C, D, E]
  implicit final def m[obj[_], props, A, B, C, D, E, F](dsl: NS_0_06[obj, props, A, B, C, D, E, F]): Molecule_0_06[props, A, B, C, D, E, F] = macro MakeMolecule.from6attr[props, A, B, C, D, E, F]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G](dsl: NS_0_07[obj, props, A, B, C, D, E, F, G]): Molecule_0_07[props, A, B, C, D, E, F, G] = macro MakeMolecule.from7attr[props, A, B, C, D, E, F, G]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H](dsl: NS_0_08[obj, props, A, B, C, D, E, F, G, H]): Molecule_0_08[props, A, B, C, D, E, F, G, H] = macro MakeMolecule.from8attr[props, A, B, C, D, E, F, G, H]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I](dsl: NS_0_09[obj, props, A, B, C, D, E, F, G, H, I]): Molecule_0_09[props, A, B, C, D, E, F, G, H, I] = macro MakeMolecule.from9attr[props, A, B, C, D, E, F, G, H, I]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J](dsl: NS_0_10[obj, props, A, B, C, D, E, F, G, H, I, J]): Molecule_0_10[props, A, B, C, D, E, F, G, H, I, J] = macro MakeMolecule.from10attr[props, A, B, C, D, E, F, G, H, I, J]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K](dsl: NS_0_11[obj, props, A, B, C, D, E, F, G, H, I, J, K]): Molecule_0_11[props, A, B, C, D, E, F, G, H, I, J, K] = macro MakeMolecule.from11attr[props, A, B, C, D, E, F, G, H, I, J, K]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L](dsl: NS_0_12[obj, props, A, B, C, D, E, F, G, H, I, J, K, L]): Molecule_0_12[props, A, B, C, D, E, F, G, H, I, J, K, L] = macro MakeMolecule.from12attr[props, A, B, C, D, E, F, G, H, I, J, K, L]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M](dsl: NS_0_13[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M]): Molecule_0_13[props, A, B, C, D, E, F, G, H, I, J, K, L, M] = macro MakeMolecule.from13attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N](dsl: NS_0_14[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N]): Molecule_0_14[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = macro MakeMolecule.from14attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
}

trait Molecule_Factory15 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
  implicit final def m[obj[_], props, A, B, C, D](dsl: NS_0_04[obj, props, A, B, C, D]): Molecule_0_04[props, A, B, C, D] = macro MakeMolecule.from4attr[props, A, B, C, D]
  implicit final def m[obj[_], props, A, B, C, D, E](dsl: NS_0_05[obj, props, A, B, C, D, E]): Molecule_0_05[props, A, B, C, D, E] = macro MakeMolecule.from5attr[props, A, B, C, D, E]
  implicit final def m[obj[_], props, A, B, C, D, E, F](dsl: NS_0_06[obj, props, A, B, C, D, E, F]): Molecule_0_06[props, A, B, C, D, E, F] = macro MakeMolecule.from6attr[props, A, B, C, D, E, F]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G](dsl: NS_0_07[obj, props, A, B, C, D, E, F, G]): Molecule_0_07[props, A, B, C, D, E, F, G] = macro MakeMolecule.from7attr[props, A, B, C, D, E, F, G]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H](dsl: NS_0_08[obj, props, A, B, C, D, E, F, G, H]): Molecule_0_08[props, A, B, C, D, E, F, G, H] = macro MakeMolecule.from8attr[props, A, B, C, D, E, F, G, H]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I](dsl: NS_0_09[obj, props, A, B, C, D, E, F, G, H, I]): Molecule_0_09[props, A, B, C, D, E, F, G, H, I] = macro MakeMolecule.from9attr[props, A, B, C, D, E, F, G, H, I]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J](dsl: NS_0_10[obj, props, A, B, C, D, E, F, G, H, I, J]): Molecule_0_10[props, A, B, C, D, E, F, G, H, I, J] = macro MakeMolecule.from10attr[props, A, B, C, D, E, F, G, H, I, J]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K](dsl: NS_0_11[obj, props, A, B, C, D, E, F, G, H, I, J, K]): Molecule_0_11[props, A, B, C, D, E, F, G, H, I, J, K] = macro MakeMolecule.from11attr[props, A, B, C, D, E, F, G, H, I, J, K]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L](dsl: NS_0_12[obj, props, A, B, C, D, E, F, G, H, I, J, K, L]): Molecule_0_12[props, A, B, C, D, E, F, G, H, I, J, K, L] = macro MakeMolecule.from12attr[props, A, B, C, D, E, F, G, H, I, J, K, L]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M](dsl: NS_0_13[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M]): Molecule_0_13[props, A, B, C, D, E, F, G, H, I, J, K, L, M] = macro MakeMolecule.from13attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N](dsl: NS_0_14[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N]): Molecule_0_14[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = macro MakeMolecule.from14attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](dsl: NS_0_15[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]): Molecule_0_15[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = macro MakeMolecule.from15attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
}

trait Molecule_Factory16 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
  implicit final def m[obj[_], props, A, B, C, D](dsl: NS_0_04[obj, props, A, B, C, D]): Molecule_0_04[props, A, B, C, D] = macro MakeMolecule.from4attr[props, A, B, C, D]
  implicit final def m[obj[_], props, A, B, C, D, E](dsl: NS_0_05[obj, props, A, B, C, D, E]): Molecule_0_05[props, A, B, C, D, E] = macro MakeMolecule.from5attr[props, A, B, C, D, E]
  implicit final def m[obj[_], props, A, B, C, D, E, F](dsl: NS_0_06[obj, props, A, B, C, D, E, F]): Molecule_0_06[props, A, B, C, D, E, F] = macro MakeMolecule.from6attr[props, A, B, C, D, E, F]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G](dsl: NS_0_07[obj, props, A, B, C, D, E, F, G]): Molecule_0_07[props, A, B, C, D, E, F, G] = macro MakeMolecule.from7attr[props, A, B, C, D, E, F, G]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H](dsl: NS_0_08[obj, props, A, B, C, D, E, F, G, H]): Molecule_0_08[props, A, B, C, D, E, F, G, H] = macro MakeMolecule.from8attr[props, A, B, C, D, E, F, G, H]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I](dsl: NS_0_09[obj, props, A, B, C, D, E, F, G, H, I]): Molecule_0_09[props, A, B, C, D, E, F, G, H, I] = macro MakeMolecule.from9attr[props, A, B, C, D, E, F, G, H, I]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J](dsl: NS_0_10[obj, props, A, B, C, D, E, F, G, H, I, J]): Molecule_0_10[props, A, B, C, D, E, F, G, H, I, J] = macro MakeMolecule.from10attr[props, A, B, C, D, E, F, G, H, I, J]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K](dsl: NS_0_11[obj, props, A, B, C, D, E, F, G, H, I, J, K]): Molecule_0_11[props, A, B, C, D, E, F, G, H, I, J, K] = macro MakeMolecule.from11attr[props, A, B, C, D, E, F, G, H, I, J, K]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L](dsl: NS_0_12[obj, props, A, B, C, D, E, F, G, H, I, J, K, L]): Molecule_0_12[props, A, B, C, D, E, F, G, H, I, J, K, L] = macro MakeMolecule.from12attr[props, A, B, C, D, E, F, G, H, I, J, K, L]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M](dsl: NS_0_13[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M]): Molecule_0_13[props, A, B, C, D, E, F, G, H, I, J, K, L, M] = macro MakeMolecule.from13attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N](dsl: NS_0_14[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N]): Molecule_0_14[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = macro MakeMolecule.from14attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](dsl: NS_0_15[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]): Molecule_0_15[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = macro MakeMolecule.from15attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](dsl: NS_0_16[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]): Molecule_0_16[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = macro MakeMolecule.from16attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
}

trait Molecule_Factory17 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
  implicit final def m[obj[_], props, A, B, C, D](dsl: NS_0_04[obj, props, A, B, C, D]): Molecule_0_04[props, A, B, C, D] = macro MakeMolecule.from4attr[props, A, B, C, D]
  implicit final def m[obj[_], props, A, B, C, D, E](dsl: NS_0_05[obj, props, A, B, C, D, E]): Molecule_0_05[props, A, B, C, D, E] = macro MakeMolecule.from5attr[props, A, B, C, D, E]
  implicit final def m[obj[_], props, A, B, C, D, E, F](dsl: NS_0_06[obj, props, A, B, C, D, E, F]): Molecule_0_06[props, A, B, C, D, E, F] = macro MakeMolecule.from6attr[props, A, B, C, D, E, F]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G](dsl: NS_0_07[obj, props, A, B, C, D, E, F, G]): Molecule_0_07[props, A, B, C, D, E, F, G] = macro MakeMolecule.from7attr[props, A, B, C, D, E, F, G]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H](dsl: NS_0_08[obj, props, A, B, C, D, E, F, G, H]): Molecule_0_08[props, A, B, C, D, E, F, G, H] = macro MakeMolecule.from8attr[props, A, B, C, D, E, F, G, H]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I](dsl: NS_0_09[obj, props, A, B, C, D, E, F, G, H, I]): Molecule_0_09[props, A, B, C, D, E, F, G, H, I] = macro MakeMolecule.from9attr[props, A, B, C, D, E, F, G, H, I]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J](dsl: NS_0_10[obj, props, A, B, C, D, E, F, G, H, I, J]): Molecule_0_10[props, A, B, C, D, E, F, G, H, I, J] = macro MakeMolecule.from10attr[props, A, B, C, D, E, F, G, H, I, J]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K](dsl: NS_0_11[obj, props, A, B, C, D, E, F, G, H, I, J, K]): Molecule_0_11[props, A, B, C, D, E, F, G, H, I, J, K] = macro MakeMolecule.from11attr[props, A, B, C, D, E, F, G, H, I, J, K]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L](dsl: NS_0_12[obj, props, A, B, C, D, E, F, G, H, I, J, K, L]): Molecule_0_12[props, A, B, C, D, E, F, G, H, I, J, K, L] = macro MakeMolecule.from12attr[props, A, B, C, D, E, F, G, H, I, J, K, L]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M](dsl: NS_0_13[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M]): Molecule_0_13[props, A, B, C, D, E, F, G, H, I, J, K, L, M] = macro MakeMolecule.from13attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N](dsl: NS_0_14[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N]): Molecule_0_14[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = macro MakeMolecule.from14attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](dsl: NS_0_15[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]): Molecule_0_15[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = macro MakeMolecule.from15attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](dsl: NS_0_16[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]): Molecule_0_16[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = macro MakeMolecule.from16attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](dsl: NS_0_17[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]): Molecule_0_17[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = macro MakeMolecule.from17attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
}

trait Molecule_Factory18 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
  implicit final def m[obj[_], props, A, B, C, D](dsl: NS_0_04[obj, props, A, B, C, D]): Molecule_0_04[props, A, B, C, D] = macro MakeMolecule.from4attr[props, A, B, C, D]
  implicit final def m[obj[_], props, A, B, C, D, E](dsl: NS_0_05[obj, props, A, B, C, D, E]): Molecule_0_05[props, A, B, C, D, E] = macro MakeMolecule.from5attr[props, A, B, C, D, E]
  implicit final def m[obj[_], props, A, B, C, D, E, F](dsl: NS_0_06[obj, props, A, B, C, D, E, F]): Molecule_0_06[props, A, B, C, D, E, F] = macro MakeMolecule.from6attr[props, A, B, C, D, E, F]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G](dsl: NS_0_07[obj, props, A, B, C, D, E, F, G]): Molecule_0_07[props, A, B, C, D, E, F, G] = macro MakeMolecule.from7attr[props, A, B, C, D, E, F, G]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H](dsl: NS_0_08[obj, props, A, B, C, D, E, F, G, H]): Molecule_0_08[props, A, B, C, D, E, F, G, H] = macro MakeMolecule.from8attr[props, A, B, C, D, E, F, G, H]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I](dsl: NS_0_09[obj, props, A, B, C, D, E, F, G, H, I]): Molecule_0_09[props, A, B, C, D, E, F, G, H, I] = macro MakeMolecule.from9attr[props, A, B, C, D, E, F, G, H, I]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J](dsl: NS_0_10[obj, props, A, B, C, D, E, F, G, H, I, J]): Molecule_0_10[props, A, B, C, D, E, F, G, H, I, J] = macro MakeMolecule.from10attr[props, A, B, C, D, E, F, G, H, I, J]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K](dsl: NS_0_11[obj, props, A, B, C, D, E, F, G, H, I, J, K]): Molecule_0_11[props, A, B, C, D, E, F, G, H, I, J, K] = macro MakeMolecule.from11attr[props, A, B, C, D, E, F, G, H, I, J, K]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L](dsl: NS_0_12[obj, props, A, B, C, D, E, F, G, H, I, J, K, L]): Molecule_0_12[props, A, B, C, D, E, F, G, H, I, J, K, L] = macro MakeMolecule.from12attr[props, A, B, C, D, E, F, G, H, I, J, K, L]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M](dsl: NS_0_13[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M]): Molecule_0_13[props, A, B, C, D, E, F, G, H, I, J, K, L, M] = macro MakeMolecule.from13attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N](dsl: NS_0_14[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N]): Molecule_0_14[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = macro MakeMolecule.from14attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](dsl: NS_0_15[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]): Molecule_0_15[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = macro MakeMolecule.from15attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](dsl: NS_0_16[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]): Molecule_0_16[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = macro MakeMolecule.from16attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](dsl: NS_0_17[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]): Molecule_0_17[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = macro MakeMolecule.from17attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](dsl: NS_0_18[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]): Molecule_0_18[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = macro MakeMolecule.from18attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
}

trait Molecule_Factory19 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
  implicit final def m[obj[_], props, A, B, C, D](dsl: NS_0_04[obj, props, A, B, C, D]): Molecule_0_04[props, A, B, C, D] = macro MakeMolecule.from4attr[props, A, B, C, D]
  implicit final def m[obj[_], props, A, B, C, D, E](dsl: NS_0_05[obj, props, A, B, C, D, E]): Molecule_0_05[props, A, B, C, D, E] = macro MakeMolecule.from5attr[props, A, B, C, D, E]
  implicit final def m[obj[_], props, A, B, C, D, E, F](dsl: NS_0_06[obj, props, A, B, C, D, E, F]): Molecule_0_06[props, A, B, C, D, E, F] = macro MakeMolecule.from6attr[props, A, B, C, D, E, F]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G](dsl: NS_0_07[obj, props, A, B, C, D, E, F, G]): Molecule_0_07[props, A, B, C, D, E, F, G] = macro MakeMolecule.from7attr[props, A, B, C, D, E, F, G]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H](dsl: NS_0_08[obj, props, A, B, C, D, E, F, G, H]): Molecule_0_08[props, A, B, C, D, E, F, G, H] = macro MakeMolecule.from8attr[props, A, B, C, D, E, F, G, H]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I](dsl: NS_0_09[obj, props, A, B, C, D, E, F, G, H, I]): Molecule_0_09[props, A, B, C, D, E, F, G, H, I] = macro MakeMolecule.from9attr[props, A, B, C, D, E, F, G, H, I]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J](dsl: NS_0_10[obj, props, A, B, C, D, E, F, G, H, I, J]): Molecule_0_10[props, A, B, C, D, E, F, G, H, I, J] = macro MakeMolecule.from10attr[props, A, B, C, D, E, F, G, H, I, J]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K](dsl: NS_0_11[obj, props, A, B, C, D, E, F, G, H, I, J, K]): Molecule_0_11[props, A, B, C, D, E, F, G, H, I, J, K] = macro MakeMolecule.from11attr[props, A, B, C, D, E, F, G, H, I, J, K]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L](dsl: NS_0_12[obj, props, A, B, C, D, E, F, G, H, I, J, K, L]): Molecule_0_12[props, A, B, C, D, E, F, G, H, I, J, K, L] = macro MakeMolecule.from12attr[props, A, B, C, D, E, F, G, H, I, J, K, L]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M](dsl: NS_0_13[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M]): Molecule_0_13[props, A, B, C, D, E, F, G, H, I, J, K, L, M] = macro MakeMolecule.from13attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N](dsl: NS_0_14[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N]): Molecule_0_14[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = macro MakeMolecule.from14attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](dsl: NS_0_15[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]): Molecule_0_15[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = macro MakeMolecule.from15attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](dsl: NS_0_16[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]): Molecule_0_16[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = macro MakeMolecule.from16attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](dsl: NS_0_17[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]): Molecule_0_17[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = macro MakeMolecule.from17attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](dsl: NS_0_18[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]): Molecule_0_18[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = macro MakeMolecule.from18attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](dsl: NS_0_19[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]): Molecule_0_19[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = macro MakeMolecule.from19attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
}

trait Molecule_Factory20 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
  implicit final def m[obj[_], props, A, B, C, D](dsl: NS_0_04[obj, props, A, B, C, D]): Molecule_0_04[props, A, B, C, D] = macro MakeMolecule.from4attr[props, A, B, C, D]
  implicit final def m[obj[_], props, A, B, C, D, E](dsl: NS_0_05[obj, props, A, B, C, D, E]): Molecule_0_05[props, A, B, C, D, E] = macro MakeMolecule.from5attr[props, A, B, C, D, E]
  implicit final def m[obj[_], props, A, B, C, D, E, F](dsl: NS_0_06[obj, props, A, B, C, D, E, F]): Molecule_0_06[props, A, B, C, D, E, F] = macro MakeMolecule.from6attr[props, A, B, C, D, E, F]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G](dsl: NS_0_07[obj, props, A, B, C, D, E, F, G]): Molecule_0_07[props, A, B, C, D, E, F, G] = macro MakeMolecule.from7attr[props, A, B, C, D, E, F, G]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H](dsl: NS_0_08[obj, props, A, B, C, D, E, F, G, H]): Molecule_0_08[props, A, B, C, D, E, F, G, H] = macro MakeMolecule.from8attr[props, A, B, C, D, E, F, G, H]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I](dsl: NS_0_09[obj, props, A, B, C, D, E, F, G, H, I]): Molecule_0_09[props, A, B, C, D, E, F, G, H, I] = macro MakeMolecule.from9attr[props, A, B, C, D, E, F, G, H, I]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J](dsl: NS_0_10[obj, props, A, B, C, D, E, F, G, H, I, J]): Molecule_0_10[props, A, B, C, D, E, F, G, H, I, J] = macro MakeMolecule.from10attr[props, A, B, C, D, E, F, G, H, I, J]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K](dsl: NS_0_11[obj, props, A, B, C, D, E, F, G, H, I, J, K]): Molecule_0_11[props, A, B, C, D, E, F, G, H, I, J, K] = macro MakeMolecule.from11attr[props, A, B, C, D, E, F, G, H, I, J, K]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L](dsl: NS_0_12[obj, props, A, B, C, D, E, F, G, H, I, J, K, L]): Molecule_0_12[props, A, B, C, D, E, F, G, H, I, J, K, L] = macro MakeMolecule.from12attr[props, A, B, C, D, E, F, G, H, I, J, K, L]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M](dsl: NS_0_13[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M]): Molecule_0_13[props, A, B, C, D, E, F, G, H, I, J, K, L, M] = macro MakeMolecule.from13attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N](dsl: NS_0_14[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N]): Molecule_0_14[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = macro MakeMolecule.from14attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](dsl: NS_0_15[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]): Molecule_0_15[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = macro MakeMolecule.from15attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](dsl: NS_0_16[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]): Molecule_0_16[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = macro MakeMolecule.from16attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](dsl: NS_0_17[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]): Molecule_0_17[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = macro MakeMolecule.from17attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](dsl: NS_0_18[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]): Molecule_0_18[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = macro MakeMolecule.from18attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](dsl: NS_0_19[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]): Molecule_0_19[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = macro MakeMolecule.from19attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](dsl: NS_0_20[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]): Molecule_0_20[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = macro MakeMolecule.from20attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
}

trait Molecule_Factory21 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
  implicit final def m[obj[_], props, A, B, C, D](dsl: NS_0_04[obj, props, A, B, C, D]): Molecule_0_04[props, A, B, C, D] = macro MakeMolecule.from4attr[props, A, B, C, D]
  implicit final def m[obj[_], props, A, B, C, D, E](dsl: NS_0_05[obj, props, A, B, C, D, E]): Molecule_0_05[props, A, B, C, D, E] = macro MakeMolecule.from5attr[props, A, B, C, D, E]
  implicit final def m[obj[_], props, A, B, C, D, E, F](dsl: NS_0_06[obj, props, A, B, C, D, E, F]): Molecule_0_06[props, A, B, C, D, E, F] = macro MakeMolecule.from6attr[props, A, B, C, D, E, F]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G](dsl: NS_0_07[obj, props, A, B, C, D, E, F, G]): Molecule_0_07[props, A, B, C, D, E, F, G] = macro MakeMolecule.from7attr[props, A, B, C, D, E, F, G]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H](dsl: NS_0_08[obj, props, A, B, C, D, E, F, G, H]): Molecule_0_08[props, A, B, C, D, E, F, G, H] = macro MakeMolecule.from8attr[props, A, B, C, D, E, F, G, H]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I](dsl: NS_0_09[obj, props, A, B, C, D, E, F, G, H, I]): Molecule_0_09[props, A, B, C, D, E, F, G, H, I] = macro MakeMolecule.from9attr[props, A, B, C, D, E, F, G, H, I]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J](dsl: NS_0_10[obj, props, A, B, C, D, E, F, G, H, I, J]): Molecule_0_10[props, A, B, C, D, E, F, G, H, I, J] = macro MakeMolecule.from10attr[props, A, B, C, D, E, F, G, H, I, J]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K](dsl: NS_0_11[obj, props, A, B, C, D, E, F, G, H, I, J, K]): Molecule_0_11[props, A, B, C, D, E, F, G, H, I, J, K] = macro MakeMolecule.from11attr[props, A, B, C, D, E, F, G, H, I, J, K]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L](dsl: NS_0_12[obj, props, A, B, C, D, E, F, G, H, I, J, K, L]): Molecule_0_12[props, A, B, C, D, E, F, G, H, I, J, K, L] = macro MakeMolecule.from12attr[props, A, B, C, D, E, F, G, H, I, J, K, L]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M](dsl: NS_0_13[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M]): Molecule_0_13[props, A, B, C, D, E, F, G, H, I, J, K, L, M] = macro MakeMolecule.from13attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N](dsl: NS_0_14[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N]): Molecule_0_14[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = macro MakeMolecule.from14attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](dsl: NS_0_15[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]): Molecule_0_15[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = macro MakeMolecule.from15attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](dsl: NS_0_16[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]): Molecule_0_16[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = macro MakeMolecule.from16attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](dsl: NS_0_17[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]): Molecule_0_17[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = macro MakeMolecule.from17attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](dsl: NS_0_18[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]): Molecule_0_18[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = macro MakeMolecule.from18attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](dsl: NS_0_19[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]): Molecule_0_19[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = macro MakeMolecule.from19attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](dsl: NS_0_20[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]): Molecule_0_20[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = macro MakeMolecule.from20attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](dsl: NS_0_21[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]): Molecule_0_21[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = macro MakeMolecule.from21attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
}

trait Molecule_Factory22 {
  implicit final def m[obj[_], props, A](dsl: NS_0_01[obj, props, A]): Molecule_0_01[props, A] = macro MakeMolecule.from1attr[props, A]
  implicit final def m[obj[_], props, A, B](dsl: NS_0_02[obj, props, A, B]): Molecule_0_02[props, A, B] = macro MakeMolecule.from2attr[props, A, B]
  implicit final def m[obj[_], props, A, B, C](dsl: NS_0_03[obj, props, A, B, C]): Molecule_0_03[props, A, B, C] = macro MakeMolecule.from3attr[props, A, B, C]
  implicit final def m[obj[_], props, A, B, C, D](dsl: NS_0_04[obj, props, A, B, C, D]): Molecule_0_04[props, A, B, C, D] = macro MakeMolecule.from4attr[props, A, B, C, D]
  implicit final def m[obj[_], props, A, B, C, D, E](dsl: NS_0_05[obj, props, A, B, C, D, E]): Molecule_0_05[props, A, B, C, D, E] = macro MakeMolecule.from5attr[props, A, B, C, D, E]
  implicit final def m[obj[_], props, A, B, C, D, E, F](dsl: NS_0_06[obj, props, A, B, C, D, E, F]): Molecule_0_06[props, A, B, C, D, E, F] = macro MakeMolecule.from6attr[props, A, B, C, D, E, F]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G](dsl: NS_0_07[obj, props, A, B, C, D, E, F, G]): Molecule_0_07[props, A, B, C, D, E, F, G] = macro MakeMolecule.from7attr[props, A, B, C, D, E, F, G]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H](dsl: NS_0_08[obj, props, A, B, C, D, E, F, G, H]): Molecule_0_08[props, A, B, C, D, E, F, G, H] = macro MakeMolecule.from8attr[props, A, B, C, D, E, F, G, H]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I](dsl: NS_0_09[obj, props, A, B, C, D, E, F, G, H, I]): Molecule_0_09[props, A, B, C, D, E, F, G, H, I] = macro MakeMolecule.from9attr[props, A, B, C, D, E, F, G, H, I]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J](dsl: NS_0_10[obj, props, A, B, C, D, E, F, G, H, I, J]): Molecule_0_10[props, A, B, C, D, E, F, G, H, I, J] = macro MakeMolecule.from10attr[props, A, B, C, D, E, F, G, H, I, J]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K](dsl: NS_0_11[obj, props, A, B, C, D, E, F, G, H, I, J, K]): Molecule_0_11[props, A, B, C, D, E, F, G, H, I, J, K] = macro MakeMolecule.from11attr[props, A, B, C, D, E, F, G, H, I, J, K]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L](dsl: NS_0_12[obj, props, A, B, C, D, E, F, G, H, I, J, K, L]): Molecule_0_12[props, A, B, C, D, E, F, G, H, I, J, K, L] = macro MakeMolecule.from12attr[props, A, B, C, D, E, F, G, H, I, J, K, L]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M](dsl: NS_0_13[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M]): Molecule_0_13[props, A, B, C, D, E, F, G, H, I, J, K, L, M] = macro MakeMolecule.from13attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N](dsl: NS_0_14[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N]): Molecule_0_14[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N] = macro MakeMolecule.from14attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](dsl: NS_0_15[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]): Molecule_0_15[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O] = macro MakeMolecule.from15attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](dsl: NS_0_16[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]): Molecule_0_16[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P] = macro MakeMolecule.from16attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q](dsl: NS_0_17[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]): Molecule_0_17[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q] = macro MakeMolecule.from17attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R](dsl: NS_0_18[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]): Molecule_0_18[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R] = macro MakeMolecule.from18attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S](dsl: NS_0_19[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]): Molecule_0_19[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S] = macro MakeMolecule.from19attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T](dsl: NS_0_20[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]): Molecule_0_20[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T] = macro MakeMolecule.from20attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U](dsl: NS_0_21[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]): Molecule_0_21[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U] = macro MakeMolecule.from21attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U]
  implicit final def m[obj[_], props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V](dsl: NS_0_22[obj, props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]): Molecule_0_22[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V] = macro MakeMolecule.from22attr[props, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V]
}
