package pl.pburcon.phoneSpam.main

import cats.Parallel
import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, Timer}
import monix.eval.TaskApp
import monix.execution.schedulers.TracingScheduler
import monix.execution.{Scheduler, UncaughtExceptionReporter}

import scala.concurrent.ExecutionContext

//
// Monix Task App
//
object Main extends AppRun with AppSetup with TaskApp {

  //
  // Define the effect type as Task
  //
  import monix.eval.Task
  override type F[T] = Task[T]

  //
  // Concrete type-class implementations for Task
  //
  override protected implicit def concurrentEffect: ConcurrentEffect[F] = Task.catsEffect(scheduler)
  override protected implicit def contextShift: ContextShift[F]         = Task.contextShift
  override protected implicit def parallel: Parallel[Task]              = Task.catsParallel
  override protected implicit def timer: Timer[F]                       = Task.timer

  // Wrap the global scheduler with:
  // - proper uncaught exception logging,
  // - better stacktrace support (`Local` propagation)
  override protected def scheduler: Scheduler = {
    val reporterLogger = org.log4s.getLogger("monix-scheduler")
    val reporter = UncaughtExceptionReporter { throwable =>
      reporterLogger.error(throwable)("Uncaught error during task execution")
    }

    TracingScheduler(Scheduler(super.scheduler, reporter))
  }

  override protected def executionContext: ExecutionContext = scheduler

  override def run(args: List[String]): F[ExitCode] = run()
}

//
// Cats Effect IO App
//

//object Main extends AppRun with AppSetup with IOApp {
//
//  import cats.effect.IO
//  override type F[T] = IO[T]
//
//  override protected implicit def concurrentEffect: ConcurrentEffect[F] = IO.ioConcurrentEffect
