package pl.pburcon.phoneSpam.util.tagged

import pl.iterators.kebs.tagged._

import java.util.UUID

trait TaggedId {
  protected def generateTaggedId[T]: UUID @@ T = UUID.randomUUID().@@[T]
}

object TaggedId extends TaggedId
