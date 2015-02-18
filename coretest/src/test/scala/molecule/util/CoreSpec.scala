package molecule.util

import java.net.URI
import java.util.Date
import java.util.UUID._

import molecule.DatomicFacade
import molecule.util.schema.CoreTestSchema
import org.specs2.specification.Scope

class CoreSetup extends Scope with DatomicFacade {
  implicit val conn = load(CoreTestSchema.tx)
  //  implicit val conn = load(CoreTestSchema.tx, "core")
}

class CoreSpec extends MoleculeSpec with DatomicFacade {

  // Sample data
  def uu = randomUUID()
  def ur(i: Int) = new URI("uri" + i)
  def da(s: Int) = new Date(1000L * s)

  lazy val List(uuid0, uuid1, uuid2, uuid3, uuid4, uuid5, uuid6, uuid7, uuid8, uuid9) = List(uu, uu, uu, uu, uu, uu, uu, uu, uu, uu).sortBy(_.toString)
  lazy val List(uri0, uri1, uri2, uri3, uri4, uri5, uri6, uri7, uri8, uri9)           = List(ur(0), ur(1), ur(2), ur(3), ur(4), ur(5), ur(6), ur(7), ur(8), ur(9)).sortBy(_.toString)
  lazy val (date0, date1, date2, date3, date4, date5, date6, date7, date8, date9)     = (da(0), da(1), da(2), da(3), da(4), da(5), da(6), da(7), da(8), da(9))

  lazy val (str0, int0, long0, float0, double0, bool0, enum0) = (" ", 0, 0L, 0.0f, 0.0, false, "enum0")
  lazy val (str1, int1, long1, float1, double1, bool1, enum1) = ("a", 1, 1L, 1.0f, 1.0, true, "enum1")
  lazy val (str2, int2, long2, float2, double2, bool2, enum2) = ("b", 2, 2L, 2.0f, 2.0, false, "enum2")
  lazy val (str3, int3, long3, float3, double3, bool3, enum3) = ("c", 3, 3L, 3.0f, 3.0, true, "enum3")
  lazy val (str4, int4, long4, float4, double4, bool4, enum4) = ("d", 4, 4L, 4.0f, 4.0, false, "enum4")
  lazy val (str5, int5, long5, float5, double5, bool5, enum5) = ("e", 4, 4L, 4.0f, 4.0, true, "enum5")
  lazy val (str6, int6, long6, float6, double6, bool6, enum6) = ("f", 4, 4L, 4.0f, 4.0, false, "enum6")
  lazy val (str7, int7, long7, float7, double7, bool7, enum7) = ("g", 4, 4L, 4.0f, 4.0, true, "enum7")
  lazy val (str8, int8, long8, float8, double8, bool8, enum8) = ("h", 4, 4L, 4.0f, 4.0, false, "enum8")
  lazy val (str9, int9, long9, float9, double9, bool9, enum9) = ("i", 4, 4L, 4.0f, 4.0, true, "enum9")

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