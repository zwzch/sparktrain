package project.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import project.spark.dao.{CourseClickDAO, CourseClickSearchDAO}
import project.spark.domain.{CourseClickCount, CourseSearchClickCount}
import project.spark.utils.DateUtils

import scala.collection.mutable.ListBuffer

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
//    messages.map(_._2).count().print
    //测试步骤2 数据清洗
    //"GET /learn/821 HTTP/1.1"
    val logs = messages.map(_._2)
    val cleanInfo = logs.map(line =>{
         val infos = line.split("\t")
         val url = infos(2).split(" ")(1)
         var courseId = 0
      //拿到课程编号
      if(url.startsWith("/class")){
        val courseIdHtml = url.split("/")(2)
        courseId = courseIdHtml.substring(0, courseIdHtml.lastIndexOf(".")).toInt
      }
      ClickLog(infos(0), DateUtils.parseToMinute(infos(1)), courseId, infos(3).toInt, infos(4))
    }).filter(ClickLog => ClickLog.courseId != 0)

//    cleanInfo.map(x => {
//      //Hbase的 RowKey的设计 20171111_88
//      ((x.time.substring(0,8)) + "_" + x.courseId,1)
//    }).reduceByKey(_+_).foreachRDD( rdd =>
//      rdd.foreachPartition( partRecords => {
//        val list = new ListBuffer[CourseClickCount]
//        partRecords.foreach(pair => {
//          print(pair._1, pair._2)
//          list.append(CourseClickCount(pair._1, pair._2))
//        })
//        CourseClickDAO.save(list)
//      })
//    )

    //统计从搜索引擎来的refer
    cleanInfo.map(x => {
      //http://www.baidu.com/s?wd=大数据面试
      val refers = x.refer.replace("//","/").split("/")
      var hosts = "";
      if (refers.length > 2){
        hosts = refers(1)
      }
      (hosts, x.courseId, x.time)
    }).filter(_._1 != "").map(x => {
      ((x._3.substring(0,8) + "_" + x._1 + "_" + x._2),1)
    }).reduceByKey(_+_).foreachRDD( rdd =>
      rdd.foreachPartition( partRecords => {
        val list = new ListBuffer[CourseSearchClickCount]
        partRecords.foreach(pair => {
          print(pair._1, pair._2)
          list.append(CourseSearchClickCount(pair._1, pair._2))
        })
        CourseClickSearchDAO.save(list)
      })
    )
    ssc.start()
    ssc.awaitTermination()
  }

}
case class ClickLog(ip:String, time:String, courseId:Int, statusCode:Int, refer:String )
