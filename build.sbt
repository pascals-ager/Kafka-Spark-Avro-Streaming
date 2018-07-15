val metaSettings = Seq(
  name := "schema-registry",
  version := "1.0.0"
)

val scalaSettings = Seq(
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")
)

val repositories = Seq(
  "confluent" at "http://packages.confluent.io/maven/",
  Resolver.sonatypeRepo("public")
)

val dependencies = Seq(
  "com.typesafe" % "config" % "1.2.1",
  "org.apache.avro" % "avro" % "1.7.7",
  "io.confluent" % "kafka-avro-serializer" % "1.0",
  "org.apache.kafka" %% "kafka" % "1.0.0",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

lazy val root = (project in file("."))
  .settings(metaSettings: _*)
  .settings(scalaSettings: _*)
  .settings(resolvers ++= repositories)
  .settings(libraryDependencies ++= dependencies)