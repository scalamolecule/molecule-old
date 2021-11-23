package molecule.core.marshalling

import java.net.URI
import java.nio.ByteBuffer
import java.util.{Date, UUID}
import boopickle.Default._
import chameleon._
import molecule.core.api.exception.EntityException
import molecule.core.exceptions._
import molecule.core.util.Helpers
import molecule.datomic.base.facade.exception.DatomicFacadeException
import scala.util.{Failure, Success, Try}


trait BooPicklers extends Helpers {

  // Common picklers
  implicit val datePickler = transformPickler((t: Long) => new java.util.Date(t))(_.getTime)
  implicit val uriPickler  = transformPickler((t: String) => new URI(t))(_.toString)

  implicit val anyPickler = transformPickler {
    (t: String) =>
      val v = t.drop(10)
      t.take(10) match {
        case "String    " => v
        case "Int       " => v.toInt
        case "Long      " => v.toLong
        case "Double    " => v.toDouble
        case "Boolean   " => v.toBoolean
        case "Date      " => str2date(v)
        case "URI       " => new java.net.URI(v)
        case "UUID      " => java.util.UUID.fromString(v)
        case "BigInt    " => BigInt(v)
        case "BigDecimal" => BigDecimal(v)
        case other        => throw MoleculeException(
          s"Unexpected Datom anyPickler type prefix '$other' for value `$v`")
      }
  } {
    case v: String     => "String    " + v
    case v: Int        => "Int       " + v.toString
    case v: Long       => "Long      " + v.toString
    case v: Double     => "Double    " + v.toString
    case v: Boolean    => "Boolean   " + v.toString
    case v: Date       => "Date      " + date2str(v)
    case v: URI        => "URI       " + v.toString
    case v: UUID       => "UUID      " + v.toString
    case v: BigInt     => "BigInt    " + v.toString
    case v: BigDecimal => "BigDecimal" + v.toString
    case v             => throw MoleculeException(
      s"Unexpected Datom anyPickler value `$v` of type " + v.getClass)
  }

  implicit val connProxyPickler = compositePickler[ConnProxy].
    addConcreteType[DatomicPeerProxy].
    addConcreteType[DatomicDevLocalProxy].
    addConcreteType[DatomicPeerServerProxy]


  implicit val exPickler = exceptionPickler
  exPickler
    .addConcreteType[MoleculeException]
    .addConcreteType[MoleculeCompileException]
    //    .addConcreteType[QueryException] // Can't add this since we can't unpickle `Element`
    .addConcreteType[EntityException]
    .addConcreteType[DatomicFacadeException]

  // Copying this method so that we can avoid `import chameleon.ext.boopickle._`
  // in all custom SlothControllers and WebClients
  implicit def boopickleSerializerDeserializer[T: Pickler]: SerializerDeserializer[T, ByteBuffer] = {
    new Serializer[T, ByteBuffer] with Deserializer[T, ByteBuffer] {
      override def serialize(arg: T): ByteBuffer = Pickle.intoBytes(arg)
      override def deserialize(arg: ByteBuffer): Either[Throwable, T] = {
        Try(Unpickle.apply[T].fromBytes(arg)) match {
          case Success(data) => Right(data)
          case Failure(exc)  => Left(exc)
        }
      }
    }
  }
}
