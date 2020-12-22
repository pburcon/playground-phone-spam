package pl.pburcon.phoneSpam.report.common.domain

import enumeratum.{CirceEnum, Enum, EnumEntry, VulcanEnum}
import pl.iterators.kebs.tagged._
import pl.pburcon.phoneSpam.util.cassandra.enumeratum.CassandraEnum
import pl.pburcon.phoneSpam.util.circe.CirceJsonCodecs
import pl.pburcon.phoneSpam.util.tagged.TaggedId

import java.util.UUID

object ReportPhoneDomain extends CirceJsonCodecs {

  sealed trait PhoneNumberEntryIdTag
  type PhoneNumberEntryId = UUID @@ PhoneNumberEntryIdTag

  object PhoneNumberEntryId extends TaggedId {
    def generate: PhoneNumberEntryId = generateTaggedId
  }

  sealed trait PhoneNumberTag
  type PhoneNumber = String @@ PhoneNumberTag

  sealed trait UserNameTag
  type UserName = String @@ UserNameTag

  sealed trait ReportCommentTag
  type ReportComment = String @@ ReportCommentTag

  sealed trait ReportCountTag
  type ReportCount = Long @@ ReportCountTag

  // TODO a macro?
  object ReportCount {
    def apply(value: Long): ReportCount = value.taggedWith[ReportCountTag]
  }

  sealed trait PhoneNumberRating extends EnumEntry
  object PhoneNumberRating
      extends Enum[PhoneNumberRating]
      with CassandraEnum[PhoneNumberRating]
      with CirceEnum[PhoneNumberRating]
      with VulcanEnum[PhoneNumberRating] {

    case object Safe       extends PhoneNumberRating
    case object Suspicious extends PhoneNumberRating
    case object Dangerous  extends PhoneNumberRating
    case object Unknown    extends PhoneNumberRating

    override def values: IndexedSeq[PhoneNumberRating] = findValues
  }
}
