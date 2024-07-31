package com.example

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

object TransformExample extends App {

  val custSchema = StructType(
    Array(
      StructField("ap_id", IntegerType, true),
      StructField("ap_state", StringType, true),
      StructField("ap_salary", IntegerType, true)
    )

  )

  val spark = SparkSession.builder()
    .appName("transform_example")
    .master("local[*]")
    .getOrCreate()

  val apPath = "src/main/resources/ap_data.csv"

  val DF=spark.read.option("delimiter", ",").option("header", "true").csv(apPath)

  def doubleSal(df: DataFrame) : DataFrame ={
    df.withColumn("double_salary", df("salary")*2)
  }

  val DF2=DF.transform(doubleSal)
  DF2.show()
}
