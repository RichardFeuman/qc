package com.example

import org.apache.spark.sql.SparkSession

object RepartitionCoalesce extends App {

  val spark = SparkSession.builder()
    .appName("repartition_and_coalesce")
    .master("local[*]")
    .getOrCreate()

  val sc = spark.sparkContext // access point to low level api

  /*
  Используйте Spark Repartition() когда данные должны быть равномерно распределены по разделам для
  повышения эффективности параллельной обработки.
  Используйте Spark Coalesce() когда количество разделов необходимо уменьшить для улучшения производительности
  без дорогостоящих полных операций перемешывания.
   */

  val numbers = sc.parallelize(1 to 1000000)

  println(numbers.partitions.length)

  // repartition will incure a shuffle

  Thread.sleep(10000)
  val repart1=numbers.repartition(2)  // uniform data distribution
  println(repart1.partitions.length)

  // coalesce has no shuffle

  val coalNumbers = numbers.coalesce(2) // coalesce does not guarantee the equal distribution

  println(coalNumbers.partitions.length)

  // when youe increase the number of partitioon coalesce is equal to repartition

  System.in.read()

  spark.stop()



}
