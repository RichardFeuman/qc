object Arrays extends App {

  // arrays stores the elements of the same type

  val arr1: Array[Int] = new Array[Int](5)
  println(arr1)
  // assign elemtns to array
  arr1(0)=6
  println(arr1.mkString(","))


  // iter over arr

  for(i<-0 to (arr1).length-1){
    println(arr1(i))
  }

  // дефолтное значение элемента для массива строк - null,
  // для массива целых чисел 0,
  // для double 0.0

  /*val strArr1=Array("jit", "yik","liv")

  val strArr2=Array("jit2", "yik2","liv2")
  // конкатенация массивов
  val concatenated=strArr1.concat(strArr2)

  for(i<-0 to (concatenated).length-1){
    println(concatenated(i))
  }*/
}
