package io.fd.kafka.avro

import com.typesafe.config.ConfigFactory
import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.GenericData
import org.apache.log4j.BasicConfigurator
import org.slf4j.{Logger, LoggerFactory}


object Runner extends App {

  BasicConfigurator.configure()
  val logger: Logger = LoggerFactory.getLogger(getClass)
  val config = ConfigFactory.load()
  config.checkValid(ConfigFactory.defaultReference(), "schema-conf")
  val schemaParser = new Parser

  val key = "users"
  val valueSchemaJson: String = config.getString("schema-conf.SCHEMA_STR")
  val valueSchemaAvro: Schema = schemaParser.parse(valueSchemaJson)
  val avroRecord = new GenericData.Record(valueSchemaAvro)

  val mary = User("Mary", 840, "Green")
  avroRecord.put("name", mary.name)
  avroRecord.put("favoriteNumber", mary.favoriteNumber)
  avroRecord.put("favoriteColor", mary.favoriteColor)

  val producer = new AvroProducer("users")

  1 to 100 foreach{_ => producer.start(key, avroRecord)}

  producer.stop()

}
