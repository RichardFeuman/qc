object recursion extends App {
  def nbYear(p0: Int, percent: Double, aug: Int, p: Int): Int = {
    // your code
    var currP=0
    var counter=0
    var total=p0
    if(total>=p){
      p0
    } else {
      currP=nbYear(p0, percent, aug, p)
      total=p0+currP
      println(total)
      counter=counter+1
      println(counter)
      //(counter, currP)
      total
    }
  }

  val s=nbYear(p0=1000, percent=0.02, aug=50, p=1200)
  println(s)

  // 2 - using if/else

  /*def max2(ints: List[Int]): Int = {
    @tailrec
    def maxAccum2(ints: List[Int], theMax: Int): Int = {
      if (ints.isEmpty) {
        return theMax
      } else {
        val newMax = if (ints.head > theMax) ints.head else theMax
        maxAccum2(ints.tail, newMax)
      }
    }
    maxAccum2(ints, 0)
  }
  // right solution for Growth of a Population
  def nbYear(p0: Int, percent: Double, aug: Int, p: Int): Int = {
    // your code
    if (p0 >= p) 0 else 1 + nbYear((p0+p0*percent/100+aug).toInt,percent,aug,p)
  } */
}
