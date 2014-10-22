//import molecule.{maybe, count}
//import molecule.util.dsl.schemaDSL.ManyRef
//import molecule.util.dsl.schemaDSL.NS.e
//import molecule.examples.dayOfDatomic.dsl.socialNews._
//import molecule.out.{Molecule_1, Molecule_0}
///*
//* AUTO-GENERATED CODE - DO NOT CHANGE!
//*
//* Manual changes to this file will likely break molecules!
//* Instead, change the molecule definition files and recompile your project with `sbt compile`.
//*/
//package molecule.examples.dayOfDatomic.dsl.socialNews
//import molecule._
//import molecule.util.dsl.schemaDSL._
//import molecule.in._
//import molecule.out._
//
//
//trait User
//object User extends User with User_0 {
//  class firstName    [Ns](ns: Ns) extends OneString   (ns) with Indexed
////  class firstName    [Ns1, Ns2](ns1: Ns1, ns2: Ns2) extends OneString   (ns1, ns2) with Indexed
//  class lastName     [Ns1, Ns2](ns1: Ns1, ns2: Ns2) extends OneString   (ns1, ns2) with Indexed
//  class email        [Ns1, Ns2](ns1: Ns1, ns2: Ns2) extends OneString   (ns1, ns2) with UniqueIdentity
//  class passwordHash [Ns1, Ns2](ns1: Ns1, ns2: Ns2) extends OneString   (ns1, ns2)
//  class upVotes      [Ns1, Ns2](ns1: Ns1, ns2: Ns2) extends ManyRefAttr (ns1, ns2)
//}
//
//trait User_0 extends User with Molecule_0 {
//  import User._
//  def apply(e: Long) = this
//  lazy val e          = new e          (this, new User_1[Long]      {}) with User_1[Long]      {}
//  lazy val firstName    = new firstName    (new User_1[String]    {}) with User_1[String]    {}
//  lazy val firstName_   = new firstName    (this) with User_0    {}
////  lazy val firstName    = new firstName    (this, new User_1[String]    {}) with User_1[String]    {}
////  lazy val firstName_   = new firstName    (this, new User_1[String]    {}) with User_1[String]    {}
//  lazy val lastName     = new lastName     (this, new User_1[String]    {}) with User_1[String]    {}
//  lazy val email        = new email        (this, new User_1[String]    {}) with User_1[String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_1[String]    {}) with User_1[String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_1[Set[Long]] {}) with User_1[Set[Long]] {}
//  def UpVotes                                   = new ManyRef[User, Story] with Story_0
//  def upVotes[T1]    (upVotes: Story_1[T1])     = new ManyRef[User, Story] with User_1[Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_1[Seq[(T1, T2)]]
//
//  def _Comments = new Comment_0 {}
//
//  def as(s: Symbol) = this
//  def ~(a: Any) = this
//
//}
//
//trait User_1[A] extends User with Molecule_1[A] {
//  import User._
//  lazy val e          = new e          (this, new User_2[A, Long]      {}) with User_2[A, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_2[A, String]    {}) with User_2[A, String]    {}
//  lazy val lastName     = new lastName     (this, new User_2[A, String]    {}) with User_2[A, String]    {}
//  lazy val email        = new email        (this, new User_2[A, String]    {}) with User_2[A, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_2[A, String]    {}) with User_2[A, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_2[A, Set[Long]] {}) with User_2[A, Set[Long]] {}
//  def UpVotes                                   = new ManyRef[User, Story] with Story_1[A]
//  def upVotes[T1]    (upVotes: Story_1[T1])     = new ManyRef[User, Story] with User_2[A, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_2[A, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_1_0[A] {}
//  def apply(in: ?!.type)   = new User_In_1_1[A, A] {}
//  def <(in: ?.type)        = new User_In_1_1[A, A] {}
//  def contains(in: ?.type) = new User_In_1_1[A, A] {}
//  def apply(m: maybe.type) = new User_1[A] {}
//  def apply(c: count.type) = new User_1[Int] {}
//
//  def apply(anyValue: Any => Any) = 7
//
//
//  def as(s: Symbol) = this
//  def apply(e: Long) = this
//
//  def ~(a: Any) = new User_0 {}
//    def <-:(m: Any) = this
//
//  def _Comments = new Comment_1[A] {}
//
//
////  def apply(s: Symbol) = new User_0 {}
//
//
//}
//
//trait User_2[A, B] extends User with Molecule_2[A, B] {
//  import User._
//  lazy val e          = new e          (this, new User_3[A, B, Long]      {}) with User_3[A, B, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_3[A, B, String]    {}) with User_3[A, B, String]    {}
//  lazy val lastName     = new lastName     (this, new User_3[A, B, String]    {}) with User_3[A, B, String]    {}
//  lazy val email        = new email        (this, new User_3[A, B, String]    {}) with User_3[A, B, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_3[A, B, String]    {}) with User_3[A, B, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_3[A, B, Set[Long]] {}) with User_3[A, B, Set[Long]] {}
//  def UpVotes                                   = new ManyRef[User, Story] with Story_2[A, B]
//  def upVotes[T1]    (upVotes: Story_1[T1])     = new ManyRef[User, Story] with User_3[A, B, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_3[A, B, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_1_1[B, A] {}
//  def apply(in: ?!.type)   = new User_In_1_2[B, A, B] {}
//  def <(in: ?.type)        = new User_In_1_2[B, A, B] {}
//  def contains(in: ?.type) = new User_In_1_2[B, A, B] {}
//  def apply(m: maybe.type) = new User_2[A, B] {}
//  def apply(c: count.type) = new User_2[A, Int] {}
//}
//
//trait User_3[A, B, C] extends User with Molecule_3[A, B, C] {
//  import User._
//  lazy val e          = new e          (this, new User_4[A, B, C, Long]      {}) with User_4[A, B, C, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_4[A, B, C, String]    {}) with User_4[A, B, C, String]    {}
//  lazy val lastName     = new lastName     (this, new User_4[A, B, C, String]    {}) with User_4[A, B, C, String]    {}
//  lazy val email        = new email        (this, new User_4[A, B, C, String]    {}) with User_4[A, B, C, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_4[A, B, C, String]    {}) with User_4[A, B, C, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_4[A, B, C, Set[Long]] {}) with User_4[A, B, C, Set[Long]] {}
//  def UpVotes                                   = new ManyRef[User, Story] with Story_3[A, B, C]
//  def upVotes[T1]    (upVotes: Story_1[T1])     = new ManyRef[User, Story] with User_4[A, B, C, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_4[A, B, C, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_1_2[C, A, B] {}
//  def apply(in: ?!.type)   = new User_In_1_3[C, A, B, C] {}
//  def <(in: ?.type)        = new User_In_1_3[C, A, B, C] {}
//  def contains(in: ?.type) = new User_In_1_3[C, A, B, C] {}
//  def apply(m: maybe.type) = new User_3[A, B, C] {}
//  def apply(c: count.type) = new User_3[A, B, Int] {}
//}
//
//trait User_4[A, B, C, D] extends User with Molecule_4[A, B, C, D] {
//  import User._
//  lazy val e          = new e          (this, new User_5[A, B, C, D, Long]      {}) with User_5[A, B, C, D, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_5[A, B, C, D, String]    {}) with User_5[A, B, C, D, String]    {}
//  lazy val lastName     = new lastName     (this, new User_5[A, B, C, D, String]    {}) with User_5[A, B, C, D, String]    {}
//  lazy val email        = new email        (this, new User_5[A, B, C, D, String]    {}) with User_5[A, B, C, D, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_5[A, B, C, D, String]    {}) with User_5[A, B, C, D, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_5[A, B, C, D, Set[Long]] {}) with User_5[A, B, C, D, Set[Long]] {}
//  def UpVotes                                   = new ManyRef[User, Story] with Story_4[A, B, C, D]
//  def upVotes[T1]    (upVotes: Story_1[T1])     = new ManyRef[User, Story] with User_5[A, B, C, D, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_5[A, B, C, D, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_1_3[D, A, B, C] {}
//  def apply(in: ?!.type)   = new User_In_1_4[D, A, B, C, D] {}
//  def <(in: ?.type)        = new User_In_1_4[D, A, B, C, D] {}
//  def contains(in: ?.type) = new User_In_1_4[D, A, B, C, D] {}
//  def apply(m: maybe.type) = new User_4[A, B, C, D] {}
//  def apply(c: count.type) = new User_4[A, B, C, Int] {}
//}
//
//trait User_5[A, B, C, D, E] extends User with Molecule_5[A, B, C, D, E] {
//  import User._
//  lazy val e          = new e          (this, new User_6[A, B, C, D, E, Long]      {}) with User_6[A, B, C, D, E, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_6[A, B, C, D, E, String]    {}) with User_6[A, B, C, D, E, String]    {}
//  lazy val lastName     = new lastName     (this, new User_6[A, B, C, D, E, String]    {}) with User_6[A, B, C, D, E, String]    {}
//  lazy val email        = new email        (this, new User_6[A, B, C, D, E, String]    {}) with User_6[A, B, C, D, E, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_6[A, B, C, D, E, String]    {}) with User_6[A, B, C, D, E, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_6[A, B, C, D, E, Set[Long]] {}) with User_6[A, B, C, D, E, Set[Long]] {}
//  def UpVotes                                   = new ManyRef[User, Story] with Story_5[A, B, C, D, E]
//  def upVotes[T1]    (upVotes: Story_1[T1])     = new ManyRef[User, Story] with User_6[A, B, C, D, E, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_6[A, B, C, D, E, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_1_4[E, A, B, C, D] {}
//  def apply(in: ?!.type)   = new User_In_1_5[E, A, B, C, D, E] {}
//  def <(in: ?.type)        = new User_In_1_5[E, A, B, C, D, E] {}
//  def contains(in: ?.type) = new User_In_1_5[E, A, B, C, D, E] {}
//  def apply(m: maybe.type) = new User_5[A, B, C, D, E] {}
//  def apply(c: count.type) = new User_5[A, B, C, D, Int] {}
//}
//
//trait User_6[A, B, C, D, E, F] extends User with Molecule_6[A, B, C, D, E, F] {
//  import User._
//  lazy val e          = new e          (this, new User_7[A, B, C, D, E, F, Long]      {}) with User_7[A, B, C, D, E, F, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_7[A, B, C, D, E, F, String]    {}) with User_7[A, B, C, D, E, F, String]    {}
//  lazy val lastName     = new lastName     (this, new User_7[A, B, C, D, E, F, String]    {}) with User_7[A, B, C, D, E, F, String]    {}
//  lazy val email        = new email        (this, new User_7[A, B, C, D, E, F, String]    {}) with User_7[A, B, C, D, E, F, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_7[A, B, C, D, E, F, String]    {}) with User_7[A, B, C, D, E, F, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_7[A, B, C, D, E, F, Set[Long]] {}) with User_7[A, B, C, D, E, F, Set[Long]] {}
//  def UpVotes                                   = new ManyRef[User, Story] with Story_6[A, B, C, D, E, F]
//  def upVotes[T1]    (upVotes: Story_1[T1])     = new ManyRef[User, Story] with User_7[A, B, C, D, E, F, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_7[A, B, C, D, E, F, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_1_5[F, A, B, C, D, E] {}
//  def apply(in: ?!.type)   = new User_In_1_6[F, A, B, C, D, E, F] {}
//  def <(in: ?.type)        = new User_In_1_6[F, A, B, C, D, E, F] {}
//  def contains(in: ?.type) = new User_In_1_6[F, A, B, C, D, E, F] {}
//  def apply(m: maybe.type) = new User_6[A, B, C, D, E, F] {}
//  def apply(c: count.type) = new User_6[A, B, C, D, E, Int] {}
//}
//
//trait User_7[A, B, C, D, E, F, G] extends User with Molecule_7[A, B, C, D, E, F, G] {
//  import User._
//  lazy val e          = new e          (this, new User_8[A, B, C, D, E, F, G, Long]      {}) with User_8[A, B, C, D, E, F, G, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_8[A, B, C, D, E, F, G, String]    {}) with User_8[A, B, C, D, E, F, G, String]    {}
//  lazy val lastName     = new lastName     (this, new User_8[A, B, C, D, E, F, G, String]    {}) with User_8[A, B, C, D, E, F, G, String]    {}
//  lazy val email        = new email        (this, new User_8[A, B, C, D, E, F, G, String]    {}) with User_8[A, B, C, D, E, F, G, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_8[A, B, C, D, E, F, G, String]    {}) with User_8[A, B, C, D, E, F, G, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_8[A, B, C, D, E, F, G, Set[Long]] {}) with User_8[A, B, C, D, E, F, G, Set[Long]] {}
//  def UpVotes                                   = new ManyRef[User, Story] with Story_7[A, B, C, D, E, F, G]
//  def upVotes[T1]    (upVotes: Story_1[T1])     = new ManyRef[User, Story] with User_8[A, B, C, D, E, F, G, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_8[A, B, C, D, E, F, G, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_1_6[G, A, B, C, D, E, F] {}
//  def apply(in: ?!.type)   = new User_In_1_7[G, A, B, C, D, E, F, G] {}
//  def <(in: ?.type)        = new User_In_1_7[G, A, B, C, D, E, F, G] {}
//  def contains(in: ?.type) = new User_In_1_7[G, A, B, C, D, E, F, G] {}
//  def apply(m: maybe.type) = new User_7[A, B, C, D, E, F, G] {}
//  def apply(c: count.type) = new User_7[A, B, C, D, E, F, Int] {}
//}
//
//trait User_8[A, B, C, D, E, F, G, H] extends User with Molecule_8[A, B, C, D, E, F, G, H]
//
//
///********* Input molecules awaiting 1 input *******************************/
//
//trait User_In_1_0[I1] extends User with In_1_0[I1] {
//  import User._
//  lazy val e          = new e          (this, new User_In_1_1[I1, Long]      {}) with User_In_1_1[I1, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_1_1[I1, String]    {}) with User_In_1_1[I1, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_1_1[I1, String]    {}) with User_In_1_1[I1, String]    {}
//  lazy val email        = new email        (this, new User_In_1_1[I1, String]    {}) with User_In_1_1[I1, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_1_1[I1, String]    {}) with User_In_1_1[I1, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_1_1[I1, Set[Long]] {}) with User_In_1_1[I1, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_1_0[I1]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_1_1[I1, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_1_1[I1, Seq[(T1, T2)]]
//}
//
//trait User_In_1_1[I1, A] extends User with In_1_1[I1, A] {
//  import User._
//  lazy val e          = new e          (this, new User_In_1_2[I1, A, Long]      {}) with User_In_1_2[I1, A, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_1_2[I1, A, String]    {}) with User_In_1_2[I1, A, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_1_2[I1, A, String]    {}) with User_In_1_2[I1, A, String]    {}
//  lazy val email        = new email        (this, new User_In_1_2[I1, A, String]    {}) with User_In_1_2[I1, A, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_1_2[I1, A, String]    {}) with User_In_1_2[I1, A, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_1_2[I1, A, Set[Long]] {}) with User_In_1_2[I1, A, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_1_1[I1, A]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_1_2[I1, A, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_1_2[I1, A, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_2_0[I1, A] {}
//  def apply(in: ?!.type)   = new User_In_2_1[I1, A, A] {}
//  def <(in: ?.type)        = new User_In_2_1[I1, A, A] {}
//  def contains(in: ?.type) = new User_In_2_1[I1, A, A] {}
//}
//
//trait User_In_1_2[I1, A, B] extends User with In_1_2[I1, A, B] {
//  import User._
//  lazy val e          = new e          (this, new User_In_1_3[I1, A, B, Long]      {}) with User_In_1_3[I1, A, B, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_1_3[I1, A, B, String]    {}) with User_In_1_3[I1, A, B, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_1_3[I1, A, B, String]    {}) with User_In_1_3[I1, A, B, String]    {}
//  lazy val email        = new email        (this, new User_In_1_3[I1, A, B, String]    {}) with User_In_1_3[I1, A, B, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_1_3[I1, A, B, String]    {}) with User_In_1_3[I1, A, B, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_1_3[I1, A, B, Set[Long]] {}) with User_In_1_3[I1, A, B, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_1_2[I1, A, B]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_1_3[I1, A, B, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_1_3[I1, A, B, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_2_1[I1, B, A] {}
//  def apply(in: ?!.type)   = new User_In_2_2[I1, B, A, B] {}
//  def <(in: ?.type)        = new User_In_2_2[I1, B, A, B] {}
//  def contains(in: ?.type) = new User_In_2_2[I1, B, A, B] {}
//}
//
//trait User_In_1_3[I1, A, B, C] extends User with In_1_3[I1, A, B, C] {
//  import User._
//  lazy val e          = new e          (this, new User_In_1_4[I1, A, B, C, Long]      {}) with User_In_1_4[I1, A, B, C, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_1_4[I1, A, B, C, String]    {}) with User_In_1_4[I1, A, B, C, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_1_4[I1, A, B, C, String]    {}) with User_In_1_4[I1, A, B, C, String]    {}
//  lazy val email        = new email        (this, new User_In_1_4[I1, A, B, C, String]    {}) with User_In_1_4[I1, A, B, C, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_1_4[I1, A, B, C, String]    {}) with User_In_1_4[I1, A, B, C, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_1_4[I1, A, B, C, Set[Long]] {}) with User_In_1_4[I1, A, B, C, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_1_3[I1, A, B, C]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_1_4[I1, A, B, C, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_1_4[I1, A, B, C, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_2_2[I1, C, A, B] {}
//  def apply(in: ?!.type)   = new User_In_2_3[I1, C, A, B, C] {}
//  def <(in: ?.type)        = new User_In_2_3[I1, C, A, B, C] {}
//  def contains(in: ?.type) = new User_In_2_3[I1, C, A, B, C] {}
//}
//
//trait User_In_1_4[I1, A, B, C, D] extends User with In_1_4[I1, A, B, C, D] {
//  import User._
//  lazy val e          = new e          (this, new User_In_1_5[I1, A, B, C, D, Long]      {}) with User_In_1_5[I1, A, B, C, D, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_1_5[I1, A, B, C, D, String]    {}) with User_In_1_5[I1, A, B, C, D, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_1_5[I1, A, B, C, D, String]    {}) with User_In_1_5[I1, A, B, C, D, String]    {}
//  lazy val email        = new email        (this, new User_In_1_5[I1, A, B, C, D, String]    {}) with User_In_1_5[I1, A, B, C, D, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_1_5[I1, A, B, C, D, String]    {}) with User_In_1_5[I1, A, B, C, D, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_1_5[I1, A, B, C, D, Set[Long]] {}) with User_In_1_5[I1, A, B, C, D, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_1_4[I1, A, B, C, D]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_1_5[I1, A, B, C, D, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_1_5[I1, A, B, C, D, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_2_3[I1, D, A, B, C] {}
//  def apply(in: ?!.type)   = new User_In_2_4[I1, D, A, B, C, D] {}
//  def <(in: ?.type)        = new User_In_2_4[I1, D, A, B, C, D] {}
//  def contains(in: ?.type) = new User_In_2_4[I1, D, A, B, C, D] {}
//}
//
//trait User_In_1_5[I1, A, B, C, D, E] extends User with In_1_5[I1, A, B, C, D, E] {
//  import User._
//  lazy val e          = new e          (this, new User_In_1_6[I1, A, B, C, D, E, Long]      {}) with User_In_1_6[I1, A, B, C, D, E, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_1_6[I1, A, B, C, D, E, String]    {}) with User_In_1_6[I1, A, B, C, D, E, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_1_6[I1, A, B, C, D, E, String]    {}) with User_In_1_6[I1, A, B, C, D, E, String]    {}
//  lazy val email        = new email        (this, new User_In_1_6[I1, A, B, C, D, E, String]    {}) with User_In_1_6[I1, A, B, C, D, E, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_1_6[I1, A, B, C, D, E, String]    {}) with User_In_1_6[I1, A, B, C, D, E, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_1_6[I1, A, B, C, D, E, Set[Long]] {}) with User_In_1_6[I1, A, B, C, D, E, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_1_5[I1, A, B, C, D, E]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_1_6[I1, A, B, C, D, E, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_1_6[I1, A, B, C, D, E, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_2_4[I1, E, A, B, C, D] {}
//  def apply(in: ?!.type)   = new User_In_2_5[I1, E, A, B, C, D, E] {}
//  def <(in: ?.type)        = new User_In_2_5[I1, E, A, B, C, D, E] {}
//  def contains(in: ?.type) = new User_In_2_5[I1, E, A, B, C, D, E] {}
//}
//
//trait User_In_1_6[I1, A, B, C, D, E, F] extends User with In_1_6[I1, A, B, C, D, E, F] {
//  import User._
//  lazy val e          = new e          (this, new User_In_1_7[I1, A, B, C, D, E, F, Long]      {}) with User_In_1_7[I1, A, B, C, D, E, F, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_1_7[I1, A, B, C, D, E, F, String]    {}) with User_In_1_7[I1, A, B, C, D, E, F, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_1_7[I1, A, B, C, D, E, F, String]    {}) with User_In_1_7[I1, A, B, C, D, E, F, String]    {}
//  lazy val email        = new email        (this, new User_In_1_7[I1, A, B, C, D, E, F, String]    {}) with User_In_1_7[I1, A, B, C, D, E, F, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_1_7[I1, A, B, C, D, E, F, String]    {}) with User_In_1_7[I1, A, B, C, D, E, F, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_1_7[I1, A, B, C, D, E, F, Set[Long]] {}) with User_In_1_7[I1, A, B, C, D, E, F, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_1_6[I1, A, B, C, D, E, F]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_1_7[I1, A, B, C, D, E, F, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_1_7[I1, A, B, C, D, E, F, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_2_5[I1, F, A, B, C, D, E] {}
//  def apply(in: ?!.type)   = new User_In_2_6[I1, F, A, B, C, D, E, F] {}
//  def <(in: ?.type)        = new User_In_2_6[I1, F, A, B, C, D, E, F] {}
//  def contains(in: ?.type) = new User_In_2_6[I1, F, A, B, C, D, E, F] {}
//}
//
//trait User_In_1_7[I1, A, B, C, D, E, F, G] extends User with In_1_7[I1, A, B, C, D, E, F, G] {
//  import User._
//  lazy val e          = new e          (this, new User_In_1_8[I1, A, B, C, D, E, F, G, Long]      {}) with User_In_1_8[I1, A, B, C, D, E, F, G, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_1_8[I1, A, B, C, D, E, F, G, String]    {}) with User_In_1_8[I1, A, B, C, D, E, F, G, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_1_8[I1, A, B, C, D, E, F, G, String]    {}) with User_In_1_8[I1, A, B, C, D, E, F, G, String]    {}
//  lazy val email        = new email        (this, new User_In_1_8[I1, A, B, C, D, E, F, G, String]    {}) with User_In_1_8[I1, A, B, C, D, E, F, G, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_1_8[I1, A, B, C, D, E, F, G, String]    {}) with User_In_1_8[I1, A, B, C, D, E, F, G, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_1_8[I1, A, B, C, D, E, F, G, Set[Long]] {}) with User_In_1_8[I1, A, B, C, D, E, F, G, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_1_7[I1, A, B, C, D, E, F, G]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_1_8[I1, A, B, C, D, E, F, G, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_1_8[I1, A, B, C, D, E, F, G, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_2_6[I1, G, A, B, C, D, E, F] {}
//  def apply(in: ?!.type)   = new User_In_2_7[I1, G, A, B, C, D, E, F, G] {}
//  def <(in: ?.type)        = new User_In_2_7[I1, G, A, B, C, D, E, F, G] {}
//  def contains(in: ?.type) = new User_In_2_7[I1, G, A, B, C, D, E, F, G] {}
//}
//
//trait User_In_1_8[I1, A, B, C, D, E, F, G, H] extends User with In_1_8[I1, A, B, C, D, E, F, G, H]
//
//
///********* Input molecules awaiting 2 inputs *******************************/
//
//trait User_In_2_0[I1, I2] extends User with In_2_0[I1, I2] {
//  import User._
//  lazy val e          = new e          (this, new User_In_2_1[I1, I2, Long]      {}) with User_In_2_1[I1, I2, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_2_1[I1, I2, String]    {}) with User_In_2_1[I1, I2, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_2_1[I1, I2, String]    {}) with User_In_2_1[I1, I2, String]    {}
//  lazy val email        = new email        (this, new User_In_2_1[I1, I2, String]    {}) with User_In_2_1[I1, I2, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_2_1[I1, I2, String]    {}) with User_In_2_1[I1, I2, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_2_1[I1, I2, Set[Long]] {}) with User_In_2_1[I1, I2, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_2_0[I1, I2]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_2_1[I1, I2, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_2_1[I1, I2, Seq[(T1, T2)]]
//}
//
//trait User_In_2_1[I1, I2, A] extends User with In_2_1[I1, I2, A] {
//  import User._
//  lazy val e          = new e          (this, new User_In_2_2[I1, I2, A, Long]      {}) with User_In_2_2[I1, I2, A, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_2_2[I1, I2, A, String]    {}) with User_In_2_2[I1, I2, A, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_2_2[I1, I2, A, String]    {}) with User_In_2_2[I1, I2, A, String]    {}
//  lazy val email        = new email        (this, new User_In_2_2[I1, I2, A, String]    {}) with User_In_2_2[I1, I2, A, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_2_2[I1, I2, A, String]    {}) with User_In_2_2[I1, I2, A, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_2_2[I1, I2, A, Set[Long]] {}) with User_In_2_2[I1, I2, A, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_2_1[I1, I2, A]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_2_2[I1, I2, A, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_2_2[I1, I2, A, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_3_0[I1, I2, A] {}
//  def apply(in: ?!.type)   = new User_In_3_1[I1, I2, A, A] {}
//  def <(in: ?.type)        = new User_In_3_1[I1, I2, A, A] {}
//  def contains(in: ?.type) = new User_In_3_1[I1, I2, A, A] {}
//}
//
//trait User_In_2_2[I1, I2, A, B] extends User with In_2_2[I1, I2, A, B] {
//  import User._
//  lazy val e          = new e          (this, new User_In_2_3[I1, I2, A, B, Long]      {}) with User_In_2_3[I1, I2, A, B, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_2_3[I1, I2, A, B, String]    {}) with User_In_2_3[I1, I2, A, B, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_2_3[I1, I2, A, B, String]    {}) with User_In_2_3[I1, I2, A, B, String]    {}
//  lazy val email        = new email        (this, new User_In_2_3[I1, I2, A, B, String]    {}) with User_In_2_3[I1, I2, A, B, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_2_3[I1, I2, A, B, String]    {}) with User_In_2_3[I1, I2, A, B, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_2_3[I1, I2, A, B, Set[Long]] {}) with User_In_2_3[I1, I2, A, B, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_2_2[I1, I2, A, B]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_2_3[I1, I2, A, B, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_2_3[I1, I2, A, B, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_3_1[I1, I2, B, A] {}
//  def apply(in: ?!.type)   = new User_In_3_2[I1, I2, B, A, B] {}
//  def <(in: ?.type)        = new User_In_3_2[I1, I2, B, A, B] {}
//  def contains(in: ?.type) = new User_In_3_2[I1, I2, B, A, B] {}
//}
//
//trait User_In_2_3[I1, I2, A, B, C] extends User with In_2_3[I1, I2, A, B, C] {
//  import User._
//  lazy val e          = new e          (this, new User_In_2_4[I1, I2, A, B, C, Long]      {}) with User_In_2_4[I1, I2, A, B, C, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_2_4[I1, I2, A, B, C, String]    {}) with User_In_2_4[I1, I2, A, B, C, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_2_4[I1, I2, A, B, C, String]    {}) with User_In_2_4[I1, I2, A, B, C, String]    {}
//  lazy val email        = new email        (this, new User_In_2_4[I1, I2, A, B, C, String]    {}) with User_In_2_4[I1, I2, A, B, C, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_2_4[I1, I2, A, B, C, String]    {}) with User_In_2_4[I1, I2, A, B, C, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_2_4[I1, I2, A, B, C, Set[Long]] {}) with User_In_2_4[I1, I2, A, B, C, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_2_3[I1, I2, A, B, C]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_2_4[I1, I2, A, B, C, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_2_4[I1, I2, A, B, C, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_3_2[I1, I2, C, A, B] {}
//  def apply(in: ?!.type)   = new User_In_3_3[I1, I2, C, A, B, C] {}
//  def <(in: ?.type)        = new User_In_3_3[I1, I2, C, A, B, C] {}
//  def contains(in: ?.type) = new User_In_3_3[I1, I2, C, A, B, C] {}
//}
//
//trait User_In_2_4[I1, I2, A, B, C, D] extends User with In_2_4[I1, I2, A, B, C, D] {
//  import User._
//  lazy val e          = new e          (this, new User_In_2_5[I1, I2, A, B, C, D, Long]      {}) with User_In_2_5[I1, I2, A, B, C, D, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_2_5[I1, I2, A, B, C, D, String]    {}) with User_In_2_5[I1, I2, A, B, C, D, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_2_5[I1, I2, A, B, C, D, String]    {}) with User_In_2_5[I1, I2, A, B, C, D, String]    {}
//  lazy val email        = new email        (this, new User_In_2_5[I1, I2, A, B, C, D, String]    {}) with User_In_2_5[I1, I2, A, B, C, D, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_2_5[I1, I2, A, B, C, D, String]    {}) with User_In_2_5[I1, I2, A, B, C, D, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_2_5[I1, I2, A, B, C, D, Set[Long]] {}) with User_In_2_5[I1, I2, A, B, C, D, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_2_4[I1, I2, A, B, C, D]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_2_5[I1, I2, A, B, C, D, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_2_5[I1, I2, A, B, C, D, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_3_3[I1, I2, D, A, B, C] {}
//  def apply(in: ?!.type)   = new User_In_3_4[I1, I2, D, A, B, C, D] {}
//  def <(in: ?.type)        = new User_In_3_4[I1, I2, D, A, B, C, D] {}
//  def contains(in: ?.type) = new User_In_3_4[I1, I2, D, A, B, C, D] {}
//}
//
//trait User_In_2_5[I1, I2, A, B, C, D, E] extends User with In_2_5[I1, I2, A, B, C, D, E] {
//  import User._
//  lazy val e          = new e          (this, new User_In_2_6[I1, I2, A, B, C, D, E, Long]      {}) with User_In_2_6[I1, I2, A, B, C, D, E, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_2_6[I1, I2, A, B, C, D, E, String]    {}) with User_In_2_6[I1, I2, A, B, C, D, E, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_2_6[I1, I2, A, B, C, D, E, String]    {}) with User_In_2_6[I1, I2, A, B, C, D, E, String]    {}
//  lazy val email        = new email        (this, new User_In_2_6[I1, I2, A, B, C, D, E, String]    {}) with User_In_2_6[I1, I2, A, B, C, D, E, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_2_6[I1, I2, A, B, C, D, E, String]    {}) with User_In_2_6[I1, I2, A, B, C, D, E, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_2_6[I1, I2, A, B, C, D, E, Set[Long]] {}) with User_In_2_6[I1, I2, A, B, C, D, E, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_2_5[I1, I2, A, B, C, D, E]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_2_6[I1, I2, A, B, C, D, E, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_2_6[I1, I2, A, B, C, D, E, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_3_4[I1, I2, E, A, B, C, D] {}
//  def apply(in: ?!.type)   = new User_In_3_5[I1, I2, E, A, B, C, D, E] {}
//  def <(in: ?.type)        = new User_In_3_5[I1, I2, E, A, B, C, D, E] {}
//  def contains(in: ?.type) = new User_In_3_5[I1, I2, E, A, B, C, D, E] {}
//}
//
//trait User_In_2_6[I1, I2, A, B, C, D, E, F] extends User with In_2_6[I1, I2, A, B, C, D, E, F] {
//  import User._
//  lazy val e          = new e          (this, new User_In_2_7[I1, I2, A, B, C, D, E, F, Long]      {}) with User_In_2_7[I1, I2, A, B, C, D, E, F, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_2_7[I1, I2, A, B, C, D, E, F, String]    {}) with User_In_2_7[I1, I2, A, B, C, D, E, F, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_2_7[I1, I2, A, B, C, D, E, F, String]    {}) with User_In_2_7[I1, I2, A, B, C, D, E, F, String]    {}
//  lazy val email        = new email        (this, new User_In_2_7[I1, I2, A, B, C, D, E, F, String]    {}) with User_In_2_7[I1, I2, A, B, C, D, E, F, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_2_7[I1, I2, A, B, C, D, E, F, String]    {}) with User_In_2_7[I1, I2, A, B, C, D, E, F, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_2_7[I1, I2, A, B, C, D, E, F, Set[Long]] {}) with User_In_2_7[I1, I2, A, B, C, D, E, F, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_2_6[I1, I2, A, B, C, D, E, F]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_2_7[I1, I2, A, B, C, D, E, F, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_2_7[I1, I2, A, B, C, D, E, F, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_3_5[I1, I2, F, A, B, C, D, E] {}
//  def apply(in: ?!.type)   = new User_In_3_6[I1, I2, F, A, B, C, D, E, F] {}
//  def <(in: ?.type)        = new User_In_3_6[I1, I2, F, A, B, C, D, E, F] {}
//  def contains(in: ?.type) = new User_In_3_6[I1, I2, F, A, B, C, D, E, F] {}
//}
//
//trait User_In_2_7[I1, I2, A, B, C, D, E, F, G] extends User with In_2_7[I1, I2, A, B, C, D, E, F, G] {
//  import User._
//  lazy val e          = new e          (this, new User_In_2_8[I1, I2, A, B, C, D, E, F, G, Long]      {}) with User_In_2_8[I1, I2, A, B, C, D, E, F, G, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_2_8[I1, I2, A, B, C, D, E, F, G, String]    {}) with User_In_2_8[I1, I2, A, B, C, D, E, F, G, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_2_8[I1, I2, A, B, C, D, E, F, G, String]    {}) with User_In_2_8[I1, I2, A, B, C, D, E, F, G, String]    {}
//  lazy val email        = new email        (this, new User_In_2_8[I1, I2, A, B, C, D, E, F, G, String]    {}) with User_In_2_8[I1, I2, A, B, C, D, E, F, G, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_2_8[I1, I2, A, B, C, D, E, F, G, String]    {}) with User_In_2_8[I1, I2, A, B, C, D, E, F, G, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_2_8[I1, I2, A, B, C, D, E, F, G, Set[Long]] {}) with User_In_2_8[I1, I2, A, B, C, D, E, F, G, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_2_7[I1, I2, A, B, C, D, E, F, G]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_2_8[I1, I2, A, B, C, D, E, F, G, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_2_8[I1, I2, A, B, C, D, E, F, G, Seq[(T1, T2)]]
//  def apply(in: ?.type)    = new User_In_3_6[I1, I2, G, A, B, C, D, E, F] {}
//  def apply(in: ?!.type)   = new User_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {}
//  def <(in: ?.type)        = new User_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {}
//  def contains(in: ?.type) = new User_In_3_7[I1, I2, G, A, B, C, D, E, F, G] {}
//}
//
//trait User_In_2_8[I1, I2, A, B, C, D, E, F, G, H] extends User with In_2_8[I1, I2, A, B, C, D, E, F, G, H]
//
//
///********* Input molecules awaiting 3 inputs *******************************/
//
//trait User_In_3_0[I1, I2, I3] extends User with In_3_0[I1, I2, I3] {
//  import User._
//  lazy val e          = new e          (this, new User_In_3_1[I1, I2, I3, Long]      {}) with User_In_3_1[I1, I2, I3, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_3_1[I1, I2, I3, String]    {}) with User_In_3_1[I1, I2, I3, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_3_1[I1, I2, I3, String]    {}) with User_In_3_1[I1, I2, I3, String]    {}
//  lazy val email        = new email        (this, new User_In_3_1[I1, I2, I3, String]    {}) with User_In_3_1[I1, I2, I3, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_3_1[I1, I2, I3, String]    {}) with User_In_3_1[I1, I2, I3, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_3_1[I1, I2, I3, Set[Long]] {}) with User_In_3_1[I1, I2, I3, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_3_0[I1, I2, I3]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_3_1[I1, I2, I3, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_3_1[I1, I2, I3, Seq[(T1, T2)]]
//}
//
//trait User_In_3_1[I1, I2, I3, A] extends User with In_3_1[I1, I2, I3, A] {
//  import User._
//  lazy val e          = new e          (this, new User_In_3_2[I1, I2, I3, A, Long]      {}) with User_In_3_2[I1, I2, I3, A, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_3_2[I1, I2, I3, A, String]    {}) with User_In_3_2[I1, I2, I3, A, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_3_2[I1, I2, I3, A, String]    {}) with User_In_3_2[I1, I2, I3, A, String]    {}
//  lazy val email        = new email        (this, new User_In_3_2[I1, I2, I3, A, String]    {}) with User_In_3_2[I1, I2, I3, A, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_3_2[I1, I2, I3, A, String]    {}) with User_In_3_2[I1, I2, I3, A, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_3_2[I1, I2, I3, A, Set[Long]] {}) with User_In_3_2[I1, I2, I3, A, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_3_1[I1, I2, I3, A]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_3_2[I1, I2, I3, A, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_3_2[I1, I2, I3, A, Seq[(T1, T2)]]
//}
//
//trait User_In_3_2[I1, I2, I3, A, B] extends User with In_3_2[I1, I2, I3, A, B] {
//  import User._
//  lazy val e          = new e          (this, new User_In_3_3[I1, I2, I3, A, B, Long]      {}) with User_In_3_3[I1, I2, I3, A, B, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_3_3[I1, I2, I3, A, B, String]    {}) with User_In_3_3[I1, I2, I3, A, B, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_3_3[I1, I2, I3, A, B, String]    {}) with User_In_3_3[I1, I2, I3, A, B, String]    {}
//  lazy val email        = new email        (this, new User_In_3_3[I1, I2, I3, A, B, String]    {}) with User_In_3_3[I1, I2, I3, A, B, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_3_3[I1, I2, I3, A, B, String]    {}) with User_In_3_3[I1, I2, I3, A, B, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_3_3[I1, I2, I3, A, B, Set[Long]] {}) with User_In_3_3[I1, I2, I3, A, B, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_3_2[I1, I2, I3, A, B]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_3_3[I1, I2, I3, A, B, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_3_3[I1, I2, I3, A, B, Seq[(T1, T2)]]
//}
//
//trait User_In_3_3[I1, I2, I3, A, B, C] extends User with In_3_3[I1, I2, I3, A, B, C] {
//  import User._
//  lazy val e          = new e          (this, new User_In_3_4[I1, I2, I3, A, B, C, Long]      {}) with User_In_3_4[I1, I2, I3, A, B, C, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_3_4[I1, I2, I3, A, B, C, String]    {}) with User_In_3_4[I1, I2, I3, A, B, C, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_3_4[I1, I2, I3, A, B, C, String]    {}) with User_In_3_4[I1, I2, I3, A, B, C, String]    {}
//  lazy val email        = new email        (this, new User_In_3_4[I1, I2, I3, A, B, C, String]    {}) with User_In_3_4[I1, I2, I3, A, B, C, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_3_4[I1, I2, I3, A, B, C, String]    {}) with User_In_3_4[I1, I2, I3, A, B, C, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_3_4[I1, I2, I3, A, B, C, Set[Long]] {}) with User_In_3_4[I1, I2, I3, A, B, C, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_3_3[I1, I2, I3, A, B, C]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_3_4[I1, I2, I3, A, B, C, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_3_4[I1, I2, I3, A, B, C, Seq[(T1, T2)]]
//}
//
//trait User_In_3_4[I1, I2, I3, A, B, C, D] extends User with In_3_4[I1, I2, I3, A, B, C, D] {
//  import User._
//  lazy val e          = new e          (this, new User_In_3_5[I1, I2, I3, A, B, C, D, Long]      {}) with User_In_3_5[I1, I2, I3, A, B, C, D, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_3_5[I1, I2, I3, A, B, C, D, String]    {}) with User_In_3_5[I1, I2, I3, A, B, C, D, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_3_5[I1, I2, I3, A, B, C, D, String]    {}) with User_In_3_5[I1, I2, I3, A, B, C, D, String]    {}
//  lazy val email        = new email        (this, new User_In_3_5[I1, I2, I3, A, B, C, D, String]    {}) with User_In_3_5[I1, I2, I3, A, B, C, D, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_3_5[I1, I2, I3, A, B, C, D, String]    {}) with User_In_3_5[I1, I2, I3, A, B, C, D, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_3_5[I1, I2, I3, A, B, C, D, Set[Long]] {}) with User_In_3_5[I1, I2, I3, A, B, C, D, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_3_4[I1, I2, I3, A, B, C, D]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_3_5[I1, I2, I3, A, B, C, D, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_3_5[I1, I2, I3, A, B, C, D, Seq[(T1, T2)]]
//}
//
//trait User_In_3_5[I1, I2, I3, A, B, C, D, E] extends User with In_3_5[I1, I2, I3, A, B, C, D, E] {
//  import User._
//  lazy val e          = new e          (this, new User_In_3_6[I1, I2, I3, A, B, C, D, E, Long]      {}) with User_In_3_6[I1, I2, I3, A, B, C, D, E, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_3_6[I1, I2, I3, A, B, C, D, E, String]    {}) with User_In_3_6[I1, I2, I3, A, B, C, D, E, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_3_6[I1, I2, I3, A, B, C, D, E, String]    {}) with User_In_3_6[I1, I2, I3, A, B, C, D, E, String]    {}
//  lazy val email        = new email        (this, new User_In_3_6[I1, I2, I3, A, B, C, D, E, String]    {}) with User_In_3_6[I1, I2, I3, A, B, C, D, E, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_3_6[I1, I2, I3, A, B, C, D, E, String]    {}) with User_In_3_6[I1, I2, I3, A, B, C, D, E, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_3_6[I1, I2, I3, A, B, C, D, E, Set[Long]] {}) with User_In_3_6[I1, I2, I3, A, B, C, D, E, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_3_5[I1, I2, I3, A, B, C, D, E]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_3_6[I1, I2, I3, A, B, C, D, E, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_3_6[I1, I2, I3, A, B, C, D, E, Seq[(T1, T2)]]
//}
//
//trait User_In_3_6[I1, I2, I3, A, B, C, D, E, F] extends User with In_3_6[I1, I2, I3, A, B, C, D, E, F] {
//  import User._
//  lazy val e          = new e          (this, new User_In_3_7[I1, I2, I3, A, B, C, D, E, F, Long]      {}) with User_In_3_7[I1, I2, I3, A, B, C, D, E, F, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]    {}) with User_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]    {}) with User_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]    {}
//  lazy val email        = new email        (this, new User_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]    {}) with User_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]    {}) with User_In_3_7[I1, I2, I3, A, B, C, D, E, F, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_3_7[I1, I2, I3, A, B, C, D, E, F, Set[Long]] {}) with User_In_3_7[I1, I2, I3, A, B, C, D, E, F, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_3_6[I1, I2, I3, A, B, C, D, E, F]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_3_7[I1, I2, I3, A, B, C, D, E, F, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_3_7[I1, I2, I3, A, B, C, D, E, F, Seq[(T1, T2)]]
//}
//
//trait User_In_3_7[I1, I2, I3, A, B, C, D, E, F, G] extends User with In_3_7[I1, I2, I3, A, B, C, D, E, F, G] {
//  import User._
//  lazy val e          = new e          (this, new User_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Long]      {}) with User_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Long]      {}
//  lazy val firstName    = new firstName    (this, new User_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]    {}) with User_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]    {}
//  lazy val lastName     = new lastName     (this, new User_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]    {}) with User_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]    {}
//  lazy val email        = new email        (this, new User_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]    {}) with User_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]    {}
//  lazy val passwordHash = new passwordHash (this, new User_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]    {}) with User_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, String]    {}
//  lazy val upVotes      = new upVotes      (this, new User_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Set[Long]] {}) with User_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Set[Long]] {}
//  def UpVotes                                    = new ManyRef[User, Story] with Story_In_3_7[I1, I2, I3, A, B, C, D, E, F, G]
//  def upVotes[T1]  (upVotes: Story_1[T1])   = new ManyRef[User, Story] with User_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Seq[T1]]
//  def upVotes[T1, T2](upVotes: Story_2[T1, T2]) = new ManyRef[User, Story] with User_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, Seq[(T1, T2)]]
//}
//
//trait User_In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H] extends User with In_3_8[I1, I2, I3, A, B, C, D, E, F, G, H]
