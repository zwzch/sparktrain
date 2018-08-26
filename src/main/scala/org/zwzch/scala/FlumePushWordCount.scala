import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

import org.apache.spark.streaming.flume._
/**
  * Created by zw on 18-8-26.
  */

object FlumePushWordCount {
  def main(args: Array[String]): Unit = {
      val sparkConf = new SparkConf().setMaster("local[3]").setAppName("FlumePushWordCount")
      val ssc = new StreamingContext(sparkConf,Seconds(5))
      //TODO Spark整合FLuume Flume的push方式
      val flumeStream = FlumeUtils.createStream(ssc, "0.0.0.0", 41414)

      flumeStream.map(x => new String(x.event.getBody.array()).trim)
      .flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).print()
      ssc.start()
      ssc.awaitTermination()
  }
}
