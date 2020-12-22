package pl.pburcon.phoneSpam.testutils.fixtures

import pl.iterators.kebs.tagged._

import java.util.UUID
import scala.reflect.runtime.universe._
import scala.util.Random

trait CommonFixture {
  def randomString(): String               = s"random-${UUID.randomUUID.toString}"
  def randomString(prefix: String): String = s"random-$prefix-${UUID.randomUUID}"

  def randomStringTagged[Tag](implicit tag: TypeTag[Tag]): String @@ Tag = {
    val tagName = tag.tpe.typeSymbol.name.toString
    randomString(tagName).taggedWith[Tag]
  }

  def randomLongTagged[Tag]: Long @@ Tag =
    Random.nextLong().taggedWith[Tag]

  def booleanTagged[Tag](bool: Boolean): Boolean @@ Tag =
    bool.taggedWith[Tag]

}
object CommonFixture extends CommonFixture
