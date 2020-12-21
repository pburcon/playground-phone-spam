package pl.pburcon.phoneSpam.report.domain

import enumeratum.{CirceEnum, Enum, EnumEntry, VulcanEnum}
import pl.iterators.kebs.tagged._
import pl.pburcon.phoneSpam.util.tagged.JsonCodecs

object ReportPhoneDomain extends JsonCodecs {

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
    def apply(value: Long): ReportCount = value.@@[ReportCountTag]
  }

  sealed trait ReportRating extends EnumEntry
  object ReportRating extends Enum[ReportRating] with CirceEnum[ReportRating] with VulcanEnum[ReportRating] {
    case object Safe       extends ReportRating
    case object Suspicious extends ReportRating
    case object Dangerous  extends ReportRating

    override def values: IndexedSeq[ReportRating] = findValues
  }
}
