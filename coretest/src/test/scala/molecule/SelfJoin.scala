package molecule
import datomic.Peer
import molecule.util.dsl.coreTest._
import molecule.util.{CoreSetup, CoreSpec, expectCompileError}

class SelfJoin extends CoreSpec {


  class Setup extends CoreSetup {

    // age - name - beverage - rating
    m(Ns.int.str.Refs1 * Ref1.str1.int1) insert List(
      (23, "Joe", List(("Coffee", 3), ("Cola", 2), ("Pepsi", 3))),
      (25, "Ben", List(("Coffee", 2), ("Tea", 3))),
      (23, "Liz", List(("Coffee", 1), ("Tea", 3), ("Pepsi", 1))))
  }


  "All attributes unifying (AND notation)" in new Setup {

    // Normally we ask for values accross attributes like
    // attr1 AND attr2 AND etc as in
    // age==23 AND name AND rating==Pepsi
    Ns.int_(23).str.Refs1.str1_("Pepsi").get === List("Liz", "Joe")

    // But when we need to compare values of the same attribute
    // accross entities we need self-joins.

    // Here's an example of a self-join where we take pairs of entities
    // where one is 23 years old and the other 25 years old and then see
    // which of those pairs have a shared preferred beverage. We say that
    // we "unify" by the beverage attribute value (Refs1.str1) - the values
    // what the two entities have in common.

    // What beverages do 23- AND 25-year-olds like in common?
    // (unifying on Refs1.str1)
    Ns.int_(23 and 25).Refs1.str1.get === List("Coffee", "Tea")

    // Does 23- and 25-years-old have some common beverage ratings?
    // (unifying on Refs1.int1)
    Ns.int_(23 and 25).Refs1.int1.get === List(2, 3)

    // Any 23- and 25-year-olds with the same name? (no)
    // (unifying on Ns.str)
    Ns.int_(23 and 25).str.get === List()


    // Which beverages do Joe AND Liz both like?
    // (unifying on Refs1.str1)
    Ns.str_("Joe" and "Liz").Refs1.str1.get === List("Pepsi", "Coffee")

    // Do Joe AND Liz have some common ratings?
    // (unifying on Refs1.int1)
    Ns.str_("Joe" and "Liz").Refs1.int1.get === List(3)

    // Do Joe AND Liz have a shared age?
    // (unifying on Ns.int)
    Ns.str_("Joe" and "Liz").int.get === List(23)


    // Who likes both Coffee AND Tea?
    // (unifying on Ns.str)
    Ns.str.Refs1.str1_("Coffee" and "Tea").get === List("Ben", "Liz")

    // What ages have those who like both Coffe and Tea?
    // (unifying on Ns.int)
    Ns.int.Refs1.str1_("Coffee" and "Tea").get === List(23, 25)

    // What shared ratings do Coffee and Tea have?
    // (unifying on Ref1.int1)
    Ref1.str1_("Coffee" and "Tea").int1.get === List(3)


    // Who rated both 2 AND 3?
    // (unifying on Ns.str)
    Ns.str.Refs1.int1_(2 and 3).get === List("Ben", "Joe")

    // What ages have those who rated both 2 AND 3?
    // (unifying on Ns.int)
    Ns.int.Refs1.int1_(2 and 3).get === List(23, 25)

    // Which beverages are rated 2 AND 3?
    // (unifying on Refs1.str1)
    Ref1.int1_(2 and 3).str1.get === List("Coffee")


    // Unifying by 2 attributes

    // Which 23- AND 25-year-olds with the same name like the same beverage? (none)
    // (unifying on Ns.str AND Refs1.str1)
    Ns.int_(23 and 25).str.Refs1.str1.get === List()

    // Do Joe and Liz share age and beverage preferences? (yes)
    // (unifying on Ns.int AND Refs1.str1)
    Ns.int.str_("Joe" and "Liz").Refs1.str1.get === List(
      (23, "Coffee"),
      (23, "Pepsi"))


    // Multiple ANDs

    Ns.str_("Joe" and "Ben" and "Liz").Refs1.str1.get === List("Coffee")


    // We can't apply AND semantics to card-one attribute returning a value
    // since a card-one attribute naturally can't have multiple values.
    expectCompileError(
      """Ns.str("str1" and "str2").get""",
      "[Dsl2Model:apply (3)] Card-one attributes cannot return multiple values and thus not have AND semantics")
  }


  "Selective unifying" in new Setup {

    // We could have written the first example above with a more verbose
    // but more flexible notation, giving the same result:
    Ns.int_(23).Refs1.str1._Ns.Self
      .int_(25).Refs1.str1_(unify).get === List("Coffee", "Tea")

    // After asking for the beverage value (Refs1.str1) of the first entity
    // we "go back" to the initial namespace (Ns) and then say that we want
    // to make a self-join (with Self) to start defining the other entity.
    // When we define the beverage value for the second entity we tell molecule
    // to "unify" that value with the equivavelent beverage value of the
    // first entity.

    // This second notation gives us freedom to fetch more values that
    // shouldn't be unified. Say for instance that we want to know the names
    // of 23-/25-year-olds sharing a beverage preference:

    Ns.int_(23).str.Refs1.str1._Ns.Self
      .int_(25).str.Refs1.str1_(unify).get.sorted === List(
      ("Joe", "Coffee", "Ben"),
      ("Liz", "Coffee", "Ben"),
      ("Liz", "Tea", "Ben")
    )

    // Let's add the ratings too
    Ns.int_(23).str.Refs1.int1.str1._Ns.Self
      .int_(25).str.Refs1.str1_(unify).int1.get.sorted === List(
      ("Joe", 3, "Coffee", "Ben", 2),
      ("Liz", 1, "Coffee", "Ben", 2),
      ("Liz", 3, "Tea", "Ben", 3)
    )

    // We can order the attributes as we like (sanity checks):
    Ns.int_(23).str.Refs1.int1.str1._Ns.Self
      .int_(25).str.Refs1.int1.str1_(unify).get.sorted === List(
      ("Joe", 3, "Coffee", "Ben", 2),
      ("Liz", 1, "Coffee", "Ben", 2),
      ("Liz", 3, "Tea", "Ben", 3)
    )
    Ns.int_(23).str.Refs1.str1.int1._Ns.Self
      .int_(25).str.Refs1.str1_(unify).int1.get.sorted === List(
      ("Joe", "Coffee", 3, "Ben", 2),
      ("Liz", "Coffee", 1, "Ben", 2),
      ("Liz", "Tea", 3, "Ben", 3)
    )
    Ns.int_(23).str.Refs1.str1.int1._Ns.Self
      .int_(25).str.Refs1.int1.str1_(unify).get.sorted === List(
      ("Joe", "Coffee", 3, "Ben", 2),
      ("Liz", "Coffee", 1, "Ben", 2),
      ("Liz", "Tea", 3, "Ben", 3)
    )
  }


  "With constraints" in new Setup {

    // Only higher rated beverages
    Ns.int_(23).str.Refs1.int1.>(1).str1._Ns.Self
      .int_(25).str.Refs1.int1.>(1).str1_(unify).get.sorted === List(
      ("Joe", 3, "Coffee", "Ben", 2),
      ("Liz", 3, "Tea", "Ben", 3)
    )

    // Only highest rated beverages
    Ns.int_(23).str.Refs1.int1(3).str1._Ns.Self
      .int_(25).str.Refs1.int1(3).str1_(unify).get.sorted === List(
      ("Liz", 3, "Tea", "Ben", 3)
    )

    // Common beverage of
    // 23-year-olds with weak preference and
    // 24-year-olds with good preference
    Ns.int_(23).str.Refs1.int1(1).str1._Ns.Self
      .int_(25).str.Refs1.int1(2).str1_(unify).get.sorted === List(
      ("Liz", 1, "Coffee", "Ben", 2)
    )
  }


  "Constraints on the unifying attribute" in new Setup {

    // Any 23- and 25-year-olds wanting to drink tea together?
    Ns.int_(23).str.Refs1.str1_("Tea")._Ns.Self
      .int_(25).str.Refs1.str1_(unify).get === List(
      ("Liz", "Ben")
    )
  }


  "Multiple self-joins" in new Setup {

    // Beverages liked by all 3 different people
    Ns.str_("Joe").Refs1.str1._Ns.Self
      .str_("Ben").Refs1.str1_(unify)._Ns.Self
      .str_("Liz").Refs1.str1_(unify).get === List("Coffee")
  }


  "Self-joins with attribute maps" in new CoreSetup {

    m(Ns.strMap.Refs1 * Ref1.int1) insert List(
      (Map("en" -> "Hi there"), List(1, 2)),
      (Map("fr" -> "Bonjour", "en" -> "Oh, Hi"), List(1, 2)),
      (Map("en" -> "Hello"), List(2, 3)),
      (Map("da" -> "Hej"), List(3, 4))
    )

//    Ns.strMap.apply("en" and "fr").Refs1.str1.debug
//    Ns.strMap.apply("en" -> "hej").Refs1.str1.debug
//    Ns.strMap.apply("en" -> "hej" and "fr" -> "Joe").Refs1.str1.debug
//    Ns.strMap.apply(("en" -> "hej") and ("fr" -> "Joe")).Refs1.str1.debug


    Ns.e.strMap("en").Refs1.int1_(1 and 2).get.map(_._2("en")) === List(
      "Hi there",
      "Oh, Hi"
    )
    //    Ns.e.strMap("en").Refs1.int1(1).debug
    //    Ns.e.strMap("en").Ref1.int1_(1 and 2).debug

    //    Ns.e.strMap("en").Refs1.int1_(1 and 2).debug
        /*
        * Model(List(
          Meta(,,e,NoValue,EntValue),
          Atom(ns,strMap,String,3,Eq(List(en)),Some(mapping),List()),
          Bond(ns,refs1,ref1),
          Atom(ref1,int1_,Long,1,And(List(1, 2)),None,List())))

        Query(
          Find(List(
            Var(a),
            AggrExpr(distinct,List(),Var(c)))),
          Where(List(
            DataClause(ImplDS,Var(a),KW(ns,strMap,),Var(c),Empty,NoBinding),
            Funct(.startsWith ^String,List(Var(c), Val(en)),NoBinding),
            DataClause(ImplDS,Var(a),KW(ns,refs1,ref1),Var(d),Empty,NoBinding),
            DataClause(ImplDS,Var(d),KW(ref1,int1,),Val(1),Empty,NoBinding),
            DataClause(ImplDS,Var(a_1),KW(ns,strMap,),Var(c),Empty,NoBinding),
            DataClause(ImplDS,Var(a_1),KW(ns,refs1,ref1),Var(d_1),Empty,NoBinding),
            DataClause(ImplDS,Var(d_1),KW(ref1,int1,),Val(2),Empty,NoBinding))))

        [:find  ?a (distinct ?c)
         :where [?a :ns/strMap ?c]
                [(.startsWith ^String ?c "en")]
                [?a :ns/refs1 ?d]
                [?d :ref1/int1 1]
                [?a_1 :ns/strMap ?c]
                [?a_1 :ns/refs1 ?d_1]
                [?d_1 :ref1/int1 2]]

        RULES: none

        INPUTS: none

        OUTPUTS:
        1  [17592186045445 #{"en@Hi there"}]
        2  [17592186045448 #{"en@Oh, Hi"}]*/


        Peer.q(
          """
            |[:find  ?a (distinct ?c)
            |     :where [?a :ns/strMap ?c]
            |            [(.startsWith ^String ?c "en")]
            |            [?a :ns/refs1 ?d]
            |            [?d :ref1/int1 1]
            |            [?d :ref1/int1 ?e]
            |            [?a_1 :ns/strMap ?c]
            |            [?a_1 :ns/refs1 ?d_1]
            |            [?d_1 :ref1/int1 2]
            |            ]
          """.stripMargin, conn.db) === 7

    //    Ns.e.strMap("en").Refs1.int1(1 and 2).debug
        Ns.e.strMap("en").Refs1.int1(1).debug
        /*
        * Model(List(
          Meta(,,e,NoValue,EntValue),
          Atom(ns,strMap,String,3,Eq(List(en)),Some(mapping),List()),
          Bond(ns,refs1,ref1),
          Atom(ref1,int1,Long,1,Eq(List(1)),None,List())))

        Query(
          Find(List(
            Var(a),
            AggrExpr(distinct,List(),Var(c)),
            Var(e))),
          Where(List(
            DataClause(ImplDS,Var(a),KW(ns,strMap,),Var(c),Empty,NoBinding),
            Funct(.startsWith ^String,List(Var(c), Val(en)),NoBinding),
            DataClause(ImplDS,Var(a),KW(ns,refs1,ref1),Var(d),Empty,NoBinding),
            DataClause(ImplDS,Var(d),KW(ref1,int1,),Val(1),Empty,NoBinding),
            DataClause(ImplDS,Var(d),KW(ref1,int1,),Var(e),Empty,NoBinding))))

        [:find  ?a (distinct ?c) ?e
         :where [?a :ns/strMap ?c]
                [(.startsWith ^String ?c "en")]
                [?a :ns/refs1 ?d]
                [?d :ref1/int1 1]
                [?d :ref1/int1 ?e]]*/


        Peer.q(
          """
            |[:find  ?a (distinct ?c) ?e
            | :where [?a :ns/strMap ?c]
            |        [(.startsWith ^String ?c "en")]
            |        [?a :ns/refs1 ?d]
            |        [?d :ref1/int1 1]
            |        [?d :ref1/int1 ?e]]
          """.stripMargin, conn.db) === 7

//    m(Ns.strMap.Ref1.int1) insert List(
//      (Map("en" -> "Hi there"), 1),
//      (Map("fr" -> "Bonjour", "en" -> "Oh, Hi"), 1)
//    )

    ok
  }
}

//Peer.q(
//  """
//    |[:find  ?d ?x ?y
//    | :where [?a :ns/int 23]
//    |        [?a :ns/str ?x]
//    |        [?a :ns/refs1 ?c]
//    |        [?c :ref1/str1 ?d]
//    |        [?a_1 :ns/int 25]
//    |        [?a_1 :ns/str ?y]
//    |        [?a_1 :ns/refs1 ?c_1]
//    |        [?c_1 :ref1/str1 ?d]
//    |        ]
//  """.stripMargin, conn.db) === 7