package pl.pburcon.phoneSpam.util.json

import dev.profunktor.redis4cats.effects.ScoreWithValue
import io.circe.parser.decode
import io.circe.{Decoder, Error => CirceError}

import scala.collection.mutable.ListBuffer

final class ItemsParsing[T](private val errors: ListBuffer[CirceError], private val items: ListBuffer[T]) {

  /**
    * Add parsed item.
    * @param item parsed item
    * @return `this`, for easy `fold` use
    */
  def addItem(item: T): ItemsParsing[T] = {
    items.addOne(item)
    this
  }

  /**
    * Add parsing error.
    * @param error parsing error
    * @return `this`, for easy `fold` use
    */
  def addError(error: CirceError): ItemsParsing[T] = {
    errors.addOne(error)
    this
  }

  /**
    * Returns an immutable list of successfully parsed items.
    */
  def getParsedItems: Seq[T] =
    items.toList

  /**
    * Performs an action on each parsing error.
    * @return `this`, for chaining
    */
  def tapErrors(f: CirceError => Unit): ItemsParsing[T] = {
    errors.foreach(f)
    this
  }

}

object ItemsParsing {
  def parseItems[T](items: Seq[String])(implicit d: Decoder[T]): ItemsParsing[T] =
    items
      .map(decode[T])
      .foldLeft(ItemsParsing.initial[T]) {
        case (acc, Left(error)) => acc.addError(error)
        case (acc, Right(item)) => acc.addItem(item)
      }

  def parseItemsWithScore[T, R](items: Seq[ScoreWithValue[String]], applyF: (T, Double) => R)(implicit
      d: Decoder[T]
  ): ItemsParsing[R] =
    items
      .map(swv => decode[T](swv.value).map(t => t -> swv.score.value))
      .foldLeft(ItemsParsing.initial[R]) {
        case (acc, Left(error))          => acc.addError(error)
        case (acc, Right((item, score))) => acc.addItem(applyF(item, score))
      }

  private def initial[T]: ItemsParsing[T] = new ItemsParsing[T](ListBuffer(), ListBuffer())
}
