//package molecule
//package examples
//package dayOfDatomic
//import molecule.ast.schema._
//
//object SocialNewsSchema {
//  import DbSchema._
//
//  object Story extends Story0[String] {
//    def apply(id: Int) = this
//  }
//
//  trait Story0[T] extends In0 {
//    val url = new OneString(this) with UniqueIdentity with Story0[String]
//    lazy val title = new OneString(this) with FulltextSearch with Indexed with Story0[String]
//    lazy val slug  = new OneString(this) with Story0[String]
//
//    lazy val upVotedBy = this
//
//    def ~(molecule: Molecule) = new Story0[T] {}
//    def ~(db: Db.type) = new Story0[T] {}
//    def apply(in: ?.type) = new Story1[T, T] {}
//    def <(in: ?.type) = new Story1[T, T] {}
//    def contains(in: ?.type) = new Story1[T, T] {}
//  }
//
//  trait Story1[A, T] extends In1[A] {
//    val url = new OneString(this) with UniqueIdentity with Story1[A, String]
//    lazy val title = new OneString(this) with FulltextSearch with Indexed with Story1[A, String]
//    lazy val slug  = new OneString(this) with Story1[A, String]
//
//    def ~(molecule: Molecule) = new Story1[A, T] {}
//    def apply(in: ?.type) = new Story2[A, T, T] {}
//    def <(in: ?.type) = new Story2[A, T, T] {}
//    def contains(in: ?.type) = new Story2[A, T, T] {}
//  }
//
//  trait Story2[A, B, T] extends In2[A, B] {
//    val url = new OneString(this) with UniqueIdentity with Story2[A, B, String]
//    lazy val title = new OneString(this) with FulltextSearch with Indexed with Story2[A, B, String]
//    lazy val slug  = new OneString(this) with Story2[A, B, String]
//
//    def ~(molecule: Molecule) = new Story2[A, B, T] {}
//  }
//
//  // =====================================================================================
//
//  object comments extends Ref(new Comment0[String] {}) with User0[String] {
//    def apply(id: Int) = this
//  }
//  //
//  //  trait comments0[T] extends In0 {
//  //    lazy val title = new OneString(this) with FulltextSearch with Indexed with Story0[String]
//  //    lazy val url   = new OneString(this) with UniqueIdentity with Story0[String]
//  //    lazy val slug  = new OneString(this) with Story0[String]
//  //  }
//
//  // =====================================================================================
//
//  object Comment extends Comment0[String]
//
//  trait Comment0[T] extends In0 {
//    lazy val body   = new OneString(this) with FulltextSearch with Comment0[String]
//    lazy val author = new OneString(this) with FulltextSearch with Comment0[String]
//
//    object User extends Ref(new User0[String] {}) with User0[String]
//
//    def apply(in: ?.type) = new Comment1[T, T] {}
//    def <(in: ?.type) = new Comment1[T, T] {}
//    def contains(in: ?.type) = new Comment1[T, T] {}
//  }
//
//  trait Comment1[A, T] extends In1[A] {
//    lazy val name = new OneString(this) with FulltextSearch with Comment1[A, String]
//
//    object User extends Ref(new User1[A, String] {}) with User1[A, String]
//
//    def apply(in: ?.type) = new Comment2[A, T, T] {}
//    def <(in: ?.type) = new Comment2[A, T, T] {}
//    def contains(in: ?.type) = new Comment2[A, T, T] {}
//  }
//
//  trait Comment2[A, B, T] extends In2[A, B] {
//    lazy val name = new OneString(this) with FulltextSearch with Comment2[A, B, String]
//
//    object User extends Ref(new User2[A, B, String] {}) with User2[A, B, String]
//
//    def apply(in: ?.type) = new Comment3[A, B, T, T] {}
//    def <(in: ?.type) = new Comment3[A, B, T, T] {}
//    def contains(in: ?.type) = new Comment3[A, B, T, T] {}
//  }
//
//  trait Comment3[A, B, C, T] extends In3[A, B, C] {
//    lazy val name = new OneString(this) with FulltextSearch with Comment3[A, B, C, String]
//
//    object User extends Ref(new User3[A, B, C, String] {}) with User3[A, B, C, String]
//  }
//
//  // =====================================================================================
//
//  object User extends User0[String]
//  val regionValues = List("n", "ne", "e", "se", "s", "sw", "w", "nw")
//
//  trait User0[T] extends In0 {
//    val email = new OneString(this) with Indexed with UniqueIdentity with User0[String]
//    lazy val firstName    = new OneString(this) with Indexed with User0[String]
//    lazy val lastName     = new OneString(this) with Indexed with User0[String]
//    lazy val passwordHash = new OneString(this) with User0[String]
//
//    object UpVotes extends ManyRef(new Story0[String] {}) with Story0[String]
//
//    def apply(in: ?.type) = new User1[T, T] {}
//    def <(in: ?.type) = new User1[T, T] {}
//    def contains(in: ?.type) = new User1[T, T] {}
//  }
//
//  trait User1[A, T] extends In1[A] {
//    val email = new OneString(this) with Indexed with UniqueIdentity with User1[A, String]
//    lazy val firstName    = new OneString(this) with Indexed with User1[A, String]
//    lazy val lastName     = new OneString(this) with Indexed with User1[A, String]
//    lazy val passwordHash = new OneString(this) with User1[A, String]
//
//    def apply(in: ?.type) = new User2[A, T, T] {}
//    def <(in: ?.type) = new User2[A, T, T] {}
//    def contains(in: ?.type) = new User2[A, T, T] {}
//  }
//
//  trait User2[A, B, T] extends In2[A, B] {
//    lazy val name   = new OneString(this) with FulltextSearch with User2[A, B, String]
//    lazy val region = new Enum(this, regionValues) with User2[A, B, String]
//
//    def apply(in: ?.type) = new User3[A, B, T, T] {}
//    def <(in: ?.type) = new User3[A, B, T, T] {}
//    def contains(in: ?.type) = new User3[A, B, T, T] {}
//  }
//
//  trait User3[A, B, C, T] extends In3[A, B, C] {
//    lazy val name   = new OneString(this) with FulltextSearch with User3[A, B, C, String]
//    lazy val region = new Enum(this, regionValues) with User3[A, B, C, String]
//  }
//}