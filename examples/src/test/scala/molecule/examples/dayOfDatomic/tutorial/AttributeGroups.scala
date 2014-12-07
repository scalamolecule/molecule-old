package molecule
package examples.dayOfDatomic.tutorial

import molecule._
import molecule.examples.dayOfDatomic.SocialNewsSetup

//import molecule.examples.dayOfDatomic.dsl.socialNews._
//import molecule.examples.dayOfDatomic.schema.SocialNewsSchema
import molecule.examples.dayOfDatomic.spec.DayOfAtomicSpec
    import molecule.schemas.Db

class AttributeGroups extends DayOfAtomicSpec {

  "Attribute groups" in new SocialNewsSetup {

    // Find all attributes in the story namespace
    Db.a.ns_("story").get === List(":story/title", ":story/url")

    // Create a reusable rule
//    val attrInNs = m(Db.a.ns_(?))
//
//    // Find all attributes in story namespace, using the rule
//    attrInNs("story").get === List(":story/title", ":story/url")
//
//    // Find all entities possessing *any* story attribute
//    Db.e.a_.ns_("story").get === List()

    ok
  }
}


//import datomic._
//
//    Peer.q(s"""
//      [:find ?a
//       :where
//         [?a :db/valueType ?k]
//         [?a :db/ident ?b1]
//         [(.getNamespace  ^clojure.lang.Keyword ?b1) ?b3]
//         [(= ?b3 "story")]]
//           """, conn.db) === 7

//    Peer.q(s"""
//      [:find ?b2
//       :where
//         [?a ?attr ?b]
//         [?attr :db/ident ?b1]
//         [(.toString ^clojure.lang.Keyword ?b1) ?b2]
//         [(.getNamespace  ^clojure.lang.Keyword ?b1) ?b3]
//         [(= ?b3 "story")]]
//           """, conn.db) === 7