package com.example

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, count, first, last, lit, max}

object pizzaTest extends App {



  val spark = SparkSession.builder()
    .appName("avocados_processing")
    .master("local")
    .getOrCreate()

  import spark.implicits._

  val pizzaDF = spark.read
    .option("inferSchema", "true")
    .option("header", "true")
    .csv("src/main/resources/pizza_orders.csv")

  pizzaDF.show()
  /*order_type
  orders_total
  address_id
  orders_cnt */

  val ordersCntDF=pizzaDF
    .select(col("order_type"),
      col("order_id"),
      col("address_id"))
    .withColumn("orders_total_per_type", count("order_id").over(Window.partitionBy("order_type")) )
    .withColumn("max_order_size", max("orders_total_per_type").over(Window.partitionBy(lit(1))) )
    .withColumn("orders_total_per_address", count("order_id").over(Window.partitionBy("order_type", "address_id")) )
    .select(col("order_type"), col("address_id"), col("orders_total_per_type"), col("orders_total_per_address"))
    .distinct

  ordersCntDF.show()

  /*val maxOrdersCntDF=ordersCntDF
    .select(col("order_type") as "order_type_maxi" ,
      col("orders_total") as "orders_cnt" ,
      col("address_id") as "address_id_maxi"
    )
    .filter(col("orders_total") === col("max_order_size") ).select("*") */

  val maxOrdersCntDF=ordersCntDF
    .select(
      col("order_type") as "order_type_maxi",
      last(col("orders_total_per_address")).over(Window.partitionBy("order_type").orderBy("orders_total_per_address").rangeBetween(Window.currentRow, Window.unboundedFollowing)) as "orders_cnt",
      last(col("address_id")).over(Window.partitionBy("order_type").orderBy("orders_total_per_address").rangeBetween(Window.currentRow, Window.unboundedFollowing)) as "address_id_maxi"
    ).distinct

        //.over as "orders_cnt" ,
      //col("address_id") as "address_id_maxi"




  maxOrdersCntDF.show()

  // , "address_id" , "address_id_maxi"
  val joinCondition = ordersCntDF("order_type") == maxOrdersCntDF("order_type_maxi") && ordersCntDF("address_id") == maxOrdersCntDF("address_id_maxi")

  val cols=List(col("order_type_maxi"), col("orders_total_per_type"), col("address_id_maxi"), col("orders_cnt"))

  val joinedDF = ordersCntDF.join(maxOrdersCntDF,
    ordersCntDF.col("order_type") === maxOrdersCntDF.col("order_type_maxi")
      && ordersCntDF("address_id") === maxOrdersCntDF("address_id_maxi")
  ).select(cols: _*)
  //.join(maxOrdersCntDF, joinCondition, "inner")

  joinedDF.show()

}
