package molecule.examples.dayOfDatomic
import molecule._
import molecule.examples.dayOfDatomic.dsl.socialNews._
import molecule.examples.dayOfDatomic.schema._
import org.specs2.specification.Scope
import scala.language.postfixOps


class SocialNewsSetup extends Scope with DatomicFacade {

  // 1-2. Make db
  implicit val conn = recreateDbFrom(SocialNewsSchema)

  // Add Stories
  val List(s1, s2, s3) = Story.title.url insert List(
    ("Teach Yourself Programming in Ten Years", "http://norvig.com/21-days.html"),
    ("Clojure Rationale", "http://clojure.org/rationale"),
    ("Beating the Averages", "http://www.paulgraham.com/avg.html")
  ) eids

  // Add Users
  val List(stu, ed) = User.firstName.lastName.email insert List(
    ("Stu", "Halloway", "stuarthalloway@datomic.com"),
    ("Ed", "Itor", "editor@example.com")
  ) eids


  // Add comments
  // Input Molecule act as a template to insert data
  val addComment = Parent.e.Comment.author.text.insert

  // Insert Stu's first comment to story 1 and return the id of this comment
  // (Parent s1 is a Story)
  val c1 = addComment(s1, stu, "blah 1") eid

  // Ed's Comment to Stu's first Comment
  // (Parent c1 is a Comment)
  val c2 = addComment(c1, ed, "blah 2") eid
  // More sub-comments
  val c3 = addComment(c2, stu, "blah 3") eid
  val c4 = addComment(c3, ed, "blah 4") eid

  // Story 2 comments
  val c5 = addComment(s2, ed, "blah 5") eid
  val c6 = addComment(c5, stu, "blah 6") eid

  // Story 3 comments
  val c7 = addComment(s3, ed, "blah 7") eid
  val c8 = addComment(c7, stu, "blah 8") eid
  // Stu comments on his own comment
  val c9 = addComment(c8, stu, "blah 9") eid

  // Story 2 again - a second thread of comments. This time Stu starts
  val c10 = addComment(s2, stu, "blah 10") eid
  val c11 = addComment(c10, ed, "blah 11") eid
  val c12 = addComment(c11, stu, "blah 12") eid
}