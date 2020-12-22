package pl.pburcon.phoneSpam.util.redis.codecs

import dev.profunktor.redis4cats.codecs.Codecs
import dev.profunktor.redis4cats.codecs.splits.SplitEpi
import dev.profunktor.redis4cats.data.RedisCodec
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import pl.pburcon.phoneSpam.util.circe.CirceJsonCodecs
import pl.pburcon.phoneSpam.util.circe.DecodeOrThrow.decodeOrThrow

/**
  * Deriving RedisCodec instances for case-classes, using Circe.
  */
trait RedisJsonCodecs { self: CirceJsonCodecs =>

  def deriveRedisJsonCodec[V: Decoder: Encoder]: RedisCodec[String, V] =
    Codecs.derive(RedisCodec.Utf8, epi)

  private def epi[V: Decoder: Encoder]: SplitEpi[String, V] =
    SplitEpi(
      str => decodeOrThrow[V](str),
      _.asJson.noSpaces
    )
}

object RedisJsonCodecs
