# from: https://docs.confluent.io/kafka-clients/python/current/overview.html#initialization
import sys
from confluent_kafka import Consumer, KafkaError, KafkaException

class LocalConsumer:
    def __init__(self, bs, topics, grId):
        self.__bs = bs
        self.__topics = topics
        self.__conf = {
            'bootstrap.servers': bs,
            'group.id': grId,
            'auto.offset.reset': 'latest'
            # "earliest" - This offset variable automatically reset the value to its earliest offset,
            # "latest" - This offset variable reset the offset value to its latest offset,
            # "none" - If no previous offset is found for the previous group, it throws an exception to the consumer
        }

        self.__consumer = Consumer(self.__conf)
        self.__consumer.subscribe(topics)

    def receiveMessages(self):
        msgToRet = ""

        try:
            msg = self.__consumer.poll(timeout=20.0)

            if msg is not None:
                if msg.error():
                    if msg.error().code() == KafkaError._PARTITION_EOF:
                        # End of partition event
                        sys.stderr.write('%% %s [%d] reached end at offset %d\n' % (msg.topic(), msg.partition(), msg.offset()))
                    elif msg.error():
                        raise KafkaException(msg.error())
                else:
                    msgToRet = msg.value().decode("utf-8")
        finally:
            # Close down consumer to commit final offsets (we close it explicitly in "closeConnection" method, since we can perform more connections).
            # self.__consumer.close()
            pass

        return msgToRet

    def closeConnection(self):
        self.__consumer.close()
