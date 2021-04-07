package molecule.datomic.base.ast

import molecule.core.util.Helpers

object schema extends Helpers {

  case class TopValue(entityCount: Int, value: String, label$: Option[String]) {
    override def toString = s"""TopValue($entityCount, "$value", ${o(label$)})"""
  }

  type FlatSchema = Seq[FlatAttr]

  case class FlatAttr(
    pos: Int,
    part: String,
    partDescr$: Option[String],
    ns: String,
    nsFull: String,
    nsDescr$: Option[String],
    attr: String,
    card: Int,
    tpe: String,
    enums: Seq[String] = Nil,
    refNs$: Option[String] = None,
    options: Seq[String] = Nil,
    doc$: Option[String] = None,
    attrGroup$: Option[String] = None,
    entityCount$: Option[Int] = None,
    distinctValueCount$: Option[Int] = None,
    descrAttr$: Option[String] = None,
    topValues: Seq[TopValue] = Nil
  ) {
    override def toString: String =
      s"""FlatAttr($pos, "$part", ${o(partDescr$)}, "$ns", "$nsFull", ${o(nsDescr$)}, "$attr", $card, "$tpe", """ +
        s"""${sq(enums)}, ${o(refNs$)}, ${sq(options)}, ${o(doc$)}, """ +
        s"""${o(attrGroup$)}, ${o(entityCount$)}, ${o(distinctValueCount$)}, ${o(descrAttr$)}, Seq(${
          if (topValues.isEmpty) "" else topValues.mkString("\n        ", ",\n        ", "")
        }))"""

  }


  case class MetaSchema(parts: Seq[Part]) {
    override def toString =
      s"""MetaSchema(Seq(${
        if (parts.isEmpty) "" else parts.mkString("\n  ", ",\n  ", "")
      }))"""
  }


  case class Part(
    pos: Int,
    name: String,
    descr$: Option[String] = None,
    entityCount$: Option[Int] = None,
    nss: Seq[Ns] = Nil
  ) {
    override def toString =
      s"""Part($pos, "$name", ${o(descr$)}, ${o(entityCount$)}, Seq(${
        if (nss.isEmpty) "" else nss.mkString("\n    ", ",\n    ", "")
      }))"""
  }


  case class Ns(
    pos: Int,
    name: String,
    nameFull: String,
    descr$: Option[String] = None,
    entityCount$: Option[Int] = None,
    attrs: Seq[Attr] = Nil
  ) {
    override def toString =
      s"""Ns($pos, "$name", "$nameFull", ${
        descr$.fold("None")(t => "Some(\"\"\"" + t + "\"\"\")")
      }, ${o(entityCount$)}, Seq(${
        if (attrs.isEmpty) "" else attrs.mkString("\n      ", ",\n      ", "")
      }))"""
  }


  case class Attr(
    pos: Int,
    name: String,
    card: Int,
    tpe: String,
    enums$: Option[Set[String]] = None,
    refNs$: Option[String] = None,
    options$: Option[Set[String]] = None,
    doc$: Option[String] = None,
    attrGroup$: Option[String] = None,
    entityCount$: Option[Int] = None,
    distinctValueCount$: Option[Int] = None,
    descrAttr$: Option[String] = None,
    topValues: Seq[TopValue] = Nil
  ) {
    override def toString: String =
      s"""Attr($pos, "$name", $card, "$tpe", ${os(enums$)}, ${o(refNs$)}, ${os(options$)}, ${o(doc$)}, """ +
        s"""${o(attrGroup$)}, ${o(entityCount$)}, ${o(distinctValueCount$)}, ${o(descrAttr$)}, Seq(${
          if (topValues.isEmpty) "" else topValues.mkString("\n        ", ",\n        ", "")
        }))"""
  }

}