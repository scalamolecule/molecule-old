package molecule.datomic.base.ast

import molecule.core.util.Helpers

object metaSchema extends Helpers {

  case class MetaSchema(parts: Seq[MetaPart]) {
    override def toString =
      s"""MetaSchema(Seq(${
        if (parts.isEmpty) "" else parts.mkString("\n    ", ",\n\n\n    ", "")
      }))"""

    def nsMapValues: Map[String, MetaNs] = {
      (for {
        part <- parts
        ns <- part.nss
      } yield {
        ns.nameFull -> ns
      }).toMap
    }

    def nsMap: String = {
      val parts2 = parts.map(part =>
        part.nss.map(ns => s""""${ns.nameFull}" -> $ns""").mkString("\n      ", ",\n\n      ", "")
      )
      val nss    = if (parts2.isEmpty) "" else parts2.mkString(",\n\n      ")
      s"Map($nss)"
    }

    def attrMapValues: Map[String, (Int, String)] = {
      (for {
        part <- parts
        ns <- part.nss
        attr <- ns.attrs
      } yield {
        s":${ns.nameFull}/${attr.name}" -> (attr.card, attr.tpe)
      }).toMap
    }

    def attrMap: String = {
      val attrData = for {
        part <- parts
        ns <- part.nss
        attr <- ns.attrs
      } yield {
        (s":${ns.nameFull}/${attr.name}", attr.card, attr.tpe)
      }
      val maxSp    = attrData.map(_._1.length).max
      val attrs    = attrData.map {
        case (a, card, tpe) => s""""$a"${padS(maxSp, a)} -> ($card, "$tpe")"""
      }
      val attrsStr = if (attrs.isEmpty) "" else attrs.mkString("\n      ", ",\n      ", "")
      s"Map($attrsStr)"
    }
  }


  case class MetaPart(
    pos: Int,
    name: String,
    descr$: Option[String] = None,
    entityCount$: Option[Int] = None,
    nss: Seq[MetaNs] = Nil
  ) {
    override def toString =
      s"""MetaPart($pos, "$name", ${o(descr$)}, ${o(entityCount$)}, Seq(${
        if (nss.isEmpty) "" else nss.mkString("\n      ", ",\n\n      ", "")
      }))"""
  }


  case class MetaNs(
    pos: Int,
    name: String,
    nameFull: String,
    descr$: Option[String] = None,
    entityCount$: Option[Int] = None,
    attrs: Seq[MetaAttr] = Nil
  ) {
    override def toString = {
      val maxPos   = attrs.length.toString.length
      val maxName  = attrs.map(_.name.length).max
      val maxTpe   = attrs.map(_.tpe.length).max
      val descrStr = descr$.fold("None")(t => "Some(\"\"\"" + t + "\"\"\")")

      val attrsStr = if (attrs.isEmpty) "Nil" else attrs.map {
        case MetaAttr(pos, name, card, tpe, enums, refNs$, options, doc$,
        attrGroup$, entityCount$, distinctValueCount$, descrAttr$, topValues) =>
          val padPos       = " " * (maxPos - pos.toString.length)
          val padName      = " " * (maxName - name.length)
          val padTpe       = " " * (maxTpe - tpe.length)
          val topValuesStr = if (topValues.isEmpty) "Nil" else
            topValues.mkString("Seq(\n          ", ",\n          ", ")")
          s"""MetaAttr($pos$padPos, "$name"$padName, $card, "$tpe"$padTpe, ${sq(enums)}, ${o(refNs$)}, ${sq(options)}, ${o(doc$)}, """ +
            s"""${o(attrGroup$)}, ${o(entityCount$)}, ${o(distinctValueCount$)}, ${o(descrAttr$)}, $topValuesStr)"""
      }.mkString("Seq(\n        ", ",\n        ", ")")

      s"""MetaNs($pos, "$name", "$nameFull", $descrStr, ${o(entityCount$)}, $attrsStr)"""
    }
  }


  case class MetaAttr(
    pos: Int,
    name: String,
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
    override def toString: String = {
      s"""MetaAttr($pos, "$name", $card, "$tpe", ${sq(enums)}, ${o(refNs$)}, ${sq(options)}, ${o(doc$)}, """ +
        s"""${o(attrGroup$)}, ${o(entityCount$)}, ${o(distinctValueCount$)}, ${o(descrAttr$)}, Seq(${
          if (topValues.isEmpty) "" else topValues.mkString("\n          ", ",\n          ", "")
        }))"""
    }
  }


  case class TopValue(
    entityCount: Int,
    value: String,
    label$: Option[String]
  ) {
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
}