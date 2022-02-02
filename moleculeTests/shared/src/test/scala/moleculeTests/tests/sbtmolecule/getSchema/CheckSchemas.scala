package moleculeTests.tests.sbtmolecule.getSchema

import molecule.core.data.model._
import molecule.core.macros.GetTransactSchema.schema
import moleculeTests.dataModels.core.base.schema.CoreTestSchema
import moleculeTests.dataModels.core.bidirectionals.schema.BidirectionalSchema
import moleculeTests.dataModels.core.schemaDef.schema.PartitionTestSchema
import moleculeTests.dataModels.examples.datomic.mbrainz.schema.MBrainzSchema
import moleculeTests.setup.AsyncTestSuite
import utest._


object CheckSchemas extends AsyncTestSuite {


  lazy val tests = Tests {

    "CoreTest" - {
      val s = schema {
        trait Ns {
          val str     = oneString.fulltext.doc("Card one String attribute")
          val int     = oneInt.doc("Card one Int attribute")
          val long    = oneLong
          val double  = oneDouble
          val bool    = oneBoolean
          val date    = oneDate
          val uuid    = oneUUID
          val uri     = oneURI
          val bigInt  = oneBigInt
          val bigDec  = oneBigDecimal
          val enumm   = oneEnum("enum0", "enum1", "enum2", "enum3", "enum4", "enum5", "enum6", "enum7", "enum8", "enum9")
          val parent  = one[Ns]
          val ref1    = one[Ref1]
          val refSub1 = one[Ref1].isComponent

          val strs     = manyString.fulltext
          val ints     = manyInt
          val longs    = manyLong
          val doubles  = manyDouble
          val bools    = manyBoolean
          val dates    = manyDate
          val uuids    = manyUUID
          val uris     = manyURI
          val bigInts  = manyBigInt
          val bigDecs  = manyBigDecimal
          val enums    = manyEnum("enum0", "enum1", "enum2", "enum3", "enum4", "enum5", "enum6", "enum7", "enum8", "enum9")
          val parents  = many[Ns]
          val refs1    = many[Ref1]
          val refsSub1 = many[Ref1].isComponent

          val strMap    = mapString.fulltext
          val intMap    = mapInt
          val longMap   = mapLong
          val doubleMap = mapDouble
          val boolMap   = mapBoolean
          val dateMap   = mapDate
          val uuidMap   = mapUUID
          val uriMap    = mapURI
          val bigIntMap = mapBigInt
          val bigDecMap = mapBigDecimal
        }

        trait Ref1 {
          val str1    = oneString.fulltext
          val int1    = oneInt
          val enum1   = oneEnum("enum10", "enum11", "enum12")
          val ref2    = one[Ref2]
          val refSub2 = one[Ref2].isComponent

          val strs1    = manyString
          val ints1    = manyInt
          val enums1   = manyEnum("enum10", "enum11", "enum12")
          val refs2    = many[Ref2]
          val refsSub2 = many[Ref2].isComponent

          val nss     = many[Ns]
          val intMap1 = mapInt
        }

        trait Ref2 {
          val str2  = oneString.uniqueIdentity.fulltext
          val int2  = oneInt.uniqueValue
          val enum2 = oneEnum("enum20", "enum21", "enum22")
          val strs2 = manyString
          val ints2 = manyInt.noHistory
          val ref3  = one[Ref3]
          val refs3 = many[Ref3]
        }

        trait Ref3 {
          val str3  = oneString
          val int3  = oneInt
          val ref4  = one[Ref4]
          val refs4 = many[Ref4]
        }

        trait Ref4 {
          val str4 = oneString
          val int4 = oneInt
        }
      }
      s.datomicPeer ==> CoreTestSchema.datomicPeer
      s.datomicClient ==> CoreTestSchema.datomicClient
      s.metaSchema ==> CoreTestSchema.metaSchema
      s.nsMap ==> CoreTestSchema.nsMap
      s.attrMap ==> CoreTestSchema.attrMap
    }


    "Bidirectional" - {
      val s = schema {

        // Base namespace ......................................................

        object Person extends Person
        trait Person {
          // A ==> a
          val spouse  = oneBi[Person]
          val friends = manyBi[Person]

          // A ==> b
          val pet     = oneBi[Animal.master.type]
          val buddies = manyBi[Animal.buddies.type]

          // A ==> edge -- a
          val loves = oneBiEdge[Loves.person.type]
          val knows = manyBiEdge[Knows.person.type]

          // A ==> edge -- b
          val favorite = oneBiEdge[Favorite.animal.type]
          val closeTo  = manyBiEdge[CloseTo.animal.type]

          val name = oneString
        }


        // Property edges to same namespace ....................................

        // (card-one)
        object Loves extends Loves
        trait Loves {
          // a --- edge ==> a
          val person: AnyRef = target[Person.loves.type]

          // Edge properties
          val weight          = oneInt
          val howWeMet        = oneEnum("inSchool", "atWork", "throughFriend")
          val commonInterests = manyString
          val commonLicences  = manyEnum("climbing", "diving", "parachuting", "flying")
          val commonScores    = mapInt
          val coreQuality     = one[Quality]
          val inCommon        = many[Quality]
        }

        // (card-many)
        object Knows extends Knows
        trait Knows {
          // a --- edge ==> a
          val person: AnyRef = target[Person.knows.type]

          // Edge properties
          val weight          = oneInt
          val howWeMet        = oneEnum("inSchool", "atWork", "throughFriend")
          val commonInterests = manyString
          val commonLicences  = manyEnum("climbing", "diving", "parachuting", "flying")
          val commonScores    = mapInt
          val coreQuality     = one[Quality]
          val inCommon        = many[Quality]
        }


        // Property edges to other namespace ..................................

        // (card-one)
        object Favorite extends Favorite
        trait Favorite {
          // a <== edge --- b
          val person: AnyRef = target[Person.favorite.type]
          // a --- edge ==> b
          val animal: AnyRef = target[Animal.favorite.type]

          // Edge properties
          val weight          = oneInt
          val howWeMet        = oneEnum("inSchool", "atWork", "throughFriend")
          val commonInterests = manyString
          val commonLicences  = manyEnum("climbing", "diving", "parachuting", "flying")
          val commonScores    = mapInt
          val coreQuality     = one[Quality]
          val inCommon        = many[Quality]
        }

        // (card-many)
        object CloseTo extends CloseTo
        trait CloseTo {
          // a <== edge --- b
          val person: AnyRef = target[Person.closeTo.type]
          // a --- edge ==> b
          val animal: AnyRef = target[Animal.closeTo.type]

          // Edge properties
          val weight          = oneInt
          val howWeMet        = oneEnum("inSchool", "atWork", "throughFriend")
          val commonInterests = manyString
          val commonLicences  = manyEnum("climbing", "diving", "parachuting", "flying")
          val commonScores    = mapInt
          val coreQuality     = one[Quality]
          val inCommon        = many[Quality]
        }

        // Sample ns to demonstrate edge ref property
        trait Quality {
          val name = oneString
        }


        // Other connected namespace .......................................

        object Animal extends Animal
        trait Animal {
          // Other end/start point of other ref
          // a <== B
          val master : AnyRef = oneBi[Person.pet.type]
          val buddies: AnyRef = manyBi[Person.buddies.type]

          // Other end/start point of edges between different namespaces
          // a -- edge <== B
          val favorite = oneBiEdge[Favorite.person.type]
          val closeTo  = manyBiEdge[CloseTo.person.type]

          val name = oneString
        }
      }
      s.datomicPeer ==> BidirectionalSchema.datomicPeer
      s.datomicClient ==> BidirectionalSchema.datomicClient
      s.metaSchema ==> BidirectionalSchema.metaSchema
      s.nsMap ==> BidirectionalSchema.nsMap
      s.attrMap ==> BidirectionalSchema.attrMap
    }


    "PartitionTest" - {
      val s = schema {
        object gen {
          trait Profession {
            val name = oneString
          }

          trait Person {
            val name        = oneString
            val gender      = oneEnum("male", "female")
            val professions = many[Profession]
          }
        }

        object lit {
          trait Book {
            val title     = oneString
            val author    = one[gen.Person]
            // To avoid attr/partition name clashes we can prepend the definition object name
            // (in case we would have needed an attribute named `gen` for instance)
            val editor    = one[gen.Person]
            val cat       = oneEnum("good", "bad")
            val reviewers = many[gen.Person]
          }
        }
      }
      s.datomicPeer ==> PartitionTestSchema.datomicPeer
      s.datomicClient ==> PartitionTestSchema.datomicClient
      s.metaSchema ==> PartitionTestSchema.metaSchema
      s.nsMap ==> PartitionTestSchema.nsMap
      s.attrMap ==> PartitionTestSchema.attrMap
    }


    "MBrainz" - {

      // Comment lines are correctly ignored

      val s = schema {
        //  trait AbstractRelease {
        //    val name         = oneString
        //    val artistCredit = oneString.fulltext
        //    val gid          = oneUUID.uniqueIdentity
        //    val `type`       = oneEnum("album", "single", "ep", "audiobook", "other")
        //    val artists      = many[Artist]
        //  }

        trait Artist {
          val startYear  = oneLong
          val startMonth = oneLong
          val startDay   = oneLong
          val endYear    = oneLong
          val endMonth   = oneLong
          val endDay     = oneLong
          val sortName   = oneString
          val name       = oneString.fulltext
          val gid        = oneUUID.uniqueIdentity
          val tpe        = oneEnum("person", "group", "other").alias("type")
          val gender     = oneEnum("male", "female", "other")
          val country    = one[Country]
        }

        trait Country {
          val name = oneString.uniqueValue
        }

        //  trait Label {
        //    val startYear  = oneLong
        //    val startMonth = oneLong
        //    val startDay   = oneLong
        //    val endYear    = oneLong
        //    val endMonth   = oneLong
        //    val endDay     = oneLong
        //    val sortName   = oneString
        //    val name       = oneString.fulltext
        //    val gid        = oneUUID.uniqueIdentity
        //    val `type`     = oneEnum("distributor", "holding", "production", "originalProduction", "bootlegProduction", "reissueProduction", "publisher")
        //    val country    = one[Country]
        //  }
        //
        //  trait Language {
        //    val name = oneString.uniqueValue
        //  }

        trait Medium {
          val name       = oneString.fulltext
          val position   = oneLong
          //    val position   = manyLong
          val trackCount = oneLong
          val format     = oneEnum("dvdVideo", "laserDisc", "cd", "hddvd", "vhs", "svcd", "dcc", "cdr", "slotMusic", "bluray", "waxCylinder", "cartridge", "umd", "miniDisc", "vinyl", "vinyl12", "sacd", "other", "dualDisc", "vinyl10", "dvd", "pianoRoll", "betamax", "vcd", "dat", "reel", "vinyl7", "dvdAudio", "digitalMedia", "hdcd", "videotape", "usbFlashDrive", "cassette", "cd8cm")
          val tracks     = many[Track].isComponent
        }

        trait Release {
          val year         = oneLong
          val month        = oneLong
          val day          = oneLong
          val artistCredit = oneString.fulltext
          val status       = oneString
          val barcode      = oneString
          val name         = oneString.fulltext
          val gid          = oneUUID.uniqueIdentity
          val artists      = many[Artist]
          //    val abstractRelease = one[AbstractRelease]
          //    val language        = one[Language]
          val media        = many[Medium].isComponent
          val packaging    = oneEnum("jewelCase", "slimJewelCase", "digipak", "none", "keepCase", "cardboardPaperSleeve", "other")
          //    val script          = one[Script]
          //    val label           = one[Label]
          val country      = one[Country]
        }

        //  trait Script {
        //    val name = oneString.uniqueValue
        //  }

        trait Track {
          val position     = oneLong
          val duration     = oneLong
          val name         = oneString.fulltext
          val artists      = many[Artist]
          val artistCredit = oneString.fulltext
        }
      }
      s.datomicPeer ==> MBrainzSchema.datomicPeer
      s.datomicClient ==> MBrainzSchema.datomicClient
      s.metaSchema ==> MBrainzSchema.metaSchema
      s.nsMap ==> MBrainzSchema.nsMap
      s.attrMap ==> MBrainzSchema.attrMap
    }
  }
}
