import HOF.{isOdd, logRunningTime}

import scala.::

case object HOF extends App {

  def math(x: Double, y: Double, f:(Double, Double)=>Double): Double = f(x,y);

  println(math(18,81, (x,y)=>x%y))

  // more complicated
  def math2(x: Double, y: Double, z: Double, f:(Double, Double)=>Double): Double = f(f(x,y),z);

  println(math2(18, 18, 18, (x,y)=>x*y))

  // using underscore

  println(math2(18, 28, 56, _ max _))


  def isOdd(i: Int): Boolean = i%2 !=0

  def isEven(i: Int): Boolean = !isOdd(i)

  def filterListOdd(l : List[Int]) : List[Int] ={
    var acc = List[Int]()
    for(i<-l){
      if(isOdd(i)) acc = i :: acc
    }
    acc.reverse
  }

  println(filterListOdd(List(9,5,4,3,2,9,8)))

  // аналогично можем создать функцию для фильтрации четных элементов

  def filterList(l: List[Int])(p: Int => Boolean) : List[Int] = {
    var acc = List[Int]()
    for (i <- l) {
      if (p(i)) acc = i :: acc
    }
    acc.reverse
  }


  // обертки
  def logRunningTime[A, B](f: A=> B): A => B = a  => {
    val start = System.currentTimeMillis()
    val b= f(a)
    val end = System.currentTimeMillis()
    println(s"Time : ${end - start}")
    b
  }

  val isOddWithRunningTime = logRunningTime(isOdd)
  isOddWithRunningTime(10) // time

  // invert
  // измененине поведения
  def inv[A](f: A => Boolean): A=> Boolean = a => {
    !f(a)
  }

  val isEven2: Int => Boolean = inv(isOdd)

  // изменение самой функции
  // посокльку мы хотим вернуть функцию, мы выбираем переменную с
  // аргументом b
  def partial[A, B, C](a: A, f: (A, B) => C): B => C = b =>{
    f(a, b)
  }

  // смотрим как метод partial может использоваться на практике
  def sum(x: Int, y: Int): Int= x + y

  val sumPart: Int => Int = partial(2,sum)

  //
  val alpha=('a' to 'z').zipWithIndex.map(p=>(p._1, p._2+1))
  //println(alpha)
  val alphaMap=alpha.toMap
  println(alphaMap)

  //def scorer(word: String)=word.toList.sorted.zipWithIndex.map(p=>p._2).foldLeft(0)((a,b) => a+b+1)

  def scorer(word: String)={
    val score= alphaMap.filter(w => word.contains(w._1)).values.toList.sum
    score
  }

  def high(s: String)= {

    val words = s.split("[^-A-Za-z]+").mkString(" ")

    val listOfwords=words.split(" ")
    val scores=listOfwords.map(w=>(w, scorer(w)))

    val mostFreq=scores.toList.sortWith(_._2<_._2).last._2
    println("m"+mostFreq)
      //.sortBy(r=>listOfwords.zipWithIndex.toMap.get(r._1))
      //.zipWithIndex.sortWith(_._2<_._2)
      val mostFreqWord=scores.sortWith((a, b) => a._2 > b._2 || (a._2 == b._2 && listOfwords.indexOf(a._1) < listOfwords.indexOf(b._1)))
    mostFreqWord.head._1
    //val groupedWords = words.groupBy(word => word)

    // val wordsWithCount = groupedWords.mapValues(_.size)

    // val mostFreq=wordsWithCount.toMap.toList.sortWith(_._2>_._2).head._1



    // val mostFreq=words.sorted.head

    /*
  val words = List("the", "the", "water")
  val groupedWords = words.groupBy(word => word)
  println(groupedWords)
  val wordsWithCount = groupedWords.mapValues(_.size)
  println(wordsWithCount)
    */


  }
  println(high("""high("man i need a taxi up to ubud")"""))
}



