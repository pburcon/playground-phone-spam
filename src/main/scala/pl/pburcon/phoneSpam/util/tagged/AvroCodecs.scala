package pl.pburcon.phoneSpam.util.tagged

import pl.iterators.kebs.tagged._
import vulcan.Codec

import java.time.{Instant, ZonedDateTime}
import java.util.UUID

trait AvroCodecs {
  implicit def taggedBigDecimalCodec[T]: Codec[BigDecimal @@ T] = Codec.string.imap(BigDecimal(_).@@[T])(_.toString)
  implicit def taggedBooleanCodec[T]: Codec[Boolean @@ T]       = Codec.boolean.imap(_.@@[T])(identity)
  implicit def taggedDoubleCodec[T]: Codec[Double @@ T]         = Codec.double.imap(_.@@[T])(identity)
  implicit def taggedFloatCodec[T]: Codec[Float @@ T]           = Codec.float.imap(_.@@[T])(identity)
  implicit def taggedInstantCodec[T]: Codec[Instant @@ T] =
    Codec.long.imap(Instant.ofEpochMilli(_).@@[T])(_.toEpochMilli)
  implicit def taggedIntCodec[T]: Codec[Int @@ T]       = Codec.int.imap(_.@@[T])(identity)
  implicit def taggedLongCodec[T]: Codec[Long @@ T]     = Codec.long.imap(_.@@[T])(identity)
  implicit def taggedStringCodec[T]: Codec[String @@ T] = Codec.string.imap(_.@@[T])(identity)
  implicit def taggedUuidCodec[T]: Codec[UUID @@ T]     = Codec.string.imap(UUID.fromString(_).@@[T])(_.toString)
  implicit def taggedZdtCodec[T]: Codec[ZonedDateTime @@ T] =
    Codec.string.imap(ZonedDateTime.parse(_).@@[T])(_.toString)
}

object AvroCodecs extends AvroCodecs
