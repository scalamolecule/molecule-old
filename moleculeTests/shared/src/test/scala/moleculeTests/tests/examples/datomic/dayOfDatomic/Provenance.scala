package moleculeTests.tests.examples.datomic.dayOfDatomic

import molecule.datomic.api.out7._
import molecule.datomic.base.util.SystemPeerServer
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.examples.datomic.dayOfDatomic.dsl.SocialNews._
import utest._
import molecule.core.util.Executor._
import scala.concurrent.Future
import scala.language.reflectiveCalls


object Provenance extends AsyncTestSuite {

  lazy val tests = Tests {

    "Transaction meta data" - socialNews { implicit conn =>
      val ecURL = "http://blog.datomic.com/2012/09/elasticache-in-5-minutes.html"
      for {
        (txR2, tx1, s1, s2, s3, stu, ed, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12) <- SocialNews.data

        // Stu adds two new stories with AddStories use case
        stuTx <- Story.title.url.Tx(MetaData.user_(stu).usecase_("AddStories")) insert List(
          ("ElastiCache in 6 minutes", "http://blog.datomic.com/2012/09/elasticache-in-5-minutes.html"),
          ("Keep Chocolate Love Atomic", "http://blog.datomic.com/2012/08/atomic-chocolate.html")
        )
        stuTxId = stuTx.tx

        // Two story entities created (and two tx meta data datoms added to transaction)
        List(elasticacheStory, chocolateStory) = stuTx.eids

        // Now we have 5 stories - the two last from the transaction above
        _ <- Story.title.url.tx.get.map(_.sortBy(t => (t._3, t._1)) ==> List(
          ("Beating the Averages", "http://www.paulgraham.com/avg.html", tx1),
          ("Clojure Rationale", "http://clojure.org/rationale", tx1),
          ("Teach Yourself Programming in Ten Years", "http://norvig.com/21-days.html", tx1),
          ("ElastiCache in 6 minutes", "http://blog.datomic.com/2012/09/elasticache-in-5-minutes.html", stuTxId),
          ("Keep Chocolate Love Atomic", "http://blog.datomic.com/2012/08/atomic-chocolate.html", stuTxId),
        ))

        // We can also traverse the generated entity ids to see what is saved
        _ <- elasticacheStory.graph.map(_ ==> Map(
          ":db/id" -> elasticacheStory,
          ":Story/title" -> "ElastiCache in 6 minutes",
          ":Story/url" -> "http://blog.datomic.com/2012/09/elasticache-in-5-minutes.html"))

        _ <- chocolateStory.graph.map(_ ==> Map(
          ":db/id" -> chocolateStory,
          ":Story/title" -> "Keep Chocolate Love Atomic",
          ":Story/url" -> "http://blog.datomic.com/2012/08/atomic-chocolate.html"))

        // Time of transaction
        stuTxInstant <- User(stuTxId).txInstant.get.map(_.head)

        // Limit entity traversal 1 level deep
        _ <- stuTxId.graphDepth(1).map(_ ==> Map(
          ":db/id" -> stuTxId,
          ":db/txInstant" -> stuTxInstant,
          ":MetaData/usecase" -> "AddStories",
          ":MetaData/user" -> stu
        ))

        // Or full traversal to Stu's data
        _ <- stuTxId.graph.map(_ ==> Map(
          ":db/id" -> stuTxId,
          ":db/txInstant" -> stuTxInstant,
          ":MetaData/usecase" -> "AddStories",
          ":MetaData/user" -> Map(
            ":db/id" -> stu,
            ":User/email" -> "stuarthalloway@datomic.com",
            ":User/firstName" -> "Stu",
            ":User/lastName" -> "Halloway"
          )
        ))

        // Find data via transaction meta data

        // Stories that Stu added (first meta information used)
        _ <- Story.title.url.Tx(MetaData.user_(stu)).get.map(_.sortBy(_._1) ==> List(
          ("ElastiCache in 6 minutes", ecURL),
          ("Keep Chocolate Love Atomic", "http://blog.datomic.com/2012/08/atomic-chocolate.html"),
        ))

        // Stories that were added with the AddStories use case (second meta information used)
        _ <- Story.title.url.Tx(MetaData.usecase_("AddStories")).get.map(_.sortBy(_._1) ==> List(
          ("ElastiCache in 6 minutes", ecURL),
          ("Keep Chocolate Love Atomic", "http://blog.datomic.com/2012/08/atomic-chocolate.html"),
        ))

        // Stories that Stu added with the AddStories use case (both meta data used)
        _ <- Story.title.url.Tx(MetaData.user_(stu).usecase_("AddStories")).get.map(_.sortBy(_._1) ==> List(
          ("ElastiCache in 6 minutes", ecURL),
          ("Keep Chocolate Love Atomic", "http://blog.datomic.com/2012/08/atomic-chocolate.html"),
        ))

        // Stories and transactions where Stu added stories (`tx` is returned)
        _ <- Story.title.tx.Tx(MetaData.user_(stu).usecase_("AddStories")).get.map(_.sortBy(_._1) ==> List(
          ("ElastiCache in 6 minutes", stuTxId),
          ("Keep Chocolate Love Atomic", stuTxId),
        ))

        // Stories and names of who added them (Note that we can have referenced meta data!)
        _ <- Story.title.Tx(MetaData.User.firstName.lastName).get.map(_.sortBy(_._1) ==> List(
          ("ElastiCache in 6 minutes", "Stu", "Halloway"),
          ("Keep Chocolate Love Atomic", "Stu", "Halloway")
        ))

        // Stories added by a user named "Stu"
        _ <- Story.title.Tx(MetaData.User.firstName_("Stu")).get.map(_.sorted ==> List(
          "ElastiCache in 6 minutes",
          "Keep Chocolate Love Atomic"
        ))

        // Stories added by a user with email "stuarthalloway@datomic.com"
        _ <- Story.title.Tx(MetaData.User.email_("stuarthalloway@datomic.com")).get.map(_.sorted ==> List(
          "ElastiCache in 6 minutes",
          "Keep Chocolate Love Atomic"
        ))

        // Count of stories added by a user with email "stuarthalloway@datomic.com"
        _ <- Story.title(count).Tx(MetaData.User.email_("stuarthalloway@datomic.com")).get.map(_.head ==> 2)

        // Emails of users who added stories
        _ <- Story.title_.Tx(MetaData.usecase_("AddStories").User.email).get.map(_ ==> List(
          "stuarthalloway@datomic.com"
        ))


        // Updating data with additional transaction meta data...

        // Ed fixes the spelling error
        edTxId <- Story(elasticacheStory).title("ElastiCache in 5 minutes").Tx(MetaData.user(ed).usecase_("UpdateStory")).update.map(_.tx)

        // Title now
        _ <- Story.url_(ecURL).title.get.map(_.head ==> "ElastiCache in 5 minutes")

        // Title before (using database as of the first transaction)
        _ <- Story.url_(ecURL).title.getAsOf(stuTx.txInstant).map(_.head ==> "ElastiCache in 6 minutes")

        _ <- if (system != SystemPeerServer) {
          for {
            // Who changed the title and when? Using the history database
            _ <- Story.url_(ecURL).title.op.tx.Tx(MetaData.usecase.User.firstName).getHistory.map(_.sortBy(r => (r._3, r._2)) ==> List(
              ("ElastiCache in 6 minutes", true, stuTxId, "AddStories", "Stu"), // Stu adds the story
              ("ElastiCache in 6 minutes", false, edTxId, "UpdateStory", "Ed"), // retraction automatically added by Datomic
              ("ElastiCache in 5 minutes", true, edTxId, "UpdateStory", "Ed") // Ed's update of the title
            ))

            // Entire attributes history of ElastiCache story _entity_
            res <- Story(elasticacheStory).a.v.op.tx.Tx(MetaData.usecase.User.firstName).getHistory.map(_.sortBy(r => (r._4, r._3)) ==> List(
              (":Story/title", "ElastiCache in 6 minutes", true, stuTxId, "AddStories", "Stu"),
              (":Story/url", "http://blog.datomic.com/2012/09/elasticache-in-5-minutes.html", true, stuTxId, "AddStories", "Stu"),
              (":Story/title", "ElastiCache in 6 minutes", false, edTxId, "UpdateStory", "Ed"),
              (":Story/title", "ElastiCache in 5 minutes", true, edTxId, "UpdateStory", "Ed"),
            ))
          } yield res
        } else Future.unit

        // Stories with latest use case meta date
        _ <- Story.title.Tx(MetaData.usecase).get.map(_.sortBy(_._1) ==> List(
          ("ElastiCache in 5 minutes", "UpdateStory"),
          ("Keep Chocolate Love Atomic", "AddStories"),
        ))

        // Stories without use case meta data
        _ <- Story.title.Tx(MetaData.usecase_(Nil)).get.map(_.sorted ==> List(
          "Beating the Averages",
          "Clojure Rationale",
          "Teach Yourself Programming in Ten Years"
        ))
      } yield ()
    }
  }
}
