import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by zw on 18-8-25.
  */
object TransformApp {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
    /*
    * 创建StreamingContext 需要 sparkconf 和 batch interval
    * */
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    /*
    * 构建黑名单
    * 正常在数据里
    * */
    val blacks = List("zs","ls")
    val backsRDD = ssc.sparkContext.parallelize(blacks).map(x => (x,true))
    val lines = ssc.socketTextStream("localhost", 6789)
    val checkLog = lines.map(x=>(x.split(",")(1),x)).transform(rdd =>{
      rdd.leftOuterJoin(backsRDD)
        .filter(x=>x._2._2.getOrElse(false) != true)
        .map(x=>x._2._1)
    })
    checkLog.print()
    //名字 和完整的信息
    //transform DStream和RDD做关联
    ssc.start()
    ssc.awaitTermination()
  }
}
