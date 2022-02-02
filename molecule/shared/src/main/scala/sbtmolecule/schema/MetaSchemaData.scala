package sbtmolecule.schema

import sbtmolecule.Helpers
//import sbtmolecule.ast.metaSchema._
import molecule.datomic.base.ast.metaSchema._
import sbtmolecule.ast.schemaModel._

trait MetaSchemaData extends Helpers {

  def getMetaSchema(d: Model): MetaSchema = {
    var prevPart  = ""
    var parts     = Seq(MetaPart(-1, "", None, None, Nil))
    var nss       = Seq.empty[MetaNs]
    var partIndex = -1
    var nsIndex   = -1
    var attrIndex = -1

    d.nss.foreach {
      case Namespace(part, partDescr, ns, nsDescr, _, attrs) =>
        if (prevPart != part) {
          // Start new partition with empty seq of nss
          partIndex += 1
          prevPart = part
          parts = parts.init :+
            parts.last.copy(nss = nss) :+
            MetaPart(partIndex, part, partDescr, None, Nil)
          nss = Seq.empty[MetaNs]
        }

        nsIndex += 1
        attrIndex = -1
        val metaAttrs = attrs.flatMap {
          case _: BackRef => None
          case a          =>
            attrIndex += 1
            val (attr, attrGroup)    = (a.attr, a.attrGroup)
            val card                 = a.clazz.take(3) match {
              case "One" => 1
              case "Man" => 2
              case "Map" => 3
            }
            val opts: Seq[String]    = a.options
              .map(_.datomicKeyValue)
              .filterNot(o => o.startsWith(":db/index") || o.startsWith(":db/doc"))
              .flatMap {
                case r":db/(.*)$opt\s+.*" => Some(opt.trim)
                case "alias"              => None
              }
            val doc : Option[String] = opts.collectFirst {
              case r":db/doc(.*)$txt" => txt.trim.tail.init
            }
            a match {
              case v: Val  => card match {
                case 1 => Some(MetaAttr(attrIndex, attr, card, v.tpe, Nil, None, opts, doc, attrGroup))
                case _ => Some(MetaAttr(attrIndex, attr, card, v.baseTpe, Nil, None, opts, doc, attrGroup))
              }
              case e: Enum => Some(MetaAttr(attrIndex, attr, card, "enum", e.enums, None, opts, doc, attrGroup))
              case r: Ref  => Some(MetaAttr(attrIndex, attr, card, "ref", Nil, Some(r.refNs), opts, doc, attrGroup))
              case _       => None
            }
        }
        val nsClean   = if (part == "db.part/user") ns else ns.split("_").last
        nss = nss :+ MetaNs(nsIndex, nsClean, ns, nsDescr, None, metaAttrs)
    }

    // Add nss to last partition
    MetaSchema((parts.init :+ parts.last.copy(nss = nss)).tail)
  }
}
