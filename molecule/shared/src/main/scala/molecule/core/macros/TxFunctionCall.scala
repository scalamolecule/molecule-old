package molecule.core.macros

import scala.reflect.macros.blackbox


/** Macro to make transaction function invocations. */
class TxFunctionCall(val c: blackbox.Context) extends MacroHelpers {

  import c.universe._

  private lazy val xx = InspectMacro("TxFunctionCall", 1)

  private[this] def resolve(txFnCall: Tree): (String, Seq[Tree]) = {
    val q"$owner.$txFn(..$args)(..$conn)" = txFnCall
    val ownerType                         = owner.tpe.toString
    val path                              = ownerType.take(ownerType.length - 4)
    val txFnDatomic                       = path + txFn + "__txFnDatomic"
    //    tfc(1, owner, ownerType)
    (txFnDatomic, args)
  }

  final def txFnCall(txFnCall: Tree, txMolecules: Tree*)(conn: Tree, ec: Tree): Tree = {
    val (txFnDatomic, args) = resolve(txFnCall)
    q"_root_.molecule.core.api.TxFunctions.txFnCall($txFnDatomic, Seq(..$txMolecules), ..$args)"
  }
  final def inspectTxFnCall(txFnCall: Tree, txMolecules: Tree*)(conn: Tree, ec: Tree): Tree = {
    val (txFnDatomic, args) = resolve(txFnCall)
    q"_root_.molecule.core.api.TxFunctions.inspectTxFnCall($txFnDatomic, Seq(..$txMolecules), ..$args)"
  }
}