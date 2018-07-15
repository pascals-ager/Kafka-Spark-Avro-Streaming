package io.fd.kafka.avro

import java.util.Properties

import org.apache.avro.Schema.Parser
import org.apache.avro.generic.GenericData
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.slf4j.{Logger, LoggerFactory}
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.avro.Schema
import com.typesafe.config.{Config, ConfigFactory}



case class User(name: String, favoriteNumber: Int, favoriteColor: String)

class AvroProducer(config: Config) {

  config.checkValid(ConfigFactory.defaultReference(), "bootstrap-conf")
  val logger: Logger = LoggerFactory.getLogger(getClass)

  val kafkaBootstrapServer: String = config.getString("bootstrap-conf.KAFKA_BOOTSTRAP_SERVER")
  val schemaRegistryUrl: String = config.getString("bootstrap-conf.SCHEMA_REGISTRY_URL")

  val props = new Properties()
  props.put("bootstrap.servers", kafkaBootstrapServer)
  props.put("schema.registry.url", schemaRegistryUrl)
  props.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer")
  props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer")
  props.put("acks", "1")

  val producer: KafkaProducer[String, GenericData.Record] = new KafkaProducer[String, GenericData.Record](props)
  val schemaParser = new Parser

  val key = "key1"
  val valueSchemaJson =
    s"""
    {
      "namespace": "io.fd.kafka.avro",
      "type": "record",
      "name": "User2",
      "fields": [
        {"name": "name", "type": "string"},
        {"name": "favoriteNumber",  "type": "int"},
        {"name": "favoriteColor", "type": "string"}
      ]
    }
  """
  val valueSchemaAvro: Schema = schemaParser.parse(valueSchemaJson)
  val avroRecord = new GenericData.Record(valueSchemaAvro)

  val mary = User("Mary", 840, "Green")
  avroRecord.put("name", mary.name)
  avroRecord.put("favoriteNumber", mary.favoriteNumber)
  avroRecord.put("favoriteColor", mary.favoriteColor)

  def start(): Unit = {
    try {
      val record = new ProducerRecord("users", key, avroRecord)
      val ack = producer.send(record).get()
      // grabbing the ack and logging for visibility
      logger.info(s"${ack.toString} written to partition ${ack.partition.toString}")
    }
    catch {
      case e: Throwable => logger.error(e.getMessage, e)
    }
  }
}
