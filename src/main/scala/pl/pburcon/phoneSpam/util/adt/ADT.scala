package pl.pburcon.phoneSpam.util.adt

/**
  * Sometimes the type inferencer needs a little help when dealing with ADT hierarchies, especially when combining
  * multiple processing steps where sometimes the ADT instance is a case class and sometimes a case object.
  *
  * For example, inferring types like:
  * {{{ Either[PhoneNumberParsingError.NotANumber.type, PhoneNumber] }}}
  *
  * Instead of:
  * {{{ Either[PhoneNumberParsingError, PhoneNumber] }}}
  *
  * The `adt` method is meant to "lift" the concrete ADT instance into the generic ADT type:
  * {{{
  * sealed trait PhoneNumberParsingError extends ADT[PhoneNumberParsingError] (...)
  *
  * PhoneNumberParsingError.NotANumber.adt - returns a value of type PhoneNumberParsingError
  * }}}
  *
  * This is an alternative to specifying the ADT type manually, like:
  * {{{
  *   PhoneNumberParsingError.NotANumber : PhoneNumberParsingError
  * }}}
  *
  * or using the Functor#widen function, which can only be used in a Functor context.
  *
  * Example ADT definition:
  * {{{
  *   sealed trait Result extends ADT[Result]
  * }}}
  */
trait ADT[A <: ADT[A]] { self: A =>
  @inline def adt: A = this
}
