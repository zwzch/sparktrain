package kafka;

/**
 * Created by zw on 18-8-19.
 */
/*
* Kafka Java API 测试
* */
public class KafkaClientApp {
    public static void main(String[] args) {
        new KafkaProducer(KafkaProteries.TOPIC).start();
//        new KafkaConsumer(KafkaProteries.TOPIC).start();
    }
}