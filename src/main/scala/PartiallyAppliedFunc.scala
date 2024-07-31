import java.util.Date

object PartiallyAppliedFunc extends App {

  // when we use _ in function params list, we reserve
  // the place for argument, which we pass while calling
  // Kolmogorov-Arnold Network
  def KAN(k: String, a: String, n: String) : Map[String, String] = Map(k -> "Kolmogorov", a -> "Arnold", n->"Network")

  val u=KAN("1", "2", _: String)

  println(u("565656"))

  // example 2

  val dt=new Date

  def logger(date: Date, msg: String): Unit ={
    println(s"$date + $msg")
  }

  val log1=logger(dt, _)

  log1("Mexico")


}
