package pl.pburcon.phoneSpam.util.cassandra.config

final case class CassandraConfig(
    host: String,
    port: Int,
    username: String,
    password: String,
    keyspace: String,
)
