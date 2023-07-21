from utils.functions import createStrFromWordsDict
from utils.inlineWordCounter import inlineWordCounter
from kafka.producer import LocalProducer
from kafka.consumer import LocalConsumer

bootstrapServers = "127.0.0.1:9092" #"host1:9092"
receiveTopicName = "send_message"
sendTopicName = "receive_counters"
consumerGroupId = "receive-counters-1"

if __name__ == '__main__':
    # Consume message
    consumer = LocalConsumer(bootstrapServers, [receiveTopicName], consumerGroupId)
    recMess = consumer.receiveMessages()
    consumer.closeConnection()

    # Count words from message
    words = inlineWordCounter(recMess)
    strWords = createStrFromWordsDict(words)

    # produce message
    producer = LocalProducer(bootstrapServers)
    producer.sendMessage(sendTopicName, strWords)
