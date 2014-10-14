//package molecule.schemas
//
//import java.util.Date
//import molecule.dsl.schemaDSL._
//import molecule.out._
//
//object Entity extends Entity_0
//
//trait Entity {
////  class txInstant[NS] extends OneLong[NS, Nothing]
//}
//
//trait Entity_0 extends Entity with Molecule_0[Entity_0, Entity_1] {
////  val txInstant: txInstant[Entity_1[Date], Nothing] with Entity_1[Date] = ???
//}
//
//trait Entity_1[T1] extends Entity with Molecule_1[Entity_1, Entity_2, T1]{
////  val txInstant: txInstant[Entity_2[T1, Date], Nothing] with Entity_2[T1, Date]
//}
//trait Entity_2[T1, T2] extends Entity with Molecule_2[Entity_2, Nothing, T1, T2]
