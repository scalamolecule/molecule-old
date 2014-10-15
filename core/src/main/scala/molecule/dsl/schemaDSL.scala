package molecule.dsl
import java.net.URI
import java.util.{UUID, Date}
import molecule._
import molecule.ast.model._


object schemaDSL {

  // todo?
  trait Partition

  trait Tree
  trait HyperEdge

  trait NS

  trait Ref[Ns1, Ns2]
  trait OneRef[Ns1, Ns2] extends Ref[Ns1, Ns2]
  trait ManyRef[Ns1, Ns2] extends Ref[Ns1, Ns2]
//  trait BackRef[BackRefNS, ThisNs] extends Ref[BackRefNS, ThisNs]
  trait ChildRef[Ns1] extends Ref[Ns1, Ns1]
  trait HyperRef[Ns1] extends Ref[Ns1, Ns1]

  trait Attr

  trait RefAttr[Ns1, T] extends Attr
  trait OneRefAttr[Ns, In] extends RefAttr[Ns,  Long] {
    def apply(value: Long): Ns = ???
  }
  trait ManyRefAttr[Ns, In] extends RefAttr[Ns,  Long] {
    def apply(value: Long*): Ns = ???
    def add(value: Long): Ns = ???
    def remove(values: Long*): Ns = ???
  }

  sealed trait ValueAttr[Ns, In, T] extends Attr {
    def apply(expr: Exp1[T]) : Ns = ???
    def eq(value: T) : Ns = ???

    def < (value: T) : Ns = ???
    def > (value: T) : Ns = ???
    def <= (value: T) : Ns = ???
    def >= (value: T) : Ns = ???

    // Input
    def < (in: ?) : In = ???
    def > (in: ?) : In = ???
    def <= (in: ?) : In = ???
    def >= (in: ?) : In = ???

    def apply(in: ?) : In = ???

    // Optional value (will return Option[T])
    //    def apply(m: maybe): Ns = ???
  }

  // One-cardinality
  trait One[Ns, In, T] extends ValueAttr[Ns, In, T] {
    // Request for no value!
    def apply(): Ns = ???
    def apply(values: Seq[T]) : Ns = ???
  }
  trait OneString [Ns, In] extends One[Ns, In, String ]  {
    def length: Ns = ???
  }
  trait OneInt    [Ns, In] extends One[Ns, In, Int    ]
  trait OneLong   [Ns, In] extends One[Ns, In, Long   ]
  trait OneFloat  [Ns, In] extends One[Ns, In, Float  ]
  trait OneDouble [Ns, In] extends One[Ns, In, Double ]
  trait OneDate   [Ns, In] extends One[Ns, In, Date   ]
  trait OneBoolean[Ns, In] extends One[Ns, In, Boolean]
  trait OneUUID   [Ns, In] extends One[Ns, In, UUID   ]
  trait OneURI    [Ns, In] extends One[Ns, In, URI    ]
  trait OneAny    [Ns, In] extends One[Ns, In, Any    ]

  // Many-cardinality
  trait Many[Ns, In, S, T] extends ValueAttr[Ns, In, T] {
    def apply(value: T*): Ns = ???
//    def apply(one: T, more: T*): Ns = ???
//    def apply(): Ns = ???

//    def apply(values: Seq[T]): Ns = ???
    def apply(oldNew: (T, T), oldNewMore: (T, T)*): Ns = ???
    //    def apply(h: Seq[(T, T)]): Ns = ???
    def add(value: T): Ns = ???
    def remove(values: T*): Ns = ???
  }
  trait ManyString[Ns, In] extends Many[Ns, In, Set[String], String]
  trait ManyInt   [Ns, In] extends Many[Ns, In, Set[Int]   , Int   ]
  trait ManyLong  [Ns, In] extends Many[Ns, In, Set[Long]  , Long  ]
  trait ManyFloat [Ns, In] extends Many[Ns, In, Set[Float] , Float ]
  trait ManyDouble[Ns, In] extends Many[Ns, In, Set[Double], Double]
  trait ManyDate  [Ns, In] extends Many[Ns, In, Set[Date]  , Date  ]
  trait ManyUUID  [Ns, In] extends Many[Ns, In, Set[UUID]  , UUID  ]
  trait ManyURI   [Ns, In] extends Many[Ns, In, Set[URI]   , URI   ]

  // Enums
  trait Enum
  trait OneEnum  [Ns, In] extends One [Ns, In, String]         with Enum
  trait ManyEnums[Ns, In] extends Many[Ns, In, String, String] with Enum
  object EnumValue

  // Attribute options
  case class Doc(msg: String)
  trait UniqueValue
  trait UniqueIdentity
  trait Indexed
  trait FulltextSearch[Ns, In] {
    def contains(that: String): Ns = ???
    def contains(in: ?) : In = ???
  }
  trait IsComponent
  trait NoHistory
}