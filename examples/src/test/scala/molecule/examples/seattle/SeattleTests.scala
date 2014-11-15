package molecule
package examples.seattle
import java.io.FileReader
import datomic._
import molecule.examples.seattle.dsl.seattle._
import molecule.schemas._
import shapeless._
import scala.language.reflectiveCalls


class SeattleTests extends SeattleSpec {

  "A first query" >> {
    // A Community-name molecule
    val communities = m(Community.name)

    // Community entity ids
    communities.ids.take(3) === List(17592186045520L, 17592186045519L, 17592186045517L)
    communities.size === 150
  }

  // Todo: Getting an entity's attribute values
  // (have focused on queries only for now)


  "Querying _for_ attribute values" >> {

    // When querying directly we can omit the implicit `m` method to create the molecule:

    // Single attribute
    Community.name.get(3) === List(
      "Capitol Hill Triangle",
      "Miller Park Neighborhood Association",
      "Capitol Hill Community Council")

    // Multiple attributes

    // Output as tuples
    Community.name.url.get(3) === List(
      ("Chinatown/International District", "http://www.cidbia.org/"),
      ("All About Belltown", "http://www.belltown.org/"),
      ("Friends of Discovery Park", "http://www.friendsdiscoverypark.org/"))

    // Output as HLists
    Community.name.url.hl(3) === List(
      "Chinatown/International District" :: "http://www.cidbia.org/" :: HNil,
      "All About Belltown" :: "http://www.belltown.org/" :: HNil,
      "Friends of Discovery Park" :: "http://www.friendsdiscoverypark.org/" :: HNil)
  }


  "Querying _by_ attribute values" >> {

    // Find attributes with a certain applied value
    Community.name.`type`("twitter").get(3) === List(
      ("Discover SLU", "twitter"),
      ("Magnolia Voice", "twitter"),
      ("MyWallingford", "twitter"))

    // Append underscore to omit applied value from output (the same anyway)
    // (different results now since order is not guaranteed)
    Community.name.type_("twitter").get(3) === List(
      "Discover SLU", "Fremont Universe", "Columbia Citizens")

    // Applying values with variables is also possible (form inputs etc)
    val tw = "twitter"
    Community.name.type_(tw).get(3) === List(
      "Discover SLU", "Fremont Universe", "Columbia Citizens")


    // Many-cardinality attributes

    // Retrieving Set of `category` values for Belltown
    Community.name_("belltown").category.one === Set("events", "news")

    // Communities with some possible categories
    Community.name.category("news" or "arts").get(3) === List(
      ("Alki News/Alki Community Council", Set("news", "council meetings")),
      ("ArtsWest", Set("arts")),
      ("At Large in Ballard", Set("news", "human interest")))

    // We can omit the category values
    Community.name.category_("news" or "arts").get(3) === List(
      "Capitol Hill Community Council",
      "KOMO Communities - Rainier Valley",
      "Discover SLU")

    // We can also apply arguments as a list (OR-semantics as using `or`)
    Community.name.category_("news", "arts").get(3) === List(
      "Capitol Hill Community Council",
      "KOMO Communities - Rainier Valley",
      "Discover SLU")
  }

  "Querying across references" >> {

    // Communities in north eastern region
    Community.name.Neighborhood.District.region_("ne").get(3) === List(
      "KOMO Communities - U-District",
      "Maple Leaf Community Council",
      "KOMO Communities - View Ridge")

    // Communities and their region
    Community.name.Neighborhood.District.region.get(3) === List(
      ("Broadview Community Council", "sw"),
      ("KOMO Communities - Green Lake", "sw"),
      ("Friends of Frink Park", "e"))
  }


  "Advanced queries - parameterizing queries" >> {

    /** ******* Single input parameter **************************/

    // Single input value for an attribute ------------------------

    // Community input molecule awaiting some type value
    // Adding an underscore means that we don't want to return that value (will be the same for all result sets...)
    val communitiesOfType = m(Community.name.type_(?))

    // Re-use input molecules to create new molecules with different community types
    val twitterCommunities = communitiesOfType.apply("twitter")
    val facebookCommunities = communitiesOfType("facebook_page")

    // Only the `name` attribute is returned since `type` is the same for all results
    twitterCommunities.get(3) === List("Discover SLU", "Fremont Universe", "Columbia Citizens")
    facebookCommunities.get(3) === List("Discover SLU", "Blogging Georgetown", "Fremont Universe")

    // If we omit the underscore we can get the type too
    val communitiesWithType = m(Community.name.`type`(?))
    communitiesWithType("twitter").get(3) === List(
      ("Discover SLU", "twitter"),
      ("Magnolia Voice", "twitter"),
      ("MyWallingford", "twitter"))


    // Multiple input values for an attribute - logical OR ------------------------

    // Finding communities of type "facebook_page" OR "twitter"
    val facebookOrTwitterCommunities = List(
      ("Fremont Universe", "facebook_page"),
      ("Fauntleroy Community Association", "facebook_page"),
      ("Magnolia Voice", "twitter"))
    //        ("Discover SLU", "twitter"))

    // Notation variations with OR-semantics for multiple inputs:

    // 1. OR expression-----------------------------------------------------

    communitiesWithType("facebook_page" or "twitter").get(3) === facebookOrTwitterCommunities

    // 2. Comma-separated list
    // Note how this has OR-semantics with a single input paramter!
    communitiesWithType("facebook_page", "twitter").get(3) === facebookOrTwitterCommunities

    // 3. List
    communitiesWithType(Seq("facebook_page", "twitter")).get(3) === facebookOrTwitterCommunities


    /** ******* Multiple input parameters **************************/

    // Tuple of input values for multiple attributes - logical AND ------------------------

    // Communities of some `type` AND some `orgtype`
    val typeAndOrgtype = m(Community.name.type_(?).orgtype_(?))

    // Finding communities of type "email_list" AND orgtype "community"
    val emailListCommunities = List(
      "Greenwood Community Council Announcements",
      "Madrona Moms",
      "Ballard Neighbor Connection")

    // Notation variations with AND-semantics for a single tuple of inputs:

    // 1. AND expression
    typeAndOrgtype("email_list" and "community").get(3) === emailListCommunities

    // 2. Comma-separated list
    // Note how this shorthand notation has AND-semantics and expects a number of
    // inputs matching the arity of input parameters, in this case 2.
    typeAndOrgtype("email_list", "community").get(3) === emailListCommunities

    // 3. List of tuples
    // Note how this has AND-semantics and how it differs from the the OR-version above!
    typeAndOrgtype(Seq(("email_list", "community"))).get(3) === emailListCommunities


    // Multiple tuples of input values ------------------------

    // Communities of some `type` AND some `orgtype` (include input values)
    val typeAndOrgtype2 = m(Community.name.`type`(?).orgtype(?))

    val emailListORcommercialWebsites = List(
      ("Madrona Moms", "email_list", "community"),
      ("Broadview Community Council", "email_list", "community"),
      ("Greenwood Community Council Discussion", "email_list", "community"),
      ("Alki News", "email_list", "community"),
      ("Discover SLU", "website", "commercial"))


    // Logic AND pairs separated by OR
    typeAndOrgtype2(("email_list" and "community") or ("website" and "commercial")).get(5) === emailListORcommercialWebsites

    // Multiple tuples of AND pairs:
    typeAndOrgtype2(("email_list", "community"), ("website", "commercial")).get(5) === emailListORcommercialWebsites

    // ..or we can supply the tuples in a list
    typeAndOrgtype2(Seq(("email_list", "community"), ("website", "commercial"))).get(5) === emailListORcommercialWebsites
  }


  "Invoking functions in queries" >> {

    //    val beforeC = List("ArtsWest", "All About South Park", "Alki News/Alki Community Council")
    val beforeC = List("ArtsWest", "All About South Park", "Ballard Neighbor Connection")

    //    m(Community.name < "C").debug
    m(Community.name < "C").get(3) === beforeC
    Community.name.<("C").get(3) === beforeC

    val communitiesBefore = m(Community.name < ?)
    communitiesBefore("C").get(3) === beforeC
    communitiesBefore("A").get(3) === List("15th Ave Community")
  }


  "Querying with fulltext search" >> {

    // (postfix notation)
    (Community.name contains "Wallingford" get 3) === List("KOMO Communities - Wallingford")

    val communitiesWith = m(Community.name contains ?)
    (communitiesWith("Wallingford") get 3) === List("KOMO Communities - Wallingford")


    // Fulltext search on many-attribute (`category`)

    val foodWebsites = List(
      ("Community Harvest of Southwest Seattle", Set("sustainable food")),
      ("InBallard", Set("food")))

    m(Community.name.type_("website").category contains "food").get(3) === foodWebsites

    val typeAndCategory = m(Community.name.type_(?).category contains ?)
    typeAndCategory("website", Set("food")).get(3) === foodWebsites
  }


  "Querying with rules (logical OR)" >> {

    // Social media
    Community.name.type_("twitter" or "facebook_page").get(3) === List(
      "Discover SLU", "Blogging Georgetown", "Fremont Universe")

    // NE and SW regions
    Community.name.Neighborhood.District.region_("ne" or "sw").get(3) === List(
      "Greenwood Community Council Announcements", "Maple Leaf Community Council", "Genesee-Schmitz Neighborhood Council")

    val southernSocialMedia = List(
      "Blogging Georgetown",
      "Columbia Citizens",
      "MyWallingford",
      "Fauntleroy Community Association")

    Community.name.type_("twitter" or "facebook_page").Neighborhood.District.region_("sw" or "s" or "se").get === southernSocialMedia

    // Parameterized
    val typeAndRegion = m(Community.name.type_(?).Neighborhood.District.region_(?))

    typeAndRegion(("twitter" or "facebook_page") and ("sw" or "s" or "se")).get === southernSocialMedia
    // ..same as
    typeAndRegion(Seq("twitter", "facebook_page"), Seq("sw", "s", "se")).get === southernSocialMedia
  }


  "Working with time" >> {

    // Loading modified schema having the 2 unique identities
    // :neighborhood/name and :district/name without uniqueness - todo: why do we get problems otherwise?
    implicit val conn = loadFromFiles("seattle-schema1a.dtm", "seattle-data0a.dtm", 2)


    val txDates = Db.txInstant.get(2).sorted.reverse
    val dataTxDate = txDates(0)
    val schemaTxDate = txDates(1)

    // Take all Community entities
    val communities = m(Community.name)

    // Revisiting the past

    communities.asOf(schemaTxDate).size === 0
    communities.asOf(dataTxDate).size === 150

    communities.since(schemaTxDate).size === 150
    communities.since(dataTxDate).size === 0

    // Imagining the future
    val data_rdr2 = new FileReader("examples/resources/seattle/seattle-data1a.dtm")
    val newDataTx = Util.readAll(data_rdr2).get(0).asInstanceOf[java.util.List[Object]]


    // future db
    communities.imagine(newDataTx).size === 258

    // existing db
    communities.size === 150

    // transact
    conn.transact(newDataTx)

    // updated db
    communities.size === 258

    // number of new transactions
    communities.since(dataTxDate).size === 108
  }


//  "Manipulating data - insert" >> {
//    implicit val conn = loadSeattle(3)
  "Manipulating data - insert" in new Setup {

    // Add Community with Neighborhood and Region
    Community
      .name("AAA")
      .url("myUrl")
      .`type`("twitter")
      .orgtype("personal")
      .category("my", "favorites") // many cardinality allows multiple values
      .Neighborhood.name("myNeighborhood")
//      .District.name("myDistrict").region("nw").add.ids === List(17592186045890L, 17592186045891L, 17592186045892L)
      .District.name("myDistrict").region("nw").add.ids === List(17592186045651L, 17592186045652L, 17592186045653L)

//    ok

    // Confirm all data is inserted
    Community.name.contains("AAA").url.`type`.orgtype.category.Neighborhood.name.District.name.region.get(1) === List(
      ("AAA", "myUrl", "twitter", "personal", Set("my", "favorites"), "myNeighborhood", "myDistrict", "nw"))

    // Now we have one more community
    Community.name.size === 151

    // We can also insert data in two steps:

    // 1. Define an "InsertMolecule" (can be re-used!)
    val insertCommunity = Community.name.url.`type`.orgtype.category.Neighborhood.name.District.name.region insert

    // 2. Apply data to the InsertMolecule
    insertCommunity("BBB", "url B", "twitter", "personal", Set("some", "cat B"), "neighborhood B", "district B", "s").ids === List(
      17592186045655L, 17592186045656L, 17592186045657L)

    // Insert data as HList
    insertCommunity("CCC" :: "url C" :: "twitter" :: "personal" :: Set("some", "cat C") :: "neighborhood C" :: "district C" :: "ne" :: HNil).ids === List(
      17592186045659L, 17592186045660L, 17592186045661L)


    // Add multiple molecules..........................

    // Data as list of tuples
    Community.name.url.insert(Seq(("Com A", "A.com"), ("Com B", "B.com"))).ids === Seq(17592186045663L, 17592186045664L)

    // Data as list of HLists
    Community.name.url.insert(Seq("Com C" :: "C.com" :: HNil, "Com D" :: "D.com" :: HNil)).ids === Seq(17592186045666L, 17592186045667L)

    // Confirm that new entities have been inserted
    Community.name.contains("Com").get.sorted === List("Com A", "Com B", "Com C", "Com D")
    Community.name.size === 157


    // Add multiple sets of entities with multiple facts across multiple namespaces in one go (!):

    // Data of 3 new communities with neighborhoods and districts
    // Attributes with many-cardinality take sets of values (`category` in this case)
    val newCommunitiesData = List(
      ("DDD Blogging Georgetown", "http://www.blogginggeorgetown.com/", "blog", "commercial", Set("DD cat 1", "DD cat 2"), "DD Georgetown", "Greater Duwamish", "s"),
      ("DDD Interbay District Blog", "http://interbayneighborhood.neighborlogs.com/", "blog", "community", Set("DD cat 3"), "DD Interbay", "Magnolia/Queen Anne", "w"),
      ("DDD KOMO Communities - West Seattle", "http://westseattle.komonews.com", "blog", "commercial", Set("DD cat 4"), "DD West Seattle", "Southwest", "sw")
    )
    // (This is how we have entered the data of the Seattle sample application - see SeattleSpec that this test class extends)

    // Categories before insert (one Set with distinct values)
    Community.category.get.head.size === 88

    // Re-use insert molecule to insert 3 new communities with 3 new neighborhoods
    insertCommunity(newCommunitiesData).ids === List(17592186045669L, 17592186045670L, 17592186045671L, 17592186045672L, 17592186045673L, 17592186045674L)

    // Data has been added
    Community.name.contains("DDD").url.`type`.orgtype.category.Neighborhood.name.District.name.region.get === newCommunitiesData
    Community.name.size === 160

    // 4 new categories added (these are facts, not entities)
    Community.category.get.head.size === 92
  }

  "Manipulating data - update/retract" >> {
    implicit val conn = loadSeattle(4)
//  "Manipulating data - update/retract" in new Setup {


    // One-cardinality attributes..........................

    // Finding the Belltown community entity id (or we could have got it along user input etc...)
    val belltown = Community.e.name_("belltown").get.head

    // Replace some belltown attributes
    Community(belltown).name("belltown 2").url("url 2").update

    Community.name("belltown 2").url("url 2").update(belltown)

    // Find belltown by its updated name and confirm that the url is also updated
    Community.name_("belltown 2").url.get === List("url 2")


    // Many-cardinality attributes..........................

    // Categories before
    Community.name_("belltown 2").category.get.head === Set("news", "events")

    // Tell which value to update
    Community.category("news" -> "Cool news").update(belltown)
    Community.name_("belltown 2").category.get.head === Set("Cool news", "events")

    // Or update multiple values in one go...
    Community.category(
      "Cool news" -> "Super cool news",
      "events" -> "Super cool events").update(belltown)
    Community.name_("belltown 2").category.get.head === Set("Super cool news", "Super cool events")

    // Add value
    Community.category.add("extra category").update(belltown)
    Community.name_("belltown 2").category.get.head === Set("Super cool news", "Super cool events", "extra category")

    // Remove value
    Community.category.remove("Super cool events").update(belltown)
    Community.name_("belltown 2").category.get.head === Set("Super cool news", "extra category")


    // Mixing updates and deletes..........................

    // Values before
    Community.name("belltown 2").`type`.url.category.hl === List(
      "belltown 2" :: "blog" :: "url 2" :: Set("Super cool news", "extra category") :: HNil)

    // Applying nothing (empty parenthesises) finds and retract all values of an attribute
    Community.name("belltown 3").url().category().update(belltown)

    // Belltown has no longer a url or any categories
    Community.name_("belltown 3").name.`type`.url.category.hl === List()

    // ..but we still have a belltown with a name and type
    Community.name_("belltown 3").name.`type`.hl === List("belltown 3" :: "blog" :: HNil)


    // Retract entities ...................................

    // Belltown exists
    Community.name("belltown 3").size === 1

    // Simply use an entity id and retract it!
    belltown.retract

    // Belltown is gone
    Community.name("belltown 3").size === 0
  }
}