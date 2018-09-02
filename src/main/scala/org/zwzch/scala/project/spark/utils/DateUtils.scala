package project.spark.utils

/**
  * Created by zw on 18-9-2.
  */
import java.util.Date

import org.apache.commons.lang3.time.FastDateFormat

object DateUtils {

  val YYYYMMDDHHMMSS_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")
  val TARGE_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmss")

  def getTime(time: String): Long = {
    YYYYMMDDHHMMSS_FORMAT.parse(time).getTime
  }

  def parseToMinute(time: String): String = {
    TARGE_FORMAT.format(new Date(getTime(time)))
  }

  def main(args: Array[String]): Unit = {
//    println(parseToMinute("2017-10. Spark Streaming进阶与案例实战-22 14. Spark Streaming整合Flume&Kafka打造通用流处理基础:46:01"))
    print(parseToMinute("2018-09-02 19:49:01"))
  }
}
