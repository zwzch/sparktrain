package project.spark.dao

import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.util.Bytes
import project.spark.domain.{CourseClickCount, CourseSearchClickCount}
import project.spark.utils.HBaseUtil

import scala.collection.mutable.ListBuffer

/**
  * Created by zw on 18-9-1.
  */
object CourseClickSearchDAO {
  //Java + Scala
  val tableName = "crease_serach_clickcount"
  val cf = "info"
  val qualifer = "click_count"


  /**
    * 保存数据到HBase
    * @param list  CourseClickCount集合
    */
  def save(list: ListBuffer[CourseSearchClickCount]): Unit = {

    val table = HBaseUtil.getInstance().getTable(tableName)

    for(ele <- list) {
      table.incrementColumnValue(Bytes.toBytes(ele.day_search_course),
        Bytes.toBytes(cf),
        Bytes.toBytes(qualifer),
        ele.click_count)
    }

  }


  /**
    * 根据rowkey查询值
    */
  def count(day_search_course: String):Long = {
    val table = HBaseUtil.getInstance().getTable(tableName)

    val get = new Get(Bytes.toBytes(day_search_course))
    val value = table.get(get).getValue(cf.getBytes, qualifer.getBytes)

    if(value == null) {
      0L
    }else{
      Bytes.toLong(value)
    }
  }

  def main(args: Array[String]): Unit = {


    val list = new ListBuffer[CourseSearchClickCount]
    list.append(CourseSearchClickCount("20171111_www.baidu.com_8",8))
    list.append(CourseSearchClickCount("20171111_www.baidu.com_9",9))
    list.append(CourseSearchClickCount("20171111_www.baidu.com_1",100))

    save(list)

//    println(count("20171111_8") + " : " + count("20171111_9")+ " : " + count("20171111_1"))
  }
}
