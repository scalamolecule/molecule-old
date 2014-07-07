package molecule

import java.net.URI
import java.util.{UUID, Date}

import molecule.db.DatomicFacade
import molecule.util.MoleculeSpec

trait CoreTest extends MoleculeSpec with DatomicFacade {
  
  // Sample data
  val (str1, int1, long1, float1, double1, bool1, date1, uuid1, uri1, enum1) =
    ("str1", -2147483648, -9223372036854775808L, -1.23f, -1.23, false, new Date, UUID.randomUUID(), new URI("uri1"), "enum1")

  val (str2, int2, long2, float2, double2, bool2, date2, uuid2, uri2, enum2) =
    ("str2", 2147483647, 9223372036854775807L, 1.23f, 1.23, true, new Date, UUID.randomUUID(), new URI("uri2"), "enum2")
}