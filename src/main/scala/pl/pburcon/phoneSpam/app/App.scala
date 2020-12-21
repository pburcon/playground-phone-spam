package pl.pburcon.phoneSpam.app

import cats.effect._
import org.http4s.server.Server
import pl.pburcon.phoneSpam.app.modules.{HttpModule, KafkaModule, RedisModule}
import pl.pburcon.phoneSpam.util.effect.ResourceSync

object App extends IOApp {

  // single place specifying a concrete effect type
  type F[T] = IO[T]

  // run all io fibers until the jvm is done
  override def run(args: List[String]): IO[ExitCode] =
    initializeAndRun()
      .use(_ => IO.never)
      .as(ExitCode.Success)

  private def initializeAndRun(): Resource[F, Server] =
    for {
      kafkaModule         <- initializeModule(new KafkaModule[F])
      kafkaReportProducer <- kafkaModule.buildReportProducer()

      redisModule <- initializeModule(new RedisModule[F])
      redisClient <- redisModule.buildRedisClient()

      httpModule <- initializeModule(new HttpModule[F](kafkaReportProducer, redisClient))
      httpServer <- httpModule.buildHttpServer()
    } yield httpServer

  // just to help with type inference, otherwise there are issues with automatically importing type-classes for F/IO
  private def initializeModule[T](thunk: => T): Resource[F, T] = ResourceSync.delay[F, T](thunk)

}
