package pl.pburcon.phoneSpam.util.cassandra

import cats.effect.{Async, Resource, Sync}
import cats.implicits._
import com.datastax.driver.core.PlainTextAuthProvider
import com.outworkers.phantom.connectors.ContactPoint
import com.outworkers.phantom.dsl._
import pl.pburcon.phoneSpam.util.cassandra.config.CassandraConfig
import pl.pburcon.phoneSpam.util.logging.BaseLogging

class PhoneSpamDatabaseBuilder[F[_]: Async](cassandraConfig: CassandraConfig) extends BaseLogging[F] {

  private def configureConnection() =
    ContactPoint(cassandraConfig.host, cassandraConfig.port)
      .withClusterBuilder(
        _.withAuthProvider(new PlainTextAuthProvider(cassandraConfig.username, cassandraConfig.password))
      )
      .keySpace(
        KeySpace(cassandraConfig.keyspace)
          .ifNotExists()
          .option(replication eqs SimpleStrategy.replication_factor(1)) // TODO this is not prod-ready by any means
      )

  def buildDatabase(): Resource[F, PhoneSpamDatabase[F]] = {
    val acquire =
      for {
        _  <- log(logger.info(s"Acquiring resource: PhoneSpamDatabase"))
        db <- Sync.delay(new PhoneSpamDatabase[F](configureConnection()))
        _  <- log(logger.info(s"Acquired resource: PhoneSpamDatabase"))
      } yield db

    val release = (db: PhoneSpamDatabase[F]) =>
      for {
        _ <- log(logger.info(s"Releasing resource: PhoneSpamDatabase"))
        _ <- Sync.delay(db.shutdown())
        _ <- log(logger.info(s"Released resource: PhoneSpamDatabase"))
      } yield ()

    val postAcquire = (db: PhoneSpamDatabase[F]) => Resource.liftF(db.autoCreateTables)

    Resource
      .make(acquire)(release)
      .flatMap(postAcquire)
  }

  private val Sync = implicitly[Sync[F]]

}
