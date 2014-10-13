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

  trait NS {
    // Common attribute classes
    abstract class e         [Ns, In] extends OneLong    [Ns, In]
    abstract class a         [Ns, In] extends OneString  [Ns, In]
    //    abstract class v         [Ns, In] extends OneAny     [Ns, In]
    //    abstract class ns        [Ns, In] extends OneString  [Ns, In]
    //    abstract class txInstant [Ns, In] extends OneDate    [Ns, In]
    //    abstract class txT       [Ns, In] extends OneLong    [Ns, In]
    //    abstract class txAdded   [Ns, In] extends OneBoolean [Ns, In]
  }

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
//  trait OneString [Ns, In] extends One[Ns, In, String ] with Ordered[Ns, In, String] {
  trait OneString [Ns, In] extends One[Ns, In, String ]  {
    def length: Ns = ???
  }
  trait OneInt    [Ns, In] extends One[Ns, In, Int    ] //with Number [Ns, In, Int   ]
  trait OneLong   [Ns, In] extends One[Ns, In, Long   ] //with Number [Ns, In, Long  ]
  trait OneFloat  [Ns, In] extends One[Ns, In, Float  ] //with Number [Ns, In, Float ]
  trait OneDouble [Ns, In] extends One[Ns, In, Double ] //with Number [Ns, In, Double]
  trait OneDate   [Ns, In] extends One[Ns, In, Date   ] //with Ordered[Ns, In, Date  ]
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
  trait ManyString[Ns, In] extends Many[Ns, In, Set[String], String] //with Ordered[Ns, In, String]
  trait ManyInt   [Ns, In] extends Many[Ns, In, Set[Int]   , Int   ] //with Number [Ns, In, Int   ]
  trait ManyLong  [Ns, In] extends Many[Ns, In, Set[Long]  , Long  ] //with Number [Ns, In, Long  ]
  trait ManyFloat [Ns, In] extends Many[Ns, In, Set[Float] , Float ] //with Number [Ns, In, Float ]
  trait ManyDouble[Ns, In] extends Many[Ns, In, Set[Double], Double] //with Number [Ns, In, Double]
  trait ManyDate  [Ns, In] extends Many[Ns, In, Set[Date]  , Date  ] //with Ordered[Ns, In, Date  ]
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


  //  Manipulation traits

//  abstract class Insert(val elements: Seq[Element]) extends DatomicFacade {
//    def save(implicit conn: Connection): Seq[Long] = upsert(conn, Model(elements)).ids
//  }
//  abstract class Update(val elements: Seq[Element], val ids: Seq[Long]) extends DatomicFacade {
//    def save(implicit conn: Connection): Seq[Long] = upsert(conn, Model(elements), Seq(), ids).ids
//  }
//  abstract class Retract(elements: Seq[Element]) extends DatomicFacade
//  abstract class Entity(elements: Seq[Element]) extends DatomicFacade

}