package pl.pburcon.phoneSpam.util.tagged

import pl.iterators.kebs.tagged._

trait TaggedMap {

  /**
    * This is an extension class re-implementing the Functor extension from `pl.iterators.kebs.tagged.AndTaggingExtensions`
    * because of naming clash with String#map(Char => Char).
    * Note that it's not possible to specify the "pretty" tagged type as the return type here, so you may have to help
    * the inferencer a bit, eg. when auto generating methods with IDE.
    */
  implicit class TMapExtension[T, U](tagged: T @@ U) {
    def tmap(f: T => T): T @@ U = f(tagged).taggedWith[U]
  }
}

object TaggedMap extends TaggedMap
