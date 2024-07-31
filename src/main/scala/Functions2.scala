object Functions2 extends App {

  // you can assign function to variable

  val fib=(a: Int, b: Int, n: Int) => {val l=List.empty[Int]; while(a<n){val sum=a+b; l:+sum}; l.tail}
  println(fib(1,9,4))

}
