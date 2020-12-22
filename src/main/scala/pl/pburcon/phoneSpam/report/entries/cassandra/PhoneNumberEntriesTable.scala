package pl.pburcon.phoneSpam.report.entries.cassandra

import com.outworkers.phantom.Table
import com.outworkers.phantom.dsl.{Ascending, ClusteringOrder}
import com.outworkers.phantom.keys.PartitionKey
import pl.pburcon.phoneSpam.report.common.domain.ReportPhoneDomain._
import pl.pburcon.phoneSpam.report.entries.domain.PhoneNumberEntry
import pl.pburcon.phoneSpam.util.cassandra.primitives.CassandraPrimitives

import java.time.Instant

abstract class PhoneNumberEntriesTable
    extends Table[PhoneNumberEntriesTable, PhoneNumberEntry]
    with CassandraPrimitives {

  object phoneNumber extends Col[PhoneNumber] with PartitionKey
  object timestamp   extends Col[Instant] with ClusteringOrder with Ascending
  object id          extends Col[PhoneNumberEntryId] with ClusteringOrder
  object user        extends Col[UserName]
  object comment     extends Col[ReportComment]
  object rating      extends Col[PhoneNumberRating]
}
