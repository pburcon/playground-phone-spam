package pl.pburcon.phoneSpam.report.entries.cassandra

import cats.effect.Async
import com.datastax.driver.core.PagingState
import com.outworkers.phantom.dsl._
import pl.pburcon.phoneSpam.report.common.domain.ReportPhoneDomain.PhoneNumber
import pl.pburcon.phoneSpam.report.entries.domain.PhoneNumberEntry
import pl.pburcon.phoneSpam.util.future.AsyncFuture

import scala.concurrent.ExecutionContextExecutor

trait PhoneNumberEntriesRepository[F[_]] {

  def insertEntry(entry: PhoneNumberEntry): F[ResultSet]

  def fetchByPhoneNumber(
      phoneNumber: PhoneNumber,
      maybePagingState: Option[PagingState],
      fetchSize: Int
  ): F[ListResult[PhoneNumberEntry]]
}

abstract class PhoneNumberEntries[F[_]: Async](ec: ExecutionContextExecutor)
    extends PhoneNumberEntriesTable
    with PhoneNumberEntriesRepository[F]
    with AsyncFuture {

  def insertEntry(entry: PhoneNumberEntry): F[ResultSet] =
    deferAsync(ec) {
//      store(entry) // TODO investigate why autogen does not work properly with tagged types, doing it manually sucks...
      insert()
        .value(_.phoneNumber, entry.phoneNumber)()
        .value(_.timestamp, entry.timestamp)()
        .value(_.id, entry.id)()
        .value(_.user, entry.user)()
        .value(_.comment, entry.comment)()
        .value(_.rating, entry.rating)()
        .consistencyLevel_=(ConsistencyLevel.LOCAL_QUORUM)
        .future()(session, ec)
    }

  def fetchByPhoneNumber(
      phoneNumber: PhoneNumber,
      maybePagingState: Option[PagingState],
      fetchSize: Int
  ): F[ListResult[PhoneNumberEntry]] =
    deferAsync(ec) {
      select
        .where(_.phoneNumber eqs phoneNumber)
        .consistencyLevel_=(ConsistencyLevel.LOCAL_QUORUM)
        .paginateRecord { statement =>
          // apply fetchSize, and pagingState if provided
          maybePagingState.fold(statement.setFetchSize(fetchSize))(statement.setFetchSize(fetchSize).setPagingState(_))
        }
    }
}
