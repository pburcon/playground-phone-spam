package pl.pburcon.phoneSpam.main.config

import cats.effect.{Resource, Sync}
import pl.pburcon.phoneSpam.util.cats.effect.ResourceSync
import pureconfig.{ConfigReader, ConfigSource, Derivation}

import scala.reflect.ClassTag

object ConfigLoader {

  def load[T: ClassTag](
      at: String
  )(implicit r: Derivation[ConfigReader[T]]): T =
    ConfigSource.default.at(at).loadOrThrow[T]

  def loadResource[F[_], T: ClassTag](
      at: String
  )(implicit r: Derivation[ConfigReader[T]], syncF: Sync[F]): Resource[F, T] =
    ResourceSync.delay(load(at))
}
