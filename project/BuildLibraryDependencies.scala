import sbt._

private object DependencyVersions {
  val catsVersion       = "2.3.1"
  val chimneyVersion    = "0.6.1"
  val circeVersion      = "0.13.0"
  val enumeratumVersion = "1.6.1"
  val fs2kafkaVersion   = "1.1.0"
  val http4sVersion     = "1.0.0-M8"
  val kebsVersion       = "1.8.1"
  val log4jVersion      = "2.14.0"
  val log4sVersion      = "1.10.0-M4"
  val macwireVersion    = "2.3.7"
  val monixVersion      = "3.3.0"
  val phantomVersion    = "2.59.0"
  val pureconfigVersion = "0.14.0"
  val redisVersion      = "0.10.3"
  val scalamockVersion  = "5.0.0"
  val scalatestVersion  = "3.2.3"
  val vulcanVersion     = "1.2.0"
}

object Dependencies {
  import DependencyVersions._

  private val cats = Seq(
    "cats-core",
    "cats-effect",
  ).map("org.typelevel" %% _ % catsVersion)

  private val circe = Seq(
    "circe-core",
    "circe-generic",
    "circe-parser",
  ).map("io.circe" %% _ % circeVersion)

  private val enumeratum = Seq(
    "enumeratum",
    "enumeratum-circe",
  ).map("com.beachape" %% _ % enumeratumVersion)

  private val http4s = Seq(
    "http4s-blaze-client",
    "http4s-blaze-server",
    "http4s-circe",
    "http4s-dsl",
  ).map("org.http4s" %% _ % http4sVersion)

  private val kafka = Seq(
    "fs2-kafka",
    "fs2-kafka-vulcan" // avro support
  ).map("com.github.fd4s" %% _ % fs2kafkaVersion)

  private val log4j = Seq(
    "log4j-core",
    "log4j-slf4j-impl",
  ).map("org.apache.logging.log4j" % _ % log4jVersion)

  private val log4s = Seq(
    "org.log4s" %% "log4s" % log4sVersion,
  )

  private val monix = Seq(
    "io.monix" %% "monix-eval" % monixVersion
  )

  private val phantom = Seq(
    "phantom-dsl"
  ).map("com.outworkers" %% _ % phantomVersion)

  private val redis = Seq(
    "dev.profunktor" %% "redis4cats-effects" % redisVersion,
  )

  private val utils = Seq(
    "com.github.pureconfig"    %% "pureconfig"  % pureconfigVersion,
    "com.softwaremill.macwire" %% "macros"      % macwireVersion % "provided",
    "io.scalaland"             %% "chimney"     % chimneyVersion,
    "pl.iterators"             %% "kebs-tagged" % kebsVersion,
  )

  private val vulcan = Seq(
    "vulcan",
    "vulcan-generic",
    "vulcan-enumeratum"
  ).map("com.github.fd4s" %% _ % vulcanVersion)

  private val test = Seq(
    "org.scalamock" %% "scalamock" % scalamockVersion,
    "org.scalatest" %% "scalatest" % scalatestVersion,
  ).map(_ % Test)

  //
  // bundles
  //

  val compileDependencies: Seq[ModuleID] = Seq(
    cats,
    circe,
    enumeratum,
    http4s,
    kafka,
    log4j,
    log4s,
    monix,
    phantom,
    redis,
    test,
    utils,
    vulcan,
  ).flatten
}

object DependencyResolvers {
  private val confluent = "Confluent" at "https://packages.confluent.io/maven/"

  val resolvers: Seq[MavenRepository] = Seq(
    confluent, // needed for kafka-avro-serializer
  )
}
