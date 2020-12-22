package pl.pburcon.phoneSpam.util.cassandra.paging

import com.datastax.driver.core.PagingState
import pl.iterators.kebs.tagged._

object CassandraPagingDomain {

  sealed trait PagingStateSerializedTag
  type PagingStateSerialized = String @@ PagingStateSerializedTag

  object PagingStateSerialized {
    def apply(pagingState: PagingState): PagingStateSerialized =
      pagingState.toString.taggedWith[PagingStateSerializedTag]
  }

  sealed trait PagingIsExhaustedTag
  type PagingIsExhausted = Boolean @@ PagingIsExhaustedTag

  object PagingIsExhausted {
    def apply(boolean: Boolean): PagingIsExhausted = boolean.taggedWith[PagingIsExhaustedTag]
  }

}
