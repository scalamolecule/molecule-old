//package molecule
//import molecule.util.dsl.coreTest._
//import molecule.util.{CoreSetup, CoreSpec, expectCompileError}
//
//class AttributeMap extends CoreSpec {
//
//
//  //  "Manipulate" in new CoreSetup {
//  //
//  //    // Insert
//  //    val eid: Long = Ns.strMap.insert(Map("en" -> "Hi")).eid
//  //    Ns.strMap.one === Map("en" -> "Hi")
//  //
//  //    // Update + Add
//  //    // When a previous populated key is encountered the old fact is
//  //    // retracted and the new one asserted (like an update).
//  //    Ns(eid).strMap.add("en" -> "Hi there", "fr" -> "Bonjour").update
//  //    Ns.strMap.one === Map("en" -> "Hi there", "fr" -> "Bonjour")
//  //
//  //    // Remove pair (by key)
//  //    Ns(eid).strMap.remove("en").update
//  //    Ns.strMap.one === Map("fr" -> "Bonjour")
//  //
//  //    // Applying nothing (empty parenthesises) finds and retract all values of an attribute
//  //    Ns(eid).strMap().update
//  //    Ns.strMap.one === Map()
//  //  }
//  //
//  //
//  //  "Types" in new CoreSetup {
//  //    Ns.strMap.insert(Map("en" -> "Hi")).eid
//  //    Ns.strMap.one === Map("en" -> "Hi")
//  //
//  //    Ns.intMap.insert(Map("Meaning of life" -> 42)).eid
//  //    Ns.intMap.one === Map("Meaning of life" -> 42)
//  //
//  //    //    Ns.int.intMap.apply("en" -> 23).get
//  //
//  //
//  //    Ns.longMap.insert(Map("pluto" -> 123456789012345L)).eid
//  //    Ns.longMap.one === Map("pluto" -> 123456789012345L)
//  //
//  //    Ns.floatMap.insert(Map("USD" -> 35.50f)).eid
//  //    Ns.floatMap.one === Map("USD" -> 35.50f)
//  //
//  //    Ns.doubleMap.insert(Map("Apple" -> 435.3547)).eid
//  //    Ns.doubleMap.one === Map("Apple" -> 435.3547)
//  //
//  //    Ns.boolMap.insert(Map("Sanders" -> true)).eid
//  //    Ns.boolMap.one === Map("Sanders" -> true)
//  //
//  //    Ns.dateMap.insert(Map("today" -> date1)).eid
//  //    Ns.dateMap.one === Map("today" -> date1)
//  //
//  //    Ns.uuidMap.insert(Map("id1" -> uuid1)).eid
//  //    Ns.uuidMap.one === Map("id1" -> uuid1)
//  //
//  //    Ns.uriMap.insert(Map("uri1" -> uri1)).eid
//  //    Ns.uriMap.one === Map("uri1" -> uri1)
//  //  }
//
//
//  class Setup extends CoreSetup {
//
//    val en    = "en"
//    val fr    = "fr"
//    val it    = "it"
//    val Hi    = "Hi"
//    val He    = "He"
//    val Bo    = "Bo"
//    val regEx = "Hi|He"
//
//    val i10 = 10
//    val i20 = 20
//    val i30 = 30
//
//    Ns.int.strMap insert List(
//      (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
//      (2, Map("en" -> "Oh, Hi", "da" -> "Hilser", "fr" -> "Bonjour", "it" -> "Bon giorno")),
//      (3, Map("en" -> "Hello", "da" -> "Hejhej")),
//      (4, Map("da" -> "Hej")),
//      (5, Map[String, String]())
//    )
//    Ns.int.intMap insert List(
//      (1, Map("en" -> 10, "da" -> 30)),
//      (2, Map("en" -> 10, "da" -> 10, "fr" -> 20, "it" -> 30)),
//      (3, Map("en" -> 30, "da" -> 30)),
//      (4, Map("da" -> 30)),
//      (5, Map[String, Int]())
//    )
//    //    Ns.int.longMap insert List(
//    //      (1, Map("en" -> 10L)),
//    //      (2, Map("fr" -> 20L, "en" -> 10L)),
//    //      (3, Map("en" -> 30L)),
//    //      (4, Map("da" -> 40L)),
//    //      (5, Map[String, Long]())
//    //    )
//    //    Ns.int.floatMap insert List(
//    //      (1, Map("en" -> 10f)),
//    //      (2, Map("fr" -> 20f, "en" -> 10f)),
//    //      (3, Map("en" -> 30f)),
//    //      (4, Map("da" -> 40f)),
//    //      (5, Map[String, Float]())
//    //    )
//    //    Ns.int.doubleMap insert List(
//    //      (1, Map("en" -> 10.0)),
//    //      (2, Map("fr" -> 20.0, "en" -> 10.0)),
//    //      (3, Map("en" -> 30.0)),
//    //      (4, Map("da" -> 40.0)),
//    //      (5, Map[String, Double]())
//    //    )
//    //    Ns.int.boolMap insert List(
//    //      (1, Map("en" -> true)),
//    //      (2, Map("fr" -> false, "en" -> true)),
//    //      (3, Map("en" -> false)),
//    //      (4, Map("da" -> true)),
//    //      (5, Map[String, Boolean]())
//    //    )
//    //    Ns.int.dateMap insert List(
//    //      (1, Map("en" -> date1)),
//    //      (2, Map("fr" -> date2, "en" -> date1)),
//    //      (3, Map("en" -> date3)),
//    //      (4, Map("da" -> date4)),
//    //      (5, Map[String, Date]())
//    //    )
//    //    Ns.int.uuidMap insert List(
//    //      (1, Map("en" -> uuid1)),
//    //      (2, Map("fr" -> uuid2, "en" -> uuid1)),
//    //      (3, Map("en" -> uuid3)),
//    //      (4, Map("da" -> uuid4)),
//    //      (5, Map[String, UUID]())
//    //    )
//    //    Ns.int.uriMap insert List(
//    //      (1, Map("en" -> uri1)),
//    //      (2, Map("fr" -> uri2, "en" -> uri1)),
//    //      (3, Map("en" -> uri3)),
//    //      (4, Map("da" -> uri4)),
//    //      (5, Map[String, URI]())
//    //    )
//  }
//
//  "Types" in new Setup {
//
//    // All mapped values
//    Ns.int.strMap.get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej"))
//    )
//    Ns.int.intMap.get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("fr" -> 20, "en" -> 10)),
//      (3, Map("en" -> 30)),
//      (4, Map("da" -> 30))
//    )
//    Ns.int.longMap.get === List(
//      (1, Map("en" -> 10L)),
//      (2, Map("fr" -> 20L, "en" -> 10L)),
//      (3, Map("en" -> 30L)),
//      (4, Map("da" -> 40L))
//    )
//    Ns.int.floatMap.get === List(
//      (1, Map("en" -> 10f)),
//      (2, Map("fr" -> 20f, "en" -> 10f)),
//      (3, Map("en" -> 30f)),
//      (4, Map("da" -> 40f))
//    )
//    Ns.int.doubleMap.get === List(
//      (1, Map("en" -> 10.0)),
//      (2, Map("fr" -> 20.0, "en" -> 10.0)),
//      (3, Map("en" -> 30.0)),
//      (4, Map("da" -> 40.0))
//    )
//    Ns.int.boolMap.get === List(
//      (1, Map("en" -> true)),
//      (2, Map("fr" -> false, "en" -> true)),
//      (3, Map("en" -> false)),
//      (4, Map("da" -> true))
//    )
//    Ns.int.dateMap.get === List(
//      (1, Map("en" -> date1)),
//      (2, Map("fr" -> date2, "en" -> date1)),
//      (3, Map("en" -> date3)),
//      (4, Map("da" -> date4))
//    )
//    Ns.int.uuidMap.get === List(
//      (1, Map("en" -> uuid1)),
//      (2, Map("fr" -> uuid2, "en" -> uuid1)),
//      (3, Map("en" -> uuid3)),
//      (4, Map("da" -> uuid4))
//    )
//    Ns.int.uriMap.get === List(
//      (1, Map("en" -> uri1)),
//      (2, Map("fr" -> uri2, "en" -> uri1)),
//      (3, Map("en" -> uri3)),
//      (4, Map("da" -> uri4))
//    )
//  }
//
//
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
//
//
//  "Multiple keys (OR semantics)" in new Setup {
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
//  //  "Multiple keys (AND semantics)" in new Setup {
//  //
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
//  //
//  //    Ns.int.strMap.apply("en" and "fr").debug
//  //    /*
//  //    * */
//  //
//  //
//  //    import ast.model._
//  //
//  //    Ns.int.strMap.apply(And2(TermValue("en"), TermValue("fr"))).get === List(
//  ////    Ns.int.strMap.apply("en" and "fr").get === List(
//  //      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))
//  //    )
//  //
//  ////    val cc = TermValue("en")
//  //    val cc = TermValue("en" -> "Hi")
//  //    val dd = And2(TermValue("en" -> "Hi"), TermValue("fr" -> "Bo"))
//  //
//  ////    Ns.int.strMap.apply(dd).get === List(
//  ////    Ns.int.strMap.apply(And2(cc, cc)).get === List(
//  ////    Ns.int.strMap.apply(And2(TermValue("en" -> "Hi"), TermValue("fr" -> "Bo"))).get === List(
//  //    Ns.int.strMap.apply("en" -> "Hi" and "fr" -> "Bo").get === List(
//  //      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))
//  //    )
//  //
//  //    ok
//  //  }
//
//
//  //  "Key (optional) - String" in new CoreSetup {
//  //
//  //    Ns.int.strMap$ insert List(
//  //      (1, Some(Map("en" -> "Hi there"))),
//  //      (2, Some(Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))),
//  //      (3, Some(Map("en" -> "Hello"))),
//  //      (4, Some(Map("da" -> "Hej"))),
//  //      (5, None)
//  //    )
//  //
//  //    // All (5th entity has no strMap)
//  //    Ns.int.strMap$.get === List(
//  //      (1, Some(Map("en" -> "Hi there"))),
//  //      (2, Some(Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))),
//  //      (3, Some(Map("en" -> "Hello"))),
//  //      (4, Some(Map("da" -> "Hej"))),
//  //      (5, None)
//  //    )
//  //
//  //    // Values with "en" key
//  //    // We can't apply values to optional values (like `Ns.int.strMap$("en")`)
//  //    // Instead we process the results:
//  //    Ns.int.strMap$.get.map { case (i, s) => (i, s.flatMap(_.get("en"))) } === List(
//  //      (1, Some("Hi there")),
//  //      (2, Some("Oh, Hi")),
//  //      (3, Some("Hello")),
//  //      (4, None),
//  //      (5, None)
//  //    )
//  //
//  //    // Or we can use implicit functions for convenience
//  //    Ns.int.strMap$.get.map { case (i, s) => (i, s.at("en")) } === List(
//  //      (1, Some("Hi there")),
//  //      (2, Some("Oh, Hi")),
//  //      (3, Some("Hello")),
//  //      (4, None),
//  //      (5, None)
//  //    )
//  //
//  //    // Get filtered map with certain keys
//  //    Ns.int.strMap$.get.map { case (i, s) => (i, s.keys("fr", "da")) } === List(
//  //      (1, Map()),
//  //      (2, Map("fr" -> "Bonjour")),
//  //      (3, Map()),
//  //      (4, Map("da" -> "Hej")),
//  //      (5, Map())
//  //    )
//  //  }
//  //
//  //  "Key (optional) - Int" in new CoreSetup {
//  //
//  //    Ns.int.intMap$ insert List(
//  //      (1, Some(Map("en" -> 10))),
//  //      (2, Some(Map("fr" -> 20, "en" -> 10))),
//  //      (3, Some(Map("en" -> 30))),
//  //      (4, Some(Map("da" -> 30))),
//  //      (5, None)
//  //    )
//  //
//  //    // All (5th entity has no strMap)
//  //    Ns.int.intMap$.get === List(
//  //      (1, Some(Map("en" -> 10))),
//  //      (2, Some(Map("fr" -> 20, "en" -> 10))),
//  //      (3, Some(Map("en" -> 30))),
//  //      (4, Some(Map("da" -> 30))),
//  //      (5, None)
//  //    )
//  //
//  //    // Values with "en" key
//  //    // We can't apply values to optional values (like `Ns.int.strMap$("en")`)
//  //    // Instead we process the results:
//  //    Ns.int.intMap$.get.map { case (i, s) => (i, s.flatMap(_.get("en"))) } === List(
//  //      (1, Some(10)),
//  //      (2, Some(10)),
//  //      (3, Some(30)),
//  //      (4, None),
//  //      (5, None)
//  //    )
//  //
//  //    // Or we can use implicit functions for convenience
//  //    Ns.int.intMap$.get.map { case (i, s) => (i, s.at("en")) } === List(
//  //      (1, Some(10)),
//  //      (2, Some(10)),
//  //      (3, Some(30)),
//  //      (4, None),
//  //      (5, None)
//  //    )
//  //
//  //    // Get filtered map with certain keys
//  //    Ns.int.intMap$.get.map { case (i, s) => (i, s.keys("fr", "da")) } === List(
//  //      (1, Map()),
//  //      (2, Map("fr" -> 20)),
//  //      (3, Map()),
//  //      (4, Map("da" -> 30)),
//  //      (5, Map())
//  //    )
//  //  }
////
////
////  "Key (tacet)" in new Setup {
////
////    // Int values of entities with mapped values
////    Ns.int.strMap_.get === List(1, 2, 3, 4)
////    Ns.int.intMap_.get === List(1, 2, 3, 4)
////
////    // Int values of entities with an english value
////    Ns.int.strMap_.k("en").get === List(1, 2, 3)
////    Ns.int.intMap_.k("en").get === List(1, 2, 3)
////
////    // Int values of entities with an english or french value
////    Ns.int.strMap_.k("fr", "da").get === List(2, 4)
////    Ns.int.intMap_.k("fr", "da").get === List(2, 4)
////  }
//
//
//  "One key / one value" in new Setup {
//
//    // One key, one value
//
//    // After asking for a key we can apply a value
//    Ns.int.strMap.k("en")("Hi there").get === List(
//      (1, Map("en" -> "Hi there"))
//    )
//    // Or we can apply a pair
//    Ns.int.strMap("en" -> "Hi there").get === List(
//      (1, Map("en" -> "Hi there"))
//    )
//    Ns.int.strMap("en" -> ".*Hi.*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi"))
//    )
//    Ns.int.strMap.k("en")(".*Hi.*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi"))
//    )
//
//    Ns.int.intMap("en" -> 10).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10))
//    )
//    Ns.int.intMap.k("en")(10).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10))
//    )
//    // We can't use regex on other types than String
//    // Ns.int.intMap("en" -> ".*1.*") // doesn't type check
//
//  }
//
//
//  "One key / multiple values" in new Setup {
//
//    // OR semantics
//
//    Ns.int.strMap.k("en")("Hi there" or "Hello").get === List(
//      (1, Map("en" -> "Hi there")),
//      (3, Map("en" -> "Hello"))
//    )
//    Ns.int.strMap.k("en")("Hi there", "Hello").get === List(
//      (1, Map("en" -> "Hi there")),
//      (3, Map("en" -> "Hello"))
//    )
//    // Pairs with same key can be used too, although more clumsy
//    Ns.int.strMap("en" -> "Hi there", "en" -> "Hello").get === List(
//      (1, Map("en" -> "Hi there")),
//      (3, Map("en" -> "Hello"))
//    )
//    // For String values we can even use a regEx in the value part
//    Ns.int.strMap.k("en")(".*(Hi there|Hello).*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (3, Map("en" -> "Hello"))
//    )
//    // And then we might as well use a pair
//    Ns.int.strMap("en" -> ".*(Hi there|Hello).*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (3, Map("en" -> "Hello"))
//    )
//
//    // Regex for subpattern (only for type String)
//    Ns.int.strMap.k("en")(".*(Hi|He).*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//    Ns.int.strMap("en" -> ".*(Hi|He).*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//    Ns.int.strMap("en" -> ".*Hi.*", "en" -> ".*He.*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//
//    // Other types can only search for full values (no regex)
//    Ns.int.intMap.k("en")(10 or 30).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10)),
//      (3, Map("en" -> 30))
//    )
//    Ns.int.intMap.k("en")(10, 30).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10)),
//      (3, Map("en" -> 30))
//    )
//    Ns.int.intMap("en" -> 10, "en" -> 30).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10)),
//      (3, Map("en" -> 30))
//    )
//
//    // AND semantics are not relevant with a single key
//    // Ns.int.strMap.k("en")("Hi there" and "Hello") // can't exist
//  }
//
//
//  "Multiple keys / one value" in new Setup {
//    val keys = Seq("en", "da")
//
//    // OR semantics
//
//    // Search value across multiple keys
//    // (we don't necessarily need a regex - we just don't have equal values in the current data set)
//    // (Note that search value is Case insensitive - "there" is included)
//    Ns.int.strMap.k("en" or "da")(".*he.*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej"))
//    )
//    // Comma-separated keys have OR-semantics
//    Ns.int.strMap.k("en", "da")(".*he.*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej"))
//    )
//    // Sequence of keys
//    Ns.int.strMap.k(keys)(".*he.*").get === List(
//      (1, Map("en" -> "Hi there")),
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej"))
//    )
//
//    Ns.int.intMap.k("en" or "da")(30).get === List(
//      (3, Map("en" -> 30)),
//      (4, Map("da" -> 30))
//    )
//    Ns.int.intMap.k("en", "da")(30).get === List(
//      (3, Map("en" -> 30)),
//      (4, Map("da" -> 30))
//    )
//    Ns.int.intMap.k(keys)(30).get === List(
//      (3, Map("en" -> 30)),
//      (4, Map("da" -> 30))
//    )
//
//    // AND semantics - multiple pairs sharing a value
//
//    Ns.int.strMap.k("fr" and "en")(".*bo.*").get === List(
//      (2, Map("fr" -> "Bonjour", "it" -> "Bon giorno"))
//    )
//
//    Ns.int.intMap.k("fr" and "en")(20).get === List(
//      (2, Map("fr" -> 20, "it" -> 20))
//    )
//  }
//
//
//  "Multiple keys / multiple values" >> {
//
//
//    "Some keys, some values" in new Setup {
//
//      Ns.int.strMap.k("en" or "da")(".*hi.*" or ".*he.*").get === List(
//        (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
//        (2, Map("en" -> "Oh, Hi", "da" -> "Hilser")),
//        (3, Map("en" -> "Hello", "da" -> "Hejhej")),
//        (4, Map("da" -> "Hej"))
//      )
//
//      Ns.int.intMap.k("en" or "da")(10 or 30).get === List(
//        (1, Map("en" -> 10, "da" -> 30)),
//        (2, Map("en" -> 10, "da" -> 10)),
//        (3, Map("en" -> 30, "da" -> 30)),
//        (4, Map("da" -> 30))
//      )
//      Ns.int.intMap.k("en" or "da" or "it")(10 or 30).get === List(
//        (1, Map("en" -> 10, "da" -> 30)),
//        (2, Map("en" -> 10, "da" -> 10, "it" -> 30)),
//        (3, Map("en" -> 30, "da" -> 30)),
//        (4, Map("da" -> 30))
//      )
//    }
//
//
//    "Some keys, certain values" in new Setup {
//
//      Ns.int.strMap.k("en" or "da")(".*hi.*" and ".*he.*").get === List(
//        (1, Map("en" -> "Hi there", "da" -> "Hejsa"))
//      )
//
//      Ns.int.intMap.k("en" or "da")(10 and 30).get === List(
//        (1, Map("en" -> 10, "da" -> 30))
//      )
//      Ns.int.intMap.k("en" or "da" or "it")(10 and 30).get === List(
//        (1, Map("en" -> 10, "da" -> 30)),
//        (2, Map("en" -> 10, "da" -> 10, "it" -> 30))
//      )
//    }
//
//
//    "Certain keys, some values" in new Setup {
//
//      Ns.int.strMap.k("en" and "da")(".*hi.*" or ".*he.*").get === List(
//        (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
//        (2, Map("en" -> "Oh, Hi", "da" -> "Hilser")),
//        (3, Map("en" -> "Hello", "da" -> "Hejhej"))
//      )
//
//      Ns.int.intMap.k("en" and "da")(10 or 30).get === List(
//        (1, Map("en" -> 10, "da" -> 30)),
//        (2, Map("en" -> 10, "da" -> 10)),
//        (3, Map("en" -> 30, "da" -> 30))
//      )
//      Ns.int.intMap.k("en" and "da" and "it")(10 or 30).get === List(
//        (2, Map("en" -> 10, "da" -> 10, "it" -> 30))
//      )
//    }
//
//
//    "Certain keys, certain values" in new Setup {
//
//      Ns.int.strMap.k("en" and "da")(".*hi.*" and ".*he.*").get === List(
//        (1, Map("en" -> "Hi there", "da" -> "Hejsa"))
//      )
//
//      Ns.int.intMap.k("en" and "da")(10 and 30).get === List(
//        (1, Map("en" -> 10, "da" -> 30))
//      )
//      Ns.int.intMap.k("en" and "da" and "it")(10 and 30).get === List(
//        (2, Map("en" -> 10, "da" -> 10, "it" -> 30))
//      )
//    }
//
//    "Multiple keys / multiple values" in new Setup {
//      // Tie specific key/values using pairs
//      Ns.int.strMap("en" -> "Hi there" or "da" -> "Hej").get === List(
//        (1, Map("en" -> "Hi there")),
//        (4, Map("da" -> "Hej"))
//      )
//      // Comma-separated pairs have OR-semantics
//      Ns.int.strMap("en" -> "Hi there", "da" -> "Hej").get === List(
//        (1, Map("en" -> "Hi there")),
//        (4, Map("da" -> "Hej"))
//      )
//      val strPairs = Seq("en" -> "Hi there", "da" -> "Hej")
//      Ns.int.strMap(strPairs).get === List(
//        (1, Map("en" -> "Hi there")),
//        (4, Map("da" -> "Hej"))
//      )
//
//      Ns.int.intMap("en" -> 10 or "fr" -> 20).get === List(
//        (1, Map("en" -> 10)),
//        (2, Map("fr" -> 20, "en" -> 10))
//      )
//      Ns.int.intMap("en" -> 10, "fr" -> 20).get === List(
//        (1, Map("en" -> 10)),
//        (2, Map("fr" -> 20, "en" -> 10))
//      )
//      val intPairs = Seq("en" -> 10, "fr" -> 20)
//      Ns.int.intMap(intPairs).get === List(
//        (1, Map("en" -> 10)),
//        (2, Map("fr" -> 20, "en" -> 10))
//      )
//
//      // Multiple keys with multiple values for some keys (OR semantics)
//      Ns.int.strMap("en" -> "Hi", "en" -> "He", "fr" -> "Bo").get === List(
//        (1, Map("en" -> "Hi there")),
//        (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
//        (3, Map("en" -> "Hello"))
//      )
//      // or
//      Ns.int.strMap("en" -> "Hi|He", "fr" -> "Bo").get === List(
//        (1, Map("en" -> "Hi there")),
//        (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
//        (3, Map("en" -> "Hello"))
//      )
//
//      // All keys with a value
//      Ns.int.strMap("_" -> "He").get === List(
//        (3, Map("en" -> "Hello")),
//        (4, Map("da" -> "Hej"))
//      )
//      Ns.int.intMap("_" -> 10).get === List(
//        (1, Map("en" -> 10)),
//        (2, Map("en" -> 10))
//      )
//
//      // All keys with multiple values (OR semantics)
//      Ns.int.strMap("_" -> "He", "_" -> "Bo").get === List(
//        (2, Map("fr" -> "Bonjour")),
//        (3, Map("en" -> "Hello")),
//        (4, Map("da" -> "Hej"))
//      )
//      Ns.int.strMap("_" -> "He|Bo").get === List(
//        (2, Map("fr" -> "Bonjour")),
//        (3, Map("en" -> "Hello")),
//        (4, Map("da" -> "Hej"))
//      )
//
//      // Note that we can't combine searching for all keys and a specific key
//      expectCompileError(
//        """m(Ns.int.strMap("_" -> "He", "fr" -> "Bo"))""",
//        "[Dsl2Model:getValues] Searching for all keys with `_` can't be combined with other key-values.")
//
//
//      // Results are coalesced to one Map when no other attributes are present in the molecule
//      Ns.strMap("_" -> "He", "_" -> "Bo").get === List(
//        Map("en" -> "Hello", "fr" -> "Bonjour", "da" -> "Hej")
//      )
//
//    }
//    // AND semantics - multiple pairs sharing multiple values ???
//  }
//
//
//  "Values (optional) - String" in new CoreSetup {
//
//    Ns.int.strMap$ insert List(
//      (1, Some(Map("en" -> "Hi there"))),
//      (2, Some(Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))),
//      (3, Some(Map("en" -> "Hello"))),
//      (4, Some(Map("da" -> "Hej"))),
//      (5, None)
//    )
//
//    // Maps with values containing "Hi"
//    Ns.int.strMap$.get.map { case (i, s) => (i, s.values("Hi")) } === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map()),
//      (4, Map()),
//      (5, Map())
//    )
//
//    // Search value is Case insensitive ("there" is included)
//    Ns.int.strMap$.get.map { case (i, s) => (i, s.values("He")) } === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map()),
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej")),
//      (5, Map())
//    )
//
//    // Search combination of key and value (Danish now excluded). A key -> value pair has AND semantics:
//    // key("en") AND value("He")
//    Ns.int.strMap$.get.map { case (i, s) => (i, s.keyValue("en" -> "He")) } === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map()),
//      (3, Map("en" -> "Hello")),
//      (4, Map()),
//      (5, Map())
//    )
//
//    // Search multiple values. Multiple values have OR semantics:
//    // value("hi" OR "he")
//    Ns.int.strMap$.get.map { case (i, s) => (i, s.values("hi", "he")) } === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej")),
//      (5, Map())
//    )
//
//    // Search combination of key and multiple values. AND and OR semantics combined:
//    // key("en") AND value("he" OR "hi")
//    Ns.int.strMap$.get.map { case (i, s) => (i, s.keyValues("en" -> Seq("he", "hi"))) } === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello")),
//      (4, Map()),
//      (5, Map())
//    )
//
//    // Search combination of multiple keys and values. OR semantics between key/value pairs:
//    // (key("en") AND value("he" OR "hi)) OR
//    // (key("fr") AND value("bo"))
//    Ns.int.strMap$.get.map { case (i, s) => (i, s.keyValues("en" -> Seq("he", "hi"), "fr" -> Seq("bo"))) } === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello")),
//      (4, Map()),
//      (5, Map())
//    )
//  }
//
//  "Values (optional) - Int" in new CoreSetup {
//
//    Ns.int.intMap$ insert List(
//      (1, Some(Map("en" -> 10))),
//      (2, Some(Map("fr" -> 20, "en" -> 10))),
//      (3, Some(Map("en" -> 30))),
//      (4, Some(Map("da" -> 30))),
//      (5, None)
//    )
//
//    // Maps with value 10
//    Ns.int.intMap$.get.map { case (i, s) => (i, s.values(10)) } === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10)),
//      (3, Map()),
//      (4, Map()),
//      (5, Map())
//    )
//
//    // Search combination of key and value (Danish now excluded).
//    // key -> value pairs have AND semantics:
//    // key("en") AND value(10)
//    Ns.int.intMap$.get.map { case (i, s) => (i, s.keyValue("en" -> 10)) } === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10)),
//      (3, Map()),
//      (4, Map()),
//      (5, Map())
//    )
//
//    // Search multiple values. Multiple values have OR semantics:
//    // value("hi" OR "he")
//    Ns.int.intMap$.get.map { case (i, s) => (i, s.values(10, 30)) } === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10)),
//      (3, Map("en" -> 30)),
//      (4, Map("da" -> 30)),
//      (5, Map())
//    )
//
//    // Search combination of key and multiple values. AND and OR semantics combined:
//    // key("en") AND value("he" OR "hi")
//    Ns.int.intMap$.get.map { case (i, s) => (i, s.keyValues("en" -> Seq(10, 30))) } === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10)),
//      (3, Map("en" -> 30)),
//      (4, Map()),
//      (5, Map())
//    )
//
//    // Search combination of multiple keys and values. OR semantics between key/value pairs:
//    // (key("en") AND value("he" OR "hi)) OR
//    // (key("fr") AND value("bo"))
//    Ns.int.intMap$.get.map { case (i, s) => (i, s.keyValues("en" -> Seq(10, 30), "fr" -> Seq(20))) } === List(
//      (1, Map("en" -> 10)),
//      (2, Map("fr" -> 20, "en" -> 10)),
//      (3, Map("en" -> 30)),
//      (4, Map()),
//      (5, Map())
//    )
//  }
//
//
//  "Value (tacet)" in new Setup {
//
//    // One key, one value
//    Ns.int.strMap_("en" -> "Hi").get === List(1, 2)
//    Ns.int.intMap_("en" -> 10).get === List(1, 2)
//
//    // One key, multiple values (OR semantics)
//    Ns.int.strMap_("en" -> "Hi", "en" -> "He").get === List(1, 2, 3)
//    Ns.int.strMap_("en" -> "Hi|He").get === List(1, 2, 3)
//
//    Ns.int.intMap_("en" -> 10, "en" -> 30).get === List(1, 2, 3)
//
//    // Multiple keys
//    Ns.int.strMap_("en" -> "Hi", "fr" -> "Bon").get === List(1, 2)
//    Ns.int.intMap_("en" -> 10, "fr" -> 20).get === List(1, 2)
//
//    // Multiple keys with multiple values for some keys (OR semantics)
//    Ns.int.strMap_("en" -> "Hi", "en" -> "He", "fr" -> "Bon").get === List(1, 2, 3)
//    Ns.int.intMap_("en" -> 10, "en" -> 30, "fr" -> 20).get === List(1, 2, 3)
//
//    // All keys
//    Ns.int.strMap_("_" -> "He").get === List(3, 4)
//    Ns.int.intMap_("_" -> 30).get === List(3, 4)
//
//    // All keys with multiple values (OR semantics)
//    Ns.int.strMap_("_" -> "He", "_" -> "Bon").get === List(2, 3, 4)
//    Ns.int.intMap_("_" -> 20, "_" -> 30).get === List(2, 3, 4)
//  }
//
//
//  "Variable" in new Setup {
//
//    // Key is variable
//    Ns.int.strMap(en -> "Hi").get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi"))
//    )
//    Ns.int.intMap(en -> 10).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10))
//    )
//
//    // Value is variable
//    Ns.int.strMap("en" -> Hi).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi"))
//    )
//    Ns.int.intMap("en" -> i10).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10))
//    )
//
//    // Key and value are variables
//    Ns.int.strMap(en -> Hi).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi"))
//    )
//    Ns.int.intMap(en -> i10).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10))
//    )
//
//    // One key, multiple values (OR semantics)
//    Ns.int.strMap(en -> Hi, en -> He).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//    Ns.int.strMap(en -> regEx).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//
//    Ns.int.intMap(en -> i10, en -> 30).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10)),
//      (3, Map("en" -> 30))
//    )
//
//    // Multiple keys
//    Ns.int.strMap(en -> Hi, fr -> Bo).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))
//    )
//    Ns.int.intMap(en -> i10, fr -> i20).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("fr" -> 20, "en" -> 10))
//    )
//
//    // Multiple keys with multiple values for some keys (OR semantics)
//    Ns.int.strMap(en -> Hi, en -> He, fr -> Bo).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//    Ns.int.strMap(en -> regEx, fr -> Bo).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//
//    // All keys
//    Ns.int.strMap("_" -> He).get === List(
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej"))
//    )
//    Ns.int.intMap("_" -> i10).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10))
//    )
//
//    // All keys with multiple values (OR semantics)
//    Ns.int.strMap("_" -> He, "_" -> Bo).get === List(
//      (2, Map("fr" -> "Bonjour")),
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej"))
//    )
//
//    // Note that we can't combine searching for all keys and a specific key
//    expectCompileError(
//      """m(Ns.int.strMap("_" -> He, fr -> Bo))""",
//      "[Dsl2Model:getValues] Searching for all keys with `_` can't be combined with other key-values.")
//
//    // Results are coalesced to one Map when no other attributes are present in the molecule
//    Ns.strMap("_" -> He, "_" -> Bo).get === List(
//      Map("en" -> "Hello", "fr" -> "Bonjour", "da" -> "Hej")
//    )
//  }
//
//
//  "Input" in new Setup {
//
//    // One key, one value
//    m(Ns.int.strMap(?))(Map("en" -> "Hi")).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi"))
//    )
//    m(Ns.int.intMap(?))(Map("en" -> 10)).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10))
//    )
//
//    // One key, multiple values
//    // Note that we can't apply a map with multiple identical keys to achieve OR semantics.
//    // m(Ns.int.strMap(?)).apply(Map("en" -> "Hi", "en" -> "He")) // no good
//
//    // Instead we can supply 2 Maps
//    m(Ns.int.strMap(?))(Map("en" -> "Hi"), Map("en" -> "He")).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//    // or a Seq of Maps (useful if saved in a variable)
//    m(Ns.int.strMap(?))(Seq(Map("en" -> "Hi"), Map("en" -> "He"))).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//    // The easiest solution is though to use a regEx
//    m(Ns.int.strMap(?))(Map("en" -> "Hi|He")).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//
//    // With other types we need to apply multiple Maps if we
//    // have multiple identical keys with varying values to lookup
//    m(Ns.int.intMap(?))(Map("en" -> 10), Map("en" -> 30)).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10)),
//      (3, Map("en" -> 30))
//    )
//    // OBS: Be aware not to use a single map since pairs with identical
//    // keys will silently coalesce!!
//    m(Ns.int.intMap(?))(Map("en" -> 10, "en" -> 30)) // keys coalesce!
//
//    // Multiple (different) keys
//    m(Ns.int.strMap(?))(Map("en" -> "Hi", "fr" -> "Bo")).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi"))
//    )
//    m(Ns.int.intMap(?))(Map("en" -> 10, "fr" -> 20)).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("fr" -> 20, "en" -> 10))
//    )
//
//    // Multiple keys with multiple values for some keys (using OR regex)
//    m(Ns.int.strMap(?))(Map("en" -> "Hi|He", "fr" -> "Bo")).get === List(
//      (1, Map("en" -> "Hi there")),
//      (2, Map("fr" -> "Bonjour", "en" -> "Oh, Hi")),
//      (3, Map("en" -> "Hello"))
//    )
//    m(Ns.int.intMap(?))(Map("en" -> 10), Map("en" -> 30), Map("fr" -> 20)).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("fr" -> 20, "en" -> 10)),
//      (3, Map("en" -> 30))
//    )
//
//    // All keys
//    m(Ns.int.strMap(?))(Map("_" -> "He")).get === List(
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej"))
//    )
//    m(Ns.int.intMap(?))(Map("_" -> 10)).get === List(
//      (1, Map("en" -> 10)),
//      (2, Map("en" -> 10))
//    )
//
//    // All keys with multiple values (using OR regex)
//    m(Ns.int.strMap(?))(Map("_" -> "He|Bo")).get === List(
//      (2, Map("fr" -> "Bonjour")),
//      (3, Map("en" -> "Hello")),
//      (4, Map("da" -> "Hej"))
//    )
//
//    // Results are coalesced to one Map when no other attributes are present in the molecule
//    m(Ns.strMap(?))(Map("_" -> "He|Bo")).get === List(
//      Map("en" -> "Hello", "fr" -> "Bonjour", "da" -> "Hej")
//    )
//  }
//
//
//  "Input (tacet)" in new Setup {
//
//    // One key, one value
//    m(Ns.int.strMap_(?))(Map("en" -> "Hi")).get === List(1, 2)
//    m(Ns.int.intMap_(?))(Map("en" -> 10)).get === List(1, 2)
//
//
//    // One key, multiple values (using OR regex)
//    m(Ns.int.strMap_(?))(Map("en" -> "Hi|He")).get === List(1, 2, 3)
//
//    // Remember to apply multiple Maps if some keys are identical to avoid
//    // that they (silently) coalese
//    m(Ns.int.intMap_(?))(Map("en" -> 10), Map("en" -> 30)).get === List(1, 2, 3)
//
//    // Multiple keys
//    m(Ns.int.strMap_(?))(Map("en" -> "Hi", "fr" -> "Bo")).get === List(1, 2)
//    m(Ns.int.intMap_(?))(Map("en" -> 10, "fr" -> 20)).get === List(1, 2)
//
//    // Multiple keys with multiple values for some keys (using OR regex)
//    m(Ns.int.strMap_(?))(Map("en" -> "Hi|He", "fr" -> "Bo")).get === List(1, 2, 3)
//    m(Ns.int.intMap_(?))(Map("en" -> 10), Map("en" -> 30, "fr" -> 20)).get === List(1, 2, 3)
//
//    // All keys
//    m(Ns.int.strMap_(?))(Map("_" -> "He")).get === List(3, 4)
//    m(Ns.int.intMap_(?))(Map("_" -> 30)).get === List(3, 4)
//
//    // All keys with multiple values (using OR regex)
//    m(Ns.int.strMap_(?))(Map("_" -> "He"), Map("_" -> "Bo")).get === List(2, 3, 4)
//    m(Ns.int.intMap_(?))(Map("_" -> 20), Map("_" -> 30)).get === List(2, 3, 4)
//
//    // Note that we can't combine searching for all keys (_) and a specific key (fr)
//    (m(Ns.int.strMap_(?))(Map("_" -> "He"), Map("fr" -> "Bo")) must throwA[RuntimeException]).message === "Got the exception java.lang.RuntimeException: " +
//      "[InputMolecule_1:bindValues1] Searching for all keys (with `_`) can't be combined with other key(s): fr"
//  }
//}