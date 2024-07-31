package com.example

import org.apache.spark
import org.apache.spark.sql
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, udf}

object ExecutionPlan extends App {


  val spark = SparkSession.builder()
    .appName("avocados_processing")
    .master("local")
    .getOrCreate()

  val numbersDF: DataFrame =
    spark.read
      .option("header", true)
      .option("inferSchema", true)
      .csv("src/main/resources/numbers.csv")


  def getSquare: String => Int = (num: String) =>
    num.split("_")(1).toInt


  val getSquareUDF = udf(getSquare)

  val squaresDF = numbersDF.select(
    col("id"),
    getSquareUDF(col("number")).as("number"))

  squaresDF.show(5)

  // без аргументов explain выводит физический план
  squaresDF.explain()

  // extended позволяет увидеть и логический, и физический планы:
  squaresDF.explain(mode="extended")

  // cost позволяет увидеть дополнительную статистику
  // (Statistics) - в нашем случае статистика отображает размер данных, с которыми будет вестись работа:
  squaresDF.explain(mode="cost")


  //formatted - выводит Physical Plan, но уже в отформатированном виде:
  squaresDF.explain(mode="formatted")


  // codegen позволяет увидеть сгенерированный (java) код (Generated Code), который будет в итоге выполнен:
  squaresDF.explain(mode="codegen")


  // задание по воссозданию плана

  import org.apache.spark.sql.types.{IntegerType,StringType,StructType,StructField}


  val simpleSchema = StructType(Array(
    StructField("department", StringType, true),
    StructField("salary", IntegerType, true)
  ))


  val depSalDF: DataFrame=
    spark.read
      .schema(simpleSchema)
      .option("header", true)
      .csv("src/main/resources/salaries.csv")


  val depAvgSalDF: DataFrame=
    depSalDF
      .groupBy("department")
      .agg(sql.functions.avg("salary")
        .as("avg_sal_per_dep"))

  depAvgSalDF.explain()
}
