/**
  * Created by zw on 18-8-25.
  */
import ForeachRDDApp.createConnection
object Test {
  def main(args: Array[String]): Unit = {
    val conn =  createConnection()
    val key =1
    val value = 2
    val sql = "insert into wordcount(word, wordcount) values('" + key + "','"+value + "')"
    print(sql)
    conn.createStatement().execute(sql)
  }
}
