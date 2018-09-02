package project.spark.domain

/**
  * Created by zw on 18-9-1.
    day_course rowkey 20180808_1
    click
  */

case class CourseClickCount(day_source:String, click_count:Long)

case class CourseSearchClickCount(day_search_course:String, click_count:Long)