package moleculeTests.tests.core.obj

import molecule.core.util.Helpers
import molecule.datomic.api.in1_out3._
import moleculeTests.setup.AsyncTestSuite
import moleculeTests.dataModels.core.base.dsl.CoreTest._
import utest._
import scala.concurrent.ExecutionContext.Implicits.global


object ObjBasics extends AsyncTestSuite with Helpers {

  lazy val tests = Tests {

    "How it works" - core { implicit conn =>
      for {
        _ <- Ns.int(0).str("x").Ref1.int1(1).save

        // `getObj` is same as `getObjList.head` - presuming one row is returned or that
        // just want the first of more rows
        _ <- Ns.int.str.Ref1.int1.getObj.map { o =>

          // The above molecule and object getter generates the following code:
          val obj = new Ns_int with Ns_str with Ns__Ref1[Ref1_int1] {
            override lazy val int: Int    = 0
            override lazy val str: String = "x"
            override def Ref1: Ref1_int1 = new Ref1_int1 {
              override lazy val int1: Int = 1
            }
          }

          // This way, we get type inference in the IDE and can access the data
          // as named object properties, even in referenced namespaces:


          // todo: check that all lines are checked
          o.int ==> 0
          o.str ==> "x"
          o.Ref1.int1 ==> 1
        }

        // We could also get the data as a tuple:
        _ <- Ns.int.str.Ref1.int1.get.map(_.head ==> (0, "x", 1))
      } yield ()
    }


    "Mandatory - Optional - Tacit" - core { implicit conn =>
      for {
        _ <- Ns.int.str$.bool$ insert List(
          (1, Some("a"), Some(true)),
          (2, Some("b"), None),
          (3, None, None),
        )

        // third row not returned since `str` is tacitly required
        _ <- Ns.int.str_.bool$.getObjs.collect { case List(o1, o2) =>
          o1.int ==> 1
          o1.bool$ ==> Some(true)

          o2.int ==> 2
          o2.bool$ ==> None
        }
      } yield ()
    }


    "Input" - core { implicit conn =>
      val inputMolecule = m(Ns.int.str(?))
      for {
        _ <- Ns.int(1).str("a").save

        // Object is created after and thus unaffected by input resolution
        _ <- inputMolecule("a").getObj.map { o =>
          o.int ==> 1
          o.str ==> "a"
        }
      } yield ()
    }


    "Mapped/Keyed" - core { implicit conn =>
      for {
        _ <- Ns.int.strMap insert List(
          (1, Map("en" -> "Hi there", "da" -> "Hejsa")),
          (2, Map("en" -> "Hello", "da" -> "Hej")),
        )
        _ <- Ns.int_(1).strMap.getObj.map(_.strMap ==> Map("en" -> "Hi there", "da" -> "Hejsa"))
        _ <- Ns.int_(1).strMap.getObj.map(_.strMap("en") ==> "Hi there")
        _ <- Ns.int_(1).strMapK("en").getObj.map(_.strMapK ==> "Hi there")
      } yield ()
    }


    "Repeated attributes" - core { implicit conn =>
      for {
        _ <- Ns.int.insert(1, 2, 3)

        // Object will only have 1 property given repeated attributes
        _ <- Ns.int.>(1).int.<(3).getObj.map(_.int ==> 2)

        // While tuple outputs the value twice
        _ <- Ns.int.>(1).int.<(3).get.map(_.head ==> (2, 2))

        // Or you can make one of the attributes tacit
        _ <- Ns.int.>(1).int_.<(3).get.map(_.head ==> 2)
      } yield ()
    }
  }
}
