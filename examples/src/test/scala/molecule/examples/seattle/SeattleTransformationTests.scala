//package molecule
//package examples.seattle
//import molecule.ast.model._
//import molecule.ast.query._
////import molecule.dsl.DbSchema._
//import molecule.examples.seattle.dsl.seattle._
//import scala.language.reflectiveCalls
//
//
//class SeattleTransformationTests extends SeattleSpec {
//
//  "A first query" >> {
//
//    // Testing that a molecule goes correctly through 3 transformations:
//
//    m(Community.name) --> {
//
//      // Dsl --> Model
//      // Each attribute is modelled as an "Atom"
//
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue)))
//
//    } --> {
//
//      // Model --> Query
//      // Each atom transforms to one or more query clauses and we
//      // introduce value markers (like 'a) to connect elements
//
//      Query(
//        Find(List(
//          Var("b", "String"))),
//        Where(List(
//          DataClause("a", KW("community", "name"), "String", "b"))))
//
//    } --> {
//
//      // Query --> Query string
//      // The Query object is transformed to a query string
//
//      """[:find ?b
//        | :where [?a :community/name ?b]]""".stripMargin
//    }
//  }
//
//
//  "Querying _for_ attribute values" >> {
//
//    // Multiple attributes
//    m(Community.name.url.category) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue),
//        Atom("community", "url", "String", 1, VarValue),
//        Atom("community", "category", "Set[String]", 2, VarValue))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"),
//          Var("c", "String"),
//          AggrExpr("distinct", List(), Var("d", "Set[String]"), "Set[String]"))),
//        Where(List(
//          DataClause("a", KW("community", "name"), "String", "b"),
//          DataClause("a", KW("community", "url"), "String", "c"),
//          DataClause("a", KW("community", "category"), "Set[String]", "d")))
//      ) -->
//      """[:find ?b ?c (distinct ?d)
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/url ?c]
//        |   [?a :community/category ?d]]""".stripMargin
//  }
//
//  "Querying _by_ attribute values" >> {
//
//    // Names of twitter communities
//    m(Community.name.`type`("twitter")) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue),
//        Atom("community", "type", "String", 1, Eq(List("twitter")), Some(":community.type/")))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"))),
//        Where(List(
//          DataClause("a", KW("community", "name"), "String", "b"),
//          DataClause("a", KW("community", "type"), "String", Val(":community.type/twitter", "String"))))
//      ) -->
//      """[:find ?b
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/type ":community.type/twitter"]]""".stripMargin
//
//
//    // Categories (many-cardinality) of the Belltown community
//    m(Community.name("belltown").category) -->
//      Model(List(
//        Atom("community", "name", "String", 1, Eq(List("belltown"))),
//        Atom("community", "category", "Set[String]", 2, VarValue))
//      ) -->
//      Query(
//        Find(List(
//          AggrExpr("distinct", List(), Var("c", "Set[String]"), "Set[String]"))),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Val("belltown", "String"), Empty),
//          DataClause(ImplDS, Var("a", "Set[String]"), KW("community", "category"), Var("c", "Set[String]"), Empty)))
//      ) -->
//      """[:find (distinct ?c)
//        | :where
//        |   [?a :community/name "belltown"]
//        |   [?a :community/category ?c]]""".stripMargin
//
//
//    // Names of news or arts communities - transforms to a query using Rules
//    m(Community.name.category("news", "arts")) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue),
//        Atom("community", "category", "Set[String]", 2, Eq(List("news", "arts")), None))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"))),
//        In(List(), List(
//          Rule("rule1", List(Var("a", "")), List(DataClause(ImplDS, Var("a", "Set[String]"), KW("community", "category"), Val("news", "String"), Empty))),
//          Rule("rule1", List(Var("a", "")), List(DataClause(ImplDS, Var("a", "Set[String]"), KW("community", "category"), Val("arts", "String"), Empty)))), List(DS)),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          RuleInvocation("rule1", List(Var("a", "")))))
//      ) -->
//      """[:find ?b
//        | :in $ %
//        | :where
//        |   [?a :community/name ?b]
//        |   (rule1 ?a)]
//        |
//        |INPUTS:
//        |List(
//        |  1 datomic.db.Db@xxx
//        |  2 [[[rule1 ?a] [?a :community/category "news"]]
//        |     [[rule1 ?a] [?a :community/category "arts"]]]
//        |)""".stripMargin
//  }
//
//
//  "Querying across references" >> {
//
//    // Communities in north eastern region
//    // Ref's are modelelled as "Bond"'s (between Atoms)
//    m(Community.name.Neighborhood.District.region("ne")) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Bond("community", "neighborhood"),
//        Bond("neighborhood", "district"),
//        Atom("district", "region", "String", 1, Eq(List("ne")), Some(":district.region/")))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"))),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "neighborhood"), Var("c", "String"), Empty),
//          DataClause(ImplDS, Var("c", "String"), KW("neighborhood", "district"), Var("d", "String"), Empty),
//          DataClause(ImplDS, Var("d", "String"), KW("district", "region"), Val(":district.region/ne", "String"), Empty)))
//      ) -->
//      """[:find ?b
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/neighborhood ?c]
//        |   [?c :neighborhood/district ?d]
//        |   [?d :district/region ":district.region/ne"]]""".stripMargin
//
//
//    // Communities and their region
//    m(Community.name.Neighborhood.District.region) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Bond("community", "neighborhood"),
//        Bond("neighborhood", "district"),
//        Atom("district", "region", "String", 1, EnumVal, Some(":district.region/")))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"),
//          Var("d2", "String"))),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "neighborhood"), Var("c", "String"), Empty),
//          DataClause(ImplDS, Var("c", "String"), KW("neighborhood", "district"), Var("d", "String"), Empty),
//          DataClause(ImplDS, Var("d", "String"), KW("district", "region"), Var("e", "String"), Empty),
//          DataClause(ImplDS, Var("e", "String"), KW("db", "ident"), Var("d1", "String"), Empty),
//          Funct(".getName ^clojure.lang.Keyword", List(Var("d1", "")), ScalarBinding(Var("d2", "")))))
//      ) -->
//      """[:find ?b ?e2
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/neighborhood ?c]
//        |   [?c :neighborhood/district ?d]
//        |   [?d :district/region ?e]
//        |   [?e :db/ident ?e1]
//        |   [(.getName ^clojure.lang.Keyword ?e1) ?e2]]""".stripMargin
//  }
//
//
//  "Advanced queries - parameterizing queries" >> {
//
//    /** ******* Single input parameter **************************/
//
//    // Community input molecule awaiting some type value
//    m(Community.name.`type`(?)) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Atom("community", "type", "String", 1, Qm, Some(":community.type/")))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"))),
//        In(List(
//          Placeholder("c", KW("community", "type"), "String", Some(":community.type/"), "a")), List(), List(DS)),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Var("c", "String"), Empty)))
//      ) -->
//      """[:find ?b
//        | :in $ ?c
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/type ?c]]""".stripMargin
//
//    // Applying a value completes the query
//    m(Community.name.`type`(?)).apply("twitter") -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Atom("community", "type", "String", 1, Qm, Some(":community.type/")))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"))),
//        In(List(
//          InVar(
//            ScalarBinding(Var("c", "String")),
//            List(List(":community.type/twitter")))), List(), List(DS)),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Var("c", "String"), Empty)))
//      ) -->
//      """[:find ?b
//        | :in $ ?c
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/type ?c]]
//        |
//        |INPUTS:
//        |List(
//        |  1 datomic.db.Db@xxx
//        |  2 :community.type/twitter
//        |)""".stripMargin
//
//
//    // Add a `!` to the question mark to return the input value too
//    m(Community.name.`type`(?!)) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Atom("community", "type", "String", 1, QmR, Some(":community.type/")))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"),
//          Var("b2", "String"))),
//        In(List(
//          Placeholder("c", KW("community", "type"), "String", Some(":community.type/"), "")), List(), List(DS)),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Var("c", "String"), Empty),
//          DataClause(ImplDS, Var("c", "String"), KW("db", "ident"), Var("b1", "String"), Empty),
//          Funct(".getName ^clojure.lang.Keyword", List(Var("b1", "")), ScalarBinding(Var("b2", "")))))
//      ) -->
//      """[:find ?b ?c2
//        | :in $ ?c
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/type ?c]
//        |   [?c :db/ident ?c1]
//        |   [(.getName ^clojure.lang.Keyword ?c1) ?c2]]""".stripMargin
//
//
//    m(Community.name.`type`(?!)).apply("twitter") -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Atom("community", "type", "String", 1, QmR, Some(":community.type/")))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"),
//          Var("b2", "String"))),
//        In(List(
//          InVar(
//            ScalarBinding(Var("c", "String")),
//            List(List(":community.type/twitter")))), List(), List(DS)),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Var("c", "String"), Empty),
//          DataClause(ImplDS, Var("c", "String"), KW("db", "ident"), Var("b1", "String"), Empty),
//          Funct(".getName ^clojure.lang.Keyword", List(Var("b1", "")), ScalarBinding(Var("b2", "")))))
//      ) -->
//      """[:find ?b ?c2
//        | :in $ ?c
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/type ?c]
//        |   [?c :db/ident ?c1]
//        |   [(.getName ^clojure.lang.Keyword ?c1) ?c2]]
//        |
//        |INPUTS:
//        |List(
//        |  1 datomic.db.Db@xxx
//        |  2 :community.type/twitter
//        |)""".stripMargin
//
//
//
//    // Multiple input values - logical OR ------------------------
//
//    m(Community.name.`type`(?!)).apply("facebook_page" or "twitter") -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Atom("community", "type", "String", 1, QmR, Some(":community.type/")))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"),
//          Var("b2", "String"))),
//        In(List(
//          InVar(
//            CollectionBinding(Var("c", "String")),
//            List(List(":community.type/facebook_page", ":community.type/twitter")))), List(), List(DS)),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Var("c", "String"), Empty),
//          DataClause(ImplDS, Var("c", "String"), KW("db", "ident"), Var("b1", "String"), Empty),
//          Funct(".getName ^clojure.lang.Keyword", List(Var("b1", "")), ScalarBinding(Var("b2", "")))))
//      ) -->
//      """[:find ?b ?c2
//        | :in $ [?c ...]
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/type ?c]
//        |   [?c :db/ident ?c1]
//        |   [(.getName ^clojure.lang.Keyword ?c1) ?c2]]
//        |
//        |INPUTS:
//        |List(
//        |  1 datomic.db.Db@xxx
//        |  2 [:community.type/facebook_page, :community.type/twitter]
//        |)""".stripMargin
//
//
//    // Finding communities of type "facebook_page" OR "twitter"
//    // The following 3 notation variations transform in the same way
//    m(Community.name.`type`(?!)).apply("facebook_page" or "twitter") -->
//      //    m(Community.name.`type`(?!)).apply("facebook_page", "twitter") -->
//      //    m(Community.name.`type`(?!)).apply(List("facebook_page", "twitter")) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Atom("community", "type", "String", 1, QmR, Some(":community.type/")))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"),
//          Var("b2", "String"))),
//        In(List(
//          InVar(
//            CollectionBinding(Var("c", "String")),
//            List(List(":community.type/facebook_page", ":community.type/twitter")))), List(), List(DS)),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Var("c", "String"), Empty),
//          DataClause(ImplDS, Var("c", "String"), KW("db", "ident"), Var("b1", "String"), Empty),
//          Funct(".getName ^clojure.lang.Keyword", List(Var("b1", "")), ScalarBinding(Var("b2", "")))))
//      ) -->
//      """[:find ?b ?c2
//        | :in $ [?c ...]
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/type ?c]
//        |   [?c :db/ident ?c1]
//        |   [(.getName ^clojure.lang.Keyword ?c1) ?c2]]
//        |
//        |INPUTS:
//        |List(
//        |  1 datomic.db.Db@xxx
//        |  2 [:community.type/facebook_page, :community.type/twitter]
//        |)""".stripMargin
//
//
//    /** ******* Multiple input parameters **************************/
//
//    // Single tuple of input values - AND-semantics ------------------------
//
//    m(Community.name.`type`(?).orgtype(?)) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Atom("community", "type", "String", 1, Qm, Some(":community.type/")),
//        Atom("community", "orgtype", "String", 1, Qm, Some(":community.orgtype/")))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"))),
//        In(
//          List(
//            Placeholder("c", KW("community", "type"), "String", Some(":community.type/"), "a"),
//            Placeholder("d", KW("community", "orgtype"), "String", Some(":community.orgtype/"), "a")),
//          List(),
//          List(DS)),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Var("c", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "orgtype"), Var("d", "String"), Empty)))
//      ) -->
//      """[:find ?b
//        | :in $ ?c ?d
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/type ?c]
//        |   [?a :community/orgtype ?d]]""".stripMargin
//
//
//    // The following 3 notation variations transform in the same way
//
//    m(Community.name.`type`(?).orgtype(?)).apply("email_list" and "community") -->
//      //    m(Community.name.`type`(?).orgtype(?)).apply("email_list", "community") -->
//      //    m(Community.name.`type`(?).orgtype(?)).apply(List(("email_list", "community"))) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Atom("community", "type", "String", 1, Qm, Some(":community.type/")),
//        Atom("community", "orgtype", "String", 1, Qm, Some(":community.orgtype/")))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"))),
//        In(
//          List(
//            InVar(
//              RelationBinding(
//                List(Var("c", "String"), Var("d", "String"))),
//              List(
//                List(":community.type/email_list", ":community.orgtype/community")))),
//          List(),
//          List(DS)),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Var("c", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "orgtype"), Var("d", "String"), Empty)))
//      ) -->
//      """[:find ?b
//        | :in $ [[ ?c ?d ]]
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/type ?c]
//        |   [?a :community/orgtype ?d]]
//        |
//        |INPUTS:
//        |List(
//        |  1 datomic.db.Db@xxx
//        |  2 [[:community.type/email_list, :community.orgtype/community]]
//        |)""".stripMargin
//
//
//    // Multiple tuples of input values ------------------------
//
//    // Communities of some `type` AND some `orgtype` (include input values!)
//    m(Community.name.`type`(?!).orgtype(?!)) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Atom("community", "type", "String", 1, QmR, Some(":community.type/")),
//        Atom("community", "orgtype", "String", 1, QmR, Some(":community.orgtype/")))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"),
//          Var("b2", "String"),
//          Var("c2", "String"))),
//        In(
//          List(
//            Placeholder("c", KW("community", "type"), "String", Some(":community.type/"), ""),
//            Placeholder("d", KW("community", "orgtype"), "String", Some(":community.orgtype/"), "")),
//          List(),
//          List(DS)),
//
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//
//          DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Var("c", "String"), Empty),
//          DataClause(ImplDS, Var("c", "String"), KW("db", "ident"), Var("b1", "String"), Empty),
//          Funct(".getName ^clojure.lang.Keyword", List(Var("b1", "")), ScalarBinding(Var("b2", ""))),
//
//          DataClause(ImplDS, Var("a", "String"), KW("community", "orgtype"), Var("d", "String"), Empty),
//          DataClause(ImplDS, Var("d", "String"), KW("db", "ident"), Var("c1", "String"), Empty),
//          Funct(".getName ^clojure.lang.Keyword", List(Var("c1", "")), ScalarBinding(Var("c2", "")))))
//      ) -->
//      """[:find ?b ?c2 ?d2
//        | :in $ ?c ?d
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/type ?c]
//        |   [?c :db/ident ?c1]
//        |   [(.getName ^clojure.lang.Keyword ?c1) ?c2]
//        |   [?a :community/orgtype ?d]
//        |   [?d :db/ident ?d1]
//        |   [(.getName ^clojure.lang.Keyword ?d1) ?d2]]""".stripMargin
//
//
//    // The following 3 notation variations transform in the same way
//
//    //    m(Community.name.`type`(?!).orgtype(?!)).apply(("email_list" and "community") or ("website" and "commercial")) -->
//    //    m(Community.name.`type`(?!).orgtype(?!)).apply(("email_list", "community"), ("website", "commercial")) -->
//    m(Community.name.`type`(?!).orgtype(?!)).apply(Seq(("email_list", "community"), ("website", "commercial"))) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Atom("community", "type", "String", 1, QmR, Some(":community.type/")),
//        Atom("community", "orgtype", "String", 1, QmR, Some(":community.orgtype/")))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"),
//          Var("b2", "String"),
//          Var("c2", "String"))),
//        In(
//          List(
//            InVar(
//              RelationBinding(
//                List(Var("c", "String"), Var("d", "String"))),
//              List(
//                List(":community.type/email_list", ":community.orgtype/community"),
//                List(":community.type/website", ":community.orgtype/commercial")))),
//          List(),
//          List(DS)),
//
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//
//          DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Var("c", "String"), Empty),
//          DataClause(ImplDS, Var("c", "String"), KW("db", "ident"), Var("b1", "String"), Empty),
//          Funct(".getName ^clojure.lang.Keyword", List(Var("b1", "")), ScalarBinding(Var("b2", ""))),
//
//          DataClause(ImplDS, Var("a", "String"), KW("community", "orgtype"), Var("d", "String"), Empty),
//          DataClause(ImplDS, Var("d", "String"), KW("db", "ident"), Var("c1", "String"), Empty),
//          Funct(".getName ^clojure.lang.Keyword", List(Var("c1", "")), ScalarBinding(Var("c2", "")))))
//      ) -->
//      """[:find ?b ?c2 ?d2
//        | :in $ [[ ?c ?d ]]
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/type ?c]
//        |   [?c :db/ident ?c1]
//        |   [(.getName ^clojure.lang.Keyword ?c1) ?c2]
//        |   [?a :community/orgtype ?d]
//        |   [?d :db/ident ?d1]
//        |   [(.getName ^clojure.lang.Keyword ?d1) ?d2]]
//        |
//        |INPUTS:
//        |List(
//        |  1 datomic.db.Db@xxx
//        |  2 [[:community.type/email_list, :community.orgtype/community], [:community.type/website, :community.orgtype/commercial]]
//        |)""".stripMargin
//  }
//
//
//  "Invoking functions in queries" >> {
//
//    m(Community.name < "C") -->
//      Model(List(
//        Atom("community", "name", "String", 1, Lt("C")))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"))),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          Funct(".compareTo ^String", List(Var("b", ""), Val("C", "String")), ScalarBinding(Var("a1", ""))),
//          Funct("<", List(Var("a1", ""), Val(0, "Int")), NoBinding)))
//      ) -->
//      """[:find ?b
//        | :where
//        |   [?a :community/name ?b]
//        |   [(.compareTo ^String ?b "C") ?b1]
//        |   [(< ?b1 0) ]]""".stripMargin
//
//    m(Community.name < ?) -->
//      Model(List(
//        Atom("community", "name", "String", 1, Lt(Qm)))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"))),
//        In(List(
//          Placeholder("a1", KW("community", "name"), "String", None, "")), List(), List(DS)),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          Funct(".compareTo ^String", List(Var("b", ""), Var("a1", "")), ScalarBinding(Var("a2", ""))),
//          Funct("<", List(Var("a2", ""), Val(0, "Int")), NoBinding)))
//      ) -->
//      """[:find ?b
//        | :in $ ?b1
//        | :where
//        |   [?a :community/name ?b]
//        |   [(.compareTo ^String ?b ?b1) ?b2]
//        |   [(< ?b2 0) ]]""".stripMargin
//
//    m(Community.name < ?).apply("C") -->
//      Model(List(
//        Atom("community", "name", "String", 1, Lt(Qm)))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"))),
//        In(List(
//          InVar(ScalarBinding(Var("a1", "String")), List(List("C")))), List(), List(DS)),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          Funct(".compareTo ^String", List(Var("b", ""), Var("a1", "")), ScalarBinding(Var("a2", ""))),
//          Funct("<", List(Var("a2", ""), Val(0, "Int")), NoBinding)))
//      ) -->
//      """[:find ?b
//        | :in $ ?b1
//        | :where
//        |   [?a :community/name ?b]
//        |   [(.compareTo ^String ?b ?b1) ?b2]
//        |   [(< ?b2 0) ]]
//        |
//        |INPUTS:
//        |List(
//        |  1 datomic.db.Db@xxx
//        |  2 C
//        |)""".stripMargin
//  }
//
//
//  "Querying with fulltext search" >> {
//
//    m(Community.name contains "Wallingford") -->
//      Model(List(
//        Atom("community", "name", "String", 1, Fulltext(List("Wallingford"))))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"))),
//        Where(List(
//          Funct("fulltext",
//            List(DS(), KW("community", "name"), Val("Wallingford", "String")),
//            RelationBinding(List(Var("a", ""), Var("b", ""))))))
//      ) -->
//      """[:find ?b
//        | :where
//        |   [(fulltext $ :community/name "Wallingford") [[ ?a ?b ]]]]""".stripMargin
//
//
//    m(Community.name contains ?) -->
//      Model(List(
//        Atom("community", "name", "String", 1, Fulltext(Seq(Qm))))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"))),
//        In(List(
//          Placeholder("a1", KW("community", "name"), "String", None, "")), List(), List(DS)),
//        Where(List(
//          Funct("fulltext",
//            List(DS(), KW("community", "name"), Var("a1", "")),
//            RelationBinding(List(Var("a", ""), Var("b", ""))))))
//      ) -->
//      """[:find ?b
//        | :in $ ?b1
//        | :where
//        |   [(fulltext $ :community/name ?b1) [[ ?a ?b ]]]]""".stripMargin
//
//
//    m(Community.name contains ?).apply("Wallingford") -->
//      Model(List(
//        Atom("community", "name", "String", 1, Fulltext(List(Qm))))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"))),
//        In(List(
//          InVar(ScalarBinding(Var("a1", "String")), List(List("Wallingford")))), List(), List(DS)),
//        Where(List(
//          Funct("fulltext",
//            List(DS(), KW("community", "name"), Var("a1", "")),
//            RelationBinding(List(Var("a", ""), Var("b", ""))))))
//      ) -->
//      """[:find ?b
//        | :in $ ?b1
//        | :where
//        |   [(fulltext $ :community/name ?b1) [[ ?a ?b ]]]]
//        |
//        |INPUTS:
//        |List(
//        |  1 datomic.db.Db@xxx
//        |  2 Wallingford
//        |)""".stripMargin
//
//
//    // Fulltext search on many-attribute (`category`)
//
//    m(Community.name.`type`("website").category contains "food") -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Atom("community", "type", "String", 1, Eq(List("website")), Some(":community.type/")),
//        Atom("community", "category", "Set[String]", 2, Fulltext(List("food")), None))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"),
//          AggrExpr("distinct", List(), Var("d", "Set[String]"), "Set[String]"))),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Val(":community.type/website", "String"), Empty),
//          Funct("fulltext",
//            List(DS(), KW("community", "category"), Val("food", "String")),
//            RelationBinding(List(Var("a", ""), Var("d", ""))))))
//      ) -->
//      """[:find ?b (distinct ?d)
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/type ":community.type/website"]
//        |   [(fulltext $ :community/category "food") [[ ?a ?d ]]]]""".stripMargin
//
//
//    m(Community.name.`type`(?).category contains ?) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Atom("community", "type", "String", 1, Qm, Some(":community.type/")),
//        Atom("community", "category", "Set[String]", 2, Fulltext(List(Qm)), None))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"),
//          AggrExpr("distinct", List(), Var("d", "Set[String]"), "Set[String]"))),
//        In(List(
//          Placeholder("c", KW("community", "type"), "String", Some(":community.type/"), "a"),
//          Placeholder("c1", KW("community", "category"), "Set[String]", None, "")), List(), List(DS)),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Var("c", "String"), Empty),
//          Funct("fulltext",
//            List(DS(), KW("community", "category"), Var("c1", "")),
//            RelationBinding(List(Var("a", ""), Var("d", ""))))))
//      ) -->
//      """[:find ?b (distinct ?d)
//        | :in $ ?c ?d1
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/type ?c]
//        |   [(fulltext $ :community/category ?d1) [[ ?a ?d ]]]]""".stripMargin
//
//
//    m(Community.name.`type`(?).category contains ?).apply("website", Set("food")) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Atom("community", "type", "String", 1, Qm, Some(":community.type/")),
//        Atom("community", "category", "Set[String]", 2, Fulltext(List(Qm)), None))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"),
//          AggrExpr("distinct", List(), Var("d", "Set[String]"), "Set[String]"))),
//        In(
//          List(
//            InVar(
//              RelationBinding(List(Var("c", "String"), Var("c1", "Set[String]"))),
//              // Todo: set is saved wrongly as a string!
//              List(List(":community.type/website", "Set(food)")))),
//          List(),
//          List(DS)),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Var("c", "String"), Empty),
//          Funct("fulltext",
//            List(DS(), KW("community", "category"), Var("c1", "")),
//            RelationBinding(List(Var("a", ""), Var("d", ""))))))
//      ) -->
//      """[:find ?b (distinct ?d)
//        | :in $ [[ ?c ?d1 ]]
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/type ?c]
//        |   [(fulltext $ :community/category ?d1) [[ ?a ?d ]]]]
//        |
//        |INPUTS:
//        |List(
//        |  1 datomic.db.Db@xxx
//        |  2 [[:community.type/website, Set(food)]]
//        |)""".stripMargin
//  }
//
//
//  "Querying with rules (logical OR)" >> {
//
//    // Social media
//
//    m(Community.name.`type`("twitter" or "facebook_page")) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Atom("community", "type", "String", 1, Eq(List("twitter", "facebook_page")), Some(":community.type/")))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"))),
//        In(List(), List(
//          Rule("rule1", List(Var("a", "")), List(
//            DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Val(":community.type/twitter", "String"), Empty))),
//          Rule("rule1", List(Var("a", "")), List(
//            DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Val(":community.type/facebook_page", "String"), Empty)))), List(DS)),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          RuleInvocation("rule1", List(Var("a", "")))))
//      ) -->
//      """[:find ?b
//        | :in $ %
//        | :where
//        |   [?a :community/name ?b]
//        |   (rule1 ?a)]
//        |
//        |INPUTS:
//        |List(
//        |  1 datomic.db.Db@xxx
//        |  2 [[[rule1 ?a] [?a :community/type ":community.type/twitter"]]
//        |     [[rule1 ?a] [?a :community/type ":community.type/facebook_page"]]]
//        |)""".stripMargin
//
//
//    m(Community.name.Neighborhood.District.region("ne" or "sw")) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Bond("community", "neighborhood"),
//        Bond("neighborhood", "district"),
//        Atom("district", "region", "String", 1, Eq(List("ne", "sw")), Some(":district.region/")))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"))),
//        In(List(), List(
//          Rule("rule1", List(Var("d", "")), List(
//            DataClause(ImplDS, Var("d", "String"), KW("district", "region"), Val(":district.region/ne", "String"), Empty))),
//          Rule("rule1", List(Var("d", "")), List(
//            DataClause(ImplDS, Var("d", "String"), KW("district", "region"), Val(":district.region/sw", "String"), Empty)))), List(DS)),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "neighborhood"), Var("c", "String"), Empty),
//          DataClause(ImplDS, Var("c", "String"), KW("neighborhood", "district"), Var("d", "String"), Empty),
//          RuleInvocation("rule1", List(Var("d", "")))))
//      ) -->
//      """[:find ?b
//        | :in $ %
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/neighborhood ?c]
//        |   [?c :neighborhood/district ?d]
//        |   (rule1 ?d)]
//        |
//        |INPUTS:
//        |List(
//        |  1 datomic.db.Db@xxx
//        |  2 [[[rule1 ?d] [?d :district/region ":district.region/ne"]]
//        |     [[rule1 ?d] [?d :district/region ":district.region/sw"]]]
//        |)""".stripMargin
//
//
//    // Social media in southern regions
//    m(Community.name.`type`("twitter" or "facebook_page").Neighborhood.District.region("sw" or "s" or "se")) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Atom("community", "type", "String", 1, Eq(List("twitter", "facebook_page")), Some(":community.type/")),
//        Bond("community", "neighborhood"),
//        Bond("neighborhood", "district"),
//        Atom("district", "region", "String", 1, Eq(List("sw", "s", "se")), Some(":district.region/")))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"))),
//
//        In(List(), List(
//          Rule("rule1", List(Var("a", "")), List(
//            DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Val(":community.type/twitter", "String"), Empty))),
//          Rule("rule1", List(Var("a", "")), List(
//            DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Val(":community.type/facebook_page", "String"), Empty))),
//
//          Rule("rule2", List(Var("e", "")), List(
//            DataClause(ImplDS, Var("e", "String"), KW("district", "region"), Val(":district.region/sw", "String"), Empty))),
//          Rule("rule2", List(Var("e", "")), List(
//            DataClause(ImplDS, Var("e", "String"), KW("district", "region"), Val(":district.region/s", "String"), Empty))),
//          Rule("rule2", List(Var("e", "")), List(
//            DataClause(ImplDS, Var("e", "String"), KW("district", "region"), Val(":district.region/se", "String"), Empty)))), List(DS)),
//
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          RuleInvocation("rule1", List(Var("a", ""))),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "neighborhood"), Var("d", "String"), Empty),
//          DataClause(ImplDS, Var("d", "String"), KW("neighborhood", "district"), Var("e", "String"), Empty),
//          RuleInvocation("rule2", List(Var("e", "")))))
//      ) -->
//      """[:find ?b
//        | :in $ %
//        | :where
//        |   [?a :community/name ?b]
//        |   (rule1 ?a)
//        |   [?a :community/neighborhood ?d]
//        |   [?d :neighborhood/district ?e]
//        |   (rule2 ?e)]
//        |
//        |INPUTS:
//        |List(
//        |  1 datomic.db.Db@xxx
//        |  2 [[[rule1 ?a] [?a :community/type ":community.type/twitter"]]
//        |     [[rule1 ?a] [?a :community/type ":community.type/facebook_page"]]
//        |     [[rule2 ?e] [?e :district/region ":district.region/sw"]]
//        |     [[rule2 ?e] [?e :district/region ":district.region/s"]]
//        |     [[rule2 ?e] [?e :district/region ":district.region/se"]]]
//        |)""".stripMargin
//
//
//    m(Community.name.`type`(?).Neighborhood.District.region(?)) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Atom("community", "type", "String", 1, Qm, Some(":community.type/")),
//        Bond("community", "neighborhood"),
//        Bond("neighborhood", "district"),
//        Atom("district", "region", "String", 1, Qm, Some(":district.region/")))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"))),
//        In(List(
//          Placeholder("c", KW("community", "type"), "String", Some(":community.type/"), "a"),
//          Placeholder("f", KW("district", "region"), "String", Some(":district.region/"), "e")), List(), List(DS)),
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Var("c", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "neighborhood"), Var("d", "String"), Empty),
//          DataClause(ImplDS, Var("d", "String"), KW("neighborhood", "district"), Var("e", "String"), Empty),
//          DataClause(ImplDS, Var("e", "String"), KW("district", "region"), Var("f", "String"), Empty)))
//      ) -->
//      """[:find ?b
//        | :in $ ?c ?e
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/type ?c]
//        |   [?a :community/neighborhood ?d]
//        |   [?d :neighborhood/district ?e]
//        |   [?e :district/region ?f]]""".stripMargin
//
//
//    m(Community.name.`type`(?).Neighborhood.District.region(?)).apply(
//      ("twitter" or "facebook_page") and ("sw" or "s" or "se")
//      // or
//      // Seq("twitter", "facebook_page"), Seq("sw", "s", "se")
//    ) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Atom("community", "type", "String", 1, Qm, Some(":community.type/")),
//        Bond("community", "neighborhood"),
//        Bond("neighborhood", "district"),
//        Atom("district", "region", "String", 1, Qm, Some(":district.region/")))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "String"))),
//
//        In(List(), List(
//          Rule("rule1", List(Var("a", "")), List(
//            DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Val(":community.type/twitter", "String"), Empty))),
//          Rule("rule1", List(Var("a", "")), List(
//            DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Val(":community.type/facebook_page", "String"), Empty))),
//
//          Rule("rule2", List(Var("e", "")), List(
//            DataClause(ImplDS, Var("e", "String"), KW("district", "region"), Val(":district.region/sw", "String"), Empty))),
//          Rule("rule2", List(Var("e", "")), List(
//            DataClause(ImplDS, Var("e", "String"), KW("district", "region"), Val(":district.region/s", "String"), Empty))),
//          Rule("rule2", List(Var("e", "")), List(
//            DataClause(ImplDS, Var("e", "String"), KW("district", "region"), Val(":district.region/se", "String"), Empty)))), List(DS)),
//
//        Where(List(
//          DataClause(ImplDS, Var("a", "String"), KW("community", "name"), Var("b", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "type"), Var("c", "String"), Empty),
//          DataClause(ImplDS, Var("a", "String"), KW("community", "neighborhood"), Var("d", "String"), Empty),
//          DataClause(ImplDS, Var("d", "String"), KW("neighborhood", "district"), Var("e", "String"), Empty),
//          DataClause(ImplDS, Var("e", "String"), KW("district", "region"), Var("f", "String"), Empty),
//          RuleInvocation("rule1", List(Var("a", ""))),
//          RuleInvocation("rule2", List(Var("e", "")))))
//      ) -->
//      """[:find ?b
//        | :in $ %
//        | :where
//        |   [?a :community/name ?b]
//        |   [?a :community/type ?c]
//        |   [?a :community/neighborhood ?d]
//        |   [?d :neighborhood/district ?e]
//        |   [?e :district/region ?f]
//        |   (rule1 ?a)
//        |   (rule2 ?e)]
//        |
//        |INPUTS:
//        |List(
//        |  1 datomic.db.Db@xxx
//        |  2 [[[rule1 ?a] [?a :community/type ":community.type/twitter"]]
//        |     [[rule1 ?a] [?a :community/type ":community.type/facebook_page"]]
//        |     [[rule2 ?e] [?e :district/region ":district.region/sw"]]
//        |     [[rule2 ?e] [?e :district/region ":district.region/s"]]
//        |     [[rule2 ?e] [?e :district/region ":district.region/se"]]]
//        |)""".stripMargin
//  }
//
//
//  "Working with time" >> {
//
//    implicit val conn = loadFromFiles("seattle-schema1a.dtm", "seattle-data0a.dtm", 2)
//
//    m(Db.txInstant) -->
//      Model(List(
//        Atom("db", "txInstant", "Long", 1, VarValue))
//      ) -->
//      Query(
//        Find(List(
//          Var("b", "Long"))),
//        Where(List(
//          DataClause("a", KW("db", "txInstant"), "Long", "b")))
//      ) -->
//      """[:find ?b
//        | :where [?a :db/txInstant ?b]]""".stripMargin
//  }
//
//
//  "Manipulating data - insert" >> {
//
//    implicit val conn = loadSeattle(3)
//
//    /** Insert data into molecule and save ***********************************************/
//
//    testInsertMolecule(
//      Community
//        .name("AAA")
//        .url("myUrl")
//        .`type`("twitter")
//        .orgtype("personal")
//        .category("my", "favorites")
//        .Neighborhood.name("myNeighborhood")
//        .District.name("myDistrict").region("nw")
//    ) -->
//      Model(List(
//        Atom("community", "name", "String", 1, Eq(List("AAA")), None),
//        Atom("community", "url", "String", 1, Eq(List("myUrl")), None),
//        Atom("community", "type", "String", 1, Eq(List("twitter")), Some(":community.type/")),
//        Atom("community", "orgtype", "String", 1, Eq(List("personal")), Some(":community.orgtype/")),
//        Atom("community", "category", "Set[String]", 2, Eq(List("my", "favorites")), None),
//        Bond("community", "neighborhood", ""),
//        Atom("neighborhood", "name", "String", 1, Eq(List("myNeighborhood")), None),
//        Bond("neighborhood", "district", ""),
//        Atom("district", "name", "String", 1, Eq(List("myDistrict")), None),
//        Atom("district", "region", "String", 1, Eq(List("nw")), Some(":district.region/")))
//      ) -->
//      //  Some things to notice:
//      //  - We start from the end of the molecule and traverse left. This allow us to create
//      //    the entities that we will subsequently refer to (#db/id[:db.part/user -1000001])
//      //  - Enum values are prefixed with their namespace ("nw" becomes ":district.region/nw")
//      //  - Multiple values of many-cardinality attributes each get their own statement ("my" + "favorites")
//      //
//      //           action             temp id                   attribute                 value
//      """List(
//        |  List(  :db/add,   #db/id[:db.part/user -1000001],   :district/region       ,   :district.region/nw             )
//        |  List(  :db/add,   #db/id[:db.part/user -1000001],   :district/name         ,   myDistrict                      )
//        |  List(  :db/add,   #db/id[:db.part/user -1000002],   :neighborhood/district ,   #db/id[:db.part/user -1000001]  )
//        |  List(  :db/add,   #db/id[:db.part/user -1000002],   :neighborhood/name     ,   myNeighborhood                  )
//        |  List(  :db/add,   #db/id[:db.part/user -1000003],   :community/neighborhood,   #db/id[:db.part/user -1000002]  )
//        |  List(  :db/add,   #db/id[:db.part/user -1000003],   :community/category    ,   my                              )
//        |  List(  :db/add,   #db/id[:db.part/user -1000003],   :community/category    ,   favorites                       )
//        |  List(  :db/add,   #db/id[:db.part/user -1000003],   :community/orgtype     ,   :community.orgtype/personal     )
//        |  List(  :db/add,   #db/id[:db.part/user -1000003],   :community/type        ,   :community.type/twitter         )
//        |  List(  :db/add,   #db/id[:db.part/user -1000003],   :community/url         ,   myUrl                           )
//        |  List(  :db/add,   #db/id[:db.part/user -1000003],   :community/name        ,   AAA                             )
//        |)""".stripMargin
//
//
//    /** Use molecule as template to insert matching data sets ********************************************/
//
//    m(Community.name.url.`type`.orgtype.category.Neighborhood.name.District.name.region) -->
//      Model(List(
//        Atom("community", "name", "String", 1, VarValue, None),
//        Atom("community", "url", "String", 1, VarValue, None),
//        Atom("community", "type", "String", 1, EnumVal, Some(":community.type/")),
//        Atom("community", "orgtype", "String", 1, EnumVal, Some(":community.orgtype/")),
//        Atom("community", "category", "Set[String]", 2, VarValue, None),
//        Bond("community", "neighborhood"),
//        Atom("neighborhood", "name", "String", 1, VarValue, None),
//        Bond("neighborhood", "district"),
//        Atom("district", "name", "String", 1, VarValue, None),
//        Atom("district", "region", "String", 1, EnumVal, Some(":district.region/")))
//      ) -->
//      List(
//        List("DDD Blogging Georgetown", "http://www.blogginggeorgetown.com/", "blog", "commercial", Set("DD cat 1", "DD cat 2"), "DD Georgetown", "Greater Duwamish", "s"),
//        List("DDD Interbay District Blog", "http://interbayneighborhood.neighborlogs.com/", "blog", "community", Set("DD cat 3"), "DD Interbay", "Magnolia/Queen Anne", "w")
//      ) -->
//      // Semantically identical to the previous transaction
//      """List(
//        |  List(  :db/add,   #db/id[:db.part/user -1000001],   :district/region       ,   :district.region/s                             )
//        |  List(  :db/add,   #db/id[:db.part/user -1000001],   :district/name         ,   Greater Duwamish                               )
//        |  List(  :db/add,   #db/id[:db.part/user -1000002],   :neighborhood/district ,   #db/id[:db.part/user -1000001]                 )
//        |  List(  :db/add,   #db/id[:db.part/user -1000002],   :neighborhood/name     ,   DD Georgetown                                  )
//        |  List(  :db/add,   #db/id[:db.part/user -1000003],   :community/neighborhood,   #db/id[:db.part/user -1000002]                 )
//        |  List(  :db/add,   #db/id[:db.part/user -1000003],   :community/category    ,   DD cat 1                                       )
//        |  List(  :db/add,   #db/id[:db.part/user -1000003],   :community/category    ,   DD cat 2                                       )
//        |  List(  :db/add,   #db/id[:db.part/user -1000003],   :community/orgtype     ,   :community.orgtype/commercial                  )
//        |  List(  :db/add,   #db/id[:db.part/user -1000003],   :community/type        ,   :community.type/blog                           )
//        |  List(  :db/add,   #db/id[:db.part/user -1000003],   :community/url         ,   http://www.blogginggeorgetown.com/             )
//        |  List(  :db/add,   #db/id[:db.part/user -1000003],   :community/name        ,   DDD Blogging Georgetown                        )
//        |  List(  :db/add,   #db/id[:db.part/user -1000004],   :district/region       ,   :district.region/w                             )
//        |  List(  :db/add,   #db/id[:db.part/user -1000004],   :district/name         ,   Magnolia/Queen Anne                            )
//        |  List(  :db/add,   #db/id[:db.part/user -1000005],   :neighborhood/district ,   #db/id[:db.part/user -1000004]                 )
//        |  List(  :db/add,   #db/id[:db.part/user -1000005],   :neighborhood/name     ,   DD Interbay                                    )
//        |  List(  :db/add,   #db/id[:db.part/user -1000006],   :community/neighborhood,   #db/id[:db.part/user -1000005]                 )
//        |  List(  :db/add,   #db/id[:db.part/user -1000006],   :community/category    ,   DD cat 3                                       )
//        |  List(  :db/add,   #db/id[:db.part/user -1000006],   :community/orgtype     ,   :community.orgtype/community                   )
//        |  List(  :db/add,   #db/id[:db.part/user -1000006],   :community/type        ,   :community.type/blog                           )
//        |  List(  :db/add,   #db/id[:db.part/user -1000006],   :community/url         ,   http://interbayneighborhood.neighborlogs.com/  )
//        |  List(  :db/add,   #db/id[:db.part/user -1000006],   :community/name        ,   DDD Interbay District Blog                     )
//        |)""".stripMargin
//  }
//
//
//  "Manipulating data - update/retract" >> {
//
//    implicit val conn = loadSeattle(4)
//
//    val belltownId = Community.name("belltown").ids.head
//
//
//    // One-cardinality attributes ..............................
//
//    // Assert new value
//    testUpdateMolecule(
//      Community.name("belltown 2").url("url 2")
//    ) --> belltownId -->
//      Model(List(
//        Atom("community", "name", "String", 1, Eq(List("belltown 2")), None),
//        Atom("community", "url", "String", 1, Eq(List("url 2")), None))
//      ) -->
//      """List(
//        |  List(  :db/add,   17592186045888,   :community/url ,   url 2       )
//        |  List(  :db/add,   17592186045888,   :community/name,   belltown 2  )
//        |)""".stripMargin
//
//
//    // Many-cardinality attributes ............................
//
//    // Retract current value + assert new value
//    testUpdateMolecule(
//      Community.category("news" -> "Cool news")
//    ) --> belltownId -->
//      Model(List(
//        Atom("community", "category", "Set[String]", 2, Replace(Map("news" -> "Cool news")), None))
//      ) -->
//      """List(
//        |  List(  :db/retract,   17592186045888,   :community/category,   news       )
//        |  List(  :db/add    ,   17592186045888,   :community/category,   Cool news  )
//        |)""".stripMargin
//
//
//    // Update multiple categories
//    testUpdateMolecule(
//      Community.category(
//        "Cool news" -> "Super cool news",
//        "events" -> "Super cool events"
//      )
//    ) --> belltownId -->
//      Model(List(
//        Atom("community", "category", "Set[String]", 2, Replace(Map(
//          "Cool news" -> "Super cool news",
//          "events" -> "Super cool events")), None))
//      ) -->
//      """List(
//        |  List(  :db/retract,   17592186045888,   :community/category,   Cool news          )
//        |  List(  :db/add    ,   17592186045888,   :community/category,   Super cool news    )
//        |  List(  :db/retract,   17592186045888,   :community/category,   events             )
//        |  List(  :db/add    ,   17592186045888,   :community/category,   Super cool events  )
//        |)""".stripMargin
//
//
//    // Add a category
//    testUpdateMolecule(
//      Community.category.add("extra category")
//    ) --> belltownId -->
//      Model(List(
//        Atom("community", "category", "Set[String]", 2, Eq(List("extra category")), None))
//      ) -->
//      """List(
//        |  List(  :db/add,   17592186045888,   :community/category,   extra category  )
//        |)""".stripMargin
//
//
//    // Remove a category
//    testUpdateMolecule(
//      Community.category.remove("Super cool events")
//    ) --> belltownId -->
//      Model(List(
//        Atom("community", "category", "Set[String]", 2, Remove(List("Super cool events")), None))
//      ) -->
//      """List(
//        |  List(  :db/retract,   17592186045888,   :community/category,   Super cool events  )
//        |)""".stripMargin
//
//
//    // Mixing updates and deletes..........................
//
//    // Applying nothing (empty parenthesises) finds and retract all values of an attribute
//    // Note how the name is updated at the same time
//    testUpdateMolecule(
//      Community.name("belltown 3").url().category()
//    ) --> belltownId -->
//      Model(List(
//        Atom("community", "name", "String", 1, Eq(List("belltown 3")), None),
//        Atom("community", "url", "String", 1, Remove(List()), None),
//        Atom("community", "category", "Set[String]", 2, Remove(List()), None))
//      ) -->
//      """List(
//        |  List(  :db/retract,   17592186045888,   :community/category,   events                          )
//        |  List(  :db/retract,   17592186045888,   :community/category,   news                            )
//        |  List(  :db/retract,   17592186045888,   :community/url     ,   http://www.belltownpeople.com/  )
//        |  List(  :db/add    ,   17592186045888,   :community/name    ,   belltown 3                      )
//        |)""".stripMargin
//  }
//}