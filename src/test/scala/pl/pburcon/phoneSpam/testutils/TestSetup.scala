package pl.pburcon.phoneSpam.testutils

import cats.Parallel
import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import monix.execution.Scheduler
import monix.execution.schedulers.TestScheduler
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

trait TestSetup extends AsyncFlatSpec with AsyncMockFactory with Matchers {

  //
  // Define the effect type as Task
  //
  import monix.eval.Task
  protected type F[T] = Task[T]
  protected val F: Task.type = Task

  //
  // Concrete type-class implementations for Task
  //
  protected implicit def scheduler: Scheduler                  = TestScheduler()
  protected implicit def concurrentEffect: ConcurrentEffect[F] = Task.catsEffect
  protected implicit def contextShift: ContextShift[F]         = Task.contextShift
  protected implicit def parallel: Parallel[Task]              = Task.catsParallel
  protected implicit def timer: Timer[F]                       = Task.timer

}
