package project

import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import spire.syntax.group

/**
  * Created by zw on 18-8-30.
  * 使用SparkStreaming处理Kafaka的数据
  */
object StatStreamingApp {
  def main(args: Array[String]): Unit = {
    if (args.length != 4){
      System.out.print("Usage: <zk><group><topics><threads>")
      System.exit(1)
    }
    val sparkConf = new SparkConf().setAppName("StatStreamingApp").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf,Seconds(60))
    val Array(zkQuorum,group,topics,numThreads) = args
    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val messages = KafkaUtils.createStream(ssc,zkQuorum,group,topicMap)
//    测试步骤1

    messages.map(_._2).count().print
    ssc.start()
    ssc.awaitTermination()

  }
}
