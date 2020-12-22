package pl.pburcon.phoneSpam.util.cassandra

import cats.effect.Async
import cats.implicits._
import com.outworkers.phantom.dsl._
import com.outworkers.phantom.{CassandraTable, Manager}
import pl.pburcon.phoneSpam.report.entries.cassandra.PhoneNumberEntries
import pl.pburcon.phoneSpam.util.future.AsyncFuture

import scala.concurrent.ExecutionContextExecutor

class PhoneSpamDatabase[F[_]: Async](override val connector: CassandraConnection)
    extends Database[PhoneSpamDatabase[F]](connector)
    with AsyncFuture {

  object entriesRepository extends PhoneNumberEntries(ec) with Connector {
    override def tableName: String = "phone_number_entries"
  }

  // just to make the executor source more explicit, it's imported from dsl._ by default
  private lazy implicit val ec: ExecutionContextExecutor = Manager.scalaExecutor

  private[cassandra] def autoCreateTables: F[PhoneSpamDatabase[F]] =
    autoCreateTable(entriesRepository).map(_ => this)

  private def autoCreateTable[T <: CassandraTable[T, R], R](t: CassandraTable[T, R]) =
    deferAsync(ec)(t.autocreate(space).future()(session, ec))
}
