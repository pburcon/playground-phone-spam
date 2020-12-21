package pl.pburcon.phoneSpam.util.tagged

import io.circe.{Decoder, Encoder}
import pl.iterators.kebs.tagged._

import java.time.{Instant, ZonedDateTime}
import java.util.UUID

trait JsonCodecs {
  implicit def bigDecimalDecoder[T]: Decoder[BigDecimal @@ T] = implicitly[Decoder[BigDecimal]].map(_.@@[T])
  implicit def booleanDecoder[T]: Decoder[Boolean @@ T]       = implicitly[Decoder[Boolean]].map(_.@@[T])
  implicit def doubleDecoder[T]: Decoder[Double @@ T]         = implicitly[Decoder[Double]].map(_.@@[T])
  implicit def floatDecoder[T]: Decoder[Float @@ T]           = implicitly[Decoder[Float]].map(_.@@[T])
  implicit def instantDecoder[T]: Decoder[Instant @@ T]       = implicitly[Decoder[Instant]].map(_.@@[T])
  implicit def intDecoder[T]: Decoder[Int @@ T]               = implicitly[Decoder[Int]].map(_.@@[T])
  implicit def longDecoder[T]: Decoder[Long @@ T]             = implicitly[Decoder[Long]].map(_.@@[T])
  implicit def stringDecoder[T]: Decoder[String @@ T]         = implicitly[Decoder[String]].map(_.@@[T])
  implicit def uuidDecoder[T]: Decoder[UUID @@ T]             = implicitly[Decoder[UUID]].map(_.@@[T])
  implicit def zdtDecoder[T]: Decoder[ZonedDateTime @@ T]     = implicitly[Decoder[ZonedDateTime]].map(_.@@[T])

  implicit def bigDecimalEncoder[T]: Encoder[BigDecimal @@ T] = implicitly[Encoder[BigDecimal]].contramap(identity)
  implicit def booleanEncoder[T]: Encoder[Boolean @@ T]       = implicitly[Encoder[Boolean]].contramap(identity)
  implicit def doubleEncoder[T]: Encoder[Double @@ T]         = implicitly[Encoder[Double]].contramap(identity)
  implicit def floatEncoder[T]: Encoder[Float @@ T]           = implicitly[Encoder[Float]].contramap(identity)
  implicit def instantEncoder[T]: Encoder[Instant @@ T]       = implicitly[Encoder[Instant]].contramap(identity)
  implicit def intEncoder[T]: Encoder[Int @@ T]               = implicitly[Encoder[Int]].contramap(identity)
  implicit def longEncoder[T]: Encoder[Long @@ T]             = implicitly[Encoder[Long]].contramap(identity)
  implicit def stringEncoder[T]: Encoder[String @@ T]         = implicitly[Encoder[String]].contramap(identity)
  implicit def uuidEncoder[T]: Encoder[UUID @@ T]             = implicitly[Encoder[UUID]].contramap(identity)
  implicit def zdtEncoder[T]: Encoder[ZonedDateTime @@ T]     = implicitly[Encoder[ZonedDateTime]].contramap(identity)
}

object JsonCodecs extends JsonCodecs
