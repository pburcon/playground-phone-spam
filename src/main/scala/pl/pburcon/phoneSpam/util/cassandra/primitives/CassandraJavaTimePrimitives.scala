package pl.pburcon.phoneSpam.util.cassandra.primitives

import com.outworkers.phantom.dsl.Primitive

import java.time.{Instant, ZonedDateTime}

trait CassandraJavaTimePrimitives {

  // it's possible to just import `com.outworkers.phantom.jdk8._`
  // but it's done this way because I really dislike top-level magic imports that pull all implicits into scope
  // it makes it a lot harder to reason about where all those things are coming from
  // I prefer explicitly mixing in traits that provide needed implicits
  import com.outworkers.phantom.jdk8

  implicit val instantPrimitive: Primitive[Instant]   = jdk8.instantPrimitive
  implicit val zdtPrimitive: Primitive[ZonedDateTime] = jdk8.indexed.zonedDateTimePrimitive
}

object CassandraJavaTimePrimitives
