//package molecule
//package types
//import java.net.URI
//import java.util.{Date, UUID}
//import molecule.types.dsl.types._
//import molecule.types.schema._
//import shapeless._
//
//class TypesTest extends CoreSpec {
//
//  // Make db
//  implicit val conn = load(TypesSchema.tx)
//
//  // Load data
//  One.str.int.long.float.double.bool.date.uuid.uri.enum.insert(
//    str1, int1, long1, float1, double1, bool1, date1, uuid1, uri1, enum1)
//
//  Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.insert(
//    Set(str1, str2),
//    Set(int1, int2),
//    Set(long1, long2),
//    Set(float1, float2),
//    Set(double1, double2),
//    Set(date1, date2),
//    Set(uuid1, uuid2),
//    Set(uri1, uri2),
//    Set(enum1, enum2))
//
//  "Infer return types of cardinality-one attributes" >> {
//
//    // `get` + `take(n)` (is calling `get`)
//    typed[String](One.str.get.head)
//    typed[Int](One.int.get.head)
//    typed[Long](One.long.get.head)
//    typed[Float](One.float.get.head)
//    typed[Double](One.double.get.head)
//    typed[Boolean](One.bool.get.head)
//    typed[Date](One.date.get.head)
//    typed[UUID](One.uuid.get.head)
//    typed[URI](One.uri.get.head)
//    typed[String](One.enum.get.head)
//
//
//    // Arity 2
//
//    // Tuple
//    typed[(String, Int)](One.str.int.tpls.head)
//    typed[(Long, Float)](One.long.float.tpls.head)
//    typed[(Double, Boolean)](One.double.bool.tpls.head)
//    typed[(Date, UUID)](One.date.uuid.tpls.head)
//    typed[(URI, String)](One.uri.enum.tpls.head)
//
//    // HList
//    typed[String :: Int :: HNil](One.str.int.hls.head)
//    typed[Long :: Float :: HNil](One.long.float.hls.head)
//    typed[Double :: Boolean :: HNil](One.double.bool.hls.head)
//    typed[Date :: UUID :: HNil](One.date.uuid.hls.head)
//    typed[URI :: String :: HNil](One.uri.enum.hls.head)
//
//
//    // Arity 3
//
//    typed[(String, Int, Long)](One.str.int.long.tpls.head)
//    typed[(Float, Double, Boolean)](One.float.double.bool.tpls.head)
//    typed[(Date, UUID, URI)](One.date.uuid.uri.tpls.head)
//    // looping from the first types...
//    typed[(String, String, Int)](One.enum.str.int.tpls.head)
//
//    typed[String :: Int :: Long :: HNil](One.str.int.long.hls.head)
//    typed[Float :: Double :: Boolean :: HNil](One.float.double.bool.hls.head)
//    typed[Date :: UUID :: URI :: HNil](One.date.uuid.uri.hls.head)
//    typed[String :: String :: Int :: HNil](One.enum.str.int.hls.head)
//
//
//    // Arity 4
//
//    typed[(String, Int, Long, Float)](One.str.int.long.float.tpls.head)
//    typed[(Double, Boolean, Date, UUID)](One.double.bool.date.uuid.tpls.head)
//    typed[(URI, String, String, Int)](One.uri.enum.str.int.tpls.head)
//
//    typed[String :: Int :: Long :: Float :: HNil](One.str.int.long.float.hls.head)
//    typed[Double :: Boolean :: Date :: UUID :: HNil](One.double.bool.date.uuid.hls.head)
//    typed[URI :: String :: String :: Int :: HNil](One.uri.enum.str.int.hls.head)
//
//
//    // Arity 5
//
//    typed[(String, Int, Long, Float, Double)](One.str.int.long.float.double.tpls.head)
//    typed[(Boolean, Date, UUID, URI, String)](One.bool.date.uuid.uri.enum.tpls.head)
//
//    typed[String :: Int :: Long :: Float :: Double :: HNil](One.str.int.long.float.double.hls.head)
//    typed[Boolean :: Date :: UUID :: URI :: String :: HNil](One.bool.date.uuid.uri.enum.hls.head)
//
//
//    // Arity 6
//
//    typed[(String, Int, Long, Float, Double, Boolean)](One.str.int.long.float.double.bool.tpls.head)
//    typed[(Date, UUID, URI, String, String, Int)](One.date.uuid.uri.enum.str.int.tpls.head)
//
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: HNil](One.str.int.long.float.double.bool.hls.head)
//    typed[Date :: UUID :: URI :: String :: String :: Int :: HNil](One.date.uuid.uri.enum.str.int.hls.head)
//
//
//    // Arity 7
//
//    typed[(String, Int, Long, Float, Double, Boolean, Date)](One.str.int.long.float.double.bool.date.tpls.head)
//    typed[(UUID, URI, String, String, Int, Long, Float)](One.uuid.uri.enum.str.int.long.float.tpls.head)
//
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: HNil](One.str.int.long.float.double.bool.date.hls.head)
//    typed[UUID :: URI :: String :: String :: Int :: Long :: Float :: HNil](One.uuid.uri.enum.str.int.long.float.hls.head)
//
//
//    // Arity 8
//
//    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID)](One.str.int.long.float.double.bool.date.uuid.tpls.head)
//    typed[(URI, String, String, Int, Long, Float, Double, Boolean)](One.uri.enum.str.int.long.float.double.bool.tpls.head)
//
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: HNil](One.str.int.long.float.double.bool.date.uuid.hls.head)
//    typed[URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: HNil](One.uri.enum.str.int.long.float.double.bool.hls.head)
//
//
//    // Arity 9
//
//    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI)](One.str.int.long.float.double.bool.date.uuid.uri.tpls.head)
//    typed[(String, String, Int, Long, Float, Double, Boolean, Date, UUID)](One.enum.str.int.long.float.double.bool.date.uuid.tpls.head)
//
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: HNil](One.str.int.long.float.double.bool.date.uuid.uri.hls.head)
//    typed[String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: HNil](One.enum.str.int.long.float.double.bool.date.uuid.hls.head)
//
//
//    // Arity 10
//
//    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String)](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.tpls.head)
//
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: HNil](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.hls.head)
//
//
//    // Arity 11
//
//    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String)](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.tpls.head)
//
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: HNil](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.hls.head)
//
//
//    // Arity 12
//
//    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int)](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.tpls.head)
//
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: HNil](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.hls.head)
//
//
//    // Arity 13
//
//    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long)](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.tpls.head)
//
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: HNil](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.hls.head)
//
//
//    // Arity 14
//
//    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long, Float)](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.tpls.head)
//
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: HNil](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.hls.head)
//
//
//    // Arity 15
//
//    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long, Float, Double)](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.tpls.head)
//
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: HNil](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.hls.head)
//
//
//    // Arity 16
//
//    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long, Float, Double, Boolean)](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.tpls.head)
//
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: HNil](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.hls.head)
//
//
//    // Arity 17
//
//    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long, Float, Double, Boolean, Date)](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.tpls.head)
//
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: HNil](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.hls.head)
//
//
//    // Arity 18
//
//    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long, Float, Double, Boolean, Date, UUID)](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.tpls.head)
//
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: HNil](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.hls.head)
//
//
//    // Arity 19
//
//    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long, Float, Double, Boolean, Date, UUID, URI)](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.tpls.head)
//
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: HNil](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.hls.head)
//
//
//    // Arity 20
//
//    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String)](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.enum.tpls.head)
//
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: HNil](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.enum.hls.head)
//
//
//    // Arity 21
//
//    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String)](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.enum.str.tpls.head)
//
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: HNil](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.enum.str.hls.head)
//
//
//    // Arity 22
//
//    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int)](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.tpls.head)
//
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: HNil](
//      One.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.hls.head)
//
//    // All types have been correctly inferred
//    ok
//  }
//
//  "Infer return types of cardinality-many attributes" >> {
//
//    // `get` + `take(n)` (is calling `get`)
//    typed[Set[String]](Many.strM.get.head)
//    typed[Set[Int]](Many.intM.get.head)
//    typed[Set[Long]](Many.longM.get.head)
//    typed[Set[Float]](Many.floatM.get.head)
//    typed[Set[Double]](Many.doubleM.get.head)
//    typed[Set[Date]](Many.dateM.get.head)
//    typed[Set[UUID]](Many.uuidM.get.head)
//    typed[Set[URI]](Many.uriM.get.head)
//    typed[Set[String]](Many.enumM.get.head)
//
//
//    // Arity 2
//
//    // Tuple
//    typed[(Set[String], Set[Int])](Many.strM.intM.tpls.head)
//    typed[(Set[Long], Set[Float])](Many.longM.floatM.tpls.head)
//    typed[(Set[Double], Set[Date])](Many.doubleM.dateM.tpls.head)
//    typed[(Set[UUID], Set[URI])](Many.uuidM.uriM.tpls.head)
//    typed[(Set[String], Set[String])](Many.enumM.strM.tpls.head)
//
//    // HList
//    typed[Set[String] :: Set[Int] :: HNil](Many.strM.intM.hls.head)
//    typed[Set[Long] :: Set[Float] :: HNil](Many.longM.floatM.hls.head)
//    typed[Set[Double] :: Set[Date] :: HNil](Many.doubleM.dateM.hls.head)
//    typed[Set[UUID] :: Set[URI] :: HNil](Many.uuidM.uriM.hls.head)
//    typed[Set[String] :: Set[String] :: HNil](Many.enumM.strM.hls.head)
//
//
//    // Arity 3
//
//    typed[(Set[String], Set[Int], Set[Long])](Many.strM.intM.longM.tpls.head)
//    typed[(Set[Float], Set[Double], Set[Date])](Many.floatM.doubleM.dateM.tpls.head)
//    typed[(Set[UUID], Set[URI], Set[String])](Many.uuidM.uriM.enumM.tpls.head)
//    typed[(Set[String], Set[String], Set[Int])](Many.enumM.strM.intM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: HNil](Many.strM.intM.longM.hls.head)
//    typed[Set[Float] :: Set[Double] :: Set[Date] :: HNil](Many.floatM.doubleM.dateM.hls.head)
//    typed[Set[UUID] :: Set[URI] :: Set[String] :: HNil](Many.uuidM.uriM.enumM.hls.head)
//    typed[Set[String] :: Set[String] :: Set[Int] :: HNil](Many.enumM.strM.intM.hls.head)
//
//
//    // Arity 4
//
//    typed[(Set[String], Set[Int], Set[Long], Set[Float])](Many.strM.intM.longM.floatM.tpls.head)
//    typed[(Set[Double], Set[Date], Set[UUID], Set[URI])](Many.doubleM.dateM.uuidM.uriM.tpls.head)
//    typed[(Set[String], Set[String], Set[Int], Set[Long])](Many.enumM.strM.intM.longM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: HNil](Many.strM.intM.longM.floatM.hls.head)
//    typed[Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: HNil](Many.doubleM.dateM.uuidM.uriM.hls.head)
//    typed[Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: HNil](Many.enumM.strM.intM.longM.hls.head)
//
//
//    // Arity 5
//
//    typed[(Set[String], Set[Int], Set[Long], Set[Float], Set[Double])](Many.strM.intM.longM.floatM.doubleM.tpls.head)
//    typed[(Set[Date], Set[UUID], Set[URI], Set[String], Set[String])](Many.dateM.uuidM.uriM.enumM.strM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: HNil](Many.strM.intM.longM.floatM.doubleM.hls.head)
//    typed[Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: HNil](Many.dateM.uuidM.uriM.enumM.strM.hls.head)
//
//
//    // Arity 6
//
//    typed[(Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date])](Many.strM.intM.longM.floatM.doubleM.dateM.tpls.head)
//    typed[(Set[UUID], Set[URI], Set[String], Set[String], Set[Int], Set[Long])](Many.uuidM.uriM.enumM.strM.intM.longM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: HNil](Many.strM.intM.longM.floatM.doubleM.dateM.hls.head)
//    typed[Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: HNil](Many.uuidM.uriM.enumM.strM.intM.longM.hls.head)
//
//
//    // Arity 7
//
//    typed[(Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID])](Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.tpls.head)
//    typed[(Set[URI], Set[String], Set[String], Set[Int], Set[Long], Set[Float], Set[Double])](Many.uriM.enumM.strM.intM.longM.floatM.doubleM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: HNil](Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.hls.head)
//    typed[Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: HNil](Many.uriM.enumM.strM.intM.longM.floatM.doubleM.hls.head)
//
//
//    // Arity 8
//
//    typed[(Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI])](Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.tpls.head)
//    typed[(Set[String], Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID])](Many.enumM.strM.intM.longM.floatM.doubleM.dateM.uuidM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: HNil](Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.hls.head)
//    typed[Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: HNil](Many.enumM.strM.intM.longM.floatM.doubleM.dateM.uuidM.hls.head)
//
//
//    // Arity 9
//
//    typed[(Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI], Set[String])](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: HNil](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.hls.head)
//
//
//    // Arity 10
//
//    typed[(Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI], Set[String], Set[String])](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: HNil](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.hls.head)
//
//
//    // Arity 11
//
//    typed[(Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI], Set[String], Set[String], Set[Int])](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: HNil](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.hls.head)
//
//
//    // Arity 12
//
//    typed[(Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI], Set[String], Set[String], Set[Int], Set[Long])](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: HNil](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.hls.head)
//
//
//    // Arity 13
//
//    typed[(Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI], Set[String], Set[String], Set[Int], Set[Long], Set[Float])](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: HNil](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.hls.head)
//
//
//    // Arity 14
//
//    typed[(Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI], Set[String], Set[String], Set[Int], Set[Long], Set[Float], Set[Double])](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.doubleM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: HNil](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.doubleM.hls.head)
//
//
//    // Arity 15
//
//    typed[(Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI], Set[String], Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date])](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.doubleM.dateM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: HNil](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.doubleM.dateM.hls.head)
//
//
//    // Arity 16
//
//    typed[(Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI], Set[String], Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID])](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.doubleM.dateM.uuidM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: HNil](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.doubleM.dateM.uuidM.hls.head)
//
//
//    // Arity 17
//
//    typed[(Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI], Set[String], Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI])](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: HNil](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.hls.head)
//
//
//    // Arity 18
//
//    typed[(Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI], Set[String], Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI], Set[String])](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: HNil](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.hls.head)
//
//
//    // Arity 19
//
//    typed[(Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI], Set[String], Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI], Set[String], Set[String])](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: HNil](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.hls.head)
//
//
//    // Arity 20
//
//    typed[(Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI], Set[String], Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI], Set[String], Set[String], Set[Int])](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: HNil](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.hls.head)
//
//
//    // Arity 21
//
//    typed[(Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI], Set[String], Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI], Set[String], Set[String], Set[Int], Set[Long])](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: HNil](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.hls.head)
//
//
//    // Arity 22
//
//    typed[(Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI], Set[String], Set[String], Set[Int], Set[Long], Set[Float], Set[Double], Set[Date], Set[UUID], Set[URI], Set[String], Set[String], Set[Int], Set[Long], Set[Float])](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.tpls.head)
//
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: HNil](
//      Many.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.doubleM.dateM.uuidM.uriM.enumM.strM.intM.longM.floatM.hls.head)
//
//    // All types have been correctly inferred
//    ok
//  }
//}