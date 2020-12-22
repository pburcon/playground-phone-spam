package pl.pburcon.phoneSpam.util.circe

import io.circe.Decoder
import io.circe.parser._

trait DecodeOrThrow {

  def decodeOrThrow[T: Decoder](string: String): T =
    decode[T](string) match {
      case Right(decoded) => decoded
      case Left(error)    => throw error
    }

}

object DecodeOrThrow extends DecodeOrThrow
