///*
// * AUTO-GENERATED CODE - DO NOT CHANGE!
// *
// * Manual changes to this file will likely break molecules!
// * Instead, change the molecule definition files and recompile your project with `sbt compile`.
// */
//package molecule.examples.dayOfDatomic.dsl.socialNews
//import molecule._
//import molecule.util.dsl.schemaDSL._
//import molecule.examples.dayOfDatomic.dsl.productsOrder.{LineItem_3, LineItem_2, LineItem, LineItem_1}
//import molecule.in._
//import molecule.out._
//
//
//trait Story
//
//object Story extends Story with Story_0 {
//  class title [Ns1, Ns2](ns1: Ns1, ns2: Ns2) extends OneString (ns1, ns2) with FulltextSearch with Indexed
//  class url   [Ns1, Ns2](ns1: Ns1, ns2: Ns2) extends OneString (ns1, ns2) with UniqueIdentity
////  class comments  [Ns1, Ns2](ns1: Ns1, ns2: Ns2) extends OneString (ns1, ns2) with UniqueIdentity
//}
//
//trait Story_0 extends Story with Molecule_0 {
//  import Story._
//  def apply(e: Long) = this
//  lazy val e   = new e   (this, new Story_1[Long]   {}) with Story_1[Long]   {}
//  lazy val title = new title (this, new Story_1[String] {}) with Story_1[String] {}
//  lazy val url   = new url   (this, new Story_1[String] {}) with Story_1[String] {}
//
//  lazy val comments   = new e   (this, new Story_1[Long]   {}) with Story_1[Long]   {}
//  def Comments                                              = new ManyRef[Story, Comment] with Comment_0
////  def Comment                                               = new ManyRef[Story, Comment] with Comment_0
////
////  def comments            (comments: Comment_0)             = new ManyRef[Story, Comment] with Story_0
////  def comments[T1]        (comments: Comment_1[T1])         = new ManyRef[Story, Comment] with Story_1[Seq[T1]]
////  def comments[T1, T2]    (comments: Comment_2[T1, T2])     = new ManyRef[Story, Comment] with Story_1[Seq[(T1, T2)]]
////  def comments[T1, T2, T3](comments: Comment_3[T1, T2, T3]) = new ManyRef[Story, Comment] with Story_1[Seq[(T1, T2, T3)]]
//
////  def apply(s: Symbol) = this
////  def ~(a: Any) = this
////  def <--(a: Any) = this
//
////  def apply(m: Molecule_1[_]) = this
//}
//
//trait Story_1[A] extends Story with Molecule_1[A] {
//  import Story._
//  lazy val e   = new e   (this, new Story_2[A, Long]   {}) with Story_2[A, Long]   {}
//  lazy val title = new title (this, new Story_2[A, String] {}) with Story_2[A, String] {}
//  lazy val url   = new url   (this, new Story_2[A, String] {}) with Story_2[A, String] {}
//  def apply(in: ?.type)    = new Story_In_1_0[A] {}
//  def apply(in: ?!.type)   = new Story_In_1_1[A, A] {}
//  def <(in: ?.type)        = new Story_In_1_1[A, A] {}
//  def contains(in: ?.type) = new Story_In_1_1[A, A] {}
//  def apply(m: maybe.type) = new Story_1[A] {}
//  def apply(c: count.type) = new Story_1[Int] {}
////
//  def Comments                                              = new ManyRef[Story, Comment] with Comment_1[A]
////  def Comment                                               = new ManyRef[Story, Comment] with Comment_1[A]
////
//  def comments            (comments: Comment_0)             = new ManyRef[Story, Comment] with Story_0
////  def comments[T1]        (comments: Comment_1[T1])         = new ManyRef[Story, Comment] with Story_2[A, Seq[T1]]
////  def comments[T1, T2]    (comments: Comment_2[T1, T2])     = new ManyRef[Story, Comment] with Story_2[A, Seq[(T1, T2)]]
////  def comments[T1, T2, T3](comments: Comment_3[T1, T2, T3]) = new ManyRef[Story, Comment] with Story_2[A, Seq[(T1, T2, T3)]]
//
////  def apply(m: Molecule_1[_]) = this
//}
//
//trait Story_2[A, B] extends Story with Molecule_2[A, B] {
//  import Story._
//  lazy val e   = new e   (this, new Story_3[A, B, Long]   {}) with Story_3[A, B, Long]   {}
//  lazy val title = new title (this, new Story_3[A, B, String] {}) with Story_3[A, B, String] {}
//  lazy val url   = new url   (this, new Story_3[A, B, String] {}) with Story_3[A, B, String] {}
//  def apply(in: ?.type)    = new Story_In_1_1[B, A] {}
//  def apply(in: ?!.type)   = new Story_In_1_2[B, A, B] {}
//  def <(in: ?.type)        = new Story_In_1_2[B, A, B] {}
//  def contains(in: ?.type) = new Story_In_1_2[B, A, B] {}
//  def apply(m: maybe.type) = new Story_2[A, B] {}
//  def apply(c: count.type) = new Story_2[A, Int] {}
//
//
////  def comments            (comments: Comment_0)             = new ManyRef[Story, Comment] with Story_0
////  def comments[T1]        (comments: Comment_1[T1])         = new ManyRef[Story, Comment] with Story_3[A, B, Seq[T1]]
////  def comments[T1, T2]    (comments: Comment_2[T1, T2])     = new ManyRef[Story, Comment] with Story_3[A, B, Seq[(T1, T2)]]
////  def comments[T1, T2, T3](comments: Comment_3[T1, T2, T3]) = new ManyRef[Story, Comment] with Story_3[A, B, Seq[(T1, T2, T3)]]
//}
//
//trait Story_3[A, B, C] extends Story with Molecule_3[A, B, C] {
//  import Story._
//  lazy val e   = new e   (this, new Story_4[A, B, C, Long]   {}) with Story_4[A, B, C, Long]   {}
//  lazy val title = new title (this, new Story_4[A, B, C, String] {}) with Story_4[A, B, C, String] {}
//  lazy val url   = new url   (this, new Story_4[A, B, C, String] {}) with Story_4[A, B, C, String] {}
//  def apply(in: ?.type)    = new Story_In_1_2[C, A, B] {}
//  def apply(in: ?!.type)   = new Story_In_1_3[C, A, B, C] {}
//  def <(in: ?.type)        = new Story_In_1_3[C, A, B, C] {}
//  def contains(in: ?.type) = new Story_In_1_3[C, A, B, C] {}
//  def apply(m: maybe.type) = new Story_3[A, B, C] {}
//  def apply(c: count.type) = new Story_3[A, B, Int] {}
//}
//
//trait Story_4[A, B, C, D] extends Story with Molecule_4[A, B, C, D] {
//  import Story._
//  lazy val e   = new e   (this, new Story_5[A, B, C, D, Long]   {}) with Story_5[A, B, C, D, Long]   {}
//  lazy val title = new title (this, new Story_5[A, B, C, D, String] {}) with Story_5[A, B, C, D, String] {}
//  lazy val url   = new url   (this, new Story_5[A, B, C, D, String] {}) with Story_5[A, B, C, D, String] {}
//  def apply(in: ?.type)    = new Story_In_1_3[D, A, B, C] {}
//  def apply(in: ?!.type)   = new Story_In_1_4[D, A, B, C, D] {}
//  def <(in: ?.type)        = new Story_In_1_4[D, A, B, C, D] {}
//  def contains(in: ?.type) = new Story_In_1_4[D, A, B, C, D] {}
//  def apply(m: maybe.type) = new Story_4[A, B, C, D] {}
//  def apply(c: count.type) = new Story_4[A, B, C, Int] {}
//}
//
//trait Story_5[A, B, C, D, E] extends Story with Molecule_5[A, B, C, D, E] {
//  import Story._
//  lazy val e   = new e   (this, new Story_6[A, B, C, D, E, Long]   {}) with Story_6[A, B, C, D, E, Long]   {}
//  lazy val title = new title (this, new Story_6[A, B, C, D, E, String] {}) with Story_6[A, B, C, D, E, String] {}
//  lazy val url   = new url   (this, new Story_6[A, B, C, D, E, String] {}) with Story_6[A, B, C, D, E, String] {}
//  def apply(in: ?.type)    = new Story_In_1_4[E, A, B, C, D] {}
//  def apply(in: ?!.type)   = new Story_In_1_5[E, A, B, C, D, E] {}
//  def <(in: ?.type)        = new Story_In_1_5[E, A, B, C, D, E] {}
//  def contains(in: ?.type) = new Story_In_1_5[E, A, B, C, D, E] {}
//  def apply(m: maybe.type) = new Story_5[A, B, C, D, E] {}
//  def apply(c: count.type) = new Story_5[A, B, C, D, Int] {}
//}
//
//trait Story_6[A, B, C, D, E, F] extends Story with Molecule_6[A, B, C, D, E, F] {
//  import Story._
//  lazy val e   = new e   (this, new Story_7[A, B, C, D, E, F, Long]   {}) with Story_7[A, B, C, D, E, F, Long]   {}
//  lazy val title = new title (this, new Story_7[A, B, C, D, E, F, String] {}) with Story_7[A, B, C, D, E, F, String] {}
//  lazy val url   = new url   (this, new Story_7[A, B, C, D, E, F, String] {}) with Story_7[A, B, C, D, E, F, String] {}
//  def apply(in: ?.type)    = new Story_In_1_5[F, A, B, C, D, E] {}
//  def apply(in: ?!.type)   = new Story_In_1_6[F, A, B, C, D, E, F] {}
//  def <(in: ?.type)        = new Story_In_1_6[F, A, B, C, D, E, F] {}
//  def contains(in: ?.type) = new Story_In_1_6[F, A, B, C, D, E, F] {}
//  def apply(m: maybe.type) = new Story_6[A, B, C, D, E, F] {}
//  def apply(c: count.type) = new Story_6[A, B, C, D, E, Int] {}
//}
//
//trait Story_7[A, B, C, D, E, F, G] extends Story with Molecule_7[A, B, C, D, E, F, G] {
//  import Story._
//  lazy val e   = new e   (this, new Story_8[A, B, C, D, E, F, G, Long]   {}) with Story_8[A, B, C, D, E, F, G, Long]   {}
//  lazy val title = new title (this, new Story_8[A, B, C, D, E, F, G, String] {}) with Story_8[A, B, C, D, E, F, G, String] {}
//  lazy val url   = new url   (this, new Story_8[A, B, C, D, E, F, G, String] {}) with Story_8[A, B, C, D, E, F, G, String] {}
//  def apply(in: ?.type)    = new Story_In_1_6[G, A, B, C, D, E, F] {}
//  def apply(in: ?!.type)   = new Story_In_1_7[G, A, B, C, D, E, F, G] {}
//  def <(in: ?.type)        = new Story_In_1_7[G, A, B, C, D, E, F, G] {}
//  def contains(in: ?.type) = new Story_In_1_7[G, A, B, C, D, E, F, G] {}
//  def apply(m: maybe.type) = new Story_7[A, B, C, D, E, F, G] {}
//  def apply(c: count.type) = new Story_7[A, B, C, D, E, F, Int] {}
//}
//
//trait Story_8[A, B, C, D, E, F, G, H] extends Story with Molecule_8[A, B, C, D, E, F, G, H]
//
//
///********* Input molecules awaiting 1 input *******************************/
//
// trait Story_In_1_0[I1] extends Story with In_1_0[I1] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_1_1[I1, Long]   {}) with Story_In_1_1[I1, Long]   {}
//  lazy val title = new title (this, new Story_In_1_1[I1, String] {}) with Story_In_1_1[I1, String] {}
//  lazy val url   = new url   (this, new Story_In_1_1[I1, String] {}) with Story_In_1_1[I1, String] {}
//}
//
// trait Story_In_1_1[I1, A] extends Story with In_1_1[I1, A] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_1_2[I1, A, Long]   {}) with Story_In_1_2[I1, A, Long]   {}
//  lazy val title = new title (this, new Story_In_1_2[I1, A, String] {}) with Story_In_1_2[I1, A, String] {}
//  lazy val url   = new url   (this, new Story_In_1_2[I1, A, String] {}) with Story_In_1_2[I1, A, String] {}
//  def apply(in: ?.type)    = new Story_In_2_0[I1, A] {}
//  def apply(in: ?!.type)   = new Story_In_2_1[I1, A, A] {}
//  def <(in: ?.type)        = new Story_In_2_1[I1, A, A] {}
//  def contains(in: ?.type) = new Story_In_2_1[I1, A, A] {}
//}
//
// trait Story_In_1_2[I1, A, B] extends Story with In_1_2[I1, A, B] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_1_3[I1, A, B, Long]   {}) with Story_In_1_3[I1, A, B, Long]   {}
//  lazy val title = new title (this, new Story_In_1_3[I1, A, B, String] {}) with Story_In_1_3[I1, A, B, String] {}
//  lazy val url   = new url   (this, new Story_In_1_3[I1, A, B, String] {}) with Story_In_1_3[I1, A, B, String] {}
//  def apply(in: ?.type)    = new Story_In_2_1[I1, B, A] {}
//  def apply(in: ?!.type)   = new Story_In_2_2[I1, B, A, B] {}
//  def <(in: ?.type)        = new Story_In_2_2[I1, B, A, B] {}
//  def contains(in: ?.type) = new Story_In_2_2[I1, B, A, B] {}
//}
//
// trait Story_In_1_3[I1, A, B, C] extends Story with In_1_3[I1, A, B, C] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_1_4[I1, A, B, C, Long]   {}) with Story_In_1_4[I1, A, B, C, Long]   {}
//  lazy val title = new title (this, new Story_In_1_4[I1, A, B, C, String] {}) with Story_In_1_4[I1, A, B, C, String] {}
//  lazy val url   = new url   (this, new Story_In_1_4[I1, A, B, C, String] {}) with Story_In_1_4[I1, A, B, C, String] {}
//  def apply(in: ?.type)    = new Story_In_2_2[I1, C, A, B] {}
//  def apply(in: ?!.type)   = new Story_In_2_3[I1, C, A, B, C] {}
//  def <(in: ?.type)        = new Story_In_2_3[I1, C, A, B, C] {}
//  def contains(in: ?.type) = new Story_In_2_3[I1, C, A, B, C] {}
//}
//
// trait Story_In_1_4[I1, A, B, C, D] extends Story with In_1_4[I1, A, B, C, D] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_1_5[I1, A, B, C, D, Long]   {}) with Story_In_1_5[I1, A, B, C, D, Long]   {}
//  lazy val title = new title (this, new Story_In_1_5[I1, A, B, C, D, String] {}) with Story_In_1_5[I1, A, B, C, D, String] {}
//  lazy val url   = new url   (this, new Story_In_1_5[I1, A, B, C, D, String] {}) with Story_In_1_5[I1, A, B, C, D, String] {}
//  def apply(in: ?.type)    = new Story_In_2_3[I1, D, A, B, C] {}
//  def apply(in: ?!.type)   = new Story_In_2_4[I1, D, A, B, C, D] {}
//  def <(in: ?.type)        = new Story_In_2_4[I1, D, A, B, C, D] {}
//  def contains(in: ?.type) = new Story_In_2_4[I1, D, A, B, C, D] {}
//}
//
// trait Story_In_1_5[I1, A, B, C, D, E] extends Story with In_1_5[I1, A, B, C, D, E] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_1_6[I1, A, B, C, D, E, Long]   {}) with Story_In_1_6[I1, A, B, C, D, E, Long]   {}
//  lazy val title = new title (this, new Story_In_1_6[I1, A, B, C, D, E, String] {}) with Story_In_1_6[I1, A, B, C, D, E, String] {}
//  lazy val url   = new url   (this, new Story_In_1_6[I1, A, B, C, D, E, String] {}) with Story_In_1_6[I1, A, B, C, D, E, String] {}
//  def apply(in: ?.type)    = new Story_In_2_4[I1, E, A, B, C, D] {}
//  def apply(in: ?!.type)   = new Story_In_2_5[I1, E, A, B, C, D, E] {}
//  def <(in: ?.type)        = new Story_In_2_5[I1, E, A, B, C, D, E] {}
//  def contains(in: ?.type) = new Story_In_2_5[I1, E, A, B, C, D, E] {}
//}
//
// trait Story_In_1_6[I1, A, B, C, D, E, F] extends Story with In_1_6[I1, A, B, C, D, E, F] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_1_7[I1, A, B, C, D, E, F, Long]   {}) with Story_In_1_7[I1, A, B, C, D, E, F, Long]   {}
//  lazy val title = new title (this, new Story_In_1_7[I1, A, B, C, D, E, F, String] {}) with Story_In_1_7[I1, A, B, C, D, E, F, String] {}
//  lazy val url   = new url   (this, new Story_In_1_7[I1, A, B, C, D, E, F, String] {}) with Story_In_1_7[I1, A, B, C, D, E, F, String] {}
//  def apply(in: ?.type)    = new Story_In_2_5[I1, F, A, B, C, D, E] {}
//  def apply(in: ?!.type)   = new Story_In_2_6[I1, F, A, B, C, D, E, F] {}
//  def <(in: ?.type)        = new Story_In_2_6[I1, F, A, B, C, D, E, F] {}
//  def contains(in: ?.type) = new Story_In_2_6[I1, F, A, B, C, D, E, F] {}
//}
//
// trait Story_In_1_7[I1, A, B, C, D, E, F, G] extends Story with In_1_7[I1, A, B, C, D, E, F, G] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_1_8[I1, A, B, C, D, E, F, G, Long]   {}) with Story_In_1_8[I1, A, B, C, D, E, F, G, Long]   {}
//  lazy val title = new title (this, new Story_In_1_8[I1, A, B, C, D, E, F, G, String] {}) with Story_In_1_8[I1, A, B, C, D, E, F, G, String] {}
//  lazy val url   = new url   (this, new Story_In_1_8[I1, A, B, C, D, E, F, G, String] {}) with Story_In_1_8[I1, A, B, C, D, E, F, G, String] {}
//  def apply(in: ?.type)    = new Story_In_2_6[I1, G, A, B, C, D, E, F] {}
//  def apply(in: ?!.type)   = new Story_In_2_7[I1, G, A, B, C, D, E, F, G] {}
//  def <(in: ?.type)        = new Story_In_2_7[I1, G, A, B, C, D, E, F, G] {}
//  def contains(in: ?.type) = new Story_In_2_7[I1, G, A, B, C, D, E, F, G] {}
//}
//
// trait Story_In_1_8[I1, A, B, C, D, E, F, G, H] extends Story with In_1_8[I1, A, B, C, D, E, F, G, H]
//
//
///********* Input molecules awaiting 2 inputs *******************************/
//
// trait Story_In_2_0[I1, I2] extends Story with In_2_0[I1, I2] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_2_1[I1, I2, Long]   {}) with Story_In_2_1[I1, I2, Long]   {}
//  lazy val title = new title (this, new Story_In_2_1[I1, I2, String] {}) with Story_In_2_1[I1, I2, String] {}
//  lazy val url   = new url   (this, new Story_In_2_1[I1, I2, String] {}) with Story_In_2_1[I1, I2, String] {}
//}
//
// trait Story_In_2_1[I1, I2, A] extends Story with In_2_1[I1, I2, A] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_2_2[I1, I2, A, Long]   {}) with Story_In_2_2[I1, I2, A, Long]   {}
//  lazy val title = new title (this, new Story_In_2_2[I1, I2, A, String] {}) with Story_In_2_2[I1, I2, A, String] {}
//  lazy val url   = new url   (this, new Story_In_2_2[I1, I2, A, String] {}) with Story_In_2_2[I1, I2, A, String] {}
//  def apply(in: ?.type)    = new Story_In_3_0[I1, I2, A] {}
//  def apply(in: ?!.type)   = new Story_In_3_1[I1, I2, A, A] {}
//  def <(in: ?.type)        = new Story_In_3_1[I1, I2, A, A] {}
//  def contains(in: ?.type) = new Story_In_3_1[I1, I2, A, A] {}
//}
//
// trait Story_In_2_2[I1, I2, A, B] extends Story with In_2_2[I1, I2, A, B] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_2_3[I1, I2, A, B, Long]   {}) with Story_In_2_3[I1, I2, A, B, Long]   {}
//  lazy val title = new title (this, new Story_In_2_3[I1, I2, A, B, String] {}) with Story_In_2_3[I1, I2, A, B, String] {}
//  lazy val url   = new url   (this, new Story_In_2_3[I1, I2, A, B, String] {}) with Story_In_2_3[I1, I2, A, B, String] {}
//  def apply(in: ?.type)    = new Story_In_3_1[I1, I2, B, A] {}
//  def apply(in: ?!.type)   = new Story_In_3_2[I1, I2, B, A, B] {}
//  def <(in: ?.type)        = new Story_In_3_2[I1, I2, B, A, B] {}
//  def contains(in: ?.type) = new Story_In_3_2[I1, I2, B, A, B] {}
//}
//
// trait Story_In_2_3[I1, I2, A, B, C] extends Story with In_2_3[I1, I2, A, B, C] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_2_4[I1, I2, A, B, C, Long]   {}) with Story_In_2_4[I1, I2, A, B, C, Long]   {}
//  lazy val title = new title (this, new Story_In_2_4[I1, I2, A, B, C, String] {}) with Story_In_2_4[I1, I2, A, B, C, String] {}
//  lazy val url   = new url   (this, new Story_In_2_4[I1, I2, A, B, C, String] {}) with Story_In_2_4[I1, I2, A, B, C, String] {}
//  def apply(in: ?.type)    = new Story_In_3_2[I1, I2, C, A, B] {}
//  def apply(in: ?!.type)   = new Story_In_3_3[I1, I2, C, A, B, C] {}
//  def <(in: ?.type)        = new Story_In_3_3[I1, I2, C, A, B, C] {}
//  def contains(in: ?.type) = new Story_In_3_3[I1, I2, C, A, B, C] {}
//}
//
// trait Story_In_2_4[I1, I2, A, B, C, D] extends Story with In_2_4[I1, I2, A, B, C, D] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_2_5[I1, I2, A, B, C, D, Long]   {}) with Story_In_2_5[I1, I2, A, B, C, D, Long]   {}
//  lazy val title = new title (this, new Story_In_2_5[I1, I2, A, B, C, D, String] {}) with Story_In_2_5[I1, I2, A, B, C, D, String] {}
//  lazy val url   = new url   (this, new Story_In_2_5[I1, I2, A, B, C, D, String] {}) with Story_In_2_5[I1, I2, A, B, C, D, String] {}
//  def apply(in: ?.type)    = new Story_In_3_3[I1, I2, D, A, B, C] {}
//  def apply(in: ?!.type)   = new Story_In_3_4[I1, I2, D, A, B, C, D] {}
//  def <(in: ?.type)        = new Story_In_3_4[I1, I2, D, A, B, C, D] {}
//  def contains(in: ?.type) = new Story_In_3_4[I1, I2, D, A, B, C, D] {}
//}
//
// trait Story_In_2_5[I1, I2, A, B, C, D, E] extends Story with In_2_5[I1, I2, A, B, C, D, E] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_2_6[I1, I2, A, B, C, D, E, Long]   {}) with Story_In_2_6[I1, I2, A, B, C, D, E, Long]   {}
//  lazy val title = new title (this, new Story_In_2_6[I1, I2, A, B, C, D, E, String] {}) with Story_In_2_6[I1, I2, A, B, C, D, E, String] {}
//  lazy val url   = new url   (this, new Story_In_2_6[I1, I2, A, B, C, D, E, String] {}) with Story_In_2_6[I1, I2, A, B, C, D, E, String] {}
//  def apply(in: ?.type)    = new Story_In_3_4[I1, I2, E, A, B, C, D] {}
//  def apply(in: ?!.type)   = new Story_In_3_5[I1, I2, E, A, B, C, D, E] {}
//  def <(in: ?.type)        = new Story_In_3_5[I1, I2, E, A, B, C, D, E] {}
//  def contains(in: ?.type) = new Story_In_3_5[I1, I2, E, A, B, C, D, E] {}
//}
//
// trait Story_In_2_6[I1, I2, A, B, C, D, E, F] extends Story with In_2_6[I1, I2, A, B, C, D, E, F] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_2_7[I1, I2, A, B, C, D, E, F, Long]   {}) with Story_In_2_7[I1, I2, A, B, C, D, E, F, Long]   {}
//  lazy val title = new title (this, new Story_In_2_7[I1, I2, A, B, C, D, E, F, String] {}) with Story_In_2_7[I1, I2, A, B, C, D, E, F, String] {}
//  lazy val url   = new url   (this, new Story_In_2_7[I1, I2, A, B, C, D, E, F, String] {}) with Story_In_2_7[I1, I2, A, B, C, D, E, F, String] {}
//  def apply(in: ?.type)    = new Story_In_3_5[I1, I2, F, A, B, C, D, E] {}
//  def apply(in: ?!.type)   = new Story_In_3_6[I1, I2, F, A, B, C, D, E, F] {}
//  def <(in: ?.type)        = new Story_In_3_6[I1, I2, F, A, B, C, D, E, F] {}
//  def contains(in: ?.type) = new Story_In_3_6[I1, I2, F, A, B, C, D, E, F] {}
//}
//
// trait Story_In_2_7[I1, I2, A, B, C, D, E, F, G] extends Story with In_2_7[I1, I2, A, B, C, D, E, F, G] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_2_8[I1, I2, A, B, C, D, E, F, G, Long]   {}) with Story_In_2_8[I1, I2, A, B, C, D, E, F, G, Long]   {}
//  lazy val title = new title (this, new Story_In_2_8[I1, I2, A, B, C, D, E, F, G, String] {}) with Story_In_2_8[I1, I2, A, B, C, D, E, F, G, String] {}
//  lazy val url   = new url   (this, new Story_In_2_8[I1, I2, A, B, C, D, E, F, G, String] {}) with Story_In_2_8[I1, I2, A, B, C, D, E, F, G, String] {}
//  def apply(in: ?.type)    = new Story_In_3_6[I1, I2, G, A, B, C, D, E, F] {}
//  def apply(in: ?!.type)   = new Story_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {}
//  def <(in: ?.type)        = new Story_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {}
//  def contains(in: ?.type) = new Story_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {}
//}
//
// trait Story_In_2_8[I1, I2, A, B, C, D, E, F, G, H] extends Story with In_2_8[I1, I2, A, B, C, D, E, F, G, H]
//
//
///********* Input molecules awaiting 3 inputs *******************************/
//
// trait Story_In_3_0[I1, I2, I3] extends Story with In_3_0[I1, I2, I3] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_3_1[I1, I2, I3, Long]   {}) with Story_In_3_1[I1, I2, I3, Long]   {}
//  lazy val title = new title (this, new Story_In_3_1[I1, I2, I3, String] {}) with Story_In_3_1[I1, I2, I3, String] {}
//  lazy val url   = new url   (this, new Story_In_3_1[I1, I2, I3, String] {}) with Story_In_3_1[I1, I2, I3, String] {}
//}
//
// trait Story_In_3_1[I1, I2, I3, A] extends Story with In_3_1[I1, I2, I3, A] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_3_2[I1, I2, I3, A, Long]   {}) with Story_In_3_2[I1, I2, I3, A, Long]   {}
//  lazy val title = new title (this, new Story_In_3_2[I1, I2, I3, A, String] {}) with Story_In_3_2[I1, I2, I3, A, String] {}
//  lazy val url   = new url   (this, new Story_In_3_2[I1, I2, I3, A, String] {}) with Story_In_3_2[I1, I2, I3, A, String] {}
//}
//
// trait Story_In_3_2[I1, I2, I3, A, B] extends Story with In_3_2[I1, I2, I3, A, B] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_3_3[I1, I2, I3, A, B, Long]   {}) with Story_In_3_3[I1, I2, I3, A, B, Long]   {}
//  lazy val title = new title (this, new Story_In_3_3[I1, I2, I3, A, B, String] {}) with Story_In_3_3[I1, I2, I3, A, B, String] {}
//  lazy val url   = new url   (this, new Story_In_3_3[I1, I2, I3, A, B, String] {}) with Story_In_3_3[I1, I2, I3, A, B, String] {}
//}
//
// trait Story_In_3_3[I1, I2, I3, A, B, C] extends Story with In_3_3[I1, I2, I3, A, B, C] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_3_4[I1, I2, I3, A, B, C, Long]   {}) with Story_In_3_4[I1, I2, I3, A, B, C, Long]   {}
//  lazy val title = new title (this, new Story_In_3_4[I1, I2, I3, A, B, C, String] {}) with Story_In_3_4[I1, I2, I3, A, B, C, String] {}
//  lazy val url   = new url   (this, new Story_In_3_4[I1, I2, I3, A, B, C, String] {}) with Story_In_3_4[I1, I2, I3, A, B, C, String] {}
//}
//
// trait Story_In_3_4[I1, I2, I3, A, B, C, D] extends Story with In_3_4[I1, I2, I3, A, B, C, D] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_3_5[I1, I2, I3, A, B, C, D, Long]   {}) with Story_In_3_5[I1, I2, I3, A, B, C, D, Long]   {}
//  lazy val title = new title (this, new Story_In_3_5[I1, I2, I3, A, B, C, D, String] {}) with Story_In_3_5[I1, I2, I3, A, B, C, D, String] {}
//  lazy val url   = new url   (this, new Story_In_3_5[I1, I2, I3, A, B, C, D, String] {}) with Story_In_3_5[I1, I2, I3, A, B, C, D, String] {}
//}
//
// trait Story_In_3_5[I1, I2, I3, A, B, C, D, E] extends Story with In_3_5[I1, I2, I3, A, B, C, D, E] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_3_6[I1, I2, I3, A, B, C, D, E, Long]   {}) with Story_In_3_6[I1, I2, I3, A, B, C, D, E, Long]   {}
//  lazy val title = new title (this, new Story_In_3_6[I1, I2, I3, A, B, C, D, E, String] {}) with Story_In_3_6[I1, I2, I3, A, B, C, D, E, String] {}
//  lazy val url   = new url   (this, new Story_In_3_6[I1, I2, I3, A, B, C, D, E, String] {}) with Story_In_3_6[I1, I2, I3, A, B, C, D, E, String] {}
//}
//
// trait Story_In_3_6[I1, I2, I3, A, B, C, D, E, F] extends Story with In_3_6[I1, I2, I3, A, B, C, D, E, F] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_3_7[I1, I2, I3, A, B, C, D, E, F, Long]   {}) with Story_In_3_7[I1, I2, I3, A, B, C, D, E, F, Long]   {}
//  lazy val title = new title (this, new Story_In_3_7[I1, I2, I3, A, B, C, D, E, F, String] {}) with Story_In_3_7[I1, I2, I3, A, B, C, D, E, F, String] {}
//  lazy val url   = new url   (this, new Story_In_3_7[I1, I2, I3, A, B, C, D, E, F, String] {}) with Story_In_3_7[I1, I2, I3, A, B, C, D, E, F, String] {}
//}
//
// trait Story_In_3_7[I1, I2, I3, A, B, C, D, E, F, G] extends Story with In_3_7[I1, I2, I3, A, B, C, D, E, F, G] {
//  import Story._
//  lazy val e   = new e   (this, new Story_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Long]   {}) with Story_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Long]   {}
//  lazy val title = new title (this, new Story_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String] {}) with Story_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String] {}
//  lazy val url   = new url   (this, new Story_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String] {}) with Story_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String] {}
//}
//
// trait Story_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H] extends Story with In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H]
