package molecule

import molecule.util.dsl.coreTest._
import molecule.util.{CoreSetup, CoreSpec, expectCompileError}


class OptionalValues extends CoreSpec {

  "Correct types returned (card-one)" >> {

    "String (no assertion)" in new CoreSetup {
      Ns.int.str.insert(1, "a")
      Ns.int.insert(2)

      // Int mandatory, String optional
      Ns.int.str$.get === List((1, Some("a")), (2, None))

      // Int and String mandatory
      Ns.int.str.get === List((1, "a"))
    }

    "String (optional assertion)" in new CoreSetup {
      Ns.int.str$ insert List((1, Some("a")), (2, None))

      Ns.int.str$.get === List((1, Some("a")), (2, None))
      Ns.int.str.get === List((1, "a"))
    }

    "Int" in new CoreSetup {
      Ns.str.int$ insert List(("a", Some(1)), ("b", None))

      Ns.str.int$.get === List(("a", Some(1)), ("b", None))
      Ns.str.int.get === List(("a", 1))
    }

    "Long" in new CoreSetup {
      Ns.int.long$ insert List((1, Some(3L)), (2, None))

      Ns.int.long$.get === List((1, Some(3L)), (2, None))
      Ns.int.long.get === List((1, 3L))
    }

    "Float" in new CoreSetup {
      Ns.int.float$ insert List((1, Some(3.0f)), (2, None))

      Ns.int.float$.get === List((1, Some(3.0f)), (2, None))
      Ns.int.float.get === List((1, 3.0f))
    }

    "Boolean" in new CoreSetup {
      Ns.int.bool$ insert List((1, Some(true)), (2, None))

      Ns.int.bool$.get === List((1, Some(true)), (2, None))
      Ns.int.bool.get === List((1, true))
    }

    "Date" in new CoreSetup {
      Ns.int.date$ insert List((1, Some(date1)), (2, None))

      Ns.int.date$.get === List((1, Some(date1)), (2, None))
      Ns.int.date.get === List((1, date1))
    }

    "UUID" in new CoreSetup {
      Ns.int.uuid$ insert List((1, Some(uuid1)), (2, None))

      Ns.int.uuid$.get === List((1, Some(uuid1)), (2, None))
      Ns.int.uuid.get === List((1, uuid1))
    }

    "URI" in new CoreSetup {
      Ns.int.uri$ insert List((1, Some(uri1)), (2, None))

      Ns.int.uri$.get === List((1, Some(uri1)), (2, None))
      Ns.int.uri.get === List((1, uri1))
    }

    "Enum" in new CoreSetup {
      Ns.int.enum$ insert List((1, Some("enum1")), (2, None))

      Ns.int.enum$.get === List((1, Some("enum1")), (2, None))
      Ns.int.enum.get === List((1, "enum1"))
    }

    "Ref Long" in new CoreSetup {
      Ns.int.ref1$ insert List((1, Some(3L)), (2, None))

      Ns.int.ref1$.get === List((1, Some(3L)), (2, None))
      Ns.int.ref1.get === List((1, 3L))
    }
  }


  "Correct types returned (card-many)" >> {

    "String (no assertion)" in new CoreSetup {
      Ns.int.strs.insert(1, Set("a", "b"))
      Ns.int.insert(2)

      Ns.int.strs$.get === List((1, Some(Set("a", "b"))), (2, None))
      Ns.int.strs.get === List((1, Set("a", "b")))
    }

    "String (empty Set asserted)" in new CoreSetup {
      Ns.int.strs.insert(1, Set("a", "b"))
      // No strings asserted from empty Set
      Ns.int.strs.insert(2, Set[String]())

      Ns.int.strs$.get === List((1, Some(Set("a", "b"))), (2, None))
      Ns.int.strs.get === List((1, Set("a", "b")))
    }

    "String (optional assertion)" in new CoreSetup {
      Ns.int.strs$ insert Seq((1, Some(Set("a", "b"))), (2, None))

      Ns.int.strs$.get === List((1, Some(Set("a", "b"))), (2, None))
      Ns.int.strs.get === List((1, Set("a", "b")))
    }

    "Int" in new CoreSetup {
      Ns.str.ints$ insert List(("a", Some(Set(1, 2))), ("b", None))

      Ns.str.ints$.get === List(("a", Some(Set(1, 2))), ("b", None))
      Ns.str.ints.get === List(("a", Set(1, 2)))
    }

    "Long" in new CoreSetup {
      Ns.int.longs$ insert Seq((1, Some(Set(3L, 4L))), (2, None))

      Ns.int.longs$.get === List((1, Some(Set(3L, 4L))), (2, None))
      Ns.int.longs.get === List((1, Set(3L, 4L)))
    }

    "Float" in new CoreSetup {
      Ns.int.floats$ insert Seq((1, Some(Set(3.0f, 4.0f))), (2, None))

      Ns.int.floats$.get === List((1, Some(Set(3.0f, 4.0f))), (2, None))
      Ns.int.floats.get === List((1, Set(3.0f, 4.0f)))
    }

    // (Boolean Sets not implemented)

    "Date" in new CoreSetup {
      Ns.int.dates$ insert Seq((1, Some(Set(date1, date2))), (2, None))

      Ns.int.dates$.get === List((1, Some(Set(date1, date2))), (2, None))
      Ns.int.dates.get === List((1, Set(date1, date2)))
    }

    "UUID" in new CoreSetup {
      Ns.int.uuids$ insert Seq((1, Some(Set(uuid1, uuid2))), (2, None))

      Ns.int.uuids$.get === List((1, Some(Set(uuid1, uuid2))), (2, None))
      Ns.int.uuids.get === List((1, Set(uuid1, uuid2)))
    }

    "URI" in new CoreSetup {
      Ns.int.uris$ insert Seq((1, Some(Set(uri1, uri2))), (2, None))

      Ns.int.uris$.get === List((1, Some(Set(uri1, uri2))), (2, None))
      Ns.int.uris.get === List((1, Set(uri1, uri2)))
    }

    "Enum" in new CoreSetup {
      Ns.int.enums$ insert Seq((1, Some(Set("enum1", "enum2"))), (2, None))

      Ns.int.enums$.get === List((1, Some(Set("enum1", "enum2"))), (2, None))
      Ns.int.enums.get === List((1, Set("enum1", "enum2")))
    }

    "Ref" in new CoreSetup {
      Ns.int.refs1$ insert Seq((1, Some(Set(3L, 4L))), (2, None))

      Ns.int.refs1$.get === List((1, Some(Set(3L, 4L))), (2, None))
      Ns.int.refs1.get === List((1, Set(3L, 4L)))
    }
  }



  "Various" >> {

    "Only optional attributes not allowed" in new CoreSetup {
      expectCompileError(
        """
              m(Ns.str$)
        """,
        """
          |[Dsl2Model:apply] Molecule is empty or has only meta/optional attributes. Please add one or more attributes.
          |Model(List(Atom(ns,str$,String,1,VarValue,None,List())))
        """)

      expectCompileError(
        """
              m(Ns.str$.int$)
        """,
        """
          |[Dsl2Model:apply] Molecule is empty or has only meta/optional attributes. Please add one or more attributes.
          |Model(List(Atom(ns,str$,String,1,VarValue,None,List()), Atom(ns,int$,Long,1,VarValue,None,List())))
        """)

      ok
    }

    "First attr can be optional" in new CoreSetup {
      Ns.str$.int insert List((Some("a"), 1), (None, 2))
      Ns.str$.int.get === List((Some("a"), 1), (None, 2))
    }

    "Ref attribute can be optional (1)" in new CoreSetup {
      Ns.str.Ref1.int1$ insert List(("a", Some(1)), ("b", None))

      // No ref from entity with "b"
      Ns.str.Ref1.int1$.get === List(("a", Some(1)))
    }

    "Ref attribute can be optional (2)" in new CoreSetup {
      Ns.str.Ref1.str1.int1$ insert List(("a", "a1", Some(1)), ("b", "b1", None))

      // Now there's a ref from entity with "b" to entity with "b1"
      Ns.str.Ref1.str1.int1$.get === List(("a", "a1", Some(1)), ("b", "b1", None))
    }

    "Nested attribute can be optional (1)" in new CoreSetup {
      // Throws runtime exception
      // m(Ns.str.Refs1 * Ref1.int1$) insert List(("a", List(Some(1))), ("b", List(None)))

      // Add None values as empty list
      m(Ns.str.Refs1 * Ref1.int1$) insert List(("a", List(Some(1))), ("b", List()))

      // No ref from entity with "b"
      m(Ns.str.Refs1 * Ref1.int1$).get === List(("a", List(Some(1))))
    }

    "Nested attribute can be optional (2)" in new CoreSetup {
      m(Ns.str.Refs1 * Ref1.str1.int1$) insert List(
        ("a", List(("a1", Some(1)))),
        ("b", List(("b1", None)))
      )

      // Now there's a ref from entity with "b" to entity with "b1"
      m(Ns.str.Refs1 * Ref1.str1.int1$).get === List(
        ("a", List(("a1", Some(1)))),
        ("b", List(("b1", None)))
      )
    }

  }


  // Todo:
  // "component"
  // "multiple optionals"



  //    "String (no assertion)" in new CoreSetup {
  //
  //      Ns.str.int$.long$ insert List(
  //        ("a", Some(1), Some(10L)),
  //        ("b", None, Some(20L)),
  //        ("c", Some(3), None)
  //      )
  //
  ///*
  //Query(
  //  Find(List(
  //    Var(b),
  //    Pull(a,ns,int,None),
  //    Pull(a,ns,long,None))),
  //  Where(List(
  //    DataClause(ImplDS,Var(a),KW(ns,str,),Var(b),Empty,NoBinding))))
  //
  ////        [:find  ?b (pull ?a [:ns/int]) (pull ?a [:ns/long])
  ////         :where [?a :ns/str ?b]]
  //
  //
  //*/
  //      Ns.str.int$.long$.debug
  //
  //      datomic.Peer.q(
  //        """
  //          |[:find  ?b (pull ?a [:ns/int :ns/long])
  //          | :where [?a :ns/str ?b]]
  //        """.stripMargin, conn.db) === 9
  //
  //
  //
  //
  //      // Todo: non-adjacent optionals shouldn't compile
  ////      Ns.str.int$.bool.long$.debug
  //
  //
  ////      Ns.str.int$.long$.get === List(
  ////        ("a", Some(1), Some(10L)),
  ////        ("b", None, Some(20L)),
  ////        ("c", Some(3), None)
  ////      )
  //
  ////      Ns.str.int$.Ref1.str1.int1$ insert List(
  ////        ("a", None, "a1", Some(10)),
  ////        ("b", Some(2), "b1", None)
  ////      )
  ////      Ns.str.int$.Ref1.int1$.debug
  ////      Ns.str.int$.Ref1.int1$.str1.debug
  ////      Ns.str.int$.Ref1.str1.int1$.debug
  ////      Ns.str.Ref1.int1$.debug
  //////      Ns.str.Ref1.int1$.get === List(
  //////      Ns.str.Ref1.int1_(nil).get === List(
  ////      Ns.str.get === List(
  ////        ("a", Some(1)),
  ////        ("b", None)
  ////      )
  //
  //
  //
  //
  ////      datomic.Peer.q(
  ////        """
  ////          |[:find  ?b (pull ?a [:ns/int :ns/long])
  ////          | :where [?a :ns/str ?b]]
  ////        """.stripMargin, conn.db) === 9
  //
  ////      Ns.str.int_.apply(nil)._query.datalog === 7
  ////      Ns.str.int_.apply(nil).get === 7
  //
  ////      Ns.str.int.>(1)._query.datalog === 7
  //    }

  // "Non-adjacent optionals in same namespace not allowed (shouldn't compile)"
  /*

[:find  ?b ?c
 :where [?a :ns/str ?b]
        [?a :ns/int ?c]
        [(.compareTo ^Long ?c 1) ?c2]
        [(> ?c2 0)]]

        [:find  ?b (pull ?a [:ns/int]) (pull ?a [:ns/long])
         :where [?a :ns/str ?b]]
  */
}