package org.example.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.DTO.ReceivedMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class ConsumerDemo {
    private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class);
    private List<String> topicNames;

    private KafkaConsumer<String, String> consumer;
    List<ReceivedMessage> allReceivedMessages;

    private void addNewReceivedMessages(List<ReceivedMessage> rmsNew) {
        List<ReceivedMessage> rmToAdd = new ArrayList<>();

        for (ReceivedMessage rm: rmsNew) {
            // check if "rm" contains specific word
            Optional<ReceivedMessage> foundMsg = allReceivedMessages.stream().filter(it -> it.getWord().equals(rm.getWord())).findFirst();

            if (foundMsg.isPresent()) {
                // Increase count of present messages
                foundMsg.get().increaseCount(rm.getCount());
            }
            else {
                // Add new message, that will be inserted at end to "allReceivedMessages" list
                rmToAdd.add(rm);
            }
        }

        // Add completely new messages
        if (rmToAdd.size() > 0) {
            allReceivedMessages.addAll(rmToAdd);
        }
    }

    public ConsumerDemo(String bootstrapServers, List<String> topicNames, String groupId) {
        this.topicNames = topicNames;
        this.allReceivedMessages = new ArrayList<>();

        // create consumer configs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        // "earliest" - This offset variable automatically reset the value to its earliest offset,
        // "latest" - This offset variable reset the offset value to its latest offset,
        // "none" - If no previous offset is found for the previous group, it throws an exception to the consumer

        // create consumer
        consumer = new KafkaConsumer<>(properties);

        // subscribe consumer to our topic(s)
        consumer.subscribe(topicNames);
    }

    // Received data
    // Message is in format: "word:count" semicolons ";"
    // Example: "mama:2;auto:3"
    public List<ReceivedMessage> receiveMessages() {
        List<ReceivedMessage> rm = new ArrayList<>();

        // poll for new data (eventually change duration time - e.g. to 5s)
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));

        for (ConsumerRecord<String, String> record : records) {
            log.info("Received new message. \n" +
                    "Key: " + record.key() + "\n" +
                    "Value: " + record.value() + "\n" +
                    "Partition: " + record.partition() + "\n" +
                    "Offset:" + record.offset());

            // Get words and counters from specific record
            String[] wordsWithCounts = record.value().split(";");
            for (String wc: wordsWithCounts) {
                String[] wcSplitted = wc.split(":");

                if (wcSplitted.length == 2) {
                    String word = wcSplitted[0];
                    Integer count = Integer.parseInt(wcSplitted[1]);

                    // check if "rm" contains specific word
                    Optional<ReceivedMessage> foundMsg = rm.stream().filter(it -> it.getWord().equals(word)).findFirst();

                    if (!foundMsg.isPresent()) {
                        rm.add(new ReceivedMessage(word, count));
                    }
                    else {
                        foundMsg.get().increaseCount(count);
                    }
                }
            }
        }

        addNewReceivedMessages(rm);
        return rm;
    }

    public void receiveMessagesInfinite() {
        // get a reference to the current thread
        final Thread mainThread = Thread.currentThread();

        // adding the shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log.info("Detected a shutdown, let's exit by calling consumer.wakeup()...");
                consumer.wakeup();

                // join the main thread to allow the execution of the code in the main thread
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            // poll for new data
            while(true) {
                receiveMessages();
                printAllReceivedMessages();
            }
        }
        catch(WakeupException e) {
            log.info("Wake up exception!");
            // we ignore this as this is an expected exception when closing a consumer
        }
        catch(Exception e) {
            log.error("Unexpected exception", e);
        }
        finally {
            closeConnection();// this will also commit the offsets if need be
            log.info("The consumer is now gracefully closed.");
        }
    }

    // Call this method everytime at the end of receiving messages (could override "finalize" method)
    public void closeConnection() {
        // close connection
        consumer.close();
    }

    public List<ReceivedMessage> getAllReceivedMessages() {
        return allReceivedMessages;
    }

    public void printAllReceivedMessages() {
        System.out.println("-- All received messages --");

        int idx = 0;
        for (ReceivedMessage recMsg: allReceivedMessages) {
            System.out.println(idx+1 + ") word=" + recMsg.getWord() + "; count=" + recMsg.getCount());
            idx++;
        }
    }
}
