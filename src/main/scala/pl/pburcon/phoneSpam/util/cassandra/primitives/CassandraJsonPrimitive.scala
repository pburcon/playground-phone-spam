package pl.pburcon.phoneSpam.util.cassandra.primitives

trait CassandraJsonPrimitive {
//  implicit def jsonPrimitive[T](implicit e: Encoder[T], d: Decoder[T]): Primitive[T] =
//    Primitive.json[T](_.asJson.noSpaces)(decodeOrThrow[T])
}

object CassandraJsonPrimitive extends CassandraJsonPrimitive
