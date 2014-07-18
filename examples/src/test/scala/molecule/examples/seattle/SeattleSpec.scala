package molecule
package examples.seattle
import java.io.FileReader
import datomic.{Connection, Peer, Util}
import molecule.examples.seattle.dsl.seattle._
import molecule.examples.seattle.schema.SeattleSchema
import molecule.util.MoleculeSpec


trait SeattleSpec extends MoleculeSpec with DatomicFacade {

  def loadFromFiles(schemaFile: String, dataFile: String, version: Int) = {
    val uri = "datomic:mem://seattle" + version
    Peer.deleteDatabase(uri)
    Peer.createDatabase(uri)
    implicit val conn = Peer.connect(uri)

    val dataDir = "examples/resources/seattle/"
    val schema_rdr = new FileReader(dataDir + schemaFile)
    val schema_tx = Util.readAll(schema_rdr).get(0).asInstanceOf[java.util.List[_]]
    conn.transact(schema_tx).get()

    val data_rdr = new FileReader(dataDir + dataFile)
    val data_tx = Util.readAll(data_rdr).get(0).asInstanceOf[java.util.List[_]]
    conn.transact(data_tx).get()

    conn
  }

  def loadSeattle(version: Int): Connection = {
    implicit val conn = load(SeattleSchema.tx, "seattle" + version)
    // Load Seattle data
    Community.name.url.`type`.orgtype.category.Neighborhood.name.District.name.region.insert(seattleData)
    conn
  }

  implicit val conn = loadSeattle(1)

  lazy val seattleData = List(
    ("15th Ave Community", "http://groups.yahoo.com/group/15thAve_Community/", "email_list", "community", Set("15th avenue residents"), "Capitol Hill", "East", "e"),
    ("Admiral Neighborhood Association", "http://groups.yahoo.com/group/AdmiralNeighborhood/", "email_list", "community", Set("neighborhood association"), "Admiral (West Seattle)", "Southwest", "sw"),
    ("Alki News", "http://groups.yahoo.com/group/alkibeachcommunity/", "email_list", "community", Set("members of the Alki Community Council and residents of the Alki Beach neighborhood"), "Alki", "Southwest", "sw"),
    ("Alki News/Alki Community Council", "http://alkinews.wordpress.com/", "blog", "community", Set("news", "council meetings"), "Alki", "Southwest", "sw"),
    ("All About Belltown", "http://www.belltown.org/", "website", "community", Set("community council"), "Belltown", "Downtown", "w"),
    ("All About South Park", "http://www.allaboutsouthpark.com/", "website", "community", Set("neighborhood info"), "South Park", "Greater Duwamish", "s"),
    ("ArtsWest", "http://www.artswest.org/?q=node/28", "website", "community", Set("arts"), "West Seattle", "Southwest", "sw"),
    ("At Large in Ballard", "http://blog.seattlepi.com/ballard/", "blog", "commercial", Set("news", "human interest"), "Ballard", "Ballard", "nw"),
    ("Aurora Seattle", "http://www.auroraseattle.com/", "blog", "community", Set("news", "traffic", "planning"), "Regional Sites", "Northeast", "ne"),
    ("Ballard Avenue", "http://www.ballardavenue.blogspot.com/", "blog", "community", Set("personal ballard-centric blog"), "Ballard", "Ballard", "nw"),
    ("Ballard Blog", "http://ballardblog.com/", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Ballard", "Ballard", "nw"),
    ("Ballard Chamber of Commerce", "http://www.ballardchamber.com/", "website", "community", Set("chamber of commerce"), "Ballard", "Ballard", "nw"),
    ("Ballard District Council", "http://www.ballarddistrictcouncil.org/", "website", "community", Set("district council"), "Ballard", "Ballard", "nw"),
    ("Ballard Gossip Girl", "http://www.ballardgossipgirl.com/", "blog", "commercial", Set("nightlift", "shopping", "restaurants"), "Ballard", "Ballard", "nw"),
    ("Ballard Historical Society", "http://www.ballardhistory.org/", "website", "community", Set("historical society"), "Ballard", "Ballard", "nw"),
    ("Ballard Moms", "http://groups.yahoo.com/group/BallardMoms/", "email_list", "community", Set("Ballard parents"), "Ballard", "Ballard", "nw"),
    ("Ballard Neighbor Connection", "http://groups.yahoo.com/group/BallardNeighborConnection/", "email_list", "community", Set("neighborhood residents"), "Ballard", "Ballard", "nw"),
    ("Beach Drive Blog", "http://www.mortgageporter.com/beach_drive/", "blog", "commercial", Set("home sales", "crime", "news"), "Alki", "Southwest", "sw"),
    ("Beacon Hill Alliance of Neighbors", "http://www.cityofseattle.net/ban/", "website", "community", Set("public safety", "community"), "Beacon Hill", "Greater Duwamish", "s"),
    ("Beacon Hill Blog", "http://beaconhill.seattle.wa.us/", "blog", "commercial", Set("events", "food", "nightlife", "criminal activity", "news"), "Beacon Hill", "Greater Duwamish", "s"),
    ("Beacon Hill Burglaries", "http://maps.google.com/maps/ms?ie=UTF8&hl=en&msa=0&msid=107398592337461190820.000449fcf97ff8bfbe281&z=14or", "email_list", "community", Set("criminal activity"), "Beacon Hill", "Greater Duwamish", "s"),
    ("Beacon Hill Community Site", "http://beaconhillcommunity.wetpaint.com/", "wiki", "commercial", Set("community concerns", "announcements", "news"), "Beacon Hill", "Greater Duwamish", "s"),

    // Manually added:
    ("BikeWorks!", "http://www.bikeworks.org/", "website", null, null, "Columbia City", "Southeast", "se"),

    ("Blogging Georgetown", "http://www.blogginggeorgetown.com/", "blog", "commercial", Set("911 blotter", "news", "events"), "Georgetown", "Greater Duwamish", "s"),
    ("Blogging Georgetown", "http://www.facebook.com/blogginggeorgetown", "facebook_page", "community", Set("911 blotter", "news", "events"), "Georgetown", "Greater Duwamish", "s"),
    ("Broadview Community Council", "http://groups.google.com/group/broadview-community-council", "email_list", "community", Set("community council"), "Broadview", "Northwest", "sw"),
    ("Broadview Community Council", "http://www.broadviewseattle.org/", "blog", "community", Set("community council"), "Broadview", "Northwest", "sw"),
    ("CHS Capitol Hill Seattle Blog", "http://www.capitolhillseattle.com/", "blog", "commercial", Set("drink", "events", "food", "criminal activity", "news"), "Capitol Hill", "East", "e"),
    ("Capitol Hill Community Council", "http://chcc.wikidot.com/", "wiki", "community", Set("community council", "news", "events"), "Capitol Hill", "East", "e"),
    ("Capitol Hill Housing", "http://capitolhillhousing.org/", "website", "community", Set("affordable housing"), "Capitol Hill", "East", "e"),
    ("Capitol Hill Triangle", "http://chtriangle.blogspot.com/", "blog", "commercial", Set("local miscellany"), "Capitol Hill", "East", "e"),
    ("Central Area Community Festival Association", "http://www.cacf.com/", "website", "community", Set("festival planning association"), "Central District", "Central", "e"),
    ("Central Ballard Community Council", "http://www.neighborhoodlink.com/seattle/cbcc/", "website", "community", Set("community council"), "Ballard", "Ballard", "nw"),
    ("Central District News", "http://www.centraldistrictnews.com/", "blog", "commercial", Set("drink", "events", "food", "reviews", "news"), "Central District", "Central", "e"),
    ("Chinatown/International District", "http://www.cidbia.org/", "website", "community", Set("business improvement association"), "International District", "Downtown", "w"),
    ("Chinese Information and Service Center", "http://www.cisc-seattle.org/", "website", "nonprofit", Set("support services", "advocacy", "referral"), "International District", "Downtown", "w"),
    ("Columbia Citizens", "http://columbiacitizens.net/", "wiki", "community", Set("favorite places", "neighborhood community site; buying/selling", "restaurants", "etc."), "Columbia City", "Southeast", "se"),
    ("Columbia Citizens", "http://twitter.com/CCitizens", "twitter", "community", Set("twitter for Columbia Citizens"), "Columbia City", "Southeast", "se"),
    ("Columbia Citizens", "http://www.facebook.com/pages/Columbia-Citizens/48558627705", "facebook_page", "community", Set("favorite places", "neighborhood community site; buying/selling", "restaurants", "etc."), "Columbia City", "Southeast", "se"),
    ("Columbia City Blog", "http://www.columbiacityblog.com/", "blog", "commercial", Set("shopping", "news", "events", "food"), "Columbia City", "Southeast", "se"),
    ("Columbia City, Seattle", "http://www.columbiacityseattle.com/", "website", "community", Set("business association"), "Columbia City", "Southeast", "se"),
    ("Columbia City: Rainier Valley", "http://www.rainiervalley.org/", "website", "community", Set("portal"), "Rainier Valley", "Southeast", "se"),
    ("Community Harvest of Southwest Seattle", "http://gleanit.org", "website", "community", Set("sustainable food"), "West Seattle", "Southwest", "sw"),
    ("Crown Hill Neighbors", "http://www.crownhillneighbors.org", "website", "community", Set("news", "neighborhood-issues", "neighborhood-planning"), "Crown Hill", "Ballard", "nw"),
    ("Delridge Grassroots Leadership", "http://delridge.blogspot.com/", "website", "community", Set("community organization"), "Delridge", "Delridge", "sw"),
    ("Delridge Neighborhoods Development Association", "http://www.dnda.org/", "website", "community", Set("neighborhood association"), "Delridge", "Delridge", "sw"),
    ("Delridge Produce Cooperative", "http://sites.google.com/site/delridgeproducecooperative/", "website", "community", Set("produce coop"), "Delridge", "Delridge", "sw"),
    ("Discover SLU", "http://www.discoverslu.com", "website", "commercial", Set("shopping", "news", "events", "dining"), "South Lake Union", "Lake Union", "w"),
    ("Discover SLU", "http://www.facebook.com/discoverslu", "facebook_page", "commercial", Set("shopping", "news", "events", "dining"), "South Lake Union", "Lake Union", "w"),
    ("Discover SLU", "http://www.twitter.com/southlakeunion", "twitter", "commercial", Set("shopping", "news", "events", "dining"), "South Lake Union", "Lake Union", "w"),
    ("Downtown Dispatch", "http://downtowndispatch.com/", "blog", "commercial", Set("news and events"), "Downtown", "Downtown", "w"),

    // Manually added:
    ("Downtown Seattle Association", "http://www.downtownseattle.com/", "website", null, Set("business association"), "Downtown", "Downtown", null),

    ("East Ballard Community Association Blog", "http://eastballard.wordpress.com/", "blog", "community", Set("community association", "news", "events", "meeting"), "Ballard", "Ballard", "nw"),
    ("Eastlake Ave. ", "http://eastlakeave.neighborlogs.com/", "blog", "commercial", Set("criminal activity", "news", "events", "traffic"), "Eastlake", "Lake Union", "w"),
    ("Eastlake Ave. ", "http://twitter.com/eastlakeave", "blog", "commercial", Set("updates on blog posts"), "Eastlake", "Lake Union", "w"),
    ("Eastlake Community Council", "http://www.eastlakeseattle.org/", "website", "community", Set("community council"), "Eastlake", "Lake Union", "w"),
    ("Eastlake Community Council", "http://www.facebook.com/group.php?gid=7145111961", "facebook_page", "community", Set("community council"), "Eastlake", "Lake Union", "w"),
    ("Fauntleroy Community Association", "http://fauntleroy.net/", "website", "community", Set("community association"), "Fauntleroy", "Southwest", "sw"),
    ("Fauntleroy Community Association", "http://www.facebook.com/pages/Seattle-WA/Fauntleroy-Community-Association/63181596775?v=wall&viewas=1779772562&ref=ts", "facebook_page", "community", Set("community assocation"), "Fauntleroy", "Southwest", "sw"),
    ("First Hill Improvement Association", "http://www.firsthill.org/", "website", "community", Set("neighborhood association"), "First Hill", "East", "e"),
    ("Fremont Arts Council", "http://groups.yahoo.com/group/fremontartscouncil/", "email_list", "community", Set("fremont arts council members"), "Fremont", "Lake Union", "w"),
    ("Fremont Arts Council", "http://www.fremontartscouncil.org/", "website", "community", Set("communtiy group"), "Fremont", "Lake Union", "w"),
    ("Fremont Chamber of Commerce", "http://www.fremontseattle.com/", "website", "community", Set("chamber of commerce"), "Fremont", "Lake Union", "w"),
    ("Fremont Universe", "http://twitter.com/fremontuniverse", "twitter", "commercial", Set("news", "events", "reviews", "food"), "Fremont", "Lake Union", "w"),
    ("Fremont Universe", "http://www.facebook.com/pages/Fremont-Universe-Seattle/88279594341?ref=s", "facebook_page", "commercial", Set("news", "events", "reviews", "food"), "Fremont", "Lake Union", "w"),
    ("Fremont Universe", "http://www.fremontuniverse.com/", "blog", "commercial", Set("news", "events", "reviews", "food"), "Fremont", "Lake Union", "w"),
    ("Friends of Discovery Park", "http://www.friendsdiscoverypark.org/", "website", "community", Set("park issues"), "Magnolia", "Magnolia/Queen Anne", "w"),
    ("Friends of Fremont Peak Park", "http://www.fremontpeakpark.org/", "website", "community", Set("community organization"), "Fremont", "Lake Union", "w"),
    ("Friends of Frink Park", "http://www.frinkpark.org/", "website", "community", Set("park issues"), "Central District", "Central", "e"),
    ("Friends of Green Lake", "http://www.friendsofgreenlake.org/", "website", "community", Set("neighborhood group"), "Green Lake", "Northwest", "sw"),
    ("Friends of Seward Park", "http://www.sewardpark.org/", "website", "community", Set("park issues"), "Seward Park", "Southeast", "se"),
    ("Genesee-Schmitz Neighborhood Council", "http://genesee-schmitz.org/", "blog", "community", Set("neighborhood council"), "Genesee-Schmitz", "Southwest", "sw"),
    ("Georgetown Art Center", "http://georgetownartcenter.blogspot.com/", "blog", "community", Set("community arts"), "Georgetown", "Greater Duwamish", "s"),
    ("Georgetown Neighborhood", "http://www.georgetownneighborhood.com/", "website", "community", Set("community council"), "Georgetown", "Greater Duwamish", "s"),
    ("Georgetown Seattle", "http://groups.yahoo.com/group/Georgetown-Seattle/", "email_list", "community", Set("community members"), "Georgetown", "Greater Duwamish", "s"),
    ("Greenlake Community Council", "http://www.greenlakecommunitycouncil.org/", "website", "community", Set("community council"), "Green Lake", "Northwest", "sw"),
    ("Greenlake Community Wiki", "http://greenlake.wetpaint.com/", "wiki", "community", Set("events", "services", "for sale"), "Green Lake", "Northwest", "sw"),
    ("Greenwood Aurora Involved Neighbors", "http://www.gainseattle.com/", "website", "community", Set("neighborhood association"), "Greenwood", "Northwest", "sw"),
    ("Greenwood Blog", "http://www.greenwoodblog.com/", "blog", "commercial", Set("news", "events"), "Greenwood", "Northwest", "sw"),
    ("Greenwood Community Council", "http://www.greenwoodcommunitycouncil.org/about/", "website", "community", Set("community council"), "Greenwood", "Northwest", "sw"),
    ("Greenwood Community Council Announcements", "http://groups.yahoo.com/group/Greenwood_News/", "email_list", "community", Set("community council"), "Greenwood", "Northwest", "sw"),
    ("Greenwood Community Council Discussion", "http://groups.yahoo.com/group/greenwood-discussion/", "email_list", "community", Set("community council"), "Greenwood", "Northwest", "sw"),
    ("Greenwood Phinney Chamber of Commerce", "http://www.greenwood-phinney.com/", "website", "community", Set("chamber of commerce"), "Greenwood", "Northwest", "sw"),
    ("Haller Lake Community Club", "http://www.hallerlake.info/", "website", "community", Set("community organization"), "Haller Lake", "North", "n"),
    ("Hawthorne Hills Community Website", "http://www.seattle.gov/hawthornehills/", "website", "community", Set("community council"), "Hawthorne Hills", "Northeast", "ne"),
    ("Highland Park Action Committee", "http://www.highlandpk.net/", "website", "community", Set("community group"), "Highland Park", "Delridge", "sw"),
    ("Highland Park Improvement Club", "http://www.hpic1919.org/", "blog", "community", Set("neighborhood group"), "Highland Park", "Delridge", "sw"),
    ("InBallard", "http://inballard.com/", "website", "commercial", Set("shopping", "nightlife", "food", "services"), "Ballard", "Ballard", "nw"),
    ("Interbay District Blog", "http://interbayneighborhood.neighborlogs.com/", "blog", "community", Set("news", "sports"), "Interbay", "Magnolia/Queen Anne", "w"),
    ("Interbay Neighborhood Association", "http://www.our-interbay.org/", "website", "community", Set("neighborhood association"), "Interbay", "Magnolia/Queen Anne", "w"),
    ("Jefferson Park Alliance", "http://www.cityofseattle.net/commnty/Beacon/groups/jpa/", "website", "community", Set("community"), "Beacon Hill", "Greater Duwamish", "s"),
    ("Junction Neighborhood Organization", "http://www.wsjuno.com/", "blog", "community", Set("meetings", "events"), "Junction", "Southwest", "sw"),
    ("KOMO Communities - Ballard", "http://ballard.komonews.com/", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Ballard", "Ballard", "nw"),
    ("KOMO Communities - Beacon Hill", "http://beaconhill.komonews.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Beacon Hill", "Greater Duwamish", "s"),
    ("KOMO Communities - Captol Hill", "http://capitolhill.komonews.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Capitol Hill", "East", "e"),
    ("KOMO Communities - Central District", "http://centraldistrict.komonews.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Central District", "Central", "e"),
    ("KOMO Communities - Columbia City", "http://columbiacity.komonews.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Columbia City", "Southeast", "se"),
    ("KOMO Communities - Downtown", "http://downtown.komonews.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Downtown", "Downtown", "w"),
    ("KOMO Communities - Fremont", "http://fremont.komonews.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Fremont", "Lake Union", "w"),
    ("KOMO Communities - Georgetown", "http://georgetown.komonews.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Georgetown", "Greater Duwamish", "s"),
    ("KOMO Communities - Green Lake", "http://greenlake.komonews.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Green Lake", "Northwest", "sw"),
    ("KOMO Communities - Greenwood-Phinney", "http://greenwood-phinney.komonews.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Greenwood-Phinney", "Northwest", "sw"),
    ("KOMO Communities - Lake City", "http://lakecity.komonews.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Lake City", "North", "n"),
    ("KOMO Communities - Madison Park", "http://madisonpark.komonews.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Madison Park", "East", "e"),
    ("KOMO Communities - Magnolia", "http://magnolia.komonews.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Magnolia", "Magnolia/Queen Anne", "w"),
    ("KOMO Communities - North Seattle", "http://northseattle.komonews.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "North Seattle", "North", "n"),
    ("KOMO Communities - Queen Anne", "http://queenanne.komonews.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Queen Anne", "Magnolia/Queen Anne", "w"),
    ("KOMO Communities - Rainier Valley", "http://rainiervalley.komonews.com", "blog", "commercial", Set("shopping", "events", "food", "criminal activity", "human interest", "news"), "Rainier Valley", "Southeast", "se"),
    ("KOMO Communities - South Lake Union", "http://southlakeunion.komonews.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "South Lake Union", "Lake Union", "w"),
    ("KOMO Communities - U-District", "http://udistrict.komonews.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "University District", "Northeast", "ne"),
    ("KOMO Communities - View Ridge", "http://viewridge.komonews.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "View Ridge", "Northeast", "ne"),
    ("KOMO Communities - Wallingford", "http://wallingford.komonews.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Wallingford", "Northwest", "sw"),
    ("KOMO Communities - West Seattle", "http://westseattle.komonews.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "West Seattle", "Southwest", "sw"),
    ("Lake City Live", "http://www.lakecitylive.net/", "blog", "commercial", Set("drink", "food", "reviews", "criminal activity", "news"), "Lake City", "North", "n"),
    ("Laurelhurst Community Club", "http://www.laurelhurstcc.com/", "website", "community", Set("community club"), "Laurelhurst", "Northeast", "ne"),
    ("Leschi Community Council", "http://groups.google.com/group/LeschiCC?hl=en", "email_list", "community", Set("meeting times", "planning"), "Leschi", "Central", "e"),
    ("Licton Springs Neighborhood ", "http://www.lictonsprings.org/", "website", "community", Set("neighborhood council"), "Licton Springs", "Northwest", "sw"),
    ("Longfellow Creek Community Website", "http://www.longfellowcreek.org/", "website", "community", Set("watershed info"), "Delridge", "Delridge", "sw"),
    ("MLK Business Association", "http://www.mlkba.org/", "website", "community", Set("business association"), "Rainier Valley", "Southeast", "se"),
    ("Madison Park Blogger", "http://madisonparkblogger.blogspot.com/", "blog", "commercial", Set("housing prices", "news", "events"), "Madison Park", "East", "e"),
    ("Madison Park Business Association", "http://www.madisonparkseattle.com/", "website", "community", Set("business association"), "Madison Park", "East", "e"),
    ("Madrona Moms", "http://health.groups.yahoo.com/group/MadronaMoms/", "email_list", "community", Set("community group (moms)"), "Madrona", "Central", "e"),
    ("Madrona Neighborhood", "http://madrona.wetpaint.com/", "wiki", "community", Set("etc.  Also community association meeting minutes", "favorite places", "neighborhood community site: buying/selling", "restaurants"), "Madrona", "Central", "e"),
    ("Magnolia Action Group", "http://www.orgsites.com/wa/mag/", "website", "community", Set("planning issues"), "Magnolia", "Magnolia/Queen Anne", "w"),
    ("Magnolia Chamber of Commerce", "http://www.magnoliachamber.org/", "website", "community", Set("chamber of commerce"), "Magnolia", "Magnolia/Queen Anne", "w"),
    ("Magnolia Historical Society", "http://www.magnoliahistoricalsociety.org/", "website", "community", Set("historical society"), "Magnolia", "Magnolia/Queen Anne", "w"),
    ("Magnolia Neighborhood Planning Council", "http://magnolianpc.com/", "website", "community", Set("neighborhood association"), "Magnolia", "Magnolia/Queen Anne", "w"),
    ("Magnolia Voice", "http://twitter.com/magnoliavoice", "twitter", "commercial", Set("criminal activity", "news", "events", "food"), "Magnolia", "Magnolia/Queen Anne", "w"),
    ("Magnolia Voice", "http://www.facebook.com/pages/Magnolia-Voice-Seattle/116057104388", "facebook_page", "commercial", Set("criminal activity", "news", "events", "food"), "Magnolia", "Magnolia/Queen Anne", "w"),
    ("Magnolia Voice", "http://www.magnoliavoice.com/", "blog", "commercial", Set("criminal activity", "news", "events", "food"), "Magnolia", "Magnolia/Queen Anne", "w"),
    ("Magnolia is Really Part of Seattle", "http://sleeplessinmagnolia.ning.com/", "wiki", "community", Set("planning issues"), "Magnolia", "Magnolia/Queen Anne", "w"),

    // Manually added
    ("Magnuson Community Garden", "http://cityofseattle.net/MAGNUSONGARDEN", "website", null, Set("garden"), "Laurelhurst", "Northeast", null),

    ("Magnuson Environmental Stewardship Alliance", "http://mesaseattle.org/", "website", "community", Set("park issues"), "Laurelhurst", "Northeast", "ne"),
    ("Maple Leaf Community Council", "http://www.mapleleafcommunity.org/", "website", "community", Set("community council", "email list available"), "Maple Leaf", "Northeast", "ne"),
    ("Maple Leaf Life", "http://twitter.com/mapleleaflife", "twitter", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Maple Leaf", "Northeast", "ne"),
    ("Maple Leaf Life", "http://www.facebook.com/pages/Seattle-WA/Maple-Leaf-Life/298056021657", "facebook_page", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Maple Leaf", "Northeast", "ne"),
    ("Maple Leaf Life", "http://www.mapleleaflife.com/", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Maple Leaf", "Northeast", "ne"),
    ("Market On Wheels", "http://marketonwheels.wordpress.com/", "blog", "community", Set("farmers' market"), "South Park", "Greater Duwamish", "s"),
    ("Miller Park Neighborhood Association", "http://millerparkseattle.blogspot.com/", "blog", "community", Set("neighborhood association; news", "events"), "Miller Park", "East", "e"),
    ("Morgan Junction Community Association", "http://morganjunction.org/", "website", "community", Set("community association"), "Morgan Junction", "Southwest", "sw"),

    // Manually added
    ("Mount Baker Community Club", "http://groups.yahoo.com/group/MBCCCommunityNotices/", "email_list", null, Set("community club"), "Mount Baker", "Southeast", null),

    ("Mount Baker Neighborhood ", "http://www.mountbaker.org/index.php", "website", "community", Set("community group"), "Mount Baker", "Southeast", "se"),
    ("My Greenlake Blog", "http://www.mygreenlake.com/", "blog", "commercial", Set("news", "events", "food"), "Green Lake", "Northwest", "sw"),
    ("MyBallard", "http://www.myballard.com/", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Ballard", "Ballard", "nw"),
    ("MyWallingford", "http://mywallingford.com", "blog", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Wallingford", "Northwest", "sw"),
    ("MyWallingford", "http://twitter.com/mywallingford", "twitter", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Wallingford", "Northwest", "sw"),
    ("MyWallingford", "http://www.facebook.com/MyWallingford?ref=ts", "facebook_page", "commercial", Set("shopping", "events", "criminal activity", "human interest", "news"), "Wallingford", "Northwest", "sw"),
    ("Nature Consortium", "http://www.naturec.org", "website", "community", Set("environmental conservation"), "Delridge", "Delridge", "sw"),
    ("ballardite blog", "http://www.ballardite.blogspot.com/", "blog", "personal", Set("personal", "news"), "Ballard", "Ballard", "nw"),
    ("belltown", "http://www.belltownpeople.com/", "blog", "commercial", Set("news", "events"), "Belltown", "Downtown", "w")
  )


  //  // Extractor
  //  val dataFromFile = Community.name.url.`type`.orgtype.category.Neighborhood.name.District.name.region.hls.map { rec =>
  //    rec.toList.map {
  //      case set: Set[_] => set.map("\"" + _.toString + "\"")
  //      case other       => "\"" + other.toString + "\""
  //    }
  //  }.map(e => e.mkString("\n(", ", ", ")"))
  //  println(dataFromFile)
  //  println()
  //  println(seattleData.map(_._1).sorted.mkString("\n"))
}