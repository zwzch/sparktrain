package kafka;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;


import java.util.*;

/**
 * Created by zw on 18-8-20.
 */
public class KafkaConsumer extends Thread {
    private String topic;

    public KafkaConsumer(String topic) {
        this.topic = topic;
    }

    private ConsumerConnector createrConnector(){
        Properties properties = new Properties();
        properties.put("zookeeper.connect",KafkaProteries.ZK);
        properties.put("group.id",KafkaProteries.GROUP_ID);
        return Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
    }

    @Override
    public void run() {
        ConsumerConnector consumer = createrConnector();
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic,1); //topic stream个数
        //String topic
        //List<KafkaStream<byte[], byte[]>> 对应的数据流
        Map<String, List<KafkaStream<byte[], byte[]>>>   messageStream = consumer.createMessageStreams(topicCountMap);

        KafkaStream<byte[], byte[]> stream = messageStream.get(topic).get(0);
        //获取我们每次获得的数据
        ConsumerIterator<byte[], byte[]> iterator = stream.iterator();

        while (iterator.hasNext()){
            String message = new String(iterator.next().message());
            System.out.println("rec: " + message);
        }
    }
}
