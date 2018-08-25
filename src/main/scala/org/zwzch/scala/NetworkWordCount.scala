import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.SparkContext._
/**
  * Created by zw on 18-8-22.
    测试 nc -lk 6789
  */
object NetworkWordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
    /*
    * 创建StreamingContext 需要 sparkconf 和 batch interval
    * */
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    val lines = ssc.socketTextStream("localhost", 6789)
    val result = lines.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_+_)
    result.print()

    ssc.start()
    ssc.awaitTermination()
  }
}



//
//
//
// start()和awaitTermination()
//
//    streamingContext类的start()函数开始这个流式应用的运行，
//    开始运行后，start()函数返回。调用awaitTermination()，driver将阻塞在这里，直到流式应用意外退出。
//    另外，通过调用stop()函数可以优雅退出流式应用，通过将传入的stopSparkContext参数设置为false，
//    可以只停止StreamingContext而不停止SparkContext
//    （目前不知道这样做的目的）。流式应用退出后，不可以通过调用start()函数再次启动。