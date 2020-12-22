package pl.pburcon.phoneSpam.util.vulcan

import pl.iterators.kebs.tagged._
import vulcan.Codec

/**
  * Automatic derivation of Avro codecs for tagged-type instances.
  *
  * Bring implicits into scope either by:
  * 1) mixing in the AvroCodecs trait - `XXX extends ... with AvroCodecs `.
  * 2) or importing from AvroCodecs companion object - `import AvroCodecs._`
  */
trait AvroCodecs {
  implicit def taggedCodec[T, U](implicit c: Codec[T]): Codec[T @@ U] = c.imap(_.@@[U])(identity)
}

object AvroCodecs extends AvroCodecs
