package molecule.ast

//trait shared {
//  def doc(s: String) = this
//}
object definition {

  trait anyAttr {
    lazy val noHistory = this
  }

  object oneString extends anyAttr {
    lazy val fullTextSearch = this
    lazy val uniqueIdentity = this
  }
  object manyStrings extends anyAttr {
    lazy val fullTextSearch = this
  }
  object oneLong extends anyAttr
  object oneInt extends anyAttr

  trait enum extends {
    // Requiring at least 2 values
    def apply(v1: Symbol, v2: Symbol, vs: Symbol*) = this
  }
  object oneEnum extends enum
  object manyEnums extends enum

  trait One[Ref]

  object oneRef {
    def apply[NS] = 7
  }
}