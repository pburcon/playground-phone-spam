package pl.pburcon.phoneSpam.util.circe

import io.circe.{Decoder, Encoder}
import pl.iterators.kebs.tagged._

/**
  * Circe Json Decoder/Encoder derivations for tagged-type instances.
  *
  * Bring implicits into scope either by:
  * 1) mixing in the CirceJsonCodecs trait - `XXX extends ... with CirceJsonCodecs `.
  * 2) or importing from CirceJsonCodecs companion object - `import CirceJsonCodecs._`
  */
trait CirceJsonCodecs {
  implicit def taggedDecoder[T, U](implicit d: Decoder[T]): Decoder[T @@ U] = d.map(_.taggedWith[U])
  implicit def taggedEncoder[T, U](implicit e: Encoder[T]): Encoder[T @@ U] = e.contramap(identity)
}

object CirceJsonCodecs extends CirceJsonCodecs
