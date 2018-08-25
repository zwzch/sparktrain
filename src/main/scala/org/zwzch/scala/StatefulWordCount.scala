import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by zw on 18-8-25.
  */
object StatefulWordCount {

  def updateFunction(values: Seq[Int], runningCount: Option[Int]) = {
    val newCount = values.sum + runningCount.getOrElse(0)
    new Some(newCount)
  }

  def main(args: Array[String]): Unit = {


    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
    /*
  * 创建StreamingContext 需要 sparkconf 和 batch interval
  * */
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    val lines = ssc.socketTextStream("localhost", 6789)
    ssc.checkpoint("/home/zw/IdeaProjects/sparktrain/checkPoint/")
    val result = lines.flatMap(_.split(" ")).map((_, 1))
    val words = lines.flatMap(_.split(" "))
    val pairs = words.map(word => (word, 1))
    val wordCounts = pairs.reduceByKey(_ + _)
    val totalWordCount = wordCounts.updateStateByKey(updateFunction _)
    totalWordCount.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
