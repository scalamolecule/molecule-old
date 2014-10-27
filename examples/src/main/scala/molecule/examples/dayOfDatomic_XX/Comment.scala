///*
//* AUTO-GENERATED CODE - DO NOT CHANGE!
//*
//* Manual changes to this file will likely break molecules!
//* Instead, change the molecule definition files and recompile your project with `sbt compile`.
//*/
//package molecule.examples.dayOfDatomic_XX
//
//import molecule._
//import molecule.dsl.schemaDSL.NS.e
//import molecule.dsl.schemaDSL._
//import molecule.in._
//import molecule.out._
//import datomic.Connection
//
//
//trait Comment
//
//object Comment extends Comment with Comment_0 {
//  class author [Ns1, Ns2](ns1: Ns1, ns2: Ns2) extends OneRefAttr (ns1, ns2)
//  class text   [Ns1, Ns2](ns1: Ns1, ns2: Ns2) extends OneString  (ns1, ns2)
//
//}
//
//object SchemaExplorer {
//  lazy val ns = this
//  lazy val attr = this
//  def get = this
//}
//
//trait Comment_0 extends Comment with SubMolecule_0 {
//  import Comment._
//  def apply(e: Long) = this
//  def apply(c: count.type) = new Comment_1[Int] {}
//
//  lazy val e     = new e    (this, new Comment_1[Long]   {}) with Comment_1[Long]   {}
//  lazy val author  = new author (this, new Comment_1[Long]   {}) with Comment_1[Long]   {}
//  lazy val text    = new text   (this, new Comment_1[String] {}) with Comment_1[String] {}
//  lazy val author_ = new author (this, this) with Comment_0 {}
//  lazy val text_   = new text   (this, this) with Comment_0 {}
//
//
//  lazy val _parent  = new e [Comment_1[Long]] with Comment_1[Long]
//  lazy val _parent_ = new e [Comment_0] with Comment_0
//
//
//
//  lazy val _parent    = new e    (this, new Comment_1[Long]   {}) with Comment_1[Long]   {}
//  lazy val root    = new e    (this, new Comment_1[Long]   {}) with Comment_1[Long]   {}
//
//  def tree(root: Long, depth: Int = 99) = this
//  def _root(e: Long) = this
//  def _parent(any: Molecule_0) = this
//
//  def _parent(any: Molecule_1[_]) = this
////  def about(any: Molecule_1[_]) = this
//
//
//  def grandParent(x: Any) = this
//
//  def Parent = SchemaExplorer
//def Parent : BackRef[Comment, Comment] with Comment_0 = ???
//
//  def Author = new OneRef[Comment, User] with User_0
//
//  // Child comment
//  def Comment = this
//
//  // Parent comment
//  def _Comment = this
//
//  def _Stories = new Story_0 {}
//  def _Story = new Story_0 {}
////  def Story = new Story_0 {}
//
//  def to = this
//
//  def <-:(m: Any) = this
////  def --:(m: Any) = this
//
////  def _author(id: Long) = this
//
////  def ~(a: Any) = this
////  def apply(s: Symbol) = this
//}
//
//trait Comment_1[A] extends Comment with SubMolecule_1[A] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_2[A, Long]   {}) with Comment_2[A, Long]   {}
//  lazy val author = new author (this, new Comment_2[A, Long]   {}) with Comment_2[A, Long]   {}
//  lazy val text   = new text   (this, new Comment_2[A, String] {}) with Comment_2[A, String] {}
//  lazy val text_   = new text   (this,this) with Comment_1[A] {}
//
////  lazy val parent = new author (this, new Comment_2[A, Long]   {}) with Comment_2[A, Long]   {}
//
//  def Author = new OneRef[Comment, User] with User_1[A]
//  def apply(in: ?.type)    = new Comment_In_1_0[A] {}
//  def apply(in: ?!.type)   = new Comment_In_1_1[A, A] {}
//  def <(in: ?.type)        = new Comment_In_1_1[A, A] {}
//  def contains(in: ?.type) = new Comment_In_1_1[A, A] {}
//  def apply(m: maybe.type) = this //new Comment_1[A] {}
////  def apply(c: count.type) = this // new Comment_1[Int] {}
//  def apply(c: count.type) =  new Comment_1[Int] {}
//  def apply(s: Symbol) = new Comment_0 {}
//
//  def _parent(x: Molecule_0) = this
//  def _parent(x: Molecule_1[_]) = this
//
//  def _Story = new Story_1[A] {}
//  def _Story(x: Molecule_0) = new Story_1[A] {}
//  def _Story(x: Molecule_1[_]) = new Story_1[A] {}
//
//  def <-:(m: Any) = this
////  def --:(m: Any) = this
//
//  def Comment = this
////  def <-:(m: Any) = this
//
//}
//
//trait Comment_2[A, B] extends Comment with SubMolecule_2[A, B] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_3[A, B, Long]   {}) with Comment_3[A, B, Long]   {}
//  lazy val author = new author (this, new Comment_3[A, B, Long]   {}) with Comment_3[A, B, Long]   {}
//  lazy val text   = new text   (this, new Comment_3[A, B, String] {}) with Comment_3[A, B, String] {}
//  def Author = new OneRef[Comment, User] with User_2[A, B]
//  def apply(in: ?.type)    = new Comment_In_1_1[B, A] {}
//  def apply(in: ?!.type)   = new Comment_In_1_2[B, A, B] {}
//  def <(in: ?.type)        = new Comment_In_1_2[B, A, B] {}
//  def contains(in: ?.type) = new Comment_In_1_2[B, A, B] {}
//  def apply(m: maybe.type) = new Comment_2[A, B] {}
//  def apply(c: count.type) = new Comment_2[A, Int] {}
//}
//
//trait Comment_3[A, B, C] extends Comment with SubMolecule_3[A, B, C] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_4[A, B, C, Long]   {}) with Comment_4[A, B, C, Long]   {}
//  lazy val author = new author (this, new Comment_4[A, B, C, Long]   {}) with Comment_4[A, B, C, Long]   {}
//  lazy val text   = new text   (this, new Comment_4[A, B, C, String] {}) with Comment_4[A, B, C, String] {}
//  def Author = new OneRef[Comment, User] with User_3[A, B, C]
//  def apply(in: ?.type)    = new Comment_In_1_2[C, A, B] {}
//  def apply(in: ?!.type)   = new Comment_In_1_3[C, A, B, C] {}
//  def <(in: ?.type)        = new Comment_In_1_3[C, A, B, C] {}
//  def contains(in: ?.type) = new Comment_In_1_3[C, A, B, C] {}
//  def apply(m: maybe.type) = new Comment_3[A, B, C] {}
//  def apply(c: count.type) = new Comment_3[A, B, Int] {}
//}
//
//trait Comment_4[A, B, C, D] extends Comment with SubMolecule_4[A, B, C, D] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_5[A, B, C, D, Long]   {}) with Comment_5[A, B, C, D, Long]   {}
//  lazy val author = new author (this, new Comment_5[A, B, C, D, Long]   {}) with Comment_5[A, B, C, D, Long]   {}
//  lazy val text   = new text   (this, new Comment_5[A, B, C, D, String] {}) with Comment_5[A, B, C, D, String] {}
//  def Author = new OneRef[Comment, User] with User_4[A, B, C, D]
//  def apply(in: ?.type)    = new Comment_In_1_3[D, A, B, C] {}
//  def apply(in: ?!.type)   = new Comment_In_1_4[D, A, B, C, D] {}
//  def <(in: ?.type)        = new Comment_In_1_4[D, A, B, C, D] {}
//  def contains(in: ?.type) = new Comment_In_1_4[D, A, B, C, D] {}
//  def apply(m: maybe.type) = new Comment_4[A, B, C, D] {}
//  def apply(c: count.type) = new Comment_4[A, B, C, Int] {}
//}
//
//trait Comment_5[A, B, C, D, E] extends Comment with SubMolecule_5[A, B, C, D, E] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_6[A, B, C, D, E, Long]   {}) with Comment_6[A, B, C, D, E, Long]   {}
//  lazy val author = new author (this, new Comment_6[A, B, C, D, E, Long]   {}) with Comment_6[A, B, C, D, E, Long]   {}
//  lazy val text   = new text   (this, new Comment_6[A, B, C, D, E, String] {}) with Comment_6[A, B, C, D, E, String] {}
//  def Author = new OneRef[Comment, User] with User_5[A, B, C, D, E]
//  def apply(in: ?.type)    = new Comment_In_1_4[E, A, B, C, D] {}
//  def apply(in: ?!.type)   = new Comment_In_1_5[E, A, B, C, D, E] {}
//  def <(in: ?.type)        = new Comment_In_1_5[E, A, B, C, D, E] {}
//  def contains(in: ?.type) = new Comment_In_1_5[E, A, B, C, D, E] {}
//  def apply(m: maybe.type) = new Comment_5[A, B, C, D, E] {}
//  def apply(c: count.type) = new Comment_5[A, B, C, D, Int] {}
//}
//
//trait Comment_6[A, B, C, D, E, F] extends Comment with SubMolecule_6[A, B, C, D, E, F] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_7[A, B, C, D, E, F, Long]   {}) with Comment_7[A, B, C, D, E, F, Long]   {}
//  lazy val author = new author (this, new Comment_7[A, B, C, D, E, F, Long]   {}) with Comment_7[A, B, C, D, E, F, Long]   {}
//  lazy val text   = new text   (this, new Comment_7[A, B, C, D, E, F, String] {}) with Comment_7[A, B, C, D, E, F, String] {}
//  def Author = new OneRef[Comment, User] with User_6[A, B, C, D, E, F]
//  def apply(in: ?.type)    = new Comment_In_1_5[F, A, B, C, D, E] {}
//  def apply(in: ?!.type)   = new Comment_In_1_6[F, A, B, C, D, E, F] {}
//  def <(in: ?.type)        = new Comment_In_1_6[F, A, B, C, D, E, F] {}
//  def contains(in: ?.type) = new Comment_In_1_6[F, A, B, C, D, E, F] {}
//  def apply(m: maybe.type) = new Comment_6[A, B, C, D, E, F] {}
//  def apply(c: count.type) = new Comment_6[A, B, C, D, E, Int] {}
//}
//
//trait Comment_7[A, B, C, D, E, F, G] extends Comment with SubMolecule_7[A, B, C, D, E, F, G] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_8[A, B, C, D, E, F, G, Long]   {}) with Comment_8[A, B, C, D, E, F, G, Long]   {}
//  lazy val author = new author (this, new Comment_8[A, B, C, D, E, F, G, Long]   {}) with Comment_8[A, B, C, D, E, F, G, Long]   {}
//  lazy val text   = new text   (this, new Comment_8[A, B, C, D, E, F, G, String] {}) with Comment_8[A, B, C, D, E, F, G, String] {}
//  def Author = new OneRef[Comment, User] with User_7[A, B, C, D, E, F, G]
//  def apply(in: ?.type)    = new Comment_In_1_6[G, A, B, C, D, E, F] {}
//  def apply(in: ?!.type)   = new Comment_In_1_7[G, A, B, C, D, E, F, G] {}
//  def <(in: ?.type)        = new Comment_In_1_7[G, A, B, C, D, E, F, G] {}
//  def contains(in: ?.type) = new Comment_In_1_7[G, A, B, C, D, E, F, G] {}
//  def apply(m: maybe.type) = new Comment_7[A, B, C, D, E, F, G] {}
//  def apply(c: count.type) = new Comment_7[A, B, C, D, E, F, Int] {}
//}
//
//trait Comment_8[A, B, C, D, E, F, G, H] extends Comment with SubMolecule_8[A, B, C, D, E, F, G, H]
//
//
///********* Input molecules awaiting 1 input *******************************/
//
//trait Comment_In_1_0[I1] extends Comment with In_1_0[I1] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_1_1[I1, Long]   {}) with Comment_In_1_1[I1, Long]   {}
//  lazy val author = new author (this, new Comment_In_1_1[I1, Long]   {}) with Comment_In_1_1[I1, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_1_1[I1, String] {}) with Comment_In_1_1[I1, String] {}
//  def Author = new OneRef[Comment, User] with User_In_1_0[I1]
//}
//
//trait Comment_In_1_1[I1, A] extends Comment with In_1_1[I1, A] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_1_2[I1, A, Long]   {}) with Comment_In_1_2[I1, A, Long]   {}
//  lazy val author = new author (this, new Comment_In_1_2[I1, A, Long]   {}) with Comment_In_1_2[I1, A, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_1_2[I1, A, String] {}) with Comment_In_1_2[I1, A, String] {}
//  def Author = new OneRef[Comment, User] with User_In_1_1[I1, A]
//  def apply(in: ?.type)    = new Comment_In_2_0[I1, A] {}
//  def apply(in: ?!.type)   = new Comment_In_2_1[I1, A, A] {}
//  def <(in: ?.type)        = new Comment_In_2_1[I1, A, A] {}
//  def contains(in: ?.type) = new Comment_In_2_1[I1, A, A] {}
//}
//
//trait Comment_In_1_2[I1, A, B] extends Comment with In_1_2[I1, A, B] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_1_3[I1, A, B, Long]   {}) with Comment_In_1_3[I1, A, B, Long]   {}
//  lazy val author = new author (this, new Comment_In_1_3[I1, A, B, Long]   {}) with Comment_In_1_3[I1, A, B, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_1_3[I1, A, B, String] {}) with Comment_In_1_3[I1, A, B, String] {}
//  def Author = new OneRef[Comment, User] with User_In_1_2[I1, A, B]
//  def apply(in: ?.type)    = new Comment_In_2_1[I1, B, A] {}
//  def apply(in: ?!.type)   = new Comment_In_2_2[I1, B, A, B] {}
//  def <(in: ?.type)        = new Comment_In_2_2[I1, B, A, B] {}
//  def contains(in: ?.type) = new Comment_In_2_2[I1, B, A, B] {}
//}
//
//trait Comment_In_1_3[I1, A, B, C] extends Comment with In_1_3[I1, A, B, C] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_1_4[I1, A, B, C, Long]   {}) with Comment_In_1_4[I1, A, B, C, Long]   {}
//  lazy val author = new author (this, new Comment_In_1_4[I1, A, B, C, Long]   {}) with Comment_In_1_4[I1, A, B, C, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_1_4[I1, A, B, C, String] {}) with Comment_In_1_4[I1, A, B, C, String] {}
//  def Author = new OneRef[Comment, User] with User_In_1_3[I1, A, B, C]
//  def apply(in: ?.type)    = new Comment_In_2_2[I1, C, A, B] {}
//  def apply(in: ?!.type)   = new Comment_In_2_3[I1, C, A, B, C] {}
//  def <(in: ?.type)        = new Comment_In_2_3[I1, C, A, B, C] {}
//  def contains(in: ?.type) = new Comment_In_2_3[I1, C, A, B, C] {}
//}
//
//trait Comment_In_1_4[I1, A, B, C, D] extends Comment with In_1_4[I1, A, B, C, D] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_1_5[I1, A, B, C, D, Long]   {}) with Comment_In_1_5[I1, A, B, C, D, Long]   {}
//  lazy val author = new author (this, new Comment_In_1_5[I1, A, B, C, D, Long]   {}) with Comment_In_1_5[I1, A, B, C, D, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_1_5[I1, A, B, C, D, String] {}) with Comment_In_1_5[I1, A, B, C, D, String] {}
//  def Author = new OneRef[Comment, User] with User_In_1_4[I1, A, B, C, D]
//  def apply(in: ?.type)    = new Comment_In_2_3[I1, D, A, B, C] {}
//  def apply(in: ?!.type)   = new Comment_In_2_4[I1, D, A, B, C, D] {}
//  def <(in: ?.type)        = new Comment_In_2_4[I1, D, A, B, C, D] {}
//  def contains(in: ?.type) = new Comment_In_2_4[I1, D, A, B, C, D] {}
//}
//
//trait Comment_In_1_5[I1, A, B, C, D, E] extends Comment with In_1_5[I1, A, B, C, D, E] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_1_6[I1, A, B, C, D, E, Long]   {}) with Comment_In_1_6[I1, A, B, C, D, E, Long]   {}
//  lazy val author = new author (this, new Comment_In_1_6[I1, A, B, C, D, E, Long]   {}) with Comment_In_1_6[I1, A, B, C, D, E, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_1_6[I1, A, B, C, D, E, String] {}) with Comment_In_1_6[I1, A, B, C, D, E, String] {}
//  def Author = new OneRef[Comment, User] with User_In_1_5[I1, A, B, C, D, E]
//  def apply(in: ?.type)    = new Comment_In_2_4[I1, E, A, B, C, D] {}
//  def apply(in: ?!.type)   = new Comment_In_2_5[I1, E, A, B, C, D, E] {}
//  def <(in: ?.type)        = new Comment_In_2_5[I1, E, A, B, C, D, E] {}
//  def contains(in: ?.type) = new Comment_In_2_5[I1, E, A, B, C, D, E] {}
//}
//
//trait Comment_In_1_6[I1, A, B, C, D, E, F] extends Comment with In_1_6[I1, A, B, C, D, E, F] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_1_7[I1, A, B, C, D, E, F, Long]   {}) with Comment_In_1_7[I1, A, B, C, D, E, F, Long]   {}
//  lazy val author = new author (this, new Comment_In_1_7[I1, A, B, C, D, E, F, Long]   {}) with Comment_In_1_7[I1, A, B, C, D, E, F, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_1_7[I1, A, B, C, D, E, F, String] {}) with Comment_In_1_7[I1, A, B, C, D, E, F, String] {}
//  def Author = new OneRef[Comment, User] with User_In_1_6[I1, A, B, C, D, E, F]
//  def apply(in: ?.type)    = new Comment_In_2_5[I1, F, A, B, C, D, E] {}
//  def apply(in: ?!.type)   = new Comment_In_2_6[I1, F, A, B, C, D, E, F] {}
//  def <(in: ?.type)        = new Comment_In_2_6[I1, F, A, B, C, D, E, F] {}
//  def contains(in: ?.type) = new Comment_In_2_6[I1, F, A, B, C, D, E, F] {}
//}
//
//trait Comment_In_1_7[I1, A, B, C, D, E, F, G] extends Comment with In_1_7[I1, A, B, C, D, E, F, G] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_1_8[I1, A, B, C, D, E, F, G, Long]   {}) with Comment_In_1_8[I1, A, B, C, D, E, F, G, Long]   {}
//  lazy val author = new author (this, new Comment_In_1_8[I1, A, B, C, D, E, F, G, Long]   {}) with Comment_In_1_8[I1, A, B, C, D, E, F, G, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_1_8[I1, A, B, C, D, E, F, G, String] {}) with Comment_In_1_8[I1, A, B, C, D, E, F, G, String] {}
//  def Author = new OneRef[Comment, User] with User_In_1_7[I1, A, B, C, D, E, F, G]
//  def apply(in: ?.type)    = new Comment_In_2_6[I1, G, A, B, C, D, E, F] {}
//  def apply(in: ?!.type)   = new Comment_In_2_7[I1, G, A, B, C, D, E, F, G] {}
//  def <(in: ?.type)        = new Comment_In_2_7[I1, G, A, B, C, D, E, F, G] {}
//  def contains(in: ?.type) = new Comment_In_2_7[I1, G, A, B, C, D, E, F, G] {}
//}
//
//trait Comment_In_1_8[I1, A, B, C, D, E, F, G, H] extends Comment with In_1_8[I1, A, B, C, D, E, F, G, H]
//
//
///********* Input molecules awaiting 2 inputs *******************************/
//
//trait Comment_In_2_0[I1, I2] extends Comment with In_2_0[I1, I2] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_2_1[I1, I2, Long]   {}) with Comment_In_2_1[I1, I2, Long]   {}
//  lazy val author = new author (this, new Comment_In_2_1[I1, I2, Long]   {}) with Comment_In_2_1[I1, I2, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_2_1[I1, I2, String] {}) with Comment_In_2_1[I1, I2, String] {}
//  def Author = new OneRef[Comment, User] with User_In_2_0[I1, I2]
//}
//
//trait Comment_In_2_1[I1, I2, A] extends Comment with In_2_1[I1, I2, A] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_2_2[I1, I2, A, Long]   {}) with Comment_In_2_2[I1, I2, A, Long]   {}
//  lazy val author = new author (this, new Comment_In_2_2[I1, I2, A, Long]   {}) with Comment_In_2_2[I1, I2, A, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_2_2[I1, I2, A, String] {}) with Comment_In_2_2[I1, I2, A, String] {}
//  def Author = new OneRef[Comment, User] with User_In_2_1[I1, I2, A]
//  def apply(in: ?.type)    = new Comment_In_3_0[I1, I2, A] {}
//  def apply(in: ?!.type)   = new Comment_In_3_1[I1, I2, A, A] {}
//  def <(in: ?.type)        = new Comment_In_3_1[I1, I2, A, A] {}
//  def contains(in: ?.type) = new Comment_In_3_1[I1, I2, A, A] {}
//}
//
//trait Comment_In_2_2[I1, I2, A, B] extends Comment with In_2_2[I1, I2, A, B] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_2_3[I1, I2, A, B, Long]   {}) with Comment_In_2_3[I1, I2, A, B, Long]   {}
//  lazy val author = new author (this, new Comment_In_2_3[I1, I2, A, B, Long]   {}) with Comment_In_2_3[I1, I2, A, B, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_2_3[I1, I2, A, B, String] {}) with Comment_In_2_3[I1, I2, A, B, String] {}
//  def Author = new OneRef[Comment, User] with User_In_2_2[I1, I2, A, B]
//  def apply(in: ?.type)    = new Comment_In_3_1[I1, I2, B, A] {}
//  def apply(in: ?!.type)   = new Comment_In_3_2[I1, I2, B, A, B] {}
//  def <(in: ?.type)        = new Comment_In_3_2[I1, I2, B, A, B] {}
//  def contains(in: ?.type) = new Comment_In_3_2[I1, I2, B, A, B] {}
//}
//
//trait Comment_In_2_3[I1, I2, A, B, C] extends Comment with In_2_3[I1, I2, A, B, C] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_2_4[I1, I2, A, B, C, Long]   {}) with Comment_In_2_4[I1, I2, A, B, C, Long]   {}
//  lazy val author = new author (this, new Comment_In_2_4[I1, I2, A, B, C, Long]   {}) with Comment_In_2_4[I1, I2, A, B, C, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_2_4[I1, I2, A, B, C, String] {}) with Comment_In_2_4[I1, I2, A, B, C, String] {}
//  def Author = new OneRef[Comment, User] with User_In_2_3[I1, I2, A, B, C]
//  def apply(in: ?.type)    = new Comment_In_3_2[I1, I2, C, A, B] {}
//  def apply(in: ?!.type)   = new Comment_In_3_3[I1, I2, C, A, B, C] {}
//  def <(in: ?.type)        = new Comment_In_3_3[I1, I2, C, A, B, C] {}
//  def contains(in: ?.type) = new Comment_In_3_3[I1, I2, C, A, B, C] {}
//}
//
//trait Comment_In_2_4[I1, I2, A, B, C, D] extends Comment with In_2_4[I1, I2, A, B, C, D] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_2_5[I1, I2, A, B, C, D, Long]   {}) with Comment_In_2_5[I1, I2, A, B, C, D, Long]   {}
//  lazy val author = new author (this, new Comment_In_2_5[I1, I2, A, B, C, D, Long]   {}) with Comment_In_2_5[I1, I2, A, B, C, D, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_2_5[I1, I2, A, B, C, D, String] {}) with Comment_In_2_5[I1, I2, A, B, C, D, String] {}
//  def Author = new OneRef[Comment, User] with User_In_2_4[I1, I2, A, B, C, D]
//  def apply(in: ?.type)    = new Comment_In_3_3[I1, I2, D, A, B, C] {}
//  def apply(in: ?!.type)   = new Comment_In_3_4[I1, I2, D, A, B, C, D] {}
//  def <(in: ?.type)        = new Comment_In_3_4[I1, I2, D, A, B, C, D] {}
//  def contains(in: ?.type) = new Comment_In_3_4[I1, I2, D, A, B, C, D] {}
//}
//
//trait Comment_In_2_5[I1, I2, A, B, C, D, E] extends Comment with In_2_5[I1, I2, A, B, C, D, E] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_2_6[I1, I2, A, B, C, D, E, Long]   {}) with Comment_In_2_6[I1, I2, A, B, C, D, E, Long]   {}
//  lazy val author = new author (this, new Comment_In_2_6[I1, I2, A, B, C, D, E, Long]   {}) with Comment_In_2_6[I1, I2, A, B, C, D, E, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_2_6[I1, I2, A, B, C, D, E, String] {}) with Comment_In_2_6[I1, I2, A, B, C, D, E, String] {}
//  def Author = new OneRef[Comment, User] with User_In_2_5[I1, I2, A, B, C, D, E]
//  def apply(in: ?.type)    = new Comment_In_3_4[I1, I2, E, A, B, C, D] {}
//  def apply(in: ?!.type)   = new Comment_In_3_5[I1, I2, E, A, B, C, D, E] {}
//  def <(in: ?.type)        = new Comment_In_3_5[I1, I2, E, A, B, C, D, E] {}
//  def contains(in: ?.type) = new Comment_In_3_5[I1, I2, E, A, B, C, D, E] {}
//}
//
//trait Comment_In_2_6[I1, I2, A, B, C, D, E, F] extends Comment with In_2_6[I1, I2, A, B, C, D, E, F] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_2_7[I1, I2, A, B, C, D, E, F, Long]   {}) with Comment_In_2_7[I1, I2, A, B, C, D, E, F, Long]   {}
//  lazy val author = new author (this, new Comment_In_2_7[I1, I2, A, B, C, D, E, F, Long]   {}) with Comment_In_2_7[I1, I2, A, B, C, D, E, F, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_2_7[I1, I2, A, B, C, D, E, F, String] {}) with Comment_In_2_7[I1, I2, A, B, C, D, E, F, String] {}
//  def Author = new OneRef[Comment, User] with User_In_2_6[I1, I2, A, B, C, D, E, F]
//  def apply(in: ?.type)    = new Comment_In_3_5[I1, I2, F, A, B, C, D, E] {}
//  def apply(in: ?!.type)   = new Comment_In_3_6[I1, I2, F, A, B, C, D, E, F] {}
//  def <(in: ?.type)        = new Comment_In_3_6[I1, I2, F, A, B, C, D, E, F] {}
//  def contains(in: ?.type) = new Comment_In_3_6[I1, I2, F, A, B, C, D, E, F] {}
//}
//
//trait Comment_In_2_7[I1, I2, A, B, C, D, E, F, G] extends Comment with In_2_7[I1, I2, A, B, C, D, E, F, G] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_2_8[I1, I2, A, B, C, D, E, F, G, Long]   {}) with Comment_In_2_8[I1, I2, A, B, C, D, E, F, G, Long]   {}
//  lazy val author = new author (this, new Comment_In_2_8[I1, I2, A, B, C, D, E, F, G, Long]   {}) with Comment_In_2_8[I1, I2, A, B, C, D, E, F, G, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_2_8[I1, I2, A, B, C, D, E, F, G, String] {}) with Comment_In_2_8[I1, I2, A, B, C, D, E, F, G, String] {}
//  def Author = new OneRef[Comment, User] with User_In_2_7[I1, I2, A, B, C, D, E, F, G]
//  def apply(in: ?.type)    = new Comment_In_3_6[I1, I2, G, A, B, C, D, E, F] {}
//  def apply(in: ?!.type)   = new Comment_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {}
//  def <(in: ?.type)        = new Comment_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {}
//  def contains(in: ?.type) = new Comment_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {}
//}
//
//trait Comment_In_2_8[I1, I2, A, B, C, D, E, F, G, H] extends Comment with In_2_8[I1, I2, A, B, C, D, E, F, G, H]
//
//
///********* Input molecules awaiting 3 inputs *******************************/
//
//trait Comment_In_3_0[I1, I2, I3] extends Comment with In_3_0[I1, I2, I3] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_3_1[I1, I2, I3, Long]   {}) with Comment_In_3_1[I1, I2, I3, Long]   {}
//  lazy val author = new author (this, new Comment_In_3_1[I1, I2, I3, Long]   {}) with Comment_In_3_1[I1, I2, I3, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_3_1[I1, I2, I3, String] {}) with Comment_In_3_1[I1, I2, I3, String] {}
//  def Author = new OneRef[Comment, User] with User_In_3_0[I1, I2, I3]
//}
//
//trait Comment_In_3_1[I1, I2, I3, A] extends Comment with In_3_1[I1, I2, I3, A] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_3_2[I1, I2, I3, A, Long]   {}) with Comment_In_3_2[I1, I2, I3, A, Long]   {}
//  lazy val author = new author (this, new Comment_In_3_2[I1, I2, I3, A, Long]   {}) with Comment_In_3_2[I1, I2, I3, A, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_3_2[I1, I2, I3, A, String] {}) with Comment_In_3_2[I1, I2, I3, A, String] {}
//  def Author = new OneRef[Comment, User] with User_In_3_1[I1, I2, I3, A]
//}
//
//trait Comment_In_3_2[I1, I2, I3, A, B] extends Comment with In_3_2[I1, I2, I3, A, B] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_3_3[I1, I2, I3, A, B, Long]   {}) with Comment_In_3_3[I1, I2, I3, A, B, Long]   {}
//  lazy val author = new author (this, new Comment_In_3_3[I1, I2, I3, A, B, Long]   {}) with Comment_In_3_3[I1, I2, I3, A, B, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_3_3[I1, I2, I3, A, B, String] {}) with Comment_In_3_3[I1, I2, I3, A, B, String] {}
//  def Author = new OneRef[Comment, User] with User_In_3_2[I1, I2, I3, A, B]
//}
//
//trait Comment_In_3_3[I1, I2, I3, A, B, C] extends Comment with In_3_3[I1, I2, I3, A, B, C] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_3_4[I1, I2, I3, A, B, C, Long]   {}) with Comment_In_3_4[I1, I2, I3, A, B, C, Long]   {}
//  lazy val author = new author (this, new Comment_In_3_4[I1, I2, I3, A, B, C, Long]   {}) with Comment_In_3_4[I1, I2, I3, A, B, C, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_3_4[I1, I2, I3, A, B, C, String] {}) with Comment_In_3_4[I1, I2, I3, A, B, C, String] {}
//  def Author = new OneRef[Comment, User] with User_In_3_3[I1, I2, I3, A, B, C]
//}
//
//trait Comment_In_3_4[I1, I2, I3, A, B, C, D] extends Comment with In_3_4[I1, I2, I3, A, B, C, D] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_3_5[I1, I2, I3, A, B, C, D, Long]   {}) with Comment_In_3_5[I1, I2, I3, A, B, C, D, Long]   {}
//  lazy val author = new author (this, new Comment_In_3_5[I1, I2, I3, A, B, C, D, Long]   {}) with Comment_In_3_5[I1, I2, I3, A, B, C, D, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_3_5[I1, I2, I3, A, B, C, D, String] {}) with Comment_In_3_5[I1, I2, I3, A, B, C, D, String] {}
//  def Author = new OneRef[Comment, User] with User_In_3_4[I1, I2, I3, A, B, C, D]
//}
//
//trait Comment_In_3_5[I1, I2, I3, A, B, C, D, E] extends Comment with In_3_5[I1, I2, I3, A, B, C, D, E] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_3_6[I1, I2, I3, A, B, C, D, E, Long]   {}) with Comment_In_3_6[I1, I2, I3, A, B, C, D, E, Long]   {}
//  lazy val author = new author (this, new Comment_In_3_6[I1, I2, I3, A, B, C, D, E, Long]   {}) with Comment_In_3_6[I1, I2, I3, A, B, C, D, E, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_3_6[I1, I2, I3, A, B, C, D, E, String] {}) with Comment_In_3_6[I1, I2, I3, A, B, C, D, E, String] {}
//  def Author = new OneRef[Comment, User] with User_In_3_5[I1, I2, I3, A, B, C, D, E]
//}
//
//trait Comment_In_3_6[I1, I2, I3, A, B, C, D, E, F] extends Comment with In_3_6[I1, I2, I3, A, B, C, D, E, F] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_3_7[I1, I2, I3, A, B, C, D, E, F, Long]   {}) with Comment_In_3_7[I1, I2, I3, A, B, C, D, E, F, Long]   {}
//  lazy val author = new author (this, new Comment_In_3_7[I1, I2, I3, A, B, C, D, E, F, Long]   {}) with Comment_In_3_7[I1, I2, I3, A, B, C, D, E, F, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_3_7[I1, I2, I3, A, B, C, D, E, F, String] {}) with Comment_In_3_7[I1, I2, I3, A, B, C, D, E, F, String] {}
//  def Author = new OneRef[Comment, User] with User_In_3_6[I1, I2, I3, A, B, C, D, E, F]
//}
//
//trait Comment_In_3_7[I1, I2, I3, A, B, C, D, E, F, G] extends Comment with In_3_7[I1, I2, I3, A, B, C, D, E, F, G] {
//  import Comment._
//  lazy val e    = new e    (this, new Comment_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Long]   {}) with Comment_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Long]   {}
//  lazy val author = new author (this, new Comment_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Long]   {}) with Comment_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Long]   {}
//  lazy val text   = new text   (this, new Comment_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String] {}) with Comment_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String] {}
//  def Author = new OneRef[Comment, User] with User_In_3_7[I1, I2, I3, A, B, C, D, E, F, G]
//}
//
//trait Comment_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H] extends Comment with In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H]
