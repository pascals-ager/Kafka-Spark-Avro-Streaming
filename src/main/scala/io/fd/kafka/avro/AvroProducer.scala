package io.pascal.kafka.avro

import java.util.Properties

import org.apache.avro.generic.GenericData
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.slf4j.{Logger, LoggerFactory}
import com.typesafe.config.{Config, ConfigFactory}



case class User(name: String, favoriteNumber: Int, favoriteColor: String)

class AvroProducer(topic: String) {

  val config: Config = ConfigFactory.load()
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


  def start(key: String, avroRecord: GenericData.Record): Unit = {
    try {
      val record = new ProducerRecord("users", key, avroRecord)
      val ack = producer.send(record).get()
      logger.info(s"${ack.toString} written to partition ${ack.partition.toString}")
    }
    catch {
      case e: Throwable => logger.error(e.getMessage, e)
    }
  }

  def stop(): Unit = {
    producer.close()
  }
}
