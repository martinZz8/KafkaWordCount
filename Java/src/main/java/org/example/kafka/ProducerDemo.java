// from: https://www.conduktor.io/kafka/complete-kafka-producer-with-java/
package org.example.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Properties;

public class ProducerDemo {
    private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class);
    private String topicName;
    private KafkaProducer<String, String> producer;

    public ProducerDemo(String bootstrapServers, String topicName) {
        this.topicName = topicName;

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create producer
        producer = new KafkaProducer<>(properties);
    }

    public void sendMessage(Optional<String> key, String value) {
        // Note: second argument of "ProducerRecord" (the key) can be omitted. Then the topic is unnamed.
        // Keys adjust specific partitions utilization - messages with same key goes to the same partition
        ProducerRecord<String, String> producerRecord;

        if (key.isPresent()) {
            producerRecord = new ProducerRecord<>(topicName, key.get(), value);
        }
        else {
            producerRecord = new ProducerRecord<>(topicName, value);
        }

        // send data - asynchronous
        producer.send(producerRecord, (recordMetadata, e) -> {
            // executes every time a record is successfully sent or an exception is thrown
            if (e == null) {
                // the record was successfully sent
                log.info("Received new metadata. \n" +
                        "Topic:" + recordMetadata.topic() + "\n" +
                        (key.isPresent() ? "Key:" + producerRecord.key() + "\n" : "") +
                        "Partition: " + recordMetadata.partition() + "\n" +
                        "Offset: " + recordMetadata.offset() + "\n" +
                        "Timestamp: " + recordMetadata.timestamp());
            } else {
                log.error("Error while producing", e);
            }
        });
    }

    // Call this method everytime at the end of sending messages (could override "finalize" method)
    public void closeConnection() {
        // flush data - synchronous
        producer.flush();

        // close connection
        producer.close();
    }
}
