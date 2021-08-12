package molecule.core.marshalling.unpack

import molecule.core.marshalling.attrIndexes._
import molecule.core.ops.TreeOps
import scala.collection.mutable.ListBuffer
import scala.reflect.macros.blackbox

trait Vs2tpl extends Unpackers {
  val c: blackbox.Context

  import c.universe._

  private lazy val xx = InspectMacro("Vs2tpl", 1)


  def vs2tpl(TplTypes: Seq[Type], indexes: Indexes): Tree = {

//        def getLambda(node: IndexNode): Seq[Tree] = {
//          node match {
//            case AttrIndex(_, castIndex, _, _, _) =>
//              Seq(unpackers(castIndex))
//
//            case Indexes(_, 2, attrs) =>
//              getNested(attrs)
//
//            case Indexes("Tx", _, List(Indexes(_, _, attrs))) =>
//              attrs.foreach(attr =>
//                getPackingLambda(attr, top)(rows))
//
//            case Indexes(_, _, attrs) =>
//              getRef(attrs)
//          }
//        }
//
//        def getLambdas(indexes: Indexes): Seq[Tree] = {
//          indexes.attrs.flatMap(getLambda)
//    //      {
//    //        case AttrIndex(_, castIndex, _, _, _) =>
//    //          Seq(unpackers(castIndex))
//    //
//    //        case Indexes(_, 2, attrs) =>
//    //          getNested(attrs)
//    //
//    //        case Indexes("Tx", _, List(Indexes(_, _, attrs))) =>
//    //            attrs.foreach(attr =>
//    //              getPackingLambda(attr, top)(rows))
//    //
//    //        case Indexes(_, _, attrs) =>
//    //          getRef(attrs)
//    //      }
//        }


    val v = q"v"
    val next = q"vs.next()"


    val unpackers1: Seq[Tree] = Seq(
      unpackLambdas(v)(14),
      unpackLambdas(next)(14),
      unpackLambdas(next)(0),
    )

    val unpackers0: Seq[Tree] = Seq(
      unpackLambdas(next)(0),
      q"nested1",
      unpackLambdas(next)(1),
    )

    def mkNested(level: Int, unpackers: Seq[Tree]): c.universe.Tree = {
      q"""
          def ${TermName("nested" + level)} = {
            v = vs.next()
            if (v == "◄◄") {
              Nil
            } else {
              val buf = new ListBuffer[Product]
              first = true
              do {
                buf.append((..$unpackers))
                v = vs.next()
              } while (v != "◄")
              buf.toList
            }
          }
       """
    }

    val nested = Seq(mkNested(1, unpackers1))

    val tree =
      q"""{
          ..$nested
          (..$unpackers0).asInstanceOf[(..$TplTypes)]
         }
       """

    xx(1, indexes, tree)
    tree
  }
}
