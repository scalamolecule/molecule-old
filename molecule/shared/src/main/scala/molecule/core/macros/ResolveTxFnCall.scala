package molecule.core.macros

import scala.reflect.macros.blackbox


/** Macro to make transaction function invocations. */
class ResolveTxFnCall(val c: blackbox.Context) extends MacroHelpers {

  import c.universe._

  private[this] def resolve(txFnCall: Tree): (String, Seq[Tree]) = {
    val q"$owner.$txFn(..$args)(..$conn)" = txFnCall
    val ownerType                         = owner.tpe.toString
    val path                              = ownerType.take(ownerType.length - 4)
    val txFnDatomic                       = path + txFn + "__classpathTxFn"
    (txFnDatomic, args)
  }

  final def resolveTxFnCall(txFnCall: Tree, txMolecules: Tree*)(conn: Tree, ec: Tree): Tree = {
    val (txFnDatomic, args) = resolve(txFnCall)
    q"_root_.molecule.core.api.TxFunctions.txFnCall($txFnDatomic, Seq(..$txMolecules), ..$args)"
  }
  final def resolveInspectTxFnCall(txFnCall: Tree, txMolecules: Tree*)(conn: Tree, ec: Tree): Tree = {
    val (txFnDatomic, args) = resolve(txFnCall)
    q"_root_.molecule.core.api.TxFunctions.inspectTxFnCall($txFnDatomic, Seq(..$txMolecules), ..$args)"
  }
}