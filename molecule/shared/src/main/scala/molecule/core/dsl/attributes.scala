package molecule.core.dsl

import java.net.URI
import java.util.{Date, UUID}
import molecule.core.expression.AttrExpressions._

/** Boilerplate interfaces for custom DSL generated from schema definition file.
  * <br><br>
  * Encodes attribute cardinality, type and mode (mandatory, tacit, optional).
  * */
object attributes {

  trait SelfJoin

  trait Ref[This, Next]
  trait OneRef[This, Next] extends Ref[This, Next]
  trait ManyRef[This, Next] extends Ref[This, Next]

  trait Attr

  trait SortMarkers[Ns] {
    // Ascending
    def a1: Ns = ???
    def a2: Ns = ???
    def a3: Ns = ???
    def a4: Ns = ???
    def a5: Ns = ???

    // Descending
    def d1: Ns = ???
    def d2: Ns = ???
    def d3: Ns = ???
    def d4: Ns = ???
    def d5: Ns = ???
  }

  trait RefAttr[Ns] extends Attr

  trait OneRefAttr[Ns, In] extends RefAttr[Ns] with OneExpr[Ns, In, Long] with SortMarkers[Ns]
  trait ManyRefAttr[Ns, In] extends RefAttr[Ns] with ManyExpr[Ns, In, Long]

  sealed trait ValueAttr[Ns, In, TT, T] extends Attr


  // Cardinality one attributes

  trait One[Ns, In, T] extends ValueAttr[Ns, In, Nothing, T] with OneExpr[Ns, In, T] with SortMarkers[Ns]

  trait OneString    [Ns, In] extends One[Ns, In, String    ]
  trait OneInt       [Ns, In] extends One[Ns, In, Int       ]
  trait OneLong      [Ns, In] extends One[Ns, In, Long      ]
  trait OneDouble    [Ns, In] extends One[Ns, In, Double    ]
  trait OneBoolean   [Ns, In] extends One[Ns, In, Boolean   ]
  trait OneDate      [Ns, In] extends One[Ns, In, Date      ]
  trait OneUUID      [Ns, In] extends One[Ns, In, UUID      ]
  trait OneURI       [Ns, In] extends One[Ns, In, URI       ]
  trait OneBigInt    [Ns, In] extends One[Ns, In, BigInt    ]
  trait OneBigDecimal[Ns, In] extends One[Ns, In, BigDecimal]
  trait OneAny       [Ns, In] extends One[Ns, In, Any       ]


  // Cardinality many attributes

  trait Many[Ns, In, TT, T] extends ValueAttr[Ns, In, TT, T] with ManyExpr[Ns, In, T]

  trait ManyString    [Ns, In] extends Many[Ns, In, Set[String    ], String    ]
  trait ManyInt       [Ns, In] extends Many[Ns, In, Set[Int       ], Int       ]
  trait ManyLong      [Ns, In] extends Many[Ns, In, Set[Long      ], Long      ]
  trait ManyDouble    [Ns, In] extends Many[Ns, In, Set[Double    ], Double    ]
  trait ManyBoolean   [Ns, In] extends Many[Ns, In, Set[Boolean   ], Boolean   ]
  trait ManyDate      [Ns, In] extends Many[Ns, In, Set[Date      ], Date      ]
  trait ManyUUID      [Ns, In] extends Many[Ns, In, Set[UUID      ], UUID      ]
  trait ManyURI       [Ns, In] extends Many[Ns, In, Set[URI       ], URI       ]
  trait ManyBigInt    [Ns, In] extends Many[Ns, In, Set[BigInt    ], BigInt    ]
  trait ManyBigDecimal[Ns, In] extends Many[Ns, In, Set[BigDecimal], BigDecimal]


  // Map attributes

  trait MapAttrK

  trait MapAttr[Ns, In, TT, T] extends ValueAttr[Ns, In, TT, T] with MapAttrExpr[Ns, In, T]

  trait MapString    [Ns, In] extends MapAttr[Ns, In, Map[String, String    ], String    ]
  trait MapInt       [Ns, In] extends MapAttr[Ns, In, Map[String, Int       ], Int       ]
  trait MapLong      [Ns, In] extends MapAttr[Ns, In, Map[String, Long      ], Long      ]
  trait MapDouble    [Ns, In] extends MapAttr[Ns, In, Map[String, Double    ], Double    ]
  trait MapBoolean   [Ns, In] extends MapAttr[Ns, In, Map[String, Boolean   ], Boolean   ]
  trait MapDate      [Ns, In] extends MapAttr[Ns, In, Map[String, Date      ], Date      ]
  trait MapUUID      [Ns, In] extends MapAttr[Ns, In, Map[String, UUID      ], UUID      ]
  trait MapURI       [Ns, In] extends MapAttr[Ns, In, Map[String, URI       ], URI       ]
  trait MapBigInt    [Ns, In] extends MapAttr[Ns, In, Map[String, BigInt    ], BigInt    ]
  trait MapBigDecimal[Ns, In] extends MapAttr[Ns, In, Map[String, BigDecimal], BigDecimal]


  // Enums

  object EnumValue
  trait Enum

  trait OneEnum  [Ns, In] extends One [Ns, In, String]              with Enum with SortMarkers[Ns]
  trait ManyEnums[Ns, In] extends Many[Ns, In, Set[String], String] with Enum

  trait Enum$[Ns, T] extends Attr with Enum with OptionalExpr[Ns, T]
  trait OneEnum$   [Ns] extends Enum$[Ns, String] with SortMarkers[Ns]
  trait ManyEnums$ [Ns] extends Enum$[Ns, Set[String]]


  // Optional attributes

  trait RefAttr$[Ns] extends Attr
  trait OneRefAttr$ [Ns] extends RefAttr$[Ns] with OptionalExpr[Ns, Long] with SortMarkers[Ns]
  trait ManyRefAttr$[Ns] extends RefAttr$[Ns] with OptionalExpr[Ns, Set[Long]]

  trait ValueAttr$[T] extends Attr

  trait OneValueAttr$[Ns, T] extends ValueAttr$[T] with OptionalExpr[Ns, T] with SortMarkers[Ns]

  trait OneString$    [Ns] extends OneValueAttr$[Ns, String    ]
  trait OneInt$       [Ns] extends OneValueAttr$[Ns, Int       ]
  trait OneLong$      [Ns] extends OneValueAttr$[Ns, Long      ]
  trait OneDouble$    [Ns] extends OneValueAttr$[Ns, Double    ]
  trait OneBoolean$   [Ns] extends OneValueAttr$[Ns, Boolean   ]
  trait OneDate$      [Ns] extends OneValueAttr$[Ns, Date      ]
  trait OneUUID$      [Ns] extends OneValueAttr$[Ns, UUID      ]
  trait OneURI$       [Ns] extends OneValueAttr$[Ns, URI       ]
  trait OneBigInt$    [Ns] extends OneValueAttr$[Ns, BigInt    ]
  trait OneBigDecimal$[Ns] extends OneValueAttr$[Ns, BigDecimal]
  trait OneAny$       [Ns] extends OneValueAttr$[Ns, Any       ]


  trait ManyValueAttr$[Ns, TT, T] extends ValueAttr$[T] with OptionalExpr[Ns, TT]

  trait ManyString$    [Ns] extends ManyValueAttr$[Ns, Set[String    ], String    ]
  trait ManyInt$       [Ns] extends ManyValueAttr$[Ns, Set[Int       ], Int       ]
  trait ManyLong$      [Ns] extends ManyValueAttr$[Ns, Set[Long      ], Long      ]
  trait ManyDouble$    [Ns] extends ManyValueAttr$[Ns, Set[Double    ], Double    ]
  trait ManyBoolean$   [Ns] extends ManyValueAttr$[Ns, Set[Boolean   ], Boolean   ]
  trait ManyDate$      [Ns] extends ManyValueAttr$[Ns, Set[Date      ], Date      ]
  trait ManyUUID$      [Ns] extends ManyValueAttr$[Ns, Set[UUID      ], UUID      ]
  trait ManyURI$       [Ns] extends ManyValueAttr$[Ns, Set[URI       ], URI       ]
  trait ManyBigInt$    [Ns] extends ManyValueAttr$[Ns, Set[BigInt    ], BigInt    ]
  trait ManyBigDecimal$[Ns] extends ManyValueAttr$[Ns, Set[BigDecimal], BigDecimal]


  trait MapAttr$[Ns, TT, T] extends Attr with OptionalExpr[Ns, TT]

  trait MapString$    [Ns] extends MapAttr$[Ns, Map[String, String    ], String    ]
  trait MapInt$       [Ns] extends MapAttr$[Ns, Map[String, Int       ], Int       ]
  trait MapLong$      [Ns] extends MapAttr$[Ns, Map[String, Long      ], Long      ]
  trait MapDouble$    [Ns] extends MapAttr$[Ns, Map[String, Double    ], Double    ]
  trait MapBoolean$   [Ns] extends MapAttr$[Ns, Map[String, Boolean   ], Boolean   ]
  trait MapDate$      [Ns] extends MapAttr$[Ns, Map[String, Date      ], Date      ]
  trait MapUUID$      [Ns] extends MapAttr$[Ns, Map[String, UUID      ], UUID      ]
  trait MapURI$       [Ns] extends MapAttr$[Ns, Map[String, URI       ], URI       ]
  trait MapBigInt$    [Ns] extends MapAttr$[Ns, Map[String, BigInt    ], BigInt    ]
  trait MapBigDecimal$[Ns] extends MapAttr$[Ns, Map[String, BigDecimal], BigDecimal]



  // Attribute options

  case class Doc(msg: String)
  trait UniqueValue
  trait UniqueIdentity
  trait Index
  trait Fulltext[Ns, In] extends FulltextExpr[Ns, In]
  trait IsComponent
  trait NoHistory


  // Bidirectional markers

  trait Bidirectional_

  // Bidirectional ref to defining ns (self-reference)
  trait BiSelfRef_ extends Bidirectional_
  trait BiSelfRefAttr_ extends Bidirectional_

  // Bidirectional ref to other ns
  trait BiOtherRef_[revRefAttr] extends Bidirectional_
  trait BiOtherRefAttr_[revRefAttr] extends Bidirectional_

  // Property edge namespace
  trait BiEdge_ extends Bidirectional_

  // Ref from defining ns to edge
  trait BiEdgeRef_[revRefAttr] extends Bidirectional_
  trait BiEdgeRefAttr_[revRefAttr] extends Bidirectional_

  // Edge properties
  trait BiEdgePropAttr_ extends Bidirectional_
  trait BiEdgePropRef_ extends Bidirectional_
  trait BiEdgePropRefAttr_ extends Bidirectional_

  // Ref from edge to target ns
  trait BiTargetRef_[biRefAttr] extends Bidirectional_
  trait BiTargetRefAttr_[biRefAttr] extends Bidirectional_
}
