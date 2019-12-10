package molecule.generic.schema

import java.util.Date
import molecule.boilerplate.dummyTypes._
import molecule.boilerplate.outSchema._
import scala.language.higherKinds


/** Schema interface to add first Schema attribute */
trait Schema_0 extends Schema with OutSchema_0[Schema_0] {
  type Next[Attr[_, _], Type] = Attr[Schema_1[Type], P2[_,_]] with Schema_1[Type]
  type Stay[Attr[_, _], Type] = Attr[Schema_0, P1[_]] with Schema_0
  
  final lazy val id           : Next[id           , Long   ] = ???
  final lazy val a            : Next[a            , String ] = ???
  final lazy val part         : Next[part         , String ] = ???
  final lazy val nsFull       : Next[nsFull       , String ] = ???
  final lazy val ns           : Next[ns           , String ] = ???
  final lazy val attr         : Next[attr         , String ] = ???
  final lazy val tpe          : Next[tpe          , String ] = ???
  final lazy val card         : Next[card         , String ] = ???
  final lazy val doc          : Next[doc          , String ] = ???
  final lazy val index        : Next[index        , Boolean] = ???
  final lazy val unique       : Next[unique       , String ] = ???
  final lazy val fulltext     : Next[fulltext     , Boolean] = ???
  final lazy val isComponent  : Next[isComponent  , Boolean] = ???
  final lazy val noHistory    : Next[noHistory    , Boolean] = ???
  final lazy val enum         : Next[enum         , String ] = ???
  final lazy val t            : Next[t            , Long   ] = ???
  final lazy val tx           : Next[tx           , Long   ] = ???
  final lazy val txInstant    : Next[txInstant    , Date   ] = ???

  final lazy val doc$         : Next[doc$         , Option[String] ] = ???
  final lazy val index$       : Next[index$       , Option[Boolean]] = ???
  final lazy val unique$      : Next[unique$      , Option[String] ] = ???
  final lazy val fulltext$    : Next[fulltext$    , Option[Boolean]] = ???
  final lazy val isComponent$ : Next[isComponent$ , Option[Boolean]] = ???
  final lazy val noHistory$   : Next[noHistory$   , Option[Boolean]] = ???
  
  final lazy val id_          : Stay[id           , Long   ] = ???
  final lazy val a_           : Stay[a            , String ] = ???
  final lazy val part_        : Stay[part         , String ] = ???
  final lazy val nsFull_      : Stay[nsFull       , String ] = ???
  final lazy val ns_          : Stay[ns           , String ] = ???
  final lazy val attr_        : Stay[attr         , String ] = ???
  final lazy val tpe_         : Stay[tpe          , String ] = ???
  final lazy val card_        : Stay[card         , String ] = ???
  final lazy val doc_         : Stay[doc          , String ] = ???
  final lazy val index_       : Stay[index        , Boolean] = ???
  final lazy val unique_      : Stay[unique       , String ] = ???
  final lazy val fulltext_    : Stay[fulltext     , Boolean] = ???
  final lazy val isComponent_ : Stay[isComponent  , Boolean] = ???
  final lazy val noHistory_   : Stay[noHistory    , Boolean] = ???
  final lazy val enum_        : Stay[enum         , String ] = ???
  final lazy val t_           : Stay[t            , Long   ] = ???
  final lazy val tx_          : Stay[tx           , Long   ] = ???
  final lazy val txInstant_   : Stay[txInstant    , Date   ] = ???
}
         