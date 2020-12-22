package pl.pburcon.phoneSpam.main.modules

import cats.effect.{Async, Resource}
import com.softwaremill.macwire.wire
import pl.pburcon.phoneSpam.main.config.ConfigLoader
import pl.pburcon.phoneSpam.util.cassandra.config.CassandraConfig
import pl.pburcon.phoneSpam.util.cassandra.{PhoneSpamDatabase, PhoneSpamDatabaseBuilder}
import pureconfig.generic.auto._

class CassandraModule[F[_]: Async] {

  //
  // public module components
  //

  def buildDatabase(): Resource[F, PhoneSpamDatabase[F]] =
    cassandraDatabaseBuilder.buildDatabase()

  //
  // private module components
  //
  protected lazy val cassandraConfig: CassandraConfig = ConfigLoader.load[CassandraConfig]("cassandra")

  protected lazy val cassandraDatabaseBuilder: PhoneSpamDatabaseBuilder[F] = wire[PhoneSpamDatabaseBuilder[F]]

}
