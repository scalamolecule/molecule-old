package molecule
package attrMap
import datomic.Peer
import molecule.util.dsl.coreTest._

class Keys extends Base {


//  "One key" in new Setup {
//
//    Ns.int.strMap.k("en").get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//
//    Ns.int.intMap.k("en").get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10)),
//      (3, Map("en" -> 30))
//    )
//
//    // OBS: Since a map attribute returns a map, we have to beware that
//    // key/value pairs are coalesced if there are no other attributes and
//    // we end up with one random pair:
//    Ns.strMap.k("en").get === List(
//      Map("en" -> "Oh, Hi") // random pair...
//    )
//
//    // A workaround to get all the mapped values only is to add the `e`
//    // entity attribute (which always has a value) and then filter it out
//    // from the result set:
//    Ns.e.strMap.k("en").get.map(_._2) === List(
//      Map("en" -> "Hi there"),
//      Map("en" -> "Oh, Hi"),
//      Map("en" -> "Hello")
//    )
//    Ns.e.intMap.k("en").get.map(_._2) === List(
//      Map("en" -> 10),
//      Map("en" -> 10),
//      Map("en" -> 30)
//    )
//
//    // Then we might as well return only the values
//    Ns.e.strMap.k("en").get.map(_._2("en")) === List(
//      "Hi there",
//      "Oh, Hi",
//      "Hello"
//    )
//    Ns.e.intMap.k("en").get.map(_._2("en")) === List(
//      10,
//      10,
//      30
//    )
//  }


//  "Multiple keys (OR semantics)" in new Setup {
//
//
//  //    // Todo
//  ////    Ns.int.strMap.apply("en").debug
//  //    /*
//  //    [:find  ?b (distinct ?c)
//  //     :where [?a :ns/int ?b]
//  //            [?a :ns/strMap ?c]
//  //            [(.startsWith ^String ?c "en")]]
//  //
//  //    1  [1 #{"en@Hi there"}]
//  //    2  [2 #{"en@Oh, Hi"}]
//  //    3  [3 #{"en@Hello"}]*/
//  //
//  //
//  ////    Ns.int.strMap.get === 8
//  //     /*
//  //     List(
//  //       (1, Map(en -> Hi there)),
//  //       (2, Map(fr -> Bonjour, en -> Oh, Hi)),
//  //       (3, Map(en -> Hello)),
//  //       (4, Map(da -> Hej))
//  //     )
//  //     * */
//  //    Peer.q(
//  //      """
//  //        |[:find  ?b (distinct ?c)
//  //        |     :where [?a :ns/int ?b]
//  //        |            [?a :ns/strMap ?c]
//  //        |            [(.startsWith ^String ?c "en")]
//  //        |            [?a :ns/strMap ?d]
//  //        |            [(.startsWith ^String ?d "fr")]
//  //        |            [?a :ns/strMap ?e]
//  //        |            ]
//  //      """.stripMargin, conn.db) === """[[2 "fr@Bonjour"]]"""
//  //
//  //    Peer.q(
//  //      """
//  //        |[:find  ?b ?c
//  //        |     :where [?a :ns/int ?b]
//  //        |            [?a :ns/strMap ?c]
//  //        |            [(.startsWith ^String ?c "fr")]
//  //        |            ]
//  //      """.stripMargin, conn.db) === """[[2 "fr@Bonjour"]]"""
//  //
//  //    Peer.q(
//  //      """
//  //        |[:find  ?b (distinct ?c)
//  //        |     :where [?a :ns/int ?b]
//  //        |            [?a :ns/strMap "fr@Bonjour"]
//  //        |            [?a :ns/strMap ?c]
//  //        |            ]
//  //      """.stripMargin, conn.db) === """[[1 #{"en@Hi there"}] [2 #{"fr@Bonjour" "en@Oh, Hi"}] [3 #{"en@Hello"}] [4 #{"da@Hej"}]]"""
//  ////        |            [(.startsWith ^String ?c "en")]
//  //
//
//    // OR semantics with comma-separated keys
//    Ns.int.strMap.k("en", "fr").get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//    Ns.int.strMap.k("en", "fr").get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//    Ns.int.intMap.k("en", "fr").get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("fr" -> 20, "en" -> 10)),
//      (3, Map("en" -> 30))
//    )
//
//    // OR semantics with "or"-separated keys
//
//    Ns.int.strMap.k("en" or "fr").debug
//    Ns.int.strMap.k("en" or "fr" or "da").debug
//    Ns.int.strMap.k("en" and "fr").debug
//    Ns.int.strMap.k("en" and "fr" and "da").debug
//
//
//    Ns.int.strMap.k("en" or "fr").get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//    Ns.int.intMap.k("en" or "fr").get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("fr" -> 20, "en" -> 10)),
//      (3, Map("en" -> 30))
//    )
//  }
//
//
//  "Multiple keys (AND semantics)" in new Setup {
//
//    Peer.q(
//      """
//        |[:find  ?b (distinct ?c)
//        |     :where [?a :ns/int ?b]
//        |            [?a :ns/strMap ?c]
//        |            [(.startsWith ^String ?c "en")]
//        |            [?a :ns/strMap ?d]
//        |            [(.startsWith ^String ?d "fr")]
//        |            [?a :ns/strMap ?e]
//        |            ]
//      """.stripMargin, conn.db) ===
//      """[[2 "fr@Bonjour"]]"""
//
//    Ns.int.strMap.k("en" and "da").get === List(
//      (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
//      (2, Map("en" -> "Oh, Hi", "da" -> "Hilser")),
//      (3, Map("en" -> "Hello", "da" -> "Hejhej"))
//    )
//
//    Ns.int.intMap.k("en" and "da").get === List(
//      (1, Map("en" -> 10, "da" -> 30)),
//      (2, Map("en" -> 10, "da" -> 10)),
//      (3, Map("en" -> 30, "da" -> 30))
//    )
//  }
}