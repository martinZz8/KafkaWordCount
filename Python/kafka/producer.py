from confluent_kafka import Producer
import socket

class LocalProducer:
    def __init__(self, bs):
        self.__bs = bs
        self.__conf = {
            'bootstrap.servers': bs,
            'client.id': socket.gethostname()
        }

        self.__producer = Producer(self.__conf)

    def sendMessage(self, topic, value, *args, **kwargs):
        key = kwargs.get("key", None)
        self.__producer.produce(topic, key=key, value=value)
        self.__producer.flush()
