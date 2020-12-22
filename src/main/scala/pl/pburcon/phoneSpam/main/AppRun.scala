package pl.pburcon.phoneSpam.main

import cats.effect.ExitCode
import cats.syntax.functor._
import fs2.Stream

trait AppRun { self: AppSetup =>

  /**
    * Run the application.
    */
  def run(): F[ExitCode] =
    Stream
      .resource(initializeAppResources())
      .flatMap(startAppResources)
      .compile
      .drain
      .as(ExitCode.Success)

}
