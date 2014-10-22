//package molecule.semantics.hlist
//import java.net.URI
//import java.util.{Date, UUID}
//import molecule.{CoreSetup, CoreSpec}
//import molecule.util.dsl.coreTest._
//import shapeless._
//
//class hl_types extends CoreSpec {
//
//  "Infer return types of cardinality-one attributes" in new CoreSetup {
//
//    // Arity 1
//    typed[String :: HNil](Ns.str.hl.head)
//    typed[Int :: HNil](Ns.int.hl.head)
//    typed[Long :: HNil](Ns.long.hl.head)
//    typed[Float :: HNil](Ns.float.hl.head)
//    typed[Double :: HNil](Ns.double.hl.head)
//    typed[Boolean :: HNil](Ns.bool.hl.head)
//    typed[Date :: HNil](Ns.date.hl.head)
//    typed[UUID :: HNil](Ns.uuid.hl.head)
//    typed[URI :: HNil](Ns.uri.hl.head)
//    typed[String :: HNil](Ns.enum.hl.head)
//    typed[String :: HNil](Ns.Ref.str.hl.head)
//
//    // Arity 2
//    typed[String :: Int :: HNil](Ns.str.int.hl.head)
//    typed[Long :: Float :: HNil](Ns.long.float.hl.head)
//    typed[Double :: Boolean :: HNil](Ns.double.bool.hl.head)
//    typed[Date :: UUID :: HNil](Ns.date.uuid.hl.head)
//    typed[URI :: String :: HNil](Ns.uri.enum.hl.head)
//
//    // Arity 3
//    typed[String :: Int :: Long :: HNil](Ns.str.int.long.hl.head)
//    typed[Float :: Double :: Boolean :: HNil](Ns.float.double.bool.hl.head)
//    typed[Date :: UUID :: URI :: HNil](Ns.date.uuid.uri.hl.head)
//    // looping from the first types...
//    typed[String :: String :: Int :: HNil](Ns.enum.str.int.hl.head)
//
//    // Arity 4
//    typed[String :: Int :: Long :: Float :: HNil](Ns.str.int.long.float.hl.head)
//    typed[Double :: Boolean :: Date :: UUID :: HNil](Ns.double.bool.date.uuid.hl.head)
//    typed[URI :: String :: String :: Int :: HNil](Ns.uri.enum.str.int.hl.head)
//
//    // Arity 5
//    typed[String :: Int :: Long :: Float :: Double :: HNil](Ns.str.int.long.float.double.hl.head)
//    typed[Boolean :: Date :: UUID :: URI :: String :: HNil](Ns.bool.date.uuid.uri.enum.hl.head)
//
//    // Arity 6
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: HNil](Ns.str.int.long.float.double.bool.hl.head)
//    typed[Date :: UUID :: URI :: String :: String :: Int :: HNil](Ns.date.uuid.uri.enum.str.int.hl.head)
//
//    // Arity 7
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: HNil](Ns.str.int.long.float.double.bool.date.hl.head)
//    typed[UUID :: URI :: String :: String :: Int :: Long :: Float :: HNil](Ns.uuid.uri.enum.str.int.long.float.hl.head)
//
//    // Arity 8
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: HNil](Ns.str.int.long.float.double.bool.date.uuid.hl.head)
//    typed[URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: HNil](Ns.uri.enum.str.int.long.float.double.bool.hl.head)
//
//    // Arity 9
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: HNil](Ns.str.int.long.float.double.bool.date.uuid.uri.hl.head)
//    typed[String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: HNil](Ns.enum.str.int.long.float.double.bool.date.uuid.hl.head)
//
//    // Arity 10
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: HNil](
//      Ns.str.int.long.float.double.bool.date.uuid.uri.enum.hl.head)
//
//    // Arity 11
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: HNil](
//      Ns.str.int.long.float.double.bool.date.uuid.uri.enum.str.hl.head)
//
//    // Arity 12
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: HNil](
//      Ns.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.hl.head)
//
//    // Arity 13
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: HNil](
//      Ns.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.hl.head)
//
//    // Arity 14
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: HNil](
//      Ns.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.hl.head)
//
//    // Arity 15
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: HNil](
//      Ns.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.hl.head)
//
//    // Arity 16
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: HNil](
//      Ns.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.hl.head)
//
//    // Arity 17
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: HNil](
//      Ns.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.hl.head)
//
//    // Arity 18
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: HNil](
//      Ns.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.hl.head)
//
//    // Arity 19
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: HNil](
//      Ns.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.hl.head)
//
//    // Arity 20
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: HNil](
//      Ns.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.enum.hl.head)
//
//    // Arity 21
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: HNil](
//      Ns.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.enum.str.hl.head)
//
//    // Arity 22
//    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: HNil](
//      Ns.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.hl.head)
//
//    // All types have been correctly inferred
//    ok
//  }
//
//
//  "Infer return types of cardinality-many attributes" in new CoreSetup {
//
//    // Arity 1
//    typed[Set[String] :: HNil](Ns.strs.hl.head)
//    typed[Set[Int] :: HNil](Ns.ints.hl.head)
//    typed[Set[Long] :: HNil](Ns.longs.hl.head)
//    typed[Set[Float] :: HNil](Ns.floats.hl.head)
//    typed[Set[Double] :: HNil](Ns.doubles.hl.head)
//    typed[Set[Date] :: HNil](Ns.dates.hl.head)
//    typed[Set[UUID] :: HNil](Ns.uuids.hl.head)
//    typed[Set[URI] :: HNil](Ns.uris.hl.head)
//    typed[Set[String] :: HNil](Ns.enums.hl.head)
//    typed[String :: HNil](Ns.Refs.str.hl.head)
//
//    // Arity 2
//    typed[Set[String] :: Set[Int] :: HNil](Ns.strs.ints.hl.head)
//    typed[Set[Long] :: Set[Float] :: HNil](Ns.longs.floats.hl.head)
//    typed[Set[Double] :: Set[Date] :: HNil](Ns.doubles.dates.hl.head)
//    typed[Set[UUID] :: Set[URI] :: HNil](Ns.uuids.uris.hl.head)
//    typed[Set[String] :: Set[String] :: HNil](Ns.enums.strs.hl.head)
//
//    // Arity 3
//    typed[Set[String] :: Set[Int] :: Set[Long] :: HNil](Ns.strs.ints.longs.hl.head)
//    typed[Set[Float] :: Set[Double] :: Set[Date] :: HNil](Ns.floats.doubles.dates.hl.head)
//    typed[Set[UUID] :: Set[URI] :: Set[String] :: HNil](Ns.uuids.uris.enums.hl.head)
//    typed[Set[String] :: Set[String] :: Set[Int] :: HNil](Ns.enums.strs.ints.hl.head)
//
//    // Arity 4
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: HNil](Ns.strs.ints.longs.floats.hl.head)
//    typed[Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: HNil](Ns.doubles.dates.uuids.uris.hl.head)
//    typed[Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: HNil](Ns.enums.strs.ints.longs.hl.head)
//
//    // Arity 5
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: HNil](Ns.strs.ints.longs.floats.doubles.hl.head)
//    typed[Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: HNil](Ns.dates.uuids.uris.enums.strs.hl.head)
//
//    // Arity 6
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: HNil](Ns.strs.ints.longs.floats.doubles.dates.hl.head)
//    typed[Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: HNil](Ns.uuids.uris.enums.strs.ints.longs.hl.head)
//
//    // Arity 7
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: HNil](Ns.strs.ints.longs.floats.doubles.dates.uuids.hl.head)
//    typed[Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: HNil](Ns.uris.enums.strs.ints.longs.floats.doubles.hl.head)
//
//    // Arity 8
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: HNil](Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.hl.head)
//    typed[Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: HNil](Ns.enums.strs.ints.longs.floats.doubles.dates.uuids.hl.head)
//
//    // Arity 9
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: HNil](
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.hl.head)
//
//    // Arity 10
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: HNil](
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.strs.hl.head)
//
//    // Arity 11
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: HNil](
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.strs.ints.hl.head)
//
//    // Arity 12
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: HNil](
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.strs.ints.longs.hl.head)
//
//    // Arity 13
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: HNil](
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.strs.ints.longs.floats.hl.head)
//
//    // Arity 14
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: HNil](
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.strs.ints.longs.floats.doubles.hl.head)
//
//    // Arity 15
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: HNil](
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.strs.ints.longs.floats.doubles.dates.hl.head)
//
//    // Arity 16
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: HNil](
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.strs.ints.longs.floats.doubles.dates.uuids.hl.head)
//
//    // Arity 17
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: HNil](
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.strs.ints.longs.floats.doubles.dates.uuids.uris.hl.head)
//
//    // Arity 18
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: HNil](
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.hl.head)
//
//    // Arity 19
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: HNil](
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.strs.hl.head)
//
//    // Arity 20
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: HNil](
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.strs.ints.hl.head)
//
//    // Arity 21
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: HNil](
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.strs.ints.longs.hl.head)
//
//    // Arity 22
//    typed[Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: Set[Double] :: Set[Date] :: Set[UUID] :: Set[URI] :: Set[String] :: Set[String] :: Set[Int] :: Set[Long] :: Set[Float] :: HNil](
//      Ns.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.strs.ints.longs.floats.doubles.dates.uuids.uris.enums.strs.ints.longs.floats.hl.head)
//
//    // All types have been correctly inferred
//    ok
//  }
//}