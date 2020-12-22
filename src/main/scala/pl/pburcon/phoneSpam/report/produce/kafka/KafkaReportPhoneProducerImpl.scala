package pl.pburcon.phoneSpam.report.produce.kafka

import cats.effect.ConcurrentEffect
import cats.implicits._
import fs2.concurrent.Queue
import fs2.{Pipe, Stream}
import pl.pburcon.phoneSpam.report.add.domain.dto.ReportPhoneAddRequest
import pl.pburcon.phoneSpam.util.logging.BaseLogging

import scala.util.control.NonFatal

trait KafkaReportPhoneProducer[F[_]] {
  def produceOne(request: ReportPhoneAddRequest): F[Unit]
  def startProducer(): Stream[F, Unit]
}

/**
  * This producer sends the messages asynchronously, batched via a queue.
  * It needs to be started in order to drain the queue.
  */
class KafkaReportPhoneProducerImpl[F[_]: ConcurrentEffect](
    queue: Queue[F, ReportPhoneAddRequest],
    processingPipe: Pipe[F, ReportPhoneAddRequest, _]
) extends KafkaReportPhoneProducer[F]
    with BaseLogging[F] {

  /**
    * Add a single request to the producer queue.
    */
  def produceOne(request: ReportPhoneAddRequest): F[Unit] =
    queue
      .enqueue1(request)
      .flatTap(_ => log(logger.debug(s"Enqueued one $request")))
      .handleError({ case NonFatal(t) => logger.error(t)(s"Failed to enqueue $request") })

  /**
    * Run the producer stream.
    */
  def startProducer(): Stream[F, Unit] =
    (for {
      _ <- logStream(logger.info(s"Starting producer $loggingClassName"))
      _ <- queue.dequeue.through(processingPipe)
    } yield ()).handleError({
      case NonFatal(t) => logger.error(t)(s"Unexpected exception - starting producer $loggingClassName")
    })
}
