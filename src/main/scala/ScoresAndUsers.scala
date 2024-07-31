package com.example

import org.apache.spark.HashPartitioner
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object ScoresAndUsers extends App {


  /* users
  8332580140144072600 lepjzzhpkateeljoe
  3635180579564555424 snxqazlijskdjwkjv
  9150666637555683215 vdunspkilotrhawhc
   */

  /* scores
  6977350426233954568 8.2
  6403437648187212006 9.7
  1545640705787603467 4.9
   */
  val spark = SparkSession.builder()
    .appName("scores_and_users")
    .master("local")
    .getOrCreate()

  val sc = spark.sparkContext

  import spark.implicits._

  // user_id, name
  val usersRDD = sc.textFile(s"src/main/resources/users.txt")
    .map{ line =>
      val fields = line.split(" ")
      (fields(0).toLong, fields(1))
    }

  // user_id, score
  val scoresRDD = sc.textFile(s"src/main/resources/scores.txt")
    .map{ line =>
      val fields = line.split(" ")
      (fields(0).toLong, fields(1).toDouble)
    }

  // user_id, score, name
  val joinedRDD: RDD[(Long, (Double, String))] = scoresRDD.join(usersRDD)

  // 8332580140144072600, lepjzzhpkateeljoe

  // (a, b) = score, name; if score_acc>score_curr then score_acc else score_curr
  // после того, как с помощью reduceByKey найдены макс оценки
  // фильтруем тех пользователей, у которых оценка >=9.0
  val resultRDD = joinedRDD
    .reduceByKey((a, b) =>
      if (a._1 > b._1) a else b)
    .filter(_._2._1 >= 9.0)


  /* Вариант 2:
  2 Провести предварительные вычисления - этим мы уменьшим количество данных, задействованных при join.
  */

  val maxScoresRDD: RDD[(Long, Double)] = scoresRDD
    .reduceByKey(Math.max)
    .filter(_._2 >= 9.0)

  val joined2RDD: RDD[(Long, (Double, String))] = maxScoresRDD.join(usersRDD)


  /* Вариант 3:
  3 Можно пойти еще дальше и вдобавок к предварительным вычислениям добавить одинаковое партицирование,
  что позволит сократить время, требуемое для shuffle
  Здесь стоит отметить, что используется не только один и тот же HashPartitioner, но и количество партиций выбирается
  одинаковое
  (для данного примера - 10) - именно это и позволяет избежать перераспределения (shuffle) данных при join.
   */

  /*
  HashPartitioning is a Catalyst Partitioning in which rows are distributed across partitions using the MurMur3 hash (of the partitioning expressions) modulo the number of partitions.

  HashPartitioning is an Expression that computes the partition Id for data distribution to be consistent across shuffling and bucketing (for joins of bucketed and regular tables).
   */
  val users3RDD = sc.textFile(s"src/main/resources/users.txt")
    .map{ line =>
      val fields = line.split(" ")
      (fields(0).toLong, fields(1))
    }.partitionBy(new HashPartitioner(10))

  /*
Выражение usersRDD.partitioner match проверяет, если ли partitioner (именно partitioner определяет,
к какой партиции будет отнесена запись) у usersRDD. Если partitioner отсутствует, то создаем новый partitioner
(new HashPartitioner), который будет состоять из указанного числа партиций (в данном случае число партиций
определяем через usersRDD.getNumPartitions).
Соответственно, если partitioner существует (case Some(partitioner)), то применяем его.
 */

  val scoresPartitioner = {
    users3RDD.partitioner match {
      case None => new HashPartitioner(users3RDD.getNumPartitions)
      case Some(partitioner) => partitioner
    }
  }

  val repartitionedSoresRDD = scoresRDD.partitionBy(scoresPartitioner)

  val maxScores3RDD: RDD[(Long, Double)] = repartitionedSoresRDD
    .reduceByKey(Math.max)
    .filter(_._2 >= 9.0)



  val joined3RDD: RDD[(Long, (Double, String))] = maxScores3RDD.join(users3RDD)

}
