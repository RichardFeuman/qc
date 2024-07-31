package com.example
import org.apache.spark.sql
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, SparkSession}
object ExecutionPlanTask1 extends App {


  /*
  Project - обозначает select. Однако Project будет возникать не только тогда, когда в коде явно прописан select, но и
  тогда, когда описываемые действия потребуют выборки данных. Например, .withColumn возвращает новый датасет, содержащий новую колонку.
  Чтобы это произошло, необходимо выбрать данные из первоначального датасета, а значит, в плане появится Project.
  Вообще,  план не изменится, если написать код следующим образом:



  LocalTableScan [points#35] - считываем данные; хотя нами были заданы две колонки, только одна будет учавствовать в вычислениях, поэтому вторая не учитывается;
  HashAggregate [partial_sum] - выполнение вычислений; наша задача - найти сумму всех баллов (points); при этом мы имеем данные, разбросанные по партициям, поэтому оптимальнее будет выполнить необходимые вычисления (промежуточные вычисления) в каждой партиции, затем промежуточные вычисления поместить в одну партицию и на их основании выдать общий результат. Данных в промежуточных вычислениях будет гораздо меньше, так что их перемещение между партициями произойдет быстрее, чем если бы мы перемещали все имеющиеся в начале данные. Более подробно процесс представлен на иллюстрации ниже.
  Exchange SinglePartition - промежуточные вычисления из разных партиций собрали в одной партиции
  HashAggregate [sum] - вычислили итоговую сумму


  Range - обозначает генерацию данных (например,  spark.range(1, 500000000, 5))
  Exchange и Sort - обозначают операции, нацеленные на подготовку данных к join (т.е. перетасовку и сортировку). Стоит отметить, что разделение по партициями для join происходит на основе hashpartitioning, т.е. берется хэш ключа, что и гарантирует попадание данных с одним и тем же ключом в одну партицию, ведь хэш для одних и тех же значений - один и тот же
  SortMergeJoin - означает join, в скобках указаны колонки, на основании которых происходит объединение
  Project [id#60L] - т.к. после объединения появляются две колонки с одними и теми же данными (колонки, на основании которых происходил join), то имеет смысл оставить только одну из этих колонок, чтобы не хранить избыточные данные, поэтому и делается select

   */

  val spark = SparkSession.builder()
    .appName("exec_plan")
    .master("local")
    .getOrCreate()

  val simpleSchema = StructType(Array(
    StructField("department", StringType, true),
    StructField("salary", IntegerType, true)
  ))

  val depSalDF: DataFrame=
    spark.read
      .schema(simpleSchema)
      .option("header", true)
      .csv("src/main/resources/employees.csv")
      .select("department", "salary")


  def calcAvg(groupByCol: String, measure: String)(df: DataFrame) : DataFrame ={
     df.groupBy(groupByCol)
      .agg(sql.functions.avg(measure)
        .as(s"avg_per_$groupByCol"))
  }

  val depAvgSalDF: DataFrame= depSalDF
    .transform(calcAvg("department", "salary"))

  depAvgSalDF.explain()

  depAvgSalDF.show()

}
