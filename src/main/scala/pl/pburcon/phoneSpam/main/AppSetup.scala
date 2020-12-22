package pl.pburcon.phoneSpam.main

import cats.Parallel
import cats.effect.{ConcurrentEffect, ContextShift, Resource, Timer}
import fs2.Stream
import pl.pburcon.phoneSpam.main.modules._
import pl.pburcon.phoneSpam.report.consume.kafka.KafkaReportPhoneConsumer
import pl.pburcon.phoneSpam.report.produce.kafka.KafkaReportPhoneProducer
import pl.pburcon.phoneSpam.util.cats.effect.ResourceSync
import pl.pburcon.phoneSpam.util.http.HttpServer

import scala.concurrent.ExecutionContext

/**
  * Trait `AppSetup` is generic, and can be used to construct concrete App instances with desired Effect type implementations.
  *
  * See `App` for examples of IO-based and Task-based. apps.
  *
  * It can also be used to easily construct modules for integration tests.
  */
trait AppSetup {

  /**
    * Abstract effect type.
    */
  type F[T]

  //
  // a combined set of type-classes required by app components
  //
  protected implicit def concurrentEffect: ConcurrentEffect[F]
  protected implicit def contextShift: ContextShift[F]
  protected implicit def parallel: Parallel[F]
  protected implicit def timer: Timer[F]

  //
  // dependencies to be provided by the Main class
  //
  protected def executionContext: ExecutionContext

  /**
    * Initialize all modules and return all app-lifecycle-bound long-living resources.
    */
  protected def initializeAppResources(): Resource[F, AppResources[F]] =
    // TODO
    //  the way it's written right now modules don't compose too well, experiment failed...
    //  think of a better way (maybe just cake pattern with Module traits? kleisli?)
    for {
      cassandraModule   <- initializeModule(new CassandraModule[F])
      cassandraDatabase <- cassandraModule.buildDatabase()

      kafkaProducerModule <- initializeModule(new KafkaProducerModule[F])
      kafkaReportProducer <- kafkaProducerModule.buildReportProducer()

      redisModule <- initializeModule(new RedisModule[F])
      redisClient <- redisModule.buildRedisClient()

      httpModule <- initializeModule(
        new HttpModule[F](cassandraDatabase.entriesRepository, kafkaReportProducer, redisClient)
      )
      httpServer <- httpModule.buildHttpServer(executionContext)

      kafkaConsumerModule <- initializeModule(
        new KafkaConsumerModule[F](cassandraDatabase.entriesRepository, redisClient)
      )
      kafkaReportConsumer <- kafkaConsumerModule.buildReportConsumer()

    } yield AppResources(httpServer, kafkaReportConsumer, kafkaReportProducer)

  /**
    * Run long-lived app resources.
    */
  protected def startAppResources(appResources: AppResources[F]): Stream[F, Unit] =
    Seq(
      appResources.httpServer.startServer(),
      appResources.kafkaReportConsumer.startConsumer(),
      appResources.kafkaReportPhoneProducer.startProducer(),
    ).reduce(_.concurrently(_)).map(_ => ())

  // just to help with type inference, otherwise there are issues with automatically importing type-classes for F
  private def initializeModule[T](thunk: => T): Resource[F, T] = ResourceSync.delay[F, T](thunk)
}

/**
  * Those resources need to be started concurrently in order to provide the app functionalities. Their lifecycle will be
  * bound to the app lifecycle.
  */
final case class AppResources[F[_]](
    httpServer: HttpServer[F],
    kafkaReportConsumer: KafkaReportPhoneConsumer[F],
    kafkaReportPhoneProducer: KafkaReportPhoneProducer[F]
)
