package molecule.generic.dsl.datom
import java.util.Date
import scala.language.higherKinds


trait Datom_13[Ns13[_,_,_,_,_,_,_,_,_,_,_,_,_], Ns14[_,_,_,_,_,_,_,_,_,_,_,_,_,_], In13[_,_,_,_,_,_,_,_,_,_,_,_,_,_], In14[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_], A, B, C, D, E, F, G, H, I, J, K, L, M] extends Datom {
  type Next_[Attr[_, _], Type] = Attr[Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Type], In14[_,_,_,_,_,_,_,_,_,_,_,_,_,_,_]] with Ns14[A, B, C, D, E, F, G, H, I, J, K, L, M, Type]
  type Stay_[Attr[_, _], Type] = Attr[Ns13[A, B, C, D, E, F, G, H, I, J, K, L, M], In13[_,_,_,_,_,_,_,_,_,_,_,_,_,_]] with Ns13[A, B, C, D, E, F, G, H, I, J, K, L, M]

  final def e          : Next_[e         , Long   ] = ???
  final def ns         : Next_[ns        , String ] = ???
  final def a          : Next_[a         , String ] = ???
  final def v          : Next_[v         , Any    ] = ???
  final def t          : Next_[t         , Long   ] = ???
  final def tx         : Next_[tx        , Long   ] = ???
  final def txInstant  : Next_[txInstant , Date   ] = ???
  final def op         : Next_[op        , Boolean] = ???

  final def e_         : Stay_[e         , Long   ] = ???
  final def ns_        : Stay_[ns        , String ] = ???
  final def a_         : Stay_[a         , String ] = ???
  final def v_         : Stay_[v         , Any    ] = ???
  final def t_         : Stay_[t         , Long   ] = ???
  final def tx_        : Stay_[tx        , Long   ] = ???
  final def txInstant_ : Stay_[txInstant , Date   ] = ???
  final def op_        : Stay_[op        , Boolean] = ???
}
