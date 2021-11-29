package molecule.core.util

import java.util.{concurrent => juc}
import java.{lang => jl, util => ju}
import scala.collection._
import scala.language.implicitConversions


trait JavaConversions {

  def asJavaIterator[A](i: Iterator[A]): ju.Iterator[A] = JavaConverters.asJavaIterator(i)
  def asJavaIterable[A](i: Iterable[A]): jl.Iterable[A] = JavaConverters.asJavaIterable(i)
  def bufferAsJavaList[A](b: mutable.Buffer[A]): ju.List[A] = JavaConverters.bufferAsJavaList(b)
  def mutableSeqAsJavaList[A](s: mutable.Seq[A]): ju.List[A] = JavaConverters.mutableSeqAsJavaList(s)
  def seqAsJavaList[A](s: Seq[A]): ju.List[A] = JavaConverters.seqAsJavaList(s)
  def mutableSetAsJavaSet[A](s: mutable.Set[A]): ju.Set[A] = JavaConverters.mutableSetAsJavaSet(s)
  def setAsJavaSet[A](s: Set[A]): ju.Set[A] = JavaConverters.setAsJavaSet(s)
  def mutableMapAsJavaMap[K, V](m: mutable.Map[K, V]): ju.Map[K, V] = JavaConverters.mutableMapAsJavaMap(m)
  def mapAsJavaMap[K, V](m: Map[K, V]): ju.Map[K, V] = JavaConverters.mapAsJavaMap(m)
  def mapAsJavaConcurrentMap[K, V](m: concurrent.Map[K, V]): juc.ConcurrentMap[K, V] = JavaConverters.mapAsJavaConcurrentMap(m)
  def asScalaIterator[A](i: ju.Iterator[A]): Iterator[A] = JavaConverters.asScalaIterator(i)
  def enumerationAsScalaIterator[A](i: ju.Enumeration[A]): Iterator[A] = JavaConverters.enumerationAsScalaIterator(i)
  def iterableAsScalaIterable[A](i: jl.Iterable[A]): Iterable[A] = JavaConverters.iterableAsScalaIterable(i)
  def collectionAsScalaIterable[A](i: ju.Collection[A]): Iterable[A] = JavaConverters.collectionAsScalaIterable(i)
  def asScalaBuffer[A](l: ju.List[A]): mutable.Buffer[A] = JavaConverters.asScalaBuffer(l)
  def asScalaSet[A](s: ju.Set[A]): mutable.Set[A] = JavaConverters.asScalaSet(s)
  def mapAsScalaMap[A, B](m: ju.Map[A, B]): mutable.Map[A, B] = JavaConverters.mapAsScalaMap(m)
  def mapAsScalaConcurrentMap[A, B](m: juc.ConcurrentMap[A, B]): concurrent.Map[A, B] = JavaConverters.mapAsScalaConcurrentMap(m)
  def dictionaryAsScalaMap[A, B](p: ju.Dictionary[A, B]): mutable.Map[A, B] = JavaConverters.dictionaryAsScalaMap(p)
  def propertiesAsScalaMap(p: ju.Properties): mutable.Map[String, String] = JavaConverters.propertiesAsScalaMap(p)

  // Deprecated implicit conversions for code that directly imports them

  /** Adds an `asJava` method that implicitly converts a Scala `Iterator` to a Java `Iterator`. */
  implicit def asJavaIteratorConverter[A](i: Iterator[A]): AsJava[ju.Iterator[A]] =
    new AsJava(asJavaIterator(i))

  /** Adds an `asJavaEnumeration` method that implicitly converts a Scala `Iterator` to a Java `Enumeration`. */
  implicit def asJavaEnumerationConverter[A](i: Iterator[A]): AsJavaEnumeration[A] =
    new AsJavaEnumeration(i)

  /** Adds an `asJava` method that implicitly converts a Scala `Iterable` to a Java `Iterable`. */
  implicit def asJavaIterableConverter[A](i: Iterable[A]): AsJava[jl.Iterable[A]] =
    new AsJava(asJavaIterable(i))

  /** Adds an `asJavaCollection` method that implicitly converts a Scala `Iterable` to an immutable Java `Collection`. */
  implicit def asJavaCollectionConverter[A](i: Iterable[A]): AsJavaCollection[A] =
    new AsJavaCollection(i)

  /** Adds an `asJava` method that implicitly converts a Scala mutable `Buffer` to a Java `List`. */
  implicit def bufferAsJavaListConverter[A](b: mutable.Buffer[A]): AsJava[ju.List[A]] =
    new AsJava(bufferAsJavaList(b))

  /** Adds an `asJava` method that implicitly converts a Scala mutable `Seq` to a Java `List`. */
  implicit def mutableSeqAsJavaListConverter[A](b: mutable.Seq[A]): AsJava[ju.List[A]] =
    new AsJava(mutableSeqAsJavaList(b))

  /** Adds an `asJava` method that implicitly converts a Scala `Seq` to a Java `List`. */
  implicit def seqAsJavaListConverter[A](b: Seq[A]): AsJava[ju.List[A]] =
    new AsJava(seqAsJavaList(b))

  /** Adds an `asJava` method that implicitly converts a Scala mutable `Set` to a Java `Set`. */
  implicit def mutableSetAsJavaSetConverter[A](s: mutable.Set[A]): AsJava[ju.Set[A]] =
    new AsJava(mutableSetAsJavaSet(s))

  /** Adds an `asJava` method that implicitly converts a Scala `Set` to a Java `Set`. */
  implicit def setAsJavaSetConverter[A](s: Set[A]): AsJava[ju.Set[A]] =
    new AsJava(setAsJavaSet(s))

  /** Adds an `asJava` method that implicitly converts a Scala mutable `Map` to a Java `Map`. */
  implicit def mutableMapAsJavaMapConverter[K, V](m: mutable.Map[K, V]): AsJava[ju.Map[K, V]] =
    new AsJava(mutableMapAsJavaMap(m))

  /** Adds an `asJavaDictionary` method that implicitly converts a Scala mutable `Map` to a Java `Dictionary`. */
  implicit def asJavaDictionaryConverter[K, V](m: mutable.Map[K, V]): AsJavaDictionary[K, V] =
    new AsJavaDictionary(m)

  /** Adds an `asJava` method that implicitly converts a Scala `Map` to a Java `Map`. */
  implicit def mapAsJavaMapConverter[K, V](m: Map[K, V]): AsJava[ju.Map[K, V]] =
    new AsJava(mapAsJavaMap(m))

  /** Adds an `asJava` method that implicitly converts a Scala mutable `concurrent.Map` to a Java `ConcurrentMap`. */
  implicit def mapAsJavaConcurrentMapConverter[K, V](m: concurrent.Map[K, V]): AsJava[juc.ConcurrentMap[K, V]] =
    new AsJava(mapAsJavaConcurrentMap(m))


  /** Adds an `asScala` method that implicitly converts a Java `Iterator` to a Scala `Iterator`. */
  implicit def asScalaIteratorConverter[A](i: ju.Iterator[A]): AsScala[Iterator[A]] =
    new AsScala(asScalaIterator(i))

  /** Adds an `asScala` method that implicitly converts a Java `Enumeration` to a Scala `Iterator`. */
  implicit def enumerationAsScalaIteratorConverter[A](i: ju.Enumeration[A]): AsScala[Iterator[A]] =
    new AsScala(enumerationAsScalaIterator(i))

  /** Adds an `asScala` method that implicitly converts a Java `Iterable` to a Scala `Iterable`. */
  implicit def iterableAsScalaIterableConverter[A](i: jl.Iterable[A]): AsScala[Iterable[A]] =
    new AsScala(iterableAsScalaIterable(i))

  /** Adds an `asScala` method that implicitly converts a Java `Collection` to an Scala `Iterable`. */
  implicit def collectionAsScalaIterableConverter[A](i: ju.Collection[A]): AsScala[Iterable[A]] =
    new AsScala(collectionAsScalaIterable(i))

  /** Adds an `asScala` method that implicitly converts a Java `List` to a Scala mutable `Buffer`. */
  implicit def asScalaBufferConverter[A](l: ju.List[A]): AsScala[mutable.Buffer[A]] =
    new AsScala(asScalaBuffer(l))

  /** Adds an `asScala` method that implicitly converts a Java `Set` to a Scala mutable `Set`. */
  implicit def asScalaSetConverter[A](s: ju.Set[A]): AsScala[mutable.Set[A]] =
    new AsScala(asScalaSet(s))

  /** Adds an `asScala` method that implicitly converts a Java `Map` to a Scala mutable `Map`. */
  implicit def mapAsScalaMapConverter[K, V](m: ju.Map[K, V]): AsScala[mutable.Map[K, V]] =
    new AsScala(mapAsScalaMap(m))

  /** Adds an `asScala` method that implicitly converts a Java `ConcurrentMap` to a Scala mutable `concurrent.Map`. */
  implicit def mapAsScalaConcurrentMapConverter[K, V](m: juc.ConcurrentMap[K, V]): AsScala[concurrent.Map[K, V]] =
    new AsScala(mapAsScalaConcurrentMap(m))

  /** Adds an `asScala` method that implicitly converts a Java `Dictionary` to a Scala mutable `Map`. */
  implicit def dictionaryAsScalaMapConverter[K, V](p: ju.Dictionary[K, V]): AsScala[mutable.Map[K, V]] =
    new AsScala(dictionaryAsScalaMap(p))

  /** Adds an `asScala` method that implicitly converts a Java `Properties` to a Scala mutable `Map[String, String]`. */
  implicit def propertiesAsScalaMapConverter(p: ju.Properties): AsScala[mutable.Map[String, String]] =
    new AsScala(propertiesAsScalaMap(p))


  /** Generic class containing the `asJava` converter method */
  class AsJava[A](op: => A) {
    /** Converts a Scala collection to the corresponding Java collection */
    def asJava: A = op
  }

  /** Generic class containing the `asScala` converter method */
  class AsScala[A](op: => A) {
    /** Converts a Java collection to the corresponding Scala collection */
    def asScala: A = op
  }

  /** Generic class containing the `asJavaCollection` converter method */
  class AsJavaCollection[A](i: Iterable[A]) {
    /** Converts a Scala `Iterable` to a Java `Collection` */
    def asJavaCollection: ju.Collection[A] = JavaConverters.asJavaCollection(i)
  }

  /** Generic class containing the `asJavaEnumeration` converter method */
  class AsJavaEnumeration[A](i: Iterator[A]) {
    /** Converts a Scala `Iterator` to a Java `Enumeration` */
    def asJavaEnumeration: ju.Enumeration[A] = JavaConverters.asJavaEnumeration(i)
  }

  /** Generic class containing the `asJavaDictionary` converter method */
  class AsJavaDictionary[K, V](m: mutable.Map[K, V]) {
    /** Converts a Scala `Map` to a Java `Dictionary` */
    def asJavaDictionary: ju.Dictionary[K, V] = JavaConverters.asJavaDictionary(m)
  }
}
