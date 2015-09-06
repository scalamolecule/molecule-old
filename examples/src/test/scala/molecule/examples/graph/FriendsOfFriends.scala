//package molecule.examples.graph
//import molecule.DatomicFacade
//import org.specs2.mutable.Specification
//import molecule.examples.graph.schema.FriendsOfFriendsSchema
//import molecule.examples.graph.dsl.friendsOfFriends._
//import scalaz._
//import Scalaz._
////import scalaz.Tree._
////import scalaz.Tree.Node
//
//class FriendsOfFriends extends Specification with DatomicFacade {
//
//  trait CrudEntity[E <: CrudEntity[E]] {self: E =>
//    def create(entityData: String): E
//    def read(id: String): Option[E]
//    def update(f: E => E): E
//    def delete(id: String): Unit
//  }
//
//  //  case class Orange(name: String, age: Int) extends CrudEntity[Orange]
//
//  trait SelfRef[T <: SelfRef[T]] {self: T =>
//
//  }
//
//  "test..." >> {
//
//    val me = Person.name.get
//
//    val friends: Seq[(String, String)] = Person.name.Friends.name.get
//
//
//    val fof: Seq[(String, String, String)] = Person.name.Friends.name.Friends.name.get
//
//    val friends2: Seq[(String, Seq[String])] = Person.name.Friends.name.get
//
//    val fof1: Seq[(String, Int)] = Person.name.age.get
//    val fof2: Seq[(String, Int, String, Int)] = Person.name.age.Friends.name.age.get
////    val fof2: Seq[(String, Int, String, Int)] = Person.name.age.maybe(Friends.name.age)
//
//
//    val allPersons: Seq[Tree[(String, Int)]] = Person.name.age.Friends.tree(1)
//
//
//    val personTree: TreeLoc[(String, Int)] = allPersons.head.loc
//
//    personTree.
//
//    personTree.getChild(2) flatMap  {_.parent} match {
//      case _ => "hej"
//    }
//
//    personTree.find(_.getLabel._1 == "Woodwinds")
//
//    val me: (String, Int) = personTree.root.getLabel
//    val firstFriend: (String, Int) = personTree.firstChild.get.getLabel
//
//    val fof2b: Seq[(String, Int, Tree[(String, Int)])] = Person.name.age.Friends(1)
//    val fof2: Seq[(String, Int, Seq[(String, Int)])] = Person.name.age.Friends(2)
//    val fof3: Seq[(String, Seq[(String, Seq[String])])] = Person.name.Friends.recurse1
//
//
//
//    val tree = ("Root", 100).node(
//            ("Category1", 30).leaf,
//            ("Category2", 20).node(
//              ("Sub1", 15).leaf,
//              ("Sub2", 3).leaf,
//              ("Sub3", 2).leaf),
//            ("Category3", 10).leaf,
//            ("Category4", 30).node(
//              ("Sub1", 20).leaf,
//              ("Sub2", 5).leaf))
//
//    List(
//      ("Marc", Seq(
//          ("Jan", Seq("Yngve", "Charlie")),
//          ("Jon", Seq("Jan"))
//        )
//      )
//    )
//
//    List(
//      ("Marc", "Jan", "Charlie"),
//      ("Marc", "Jan", "Yngve"),
//      ("Marc", "Jon", "Jan")
//    )
//
//    val fof3: Seq[(String, )] = Person.name.Friends(2).name.get
//
//
//  }
//
//  //
//  //  case class Person(name: String, friends: Seq[Person]) extends SelfRef[Person] {
//  //
//  //  }
//  //
//  //  val charlie = Person("Charlie", Seq())
//  //  val yngve= Person("Yngve", Seq())
//  //  val jan = Person("Jan", Seq(charlie, yngve))
//  //  val jon = Person("Jon", Seq(jan))
//  //
//  //  val me = Person("Marc", Seq(jan, jon))
//
//
//  //  me.name.friends
//
//  //  f_names = for {
//  //      f <- Facebook.friends("me")
//  //      fof <- Facebook.friends(f)
//  //      if fof.age > 21
//  //  } yield (f.name, fof.name)
//
//}
