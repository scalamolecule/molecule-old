package moleculeTests.tests.examples.datomic.seattle

import molecule.datomic.api.in2_out8._
import molecule.datomic.base.facade.{Conn, TxReport}
import molecule.datomic.base.util.SystemPeer
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.setup.examples.seattle.SeattleData
import moleculeTests.dataModels.examples.datomic.seattle.dsl.Seattle._
import utest._
import scala.concurrent.{ExecutionContext, Future}


object SeattleTests extends AsyncTestSuite with SeattleData {

  def loadData(implicit conn: Future[Conn], ec: ExecutionContext): Future[TxReport] = {
    Community.name.url.tpe.orgtype$.category$.Neighborhood.name.District.name.region$ insert seattleData
  }

  lazy val tests = Tests {
    import scala.concurrent.ExecutionContext.Implicits.global

    "A first query" - seattle { implicit conn =>
      for {
        _ <- loadData

        communities = m(Community.e.name_)

        // We have 150 communities
        _ <- communities.get.map(_.size ==> 150)
      } yield ()
    }


    "Getting an entity's attribute values" - seattle { implicit conn =>
      for {
        _ <- loadData

        // Using the entity api

        // Get a community id and its related entities
        (communityId, neighborhoodId, districtId) <-
          Community.e.name_("Greenlake Community Wiki").Neighborhood.e.District.e.get.map(_.head)

        // Use the community id to touch all the entity's attribute values
        _ <- communityId.graph.map(_ ==> Map(
          ":Community/category" -> Set("events", "for sale", "services"),
          ":Community/neighborhood" -> Map(
            ":db/id" -> neighborhoodId,
            ":Neighborhood/district" -> Map(
              ":db/id" -> districtId,
              ":District/name" -> "Northwest",
              ":District/region" -> ":District.region/sw"),
            ":Neighborhood/name" -> "Green Lake"),
          ":Community/orgtype" -> ":Community.orgtype/community",
          ":Community/name" -> "Greenlake Community Wiki",
          ":db/id" -> communityId,
          ":Community/url" -> "http://greenlake.wetpaint.com/",
          ":Community/tpe" -> ":Community.tpe/wiki"))

        // We can also use the entity id to query for attribute values
        _ <- Community(communityId).name.get.map(_ ==> List("Greenlake Community Wiki"))
        _ <- Community(communityId).url.get.map(_ ==> List("http://greenlake.wetpaint.com/"))
        _ <- Community(communityId).category.get.map(_.head.toList.sorted ==> List("events", "for sale", "services"))
      } yield ()
    }

    "Querying _for_ attribute values" - seattle { implicit conn =>
      for {
        _ <- loadData

        // When querying directly we can omit the implicit `m` method to create the molecule:

        // Single attribute
        _ <- Community.name.get(3).map(_ ==> List(
          "KOMO Communities - Ballard",
          "Ballard Blog",
          "Ballard Historical Society"))

        // Multiple attributes

        // Output as tuples
        _ <- Community.name.url.get(3).map(_ ==> List(
          ("Broadview Community Council", "http://groups.google.com/group/broadview-community-council"),
          ("KOMO Communities - Wallingford", "http://wallingford.komonews.com"),
          ("Aurora Seattle", "http://www.auroraseattle.com/")))
      } yield ()
    }

    "Querying _by_ attribute values" - seattle { implicit conn =>
      for {
        _ <- loadData

        // Find attributes with a certain applied value
        _ <- Community.name.tpe("twitter").get(3).map(_.sortBy(_._1) ==> List(
          ("Columbia Citizens", "twitter"),
          ("Discover SLU", "twitter"),
          ("Fremont Universe", "twitter")))

        // Append underscore to omit applied value from output (the same anyway)
        // (different results now since order is not guaranteed)
        _ <- Community.name.tpe_("twitter").get(3).map(_ ==> List(
          "Magnolia Voice", "Columbia Citizens", "Discover SLU"))

        // Applying values with variables is also possible (form inputs etc)
        tw = "twitter"
        _ <- Community.name.tpe_(tw).get(3).map(_ ==> List(
          "Magnolia Voice", "Columbia Citizens", "Discover SLU"))


        // Many-cardinality attributes

        // Retrieving Set of `category` values for Belltown
        _ <- Community.name_("belltown").category.get.map(_.head ==> Set("events", "news"))

        // Communities with some possible categories
        _ <- Community.name.category("news" or "arts").get(3).map(_ ==> List(
          ("Alki News/Alki Community Council", Set("news", "council meetings")),
          ("ArtsWest", Set("arts")),
          ("At Large in Ballard", Set("news", "human interest"))))

        // We can omit the category values
        _ <- Community.name.category_("news" or "arts").get(3).map(_ ==> List(
          "Beach Drive Blog",
          "KOMO Communities - Ballard",
          "Ballard Blog"))

        // We can also apply arguments as a list (OR-semantics as using `or`)
        _ <- Community.name.category_("news", "arts").get(3).map(_ ==> List(
          "Beach Drive Blog",
          "KOMO Communities - Ballard",
          "Ballard Blog"))
      } yield ()
    }

    "Querying across references" - seattle { implicit conn =>
      for {
        _ <- loadData

        // Communities in north eastern region
        _ <- Community.name.Neighborhood.District.region_("ne").get(3).map(_ ==> List(
          "Maple Leaf Community Council",
          "Hawthorne Hills Community Website",
          "KOMO Communities - View Ridge"))

        // Communities and their region
        _ <- Community.name.Neighborhood.District.region.get(3).map(_ ==> List(
          ("Morgan Junction Community Association", "sw"),
          ("KOMO Communities - North Seattle", "n"),
          ("Friends of Seward Park", "se")))
      } yield ()
    }

    "Advanced queries - parameterizing queries" - seattle { implicit conn =>
      for {
        _ <- loadData

        /** ******* Single input parameter ************************* */

        // Single input value for an attribute ------------------------

        // Community input molecule awaiting some type value
        // Adding an underscore means that we don't want to return that value (will be the same for all result sets...)
        communitiesOfType = m(Community.name.tpe_.apply(?))

        // Re-use input molecules to create new molecules with different community types
        twitterCommunities = communitiesOfType("twitter")
        facebookCommunities = communitiesOfType("facebook_page")

        // Only the `name` attribute is returned since tpe is the same for all results
        _ <- twitterCommunities.get(3).map(_ ==> List("Magnolia Voice", "Columbia Citizens", "Discover SLU"))
        _ <- facebookCommunities.get(3).map(_ ==> List("Magnolia Voice", "Columbia Citizens", "Discover SLU"))

        // If we omit the underscore we can get the type too
        communitiesWithType = m(Community.name.tpe(?))
        _ <- communitiesWithType("twitter").get(3).map(_ ==> List(
          ("Discover SLU", "twitter"),
          ("Fremont Universe", "twitter"),
          ("Columbia Citizens", "twitter")))


        // Multiple input values for an attribute - logical OR ------------------------

        // Finding communities of type "facebook_page" OR "twitter"
        facebookOrTwitterCommunities = List(
          ("Discover SLU", "twitter"),
          ("Eastlake Community Council", "facebook_page"),
          ("MyWallingford", "facebook_page")
        )

        // Notation variations with OR-semantics for multiple inputs:

        // 1. OR expression-----------------------------------------------------

        _ <- communitiesWithType("facebook_page" or "twitter").get(3).map(_.sorted ==> facebookOrTwitterCommunities)

        // 2. Comma-separated list
        // Note how this has OR-semantics with a single input paramter!
        _ <- communitiesWithType("facebook_page", "twitter").get(3).map(_.sorted ==> facebookOrTwitterCommunities)

        // 3. List
        _ <- communitiesWithType(Seq("facebook_page", "twitter")).get(3).map(_.sorted ==> facebookOrTwitterCommunities)


        /** ******* Multiple input parameters ************************* */

        // Tuple of input values for multiple attributes - logical AND ------------------------

        // Communities of some tpe AND some `orgtype`
        typeAndOrgtype = m(Community.name.tpe_(?).orgtype_(?))

        // Finding communities of type "email_list" AND orgtype "community"
        emailListCommunities = List(
          "15th Ave Community",
          "Admiral Neighborhood Association",
          "Ballard Moms",
        )

        // Notation variations with AND-semantics for a single tuple of inputs:

        // 1. AND expression
        _ <- typeAndOrgtype("email_list" and "community").get(3).map(_.sorted ==> emailListCommunities)

        // 2. Comma-separated list
        // Note how this shorthand notation has AND-semantics and expects a number of
        // inputs matching the arity of input parameters, in this case 2.
        _ <- typeAndOrgtype("email_list", "community").get(3).map(_.sorted ==> emailListCommunities)

        // 3. List of tuples
        // Note how this has AND-semantics and how it differs from the the OR-version above!
        _ <- typeAndOrgtype(Seq(("email_list", "community"))).get(3).map(_.sorted ==> emailListCommunities)


        // Multiple tuples of input values ------------------------

        // Communities of some tpe AND some `orgtype` (include input values)
        typeAndOrgtype2 = m(Community.name.tpe(?).orgtype(?))

        emailListORcommercialWebsites = List(
          ("Fremont Arts Council", "email_list", "community"),
          ("Greenwood Community Council Announcements", "email_list", "community"),
          ("Broadview Community Council", "email_list", "community"),
          ("Alki News", "email_list", "community"),
          ("Beacon Hill Burglaries", "email_list", "community"))


        // Logic AND pairs separated by OR
        _ <- typeAndOrgtype2(("email_list" and "community") or ("website" and "commercial"))
          .get(5).map(_ ==> emailListORcommercialWebsites)

        // Multiple tuples of AND pairs:
        _ <- typeAndOrgtype2(("email_list", "community"), ("website", "commercial"))
          .get(5).map(_ ==> emailListORcommercialWebsites)

        // ..or we can supply the tuples in a list
        _ <- typeAndOrgtype2(Seq(("email_list", "community"), ("website", "commercial")))
          .get(5).map(_ ==> emailListORcommercialWebsites)
      } yield ()
    }

    "Invoking functions in queries" - seattle { implicit conn =>
      for {
        _ <- loadData

        beforeC = List("Ballard Blog", "Beach Drive Blog", "Beacon Hill Blog")

        _ <- m(Community.name < "C").get(3).map(_.sorted ==> beforeC)
        _ <- Community.name.<("C").get(3).map(_.sorted ==> beforeC)

        communitiesBefore = m(Community.name < ?)
        _ <- communitiesBefore("C").get(3).map(_.sorted ==> beforeC)
        _ <- communitiesBefore("A").get(3).map(_.sorted ==> List("15th Ave Community"))
      } yield ()
    }

    "Querying with fulltext search" - seattle { implicit conn =>
      // Datomic only implements fulltext search for Peer
      if (system == SystemPeer) {
        for {
          _ <- loadData

          communitiesWith = m(Community.name.contains(?))
          _ <- communitiesWith("Wallingford").get.map(_ ==> List("KOMO Communities - Wallingford"))

          // Fulltext search on many-attribute (`category`)

          foodWebsites = List(
            ("Community Harvest of Southwest Seattle", Set("sustainable food")),
            ("InBallard", Set("nightlife", "food", "shopping", "services")))

          foodShoppingWebsites = List(
            ("InBallard", Set("nightlife", "food", "shopping", "services")))

          _ <- m(Community.name.tpe_("website").category.contains("food")).get.map(_ ==> foodWebsites)
          _ <- m(Community.name.tpe_("website").category.contains("food", "shopping")).get.map(_ ==> foodShoppingWebsites)

          typeAndCategory = m(Community.name.tpe_(?).category contains ?)
          _ <- typeAndCategory("website", Set("food")).get.map(_ ==> foodWebsites)
          res <- typeAndCategory("website", Set("food", "shopping")).get.map(_ ==> foodShoppingWebsites)
        } yield ()
      }
    }

    "Querying with rules (logical OR)" - seattle { implicit conn =>
      for {
        _ <- loadData

        // Social media
        _ <- Community.name.tpe_("twitter" or "facebook_page").get(3).map(_ ==> List(
          "Magnolia Voice", "Columbia Citizens", "Discover SLU"))

        // NE and SW regions
        _ <- Community.name.Neighborhood.District.region_("ne" or "sw").get(3).map(_ ==> List(
          "Beach Drive Blog", "KOMO Communities - Green Lake", "Delridge Produce Cooperative"))

        southernSocialMedia = List(
          "Blogging Georgetown",
          "Columbia Citizens",
          "Fauntleroy Community Association",
          "MyWallingford",
        )

        _ <- Community.name.tpe_("twitter" or "facebook_page")
          .Neighborhood.District.region_("sw" or "s" or "se")
          .get.map(_.sorted ==> southernSocialMedia)

        // Parameterized
        typeAndRegion = m(Community.name.tpe_(?).Neighborhood.District.region_(?))

        _ <- typeAndRegion(("twitter" or "facebook_page") and ("sw" or "s" or "se"))
          .get.map(_.sorted ==> southernSocialMedia)
        // ..same as
        _ <- typeAndRegion(Seq("twitter", "facebook_page"), Seq("sw", "s", "se"))
          .get.map(_.sorted ==> southernSocialMedia)
      } yield ()
    }

    "Bonus: Cardinality-many queries" - seattle { implicit conn =>
      for {
        _ <- loadData

        // What communities are both about restaurants AND shopping?
        _ <- Community.name.category_("restaurants" and "shopping").get.map(_ ==> List("Ballard Gossip Girl"))

        // It's a self-join that unifies on community name - what the following two queries have in common:

        /*
      _ <- Community.name.category("shopping").get
      OUTPUTS:
      1  ["Ballard Blog" #{"shopping" "news" "human interest" "events" "criminal activity"}]
  --> 2  ["Ballard Gossip Girl" #{"nightlift" "shopping" "restaurants"}]
      3  ["Columbia City Blog" #{"food" "shopping" "news" "events"}]
      4  ["Discover SLU" #{"shopping" "news" "events" "dining"}]
      5  ["InBallard" #{"nightlife" "food" "shopping" "services"}]
      6  ["KOMO Communities - Ballard" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      7  ["KOMO Communities - Beacon Hill" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      8  ["KOMO Communities - Captol Hill" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      9  ["KOMO Communities - Central District" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      10  ["KOMO Communities - Columbia City" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      11  ["KOMO Communities - Downtown" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      12  ["KOMO Communities - Fremont" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      13  ["KOMO Communities - Georgetown" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      14  ["KOMO Communities - Green Lake" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      15  ["KOMO Communities - Greenwood-Phinney" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      16  ["KOMO Communities - Lake City" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      17  ["KOMO Communities - Madison Park" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      18  ["KOMO Communities - Magnolia" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      19  ["KOMO Communities - North Seattle" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      20  ["KOMO Communities - Queen Anne" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      21  ["KOMO Communities - Rainier Valley" #{"food" "shopping" "news" "human interest" "events" "criminal activity"}]
      22  ["KOMO Communities - South Lake Union" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      23  ["KOMO Communities - U-District" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      24  ["KOMO Communities - View Ridge" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      25  ["KOMO Communities - Wallingford" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      26  ["KOMO Communities - West Seattle" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      27  ["Maple Leaf Life" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      28  ["MyBallard" #{"shopping" "news" "human interest" "events" "criminal activity"}]
      29  ["MyWallingford" #{"shopping" "news" "human interest" "events" "criminal activity"}]

      _ <- Community.name.category("restaurants").inspect
      OUTPUTS:
  --> 1  ["Ballard Gossip Girl" #{"nightlift" "shopping" "restaurants"}]
      2  ["Columbia Citizens" #{"restaurants" "neighborhood community site; buying/selling" "favorite places" "etc."}]
      3  ["Madrona Neighborhood" #{"neighborhood community site: buying/selling" "restaurants" "etc.  Also community association meeting minutes" "favorite places"}]
    */
      } yield ()
    }

    "Working with time" - seattle { implicit conn =>
      for {
        _ <- loadData

        r1 <- Schema.t.get
        schemaTxT = r1.head
        r2 <- Community.name_.t.get
        dataTxT = r2.head

        // Take all Community entities
        communities = m(Community.e.name_)

        // Revisiting the past

        _ <- communities.getAsOf(schemaTxT).map(_.size ==> 0)
        _ <- communities.getAsOf(dataTxT).map(_.size ==> 150)

        _ <- communities.getSince(schemaTxT).map(_.size ==> 150)
        _ <- communities.getSince(dataTxT).map(_.size ==> 0)


        // This depends on jvm-only datomic dependency - tested in jvm.examples.datomic.seattle.Seattle

        //        // Imagining the future
        //        data_rdr2 = new FileReader("moleculeTests/jvm/resources/tests/examples/seattle/seattle-data2upper.dtm")
        //        newDataTx = Util.readAll(data_rdr2).get(0).asInstanceOf[jList[jList[_]]]
        //
        //        // future db
        //        _ <- communities.getWith(newDataTx).map(_.size ==> 258)
        //
        //        // existing db
        //        _ <- communities.get.map(_.size ==> 150)
        //
        //        // transact
        //        _ <- conn.map(_.transactRaw(newDataTx))
        //
        //        // updated db
        //        _ <- communities.get.map(_.size ==> 258)
        //
        //        // number of new transactions
        //        _ <- communities.getSince(dataTxT).map(_.size ==> 108)
      } yield ()
    }

    "Manipulating data - insert" - seattle { implicit conn =>
      for {
        _ <- loadData

        // Add Community with Neighborhood and Region
        _ <- Community
          .name("AAA")
          .url("myUrl")
          .tpe("twitter")
          .orgtype("personal")
          .category("my", "favorites") // many cardinality allows multiple values
          .Neighborhood.name("myNeighborhood")
          .District.name("myDistrict").region("nw").save

        // Confirm all data is inserted
        _ <- Community.name("AAA").url.tpe.orgtype.category.Neighborhood.name.District.name.region.get(1).map(_ ==> List(
          ("AAA", "myUrl", "twitter", "personal", Set("my", "favorites"), "myNeighborhood", "myDistrict", "nw")))

        // Now we have one more community
        _ <- Community.e.name_.get.map(_.size ==> 151)

        // We can also insert data in two steps:

        // 1. Define an "insert-molecule" (can be re-used!)
        insertCommunity = Community.name.url.tpe.orgtype.category.Neighborhood.name.District.name.region.insert

        // 2. Apply data to the insert-molecule
        _ <- insertCommunity("BBB", "url B", "twitter", "personal", Set("some", "cat B"), "neighborhood B", "district B", "s")


        // Add multiple molecules..........................

        // Data as list of tuples
        _ <- Community.name.url.insert(Seq(("Com A", "A.com"), ("Com B", "B.com")))

        // Confirm that new entities have been inserted
        _ <- Community.name("Com A").get.map(_ ==> List("Com A"))
        _ <- Community.name("Com B").get.map(_ ==> List("Com B"))
        _ <- Community.e.name_.get.map(_.size ==> 154)


        // Add multiple sets of entities with multiple facts across multiple namespaces in one go (!):

        // Data of 3 new communities with neighborhoods and districts
        // Attributes with many-cardinality take sets of values (`category` in this case)
        newCommunitiesData = List(
          ("DDD Blogging Georgetown", "http://www.blogginggeorgetown.com/", "blog", "commercial", Set("DD cat 1", "DD cat 2"), "DD Georgetown", "Greater Duwamish", "s"),
          ("DDD Interbay District Blog", "http://interbayneighborhood.neighborlogs.com/", "blog", "community", Set("DD cat 3"), "DD Interbay", "Magnolia/Queen Anne", "w"),
          ("DDD KOMO Communities - West Seattle", "http://westseattle.komonews.com", "blog", "commercial", Set("DD cat 4"), "DD West Seattle", "Southwest", "sw")
        )
        // (This is how we have entered the data of the Seattle sample application - see SeattleSpec that this test class extends)

        // Categories before insert (one Set with distinct values)
        _ <- Community.category.get.map(_.head.size ==> 87)

        // Re-use insert molecule to insert 3 new communities with 3 new neighborhoods and references to 3 existing Districts
        _ <- insertCommunity(newCommunitiesData)

        // Data has been added
        _ <- if (system == SystemPeer) // Only Peer has fulltext search
          Community.name.contains("DDD").url.tpe.orgtype.category.Neighborhood.name.District.name.region.get.map(_ ==> newCommunitiesData)
        else Future.unit

        _ <- Community.e.name_.get.map(_.size ==> 157)

        // 4 new categories added (these are facts, not entities)
        _ <- Community.category.get.map(_.head.size ==> 91)
      } yield ()
    }


    "Manipulating data - update/retract" - seattle { implicit conn =>
      for {
        _ <- loadData

        // One-cardinality attributes..........................

        // Finding the Belltown community entity id (or we could have got it along user input etc...)
        r1 <- Community.e.name_("belltown").get
        belltown = r1.head

        // Replace some belltown attributes
        _ <- Community(belltown).name("belltown 2").url("url 2").update


        // Find belltown by its updated name and confirm that the url is also updated
        _ <- Community.name_("belltown 2").url.get.map(_ ==> List("url 2"))


        // Many-cardinality attributes..........................

        // Categories before
        _ <- Community.name_("belltown 2").category.get.map(_.head ==> Set("news", "events"))

        // Replace value
        _ <- Community(belltown).category.replace("news" -> "Cool news").update
        _ <- Community.name_("belltown 2").category.get.map(_.head ==> Set("Cool news", "events"))

        // Replace multiple values
        _ <- Community(belltown).category.replace(
          "Cool news" -> "Super cool news",
          "events" -> "Super cool events").update
        _ <- Community.name_("belltown 2").category.get.map(_.head ==> Set("Super cool news", "Super cool events"))

        // Add value
        _ <- Community(belltown).category.assert("extra category").update
        _ <- Community.name_("belltown 2").category.get.map(_.head ==> Set("Super cool news", "Super cool events", "extra category"))

        // Remove value
        _ <- Community(belltown).category.retract("Super cool events").update
        _ <- Community.name_("belltown 2").category.get.map(_.head ==> Set("Super cool news", "extra category"))


        // Mixing updates and deletes..........................

        // Values before
        _ <- Community.name("belltown 2").tpe.url.category.get.map(_ ==> List(
          ("belltown 2", "blog", "url 2", Set("Super cool news", "extra category"))
        ))

        // Applying nothing (empty parenthesises) finds and retract all values of an attribute
        _ <- Community(belltown).name("belltown 3").url().category().update

        // Belltown has no longer a url or any categories
        _ <- Community.name("belltown 3").tpe.url.category.get.map(_ ==> List())

        // ..but we still have a belltown with a name and type
        _ <- Community.name("belltown 3").tpe.get.map(_ ==> List(("belltown 3", "blog")))


        // Retract entities ...................................

        // Belltown exists
        _ <- Community.name("belltown 3").get.map(_.size ==> 1)

        // Simply use an entity id and retract it!
        _ <- belltown.retract

        // Belltown is gone
        _ <- Community.name("belltown 3").get.map(_.size ==> 0)
      } yield ()
    }
  }
}