package molecule.manipulation.updateMap

import molecule._
import molecule.util.dsl.coreTest._
import molecule.util.{CoreSetup, CoreSpec, expectCompileError}

class UpdateMapBoolean extends CoreSpec {


  "Mapped variables" >> {

    "add" in new CoreSetup {

      val eid = Ns.boolMap(str1 -> bool1).save.eid

      // Add pair
      Ns(eid).boolMap.add(str2 -> bool3).update
      Ns.boolMap.one === Map(str1 -> bool1, str2 -> bool3)

      // Add pair at existing key - replaces the value (not the key)
      Ns(eid).boolMap.add(str2 -> bool2).update
      Ns.boolMap.one === Map(str1 -> bool1, str2 -> bool2)

      // Add multiple pairs (vararg)
      Ns(eid).boolMap.add(str3 -> bool3, str4 -> bool4).update
      Ns.boolMap.one === Map(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4)

      // Add Map of pairs. Existing identical pairs (key and value the same) unaffected
      Ns(eid).boolMap.add(Seq(str4 -> bool4, str5 -> bool5)).update
      Ns.boolMap.one === Map(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4, str5 -> bool5)

      // Add empty Map of pair (no effect)
      Ns(eid).boolMap.add(Seq[(String, Boolean)]()).update
      Ns.boolMap.one === Map(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4, str5 -> bool5)


      // Can't add pairs with duplicate keys

      // vararg
      expectCompileError(
        """Ns(eid).boolMap.add(str1 -> bool1, str1 -> bool2).update""",
        "[Dsl2Model:apply (14)] Can't add multiple key/value pairs with the same key for attribute `:ns/boolMap`:" +
          "\n__ident__str1 -> __ident__bool1" +
          "\n__ident__str1 -> __ident__bool2")

      // Seq
      expectCompileError(
        """Ns(eid).boolMap.add(Seq(str1 -> bool1, str1 -> bool2)).update""",
        "[Dsl2Model:apply (14)] Can't add multiple key/value pairs with the same key for attribute `:ns/boolMap`:" +
          "\n__ident__str1 -> __ident__bool1" +
          "\n__ident__str1 -> __ident__bool2")

      // If duplicate values are added with non-equally-named variables we can still catch them at runtime
      val str1x = str1

      // vararg
      (Ns(eid).boolMap.add(str1 -> bool1, str1x -> bool2).update must throwA[IllegalArgumentException])
        .message === "Got the exception java.lang.IllegalArgumentException: " +
        "[molecule.transform.Model2Transaction.valueStmts:default]  Can't add multiple key/value pairs with the same key for attribute `:ns/boolMap`:" +
        "\na -> true" +
        "\na -> false"


      // Seq
      (Ns(eid).boolMap.add(Seq(str1 -> bool1, str1x -> bool2)).update must throwA[IllegalArgumentException])
        .message === "Got the exception java.lang.IllegalArgumentException: " +
        "[molecule.transform.Model2Transaction.valueStmts:default]  Can't add multiple key/value pairs with the same key for attribute `:ns/boolMap`:" +
        "\na -> true" +
        "\na -> false"
    }


    "replace" in new CoreSetup {

      val eid = Ns.boolMap(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4, str5 -> bool5, str6 -> bool6).save.eid

      // Replace value
      Ns(eid).boolMap.replace(str6 -> bool8).update
      Ns.boolMap.one.toList.sorted === List(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4, str5 -> bool5, str6 -> bool8)

      // Replace value to existing value at another key is ok
      Ns(eid).boolMap.replace(str5 -> bool8).update
      Ns.boolMap.one.toList.sorted === List(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4, str5 -> bool8, str6 -> bool8)

      // Replace multiple values (vararg)
      Ns(eid).boolMap.replace(str3 -> bool6, str4 -> bool7).update
      Ns.boolMap.one.toList.sorted === List(str1 -> bool1, str2 -> bool2, str3 -> bool6, str4 -> bool7, str5 -> bool8, str6 -> bool8)

      // Missing old value has no effect. The new value is inserted (upsert semantics)
      Ns(eid).boolMap.replace(str3 -> bool6, str4 -> bool7).update
      Ns.boolMap.one.toList.sorted === List(str1 -> bool1, str2 -> bool2, str3 -> bool6, str4 -> bool7, str5 -> bool8, str6 -> bool8)

      // Replace with Seq of key/newValue pairs
      Ns(eid).boolMap.replace(Seq(str2 -> bool5)).update
      Ns.boolMap.one.toList.sorted === List(str1 -> bool1, str2 -> bool5, str3 -> bool6, str4 -> bool7, str5 -> bool8, str6 -> bool8)

      // Replacing with empty Seq of key/newValue mapped values has no effect
      Ns(eid).boolMap.replace(Seq[(String, Boolean)]()).update
      Ns.boolMap.one.toList.sorted === List(str1 -> bool1, str2 -> bool5, str3 -> bool6, str4 -> bool7, str5 -> bool8, str6 -> bool8)


      // Can't replace pairs with duplicate keys

      expectCompileError(
        """Ns(eid).boolMap.replace(str1 -> bool1, str1 -> bool2).update""",
        "[Dsl2Model:apply (15)] Can't replace multiple key/value pairs with the same key for attribute `:ns/boolMap`:" +
          "\n__ident__str1 -> __ident__bool1" +
          "\n__ident__str1 -> __ident__bool2")

      expectCompileError(
        """Ns(eid).boolMap.replace(Seq(str1 -> bool1, str1 -> bool2)).update""",
        "[Dsl2Model:apply (15)] Can't replace multiple key/value pairs with the same key for attribute `:ns/boolMap`:" +
          "\n__ident__str1 -> __ident__bool1" +
          "\n__ident__str1 -> __ident__bool2")
    }


    "remove" in new CoreSetup {

      val eid = Ns.boolMap(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4, str5 -> bool5, str6 -> bool6).save.eid

      // Remove pair by key
      Ns(eid).boolMap.remove(str6).update
      Ns.boolMap.one.toList.sorted === List(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4, str5 -> bool5)

      // Removing pair by non-existing key has no effect
      Ns(eid).boolMap.remove(str7).update
      Ns.boolMap.one.toList.sorted === List(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4, str5 -> bool5)

      // Removing duplicate keys removes the distinct key
      Ns(eid).boolMap.remove(str5, str5).update
      Ns.boolMap.one.toList.sorted === List(str1 -> bool1, str2 -> bool2, str3 -> bool3, str4 -> bool4)

      // Remove pairs by multiple keys (vararg)
      Ns(eid).boolMap.remove(str3, str4).update
      Ns.boolMap.one.toList.sorted === List(str1 -> bool1, str2 -> bool2)

      // Remove pairs by Seq of keys
      Ns(eid).boolMap.remove(Seq(str2)).update
      Ns.boolMap.one.toList.sorted === List(str1 -> bool1)

      // Removing pairs by empty Seq of keys has no effect
      Ns(eid).boolMap.remove(Seq[String]()).update
      Ns.boolMap.one.toList.sorted === List(str1 -> bool1)
    }


    "apply" in new CoreSetup {

      val eid = Ns.boolMap(str1 -> bool1, str2 -> bool2).save.eid

      // Apply value (replaces all current values!)
      Ns(eid).boolMap(str1 -> bool1).update
      Ns.boolMap.one.toList.sorted === List(str1 -> bool1)

      // Apply multiple values (vararg)
      Ns(eid).boolMap(str2 -> bool2, str3 -> bool3).update
      Ns.boolMap.one.toList.sorted === List(str2 -> bool2, str3 -> bool3)

      // Apply Map of values
      Ns(eid).boolMap(Seq(str4 -> bool4)).update
      Ns.boolMap.one.toList.sorted === List(str4 -> bool4)

      // Apply empty Map of values (retracting all values!)
      Ns(eid).boolMap(Seq[(String, Boolean)]()).update
      Ns.boolMap.get === List()


      Ns(eid).boolMap(Seq(str1 -> bool1, str2 -> bool2)).update

      // Delete all (apply no values)
      Ns(eid).boolMap().update
      Ns.boolMap.get === List()


      // Can't apply pairs with duplicate keys
      (Ns(eid).longMap(str1 -> long1, str1 -> long2).update must throwA[IllegalArgumentException])
        .message === "Got the exception java.lang.IllegalArgumentException: " +
        "[molecule.transform.Model2Transaction.valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:ns/longMap`:" +
        "\na -> 1" +
        "\na -> 2"

      (Ns(eid).longMap(Seq(str1 -> long1, str1 -> long2)).update must throwA[IllegalArgumentException])
        .message === "Got the exception java.lang.IllegalArgumentException: " +
        "[molecule.transform.Model2Transaction.valueStmts:default]  Can't apply multiple key/value pairs with the same key for attribute `:ns/longMap`:" +
        "\na -> 1" +
        "\na -> 2"
    }
  }
}
