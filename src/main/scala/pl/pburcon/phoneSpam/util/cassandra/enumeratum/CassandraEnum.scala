package pl.pburcon.phoneSpam.util.cassandra.enumeratum

import com.outworkers.phantom.dsl.Primitive
import enumeratum.{Enum, EnumEntry}

/**
  * Cassandra Primitive derivation for Enumeratum Enums.
  *
  * The implicits should be available in scope by default, via the companion object.
  */
trait CassandraEnum[A <: EnumEntry] { self: Enum[A] =>
  implicit val primitive: Primitive[A] = Primitive.iso[A, String](self.withName)(_.entryName)
}
