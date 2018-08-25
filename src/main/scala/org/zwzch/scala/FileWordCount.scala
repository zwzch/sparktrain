import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by zw on 18-8-25.
  */
/*
* 使用sparkStreaming处理文件系统数据
* */
object FileWordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local").setAppName("FileCount")
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    // 监控文件系统moving的方式 移动文件
    val lines = ssc.textFileStream("/home/zw/IdeaProjects/sparktrain/src/main/scala/org/zwzch/resource/")
    val result = lines.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_+_)
    result.print()

    ssc.start()
    ssc.awaitTermination()
  }
}
