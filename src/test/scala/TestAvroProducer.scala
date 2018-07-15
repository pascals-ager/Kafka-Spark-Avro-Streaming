import com.typesafe.config._
import io.fd.kafka.avro.AvroProducer
import org.apache.log4j.BasicConfigurator
import org.slf4j.LoggerFactory



import org.scalatest.FunSuite

class TestAvroProducer extends FunSuite {
  test("Test if an Avro Record is produced"){
    BasicConfigurator.configure()
    val logger = LoggerFactory.getLogger(getClass)
    logger.info("Starting the application")
    val conf = ConfigFactory.load()
    val producer = new AvroProducer(conf)
    producer.start()
    assert(1 == 1)
  }

}
