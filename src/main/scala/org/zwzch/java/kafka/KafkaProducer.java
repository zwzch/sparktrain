package kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.server.KafkaConfig;
import scala.util.parsing.combinator.testing.Str;

import java.util.Properties;

/**
 * Created by zw on 18-8-19.
 */
public class KafkaProducer extends Thread{
    /*
    *  kafka生产者
    * */
    private String topic;
    private Producer <Integer, String> producer;

    public KafkaProducer(String topic) {
        this.topic = topic;
        Properties properties = new Properties();
        properties.put("metadata.broker.list", KafkaProteries.BROKER_LIST);
        properties.put("serializer.class","kafka.serializer.StringEncoder");
        properties.put("request.required.acks","1");
        producer = new Producer<Integer, String>(new ProducerConfig(properties));

    }

    @Override
    public void run() {
        int messageNum = 1;
        while (true){
            String message = "message" + messageNum;
            System.out.println("sendmessage: " + message);
            producer.send(new KeyedMessage<Integer, String>(topic,message));
            messageNum++;
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
