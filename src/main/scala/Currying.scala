object Currying extends App {

  //currying is the technique of transformation
  // a function that takes multiple arguments into
  // a function that takes a single argument

  def add(x: Int, y: Int)= x + y;

  def add2(x: Int) = (y: Int) => x+y

  def accum(x: Int) = (y: Int) => x*y

  val accumulate=accum(56)
  println(accumulate(45))

  // simple notation for curried function
  def fuelTracker(mileage: Int)(perMile: Double) = mileage*perMile

  println(fuelTracker(1000)(1.5))

  // we can use partial application with curring function

  val u=fuelTracker(1000)_ // insted underscore we can pass any other argument
  println(u(1.0))



}
