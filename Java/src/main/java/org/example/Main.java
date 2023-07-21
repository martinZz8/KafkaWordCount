// from: https://www.conduktor.io/kafka/creating-a-kafka-java-project-using-maven-pom-xml/
package org.example;

import org.example.kafka.ConsumerDemo;
import org.example.kafka.ProducerDemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final String bootstrapServers = "127.0.0.1:9092";
    private static final String sendTopicName = "send_message";
    private static final String receiveTopicName = "receive_counters";
    private static final String consumerGroupId = "receive-counters-1";
    private static final String messageToCount = "mama kot jodła kot";

    public static void main(String[] args) {
        // Send messages
        ProducerDemo pd = new ProducerDemo(bootstrapServers, sendTopicName);
        pd.sendMessage(Optional.empty(), messageToCount); //key: Optional.of("test2")
        //pd.sendMessage(Optional.empty(), "test message3");
        pd.closeConnection();

        // Receive messages
        ConsumerDemo cs = new ConsumerDemo(bootstrapServers, Arrays.asList(receiveTopicName), consumerGroupId);
        cs.receiveMessages();
        //cs.receiveMessagesInfinite();
        cs.closeConnection();
        cs.printAllReceivedMessages();
    }
}