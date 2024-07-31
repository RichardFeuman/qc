object Closures extends App {

  // closure - is a function, which uses one or more variable,
  // declared outside the function

  var ntimes=10
  def duplicate(text: String): String={
    val duplicatedText=text.concat(" ")*ntimes
    duplicatedText
  }

  println(duplicate("chocolate"))

}
