package pl.pburcon.phoneSpam.util.java

import java.time.{Duration => JavaDuration}
import scala.concurrent.duration.{Duration, FiniteDuration}

/**
 * Implicit conversions for the `java.time`` package
 */
trait JavaTimeConverters {

  /**
    * Implicit conversion:
    * scala.concurrent.duration.Duration => java.time.Duration
    */
  implicit class ScalaDurationAsJava(duration: Duration) {
    def asJava: JavaDuration = JavaDuration.ofNanos(duration.toNanos)
  }

  /**
    * Implicit conversion:
    * java.time.Duration => scala.concurrent.duration.Duration
    */
  implicit class JavaDurationAsScala(duration: JavaDuration) {
    def asJava: FiniteDuration = Duration.fromNanos(duration.toNanos)
  }

}

object JavaTimeConverters extends JavaTimeConverters
