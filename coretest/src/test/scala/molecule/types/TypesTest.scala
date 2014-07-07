package molecule
package types
import java.net.URI
import java.util._
import molecule.types.dsl._
import molecule.types.schema._
import shapeless._

class TypesTest extends CoreTest {

  // Make db
  implicit val conn = load(TypesSchema.tx, "datomic:mem://types")

  // Load data
  OneType.str.int.long.float.double.bool.date.uuid.uri.enum.insert(
    str1, int1, long1, float1, double1, bool1, date1, uuid1, uri1, enum1)


  "Infer return types of cardinality-one attributes" >> {

    // `get` + `take(n)` (is calling `get`)
    typed[String](OneType.str.get.head)
    typed[Int](OneType.int.get.head)
    typed[Long](OneType.long.get.head)
    typed[Float](OneType.float.get.head)
    typed[Double](OneType.double.get.head)
    typed[Boolean](OneType.bool.get.head)
    typed[Date](OneType.date.get.head)
    typed[UUID](OneType.uuid.get.head)
    typed[URI](OneType.uri.get.head)
    typed[String](OneType.enum.get.head)


    // Arity 2

    // Tuple
    typed[(String, Int)](OneType.str.int.tpls.head)
    typed[(Long, Float)](OneType.long.float.tpls.head)
    typed[(Double, Boolean)](OneType.double.bool.tpls.head)
    typed[(Date, UUID)](OneType.date.uuid.tpls.head)
    typed[(URI, String)](OneType.uri.enum.tpls.head)

    // HList
    typed[String :: Int :: HNil](OneType.str.int.hls.head)
    typed[Long :: Float :: HNil](OneType.long.float.hls.head)
    typed[Double :: Boolean :: HNil](OneType.double.bool.hls.head)
    typed[Date :: UUID :: HNil](OneType.date.uuid.hls.head)
    typed[URI :: String :: HNil](OneType.uri.enum.hls.head)


    // Arity 3

    typed[(String, Int, Long)](OneType.str.int.long.tpls.head)
    typed[(Float, Double, Boolean)](OneType.float.double.bool.tpls.head)
    typed[(Date, UUID, URI)](OneType.date.uuid.uri.tpls.head)
    typed[(String, String, Int)](OneType.enum.str.int.tpls.head)

    typed[String :: Int :: Long :: HNil](OneType.str.int.long.hls.head)
    typed[Float :: Double :: Boolean :: HNil](OneType.float.double.bool.hls.head)
    typed[Date :: UUID :: URI :: HNil](OneType.date.uuid.uri.hls.head)
    typed[String :: String :: Int :: HNil](OneType.enum.str.int.hls.head)


    // Arity 4

    typed[(String, Int, Long, Float)](OneType.str.int.long.float.tpls.head)
    typed[(Double, Boolean, Date, UUID)](OneType.double.bool.date.uuid.tpls.head)
    typed[(URI, String, String, Int)](OneType.uri.enum.str.int.tpls.head)

    typed[String :: Int :: Long :: Float :: HNil](OneType.str.int.long.float.hls.head)
    typed[Double :: Boolean :: Date :: UUID :: HNil](OneType.double.bool.date.uuid.hls.head)
    typed[URI :: String :: String :: Int :: HNil](OneType.uri.enum.str.int.hls.head)


    // Arity 5

    typed[(String, Int, Long, Float, Double)](OneType.str.int.long.float.double.tpls.head)
    typed[(Boolean, Date, UUID, URI, String)](OneType.bool.date.uuid.uri.enum.tpls.head)

    typed[String :: Int :: Long :: Float :: Double :: HNil](OneType.str.int.long.float.double.hls.head)
    typed[Boolean :: Date :: UUID :: URI :: String :: HNil](OneType.bool.date.uuid.uri.enum.hls.head)


    // Arity 6

    typed[(String, Int, Long, Float, Double, Boolean)](OneType.str.int.long.float.double.bool.tpls.head)
    typed[(Date, UUID, URI, String, String, Int)](OneType.date.uuid.uri.enum.str.int.tpls.head)

    typed[String :: Int :: Long :: Float :: Double :: Boolean :: HNil](OneType.str.int.long.float.double.bool.hls.head)
    typed[Date :: UUID :: URI :: String :: String :: Int :: HNil](OneType.date.uuid.uri.enum.str.int.hls.head)


    // Arity 7

    typed[(String, Int, Long, Float, Double, Boolean, Date)](OneType.str.int.long.float.double.bool.date.tpls.head)
    typed[(UUID, URI, String, String, Int, Long, Float)](OneType.uuid.uri.enum.str.int.long.float.tpls.head)

    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: HNil](OneType.str.int.long.float.double.bool.date.hls.head)
    typed[UUID :: URI :: String :: String :: Int :: Long :: Float :: HNil](OneType.uuid.uri.enum.str.int.long.float.hls.head)


    // Arity 8

    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID)](OneType.str.int.long.float.double.bool.date.uuid.tpls.head)
    typed[(URI, String, String, Int, Long, Float, Double, Boolean)](OneType.uri.enum.str.int.long.float.double.bool.tpls.head)

    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: HNil](OneType.str.int.long.float.double.bool.date.uuid.hls.head)
    typed[URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: HNil](OneType.uri.enum.str.int.long.float.double.bool.hls.head)


    // Arity 9

    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI)](OneType.str.int.long.float.double.bool.date.uuid.uri.tpls.head)
    typed[(String, String, Int, Long, Float, Double, Boolean, Date, UUID)](OneType.enum.str.int.long.float.double.bool.date.uuid.tpls.head)

    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: HNil](OneType.str.int.long.float.double.bool.date.uuid.uri.hls.head)
    typed[String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: HNil](OneType.enum.str.int.long.float.double.bool.date.uuid.hls.head)


    // Arity 10

    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String)](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.tpls.head)

    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: HNil](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.hls.head)


    // Arity 11

    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String)](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.tpls.head)

    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: HNil](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.hls.head)


    // Arity 12

    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int)](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.tpls.head)

    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: HNil](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.hls.head)


    // Arity 13

    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long)](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.tpls.head)

    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: HNil](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.hls.head)


    // Arity 14

    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long, Float)](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.tpls.head)

    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: HNil](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.hls.head)


    // Arity 15

    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long, Float, Double)](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.tpls.head)

    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: HNil](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.hls.head)


    // Arity 16

    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long, Float, Double, Boolean)](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.tpls.head)

    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: HNil](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.hls.head)


    // Arity 17

    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long, Float, Double, Boolean, Date)](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.tpls.head)

    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: HNil](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.hls.head)


    // Arity 18

    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long, Float, Double, Boolean, Date, UUID)](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.tpls.head)

    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: HNil](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.hls.head)


    // Arity 19

    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long, Float, Double, Boolean, Date, UUID, URI)](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.tpls.head)

    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: HNil](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.hls.head)


    // Arity 20

    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String)](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.enum.tpls.head)

    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: HNil](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.enum.hls.head)


    // Arity 21

    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String)](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.enum.str.tpls.head)

    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: HNil](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.enum.str.hls.head)


    // Arity 22

    typed[(String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int, Long, Float, Double, Boolean, Date, UUID, URI, String, String, Int)](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.tpls.head)

    typed[String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: Long :: Float :: Double :: Boolean :: Date :: UUID :: URI :: String :: String :: Int :: HNil](
      OneType.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.long.float.double.bool.date.uuid.uri.enum.str.int.hls.head)
    ok
  }

  "Infer return types of cardinality-many attributes" >> {

    ok
  }
}