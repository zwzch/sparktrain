import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by zw on 18-8-28.
  */
object KafkaReciverWordCount {
  def main(args: Array[String]): Unit = {
    if (args.length != 4){
      System.out.print("Usage: need 4 param")
    }
    val Array(zkQuorum,group,topics,numThreads) = args
    val sparkConf = new SparkConf().setAppName("KafkaReciverWordCount")
        .setMaster("local[2]")
    //基于reciver的 local必须是大于1的
    val ssc = new StreamingContext(sparkConf,Seconds(5))
    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val messages = KafkaUtils.createStream(ssc,zkQuorum,group,topicMap)
    //SparkStreaming Kafka
    messages.map(_._2).flatMap(_.split(" ").map((_,1))).reduceByKey(_+_).print()

    ssc.start()
    ssc.awaitTermination()
  }
}
