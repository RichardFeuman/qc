import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SparkSession}

object RDDS extends App {

  val spark = SparkSession.builder()
    .appName("rdd_basics")
    .master("local")
    .getOrCreate()

  /*
  Создается список ids, который состоит из 5 элементов "id-0", "id-1", "id-2", "id-3", "id-4". Этот список формируется с помощью методов List.fill(5)("id-"), который создает список с пятью элементами "id-" и zipWithIndex, который добавляет индекс к каждому элементу.
Создается Dataset с именем idsDS из списка ids. Для этого список ids преобразуется в DataFrame с одним столбцом типа String, а затем в Dataset.
Создается новый Dataset с именем idsPartitioned, который разбивается на 6 разделов с помощью метода repartition(6). Репартиционирование может улучшить производительность операций над данными, распределенными по кластеру.
Вычисляется количество разделов после репартиционирования с помощью idsPartitioned.rdd.partitions.length и выводится в консоль.
Происходит работа с разделами через mapPartitionsWithIndex. Для каждого раздела выводится информация о разделе и идентификаторе, присутствующем в разделе.
Метод collect приводит к выполнению операций над разделами и сбору результатов обработки в коллекцию.
Таким образом, в коде происходит генерация и обработка идентификаторов, их репартиционирование, вывод информации о разделах и идентификаторах, присутствующих в разделах.
   */

  val ids = List.fill(5)("id-").zipWithIndex.map(x => x._1 + x._2)

  import spark.implicits._
  val  idsDS: Dataset[String] = ids.toDF.as[String]

  val idsPartitioned = idsDS.repartition(6)

  val numPartitions = idsPartitioned.rdd.partitions.length
  println(s"partitions = ${numPartitions}")

  /*Видно, что в патриции номер 3 больше данных, чем в других. При этом партиции номер 2 и 6 - пустуют: */

  /*Чтобы исправить неравномерное распределение по партициями, необходимо более точно задать способ деления на партиции
  (custom partitioner).
  И возможность сделать это появляется только при использовании RDD. */
  idsPartitioned.rdd
    .mapPartitionsWithIndex(
      (partition: Int, it: Iterator[String]) =>
        it.toList.map(id => {
          println(s" partition = $partition; id = $id")
        }).iterator
    ).collect

  //

  // для работы с rdd нужен конкекст
  val sc = spark.sparkContext

  case class Store(
                    state: String,
                    location: String,
                    address: String,
                    latitude: Double,
                    longitude: Double
                  )

  // Способы получения rdd

  //1) считывание из файла

  import scala.io.Source

  def readStores(filename: String) =
    Source.fromFile(filename)
      .getLines()
      //удаляем первую строчку, тк в ней содержатся названия колонок
      .drop(1)
      // данные в колонках разделяются запятой
      .map(line => line.split(","))
      // построчно считываем данные в case класс
      .map(values => Store(
        values(0),
        values(1),
        values(2),
        values(3).toDouble,
        values(4).toDouble)
      ).toList

  val storesRDD = sc.parallelize(readStores("src/main/resources/chipotle_stores.csv"))

  storesRDD.foreach(println)

  // 2. использование метода  .textFile

  val storesRDD2 = sc.textFile("src/main/resources/chipotle_stores.csv")
    .map(line => line.split(","))
    .filter(values => values(0) == "Alabama")
    .map(values => Store(
      values(0),
      values(1),
      values(2),
      values(3).toDouble,
      values(4).toDouble))

  storesRDD2.foreach(println)

  // 3. Из DF в RDD

  // 3.1 Напрямую из DF в RDD[Row]

  val storesDF = spark.read
    .option("header", "true")
    .option("inferSchema", "true")
    .csv("src/main/resources/chipotle_stores.csv")


  val storesRDD3 = storesDF.rdd

  storesRDD3.foreach(println) // [Alabama,Auburn,AL 36832 US,32.606812966051244,-85.48732833164195]

  //3.2 DF -> DS ->  RDD[Store]

  import spark.implicits._

  val storesDS = storesDF.as[Store]
  val storesRDD4 = storesDS.rdd

  storesRDD4.foreach(println) // Store(Alabama,Auburn,AL 36832 US,32.606812966051244,-85.48732833164195)

  //
  //Теперь мы поговорим о похожем преобразовании, называемом mapPartitionsWithIndex.
  //Оно аналогично mapPartitions, но принимает два параметра. Первый параметр представляет собой индекс раздела,
  //а второй - итератор через все элементы внутри после применения того преобразования, которое закодировано функцией.

  /*
  mapPartitions: Этот метод применяет указанную функцию к каждому разделу набора данных, в отличие от map,
  который работает с каждой записью независимо от раздела.
  Преимущество mapPartitions заключается в том, что он позволяет снизить накладные расходы на передачу данных.
   */

  val rdd = sc.parallelize(List(1, 2, 3, 4, 5, 6, 7, 8), 4) // создаем RDD с 4 разделами
  val result = rdd.mapPartitions(iter => Iterator(iter.sum)) // считаем сумму элементов в каждом разделе

  result.collect() // получаем результат: Array(3, 7, 11, 15)


  /* mapPartitionsWithIndex: Этот метод позволяет выполнить трансформацию данных на уровне каждого раздела с возможностью доступа к индексу этого раздела. */
  val rdd2 = sc.parallelize(List(1, 2, 3, 4, 5, 6, 7, 8), 4)
  val result2 = rdd.mapPartitionsWithIndex((index, iter) => Iterator((index, iter.sum))) // возвращает индекс раздела и сумму элементов

  result.collect() // получаем результат: Array((0, 3), (1, 7), (2, 11), (3, 15))


  // my own examples

  val days = List("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

  val daysRDD=sc.parallelize(days)

  val pairs = daysRDD.mapPartitions(iterator=>{
      val daysList=iterator.toList
      val tuple=daysList.map(d=>(d.substring(1,2), d.length))
    tuple.iterator
  }).collect()

  println("my pairs")
  pairs.foreach(println)

  val donuts = List(
    "Glazed donut",
    "Chocolate donut",
    "Sprinkles donut",
    "Jelly-filled donut",
    "Boston cream donut",
    "Vanilla cream donut",
    "Coconut donut",
    "Cinnamon sugar donut",
    "Maple bacon donut",
    "Red velvet donut"
  )

  val donutsRDD=sc.parallelize(donuts).repartition(6)
  println(s"donuts rdd num parts ${donutsRDD.partitions.length}")

  /*Now we will talk about a similar transformation called mapPartitionsWithIndex.
  It is similar to mapPartitions, but takes two parameters. The first parameter is
  the index of the partition and the second is an iterator through all the items within
  after applying whatever transformation the function encodes.*/

  val donutsOrdersWithIds=donutsRDD.mapPartitionsWithIndex(
    (part, iterator)=>{
      val order=iterator.toList
      val orderWithSize=order.map(o=>(part, o, order.length))
      orderWithSize.iterator
    }

  ).collect()

  donutsOrdersWithIds.foreach(println)

  // найдем уникальные значения для location
  val locationNamesRDD: RDD[String] = storesRDD.map(_.location).distinct()

  locationNamesRDD.foreach(println)


  // найдем локацию с самым длинным названием
  implicit val storeOrdering: Ordering[Store] =
    Ordering.fromLessThan[Store]((sa: Store, sb: Store) => sa.location.length < sb.location.length)

  val longestLocationName = storesRDD.max().location

  println(s"location = $longestLocationName") // Birmingham

  // отфильтруем данные
  val locationRDD = storesRDD.filter(_.location == longestLocationName)

  locationRDD.foreach(println)




}
