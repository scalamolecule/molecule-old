package molecule.core.marshalling

import java.net.URI
import java.nio.ByteBuffer
import boopickle.Default._
import chameleon._
import molecule.core.api.exception.EntityException
import molecule.core.exceptions._
import molecule.datomic.base.facade.exception.DatomicFacadeException
import scala.util.{Failure, Success, Try}


trait Serializations {

  // Common picklers
  implicit val datePickler = transformPickler((t: Long) => new java.util.Date(t))(_.getTime)
  implicit val uriPickler  = transformPickler((t: String) => new URI(t))(_.toString)

  implicit val exPickler = exceptionPickler

  exPickler
    .addConcreteType[MoleculeException]
    .addConcreteType[MoleculeCompileException]
  //    .addConcreteType[QueryException] // Can't add this since we can't unpickle `Element`s
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
