/*
* AUTO-GENERATED Molecule DSL for namespace `EAVT`
*
* To change:
* 1. Edit data model in molecule.core.generic.dataModel/EAVTDataModel
* 2. `sbt clean compile`
* 3. Re-compile project in IDE
*/
package molecule.core.generic.EAVT

import java.net.URI
import java.util.Date
import java.util.UUID
import molecule.core.composition.CompositeInit_0._
import molecule.core.composition.CompositeInit_1._
import molecule.core.composition.CompositeInit_2._
import molecule.core.composition.CompositeInit_3._
import molecule.core.composition.nested._
import molecule.core.composition.Nested_1._
import molecule.core.composition.Nested_2._
import molecule.core.composition.Nested_3._
import molecule.core.composition.Tx._
import molecule.core.dsl.attributes._
import molecule.core.dsl.base._
import molecule.core.expression._
import molecule.core.expression.AttrExpressions.?
import molecule.core.generic.Datom._
import molecule.core.generic.GenericNs
import scala.language.higherKinds

trait EAVT_0_7[o0[_], p0, A, B, C, D, E, F, G] extends EAVT_[p0] with EAVT with NS_0_07[o0, p0, A, B, C, D, E, F, G]

trait EAVT_0_7_L0[o0[_], p0, A, B, C, D, E, F, G] extends EAVT_0_7[o0, p0, A, B, C, D, E, F, G] {
  
  type Stay[Attr[_, _], Prop, Tpe] = Attr[EAVT_0_7_L0[o0, p0          , A, B, C, D, E, F, G     ], Nothing] with EAVT_0_7_L0[o0, p0          , A, B, C, D, E, F, G     ]

  
}


trait EAVT_0_7_L1[o0[_], p0, o1[_], p1, A, B, C, D, E, F, G] extends EAVT_0_7[o0, p0 with o1[p1], A, B, C, D, E, F, G] {
  
  type Stay[Attr[_, _], Prop, Tpe] = Attr[EAVT_0_7_L1[o0, p0, o1, p1          , A, B, C, D, E, F, G     ], Nothing] with EAVT_0_7_L1[o0, p0, o1, p1          , A, B, C, D, E, F, G     ]

  
}


trait EAVT_0_7_L2[o0[_], p0, o1[_], p1, o2[_], p2, A, B, C, D, E, F, G] extends EAVT_0_7[o0, p0 with o1[p1 with o2[p2]], A, B, C, D, E, F, G] {
  
  type Stay[Attr[_, _], Prop, Tpe] = Attr[EAVT_0_7_L2[o0, p0, o1, p1, o2, p2          , A, B, C, D, E, F, G     ], Nothing] with EAVT_0_7_L2[o0, p0, o1, p1, o2, p2          , A, B, C, D, E, F, G     ]

  
}


trait EAVT_0_7_L3[o0[_], p0, o1[_], p1, o2[_], p2, o3[_], p3, A, B, C, D, E, F, G] extends EAVT_0_7[o0, p0 with o1[p1 with o2[p2 with o3[p3]]], A, B, C, D, E, F, G] {
  
  type Stay[Attr[_, _], Prop, Tpe] = Attr[EAVT_0_7_L3[o0, p0, o1, p1, o2, p2, o3, p3          , A, B, C, D, E, F, G     ], Nothing] with EAVT_0_7_L3[o0, p0, o1, p1, o2, p2, o3, p3          , A, B, C, D, E, F, G     ]

  
}


trait EAVT_0_7_L4[o0[_], p0, o1[_], p1, o2[_], p2, o3[_], p3, o4[_], p4, A, B, C, D, E, F, G] extends EAVT_0_7[o0, p0 with o1[p1 with o2[p2 with o3[p3 with o4[p4]]]], A, B, C, D, E, F, G] {
  
  type Stay[Attr[_, _], Prop, Tpe] = Attr[EAVT_0_7_L4[o0, p0, o1, p1, o2, p2, o3, p3, o4, p4          , A, B, C, D, E, F, G     ], Nothing] with EAVT_0_7_L4[o0, p0, o1, p1, o2, p2, o3, p3, o4, p4          , A, B, C, D, E, F, G     ]

  
}


trait EAVT_0_7_L5[o0[_], p0, o1[_], p1, o2[_], p2, o3[_], p3, o4[_], p4, o5[_], p5, A, B, C, D, E, F, G] extends EAVT_0_7[o0, p0 with o1[p1 with o2[p2 with o3[p3 with o4[p4 with o5[p5]]]]], A, B, C, D, E, F, G] {
  
  type Stay[Attr[_, _], Prop, Tpe] = Attr[EAVT_0_7_L5[o0, p0, o1, p1, o2, p2, o3, p3, o4, p4, o5, p5          , A, B, C, D, E, F, G     ], Nothing] with EAVT_0_7_L5[o0, p0, o1, p1, o2, p2, o3, p3, o4, p4, o5, p5          , A, B, C, D, E, F, G     ]

  
}


trait EAVT_0_7_L6[o0[_], p0, o1[_], p1, o2[_], p2, o3[_], p3, o4[_], p4, o5[_], p5, o6[_], p6, A, B, C, D, E, F, G] extends EAVT_0_7[o0, p0 with o1[p1 with o2[p2 with o3[p3 with o4[p4 with o5[p5 with o6[p6]]]]]], A, B, C, D, E, F, G] {
  
  type Stay[Attr[_, _], Prop, Tpe] = Attr[EAVT_0_7_L6[o0, p0, o1, p1, o2, p2, o3, p3, o4, p4, o5, p5, o6, p6          , A, B, C, D, E, F, G     ], Nothing] with EAVT_0_7_L6[o0, p0, o1, p1, o2, p2, o3, p3, o4, p4, o5, p5, o6, p6          , A, B, C, D, E, F, G     ]

  
}


trait EAVT_0_7_L7[o0[_], p0, o1[_], p1, o2[_], p2, o3[_], p3, o4[_], p4, o5[_], p5, o6[_], p6, o7[_], p7, A, B, C, D, E, F, G] extends EAVT_0_7[o0, p0 with o1[p1 with o2[p2 with o3[p3 with o4[p4 with o5[p5 with o6[p6 with o7[p7]]]]]]], A, B, C, D, E, F, G] {
  
  type Stay[Attr[_, _], Prop, Tpe] = Attr[EAVT_0_7_L7[o0, p0, o1, p1, o2, p2, o3, p3, o4, p4, o5, p5, o6, p6, o7, p7          , A, B, C, D, E, F, G     ], Nothing] with EAVT_0_7_L7[o0, p0, o1, p1, o2, p2, o3, p3, o4, p4, o5, p5, o6, p6, o7, p7          , A, B, C, D, E, F, G     ]

  
}

     