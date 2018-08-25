import java.sql.DriverManager

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by zw on 18-8-25.
  */
/*
* 完成词频登记 统计 将结果写入到mysql
* */
object ForeachRDDApp {

  def updateFunction(values: Seq[Int], runningCount: Option[Int]) = {
    val newCount = values.sum + runningCount.getOrElse(0)
    new Some(newCount)
  }

  def createConnection() = {
    Class.forName("com.mysql.jdbc.Driver")
    DriverManager.getConnection("jdbc:mysql://localhost:3306/sparktest","root","root")
  }
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
    /*
  * 创建StreamingContext 需要 sparkconf 和 batch interval
  * */
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    val lines = ssc.socketTextStream("localhost", 6789)

//    val result = lines.flatMap(_.split(" ")).map((_, 1))
    val result = lines.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_+_)
    result.print()
//    wordCounts.foreachRDD(rdd => {
//      val connection = createConnection()
//      rdd.foreach(record => {
//        val sql = "insert into wordcount(word, wordcount) values('" + record._1 + "',"
//        +record._2 + ")"
//        connection.createStatement().execute(sql)
//      }
//      )
//    }
    result.foreachRDD(rdd=>{
       rdd.foreachPartition(partionOfRecords => {
           val connection = createConnection()
           partionOfRecords.foreach(record =>{
             val sql = "insert into wordcount(word, wordcount) values('" + record._1 + "','"+record._2 + "')"
             print(sql)
             connection.createStatement().execute(sql)
           }
           )
           connection.close()
          })
      })
    /*
    * 写入mysql就不需要checkPoint了
    * 每次都写入mysql中
    * foreachRDD
    * 要保证高效
    * Connection中不能在forEachRDD中创建Connection
    * Connection 可以自己实现连接池
    * */
//    val totalWordCount = wordCounts.updateStateByKey(updateFunction _)
//    totalWordCount.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
