package molecule

import java.net.URI
import java.util.UUID._
import java.util.Date
import molecule.util.MoleculeSpec
import molecule.semantics.schema.CoreTestSchema
import org.specs2.specification.Scope

class CoreSetup extends Scope with DatomicFacade {
  implicit val conn = load(CoreTestSchema.tx)
//  implicit val conn = load(CoreTestSchema.tx, "core")
}

class CoreSpec extends MoleculeSpec with DatomicFacade {

  // Sample data

  lazy val List(uuid0, uuid1, uuid2) = List(randomUUID(), randomUUID(), randomUUID()).sortBy(_.toString)
  lazy val List(uri0, uri1, uri2)    = List(new URI(""), new URI("uri1"), new URI("uri2")).sortBy(_.toString)
  lazy val (date0, date1, date2)     = (new Date(0L), new Date(1L), new Date(2L))

  lazy val (str0, int0, long0, float0, double0, bool0, enum0) = (" ", 0, 0L, 0.0f, 0.0, false, "enum0")
  lazy val (str1, int1, long1, float1, double1, bool1, enum1) = ("str1", 1, 1L, 1.1f, 1.1, true, "enum1")
  lazy val (str2, int2, long2, float2, double2, bool2, enum2) = ("str2", 2, 2L, 2.2f, 2.2, false, "enum2")

  //  lazy val (strs0, ints0, longs0, floats0, doubles0, dates0, uuids0, uris0, enums0) = (
  //    Set[String](),
  //    Set[Int](),
  //    Set[Long](),
  //    Set[Float](),
  //    Set[Double](),
  //    Set[Date](),
  //    Set[UUID](),
  //    Set[URI](),
  //    Set[String]())

  lazy val (strs0, ints0, longs0, floats0, doubles0, dates0, uuids0, uris0, enums0) = (
    Set(str0),
    Set(int0),
    Set(long0),
    Set(float0),
    Set(double0),
    Set(date0),
    Set(uuid0),
    Set(uri0),
    Set(enum0))

  lazy val (strs1, ints1, longs1, floats1, doubles1, dates1, uuids1, uris1, enums1) = (
    Set(str1),
    Set(int1),
    Set(long1),
    Set(float1),
    Set(double1),
    Set(date1),
    Set(uuid1),
    Set(uri1),
    Set(enum1))

  lazy val (strs2, ints2, longs2, floats2, doubles2, dates2, uuids2, uris2, enums2) = (
    Set(str1, str2),
    Set(int1, int2),
    Set(long1, long2),
    Set(float1, float2),
    Set(double1, double2),
    Set(date1, date2),
    Set(uuid1, uuid2),
    Set(uri1, uri2),
    Set(enum1, enum2))

  //  lazy val (strs3, ints3, longs3, floats3, doubles3, dates3, uuids3, uris3, enums3) = (
  //    Set(str0, str1, str2),
  //    Set(int0, int1, int2),
  //    Set(long0, long1, long2),
  //    Set(float0, float1, float2),
  //    Set(double0, double1, double2),
  //    Set(date0, date1, date2),
  //    Set(uuid0, uuid1, uuid2),
  //    Set(uri0, uri1, uri2),
  //    Set(enum0, enum1, enum2))
}