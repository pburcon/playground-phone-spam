package pl.pburcon.phoneSpam.util.cassandra.primitives

import com.outworkers.phantom.dsl.Primitive
import pl.iterators.kebs.tagged._

trait CassandraTaggedPrimitive {

  implicit def taggedPrimitive[T: Primitive, U]: Primitive[T @@ U] =
    Primitive.iso[T @@ U, T](_.taggedWith[U])(identity)

}

object CassandraTaggedPrimitive extends CassandraTaggedPrimitive
