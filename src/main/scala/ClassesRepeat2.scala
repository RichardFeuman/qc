package com.example

object ClassesRepeat2 extends App {

  // чтобы задать первичный конструктор класса, нужно в теле класса задать
  // переменные var
  class Cat(var name: String, var age: Int);

  val Jack=new Cat("Jack", 3)
  println(Jack.name)
  println(Jack.age)
  // изменить значения полей можно так
  Jack.name="Jacki Chan"
  Jack.age=2
  println(Jack.name)
  println(Jack.age)


  // если убрать var и val перед параметрами в конструкторе класса, то будет ошибка
  class Bird(name: String, age: Int);

  val bird1=new Bird("Flappy", 5)

  //println(bird1.name)
  //println(bird1.age)

  // если определить параметр конструктора класса, то мы можем получить доступ к нему только в са
  // мом классе, но не вне его
  class Cubit(private var state: Int, private var volume: Int);

  val c1=new Cubit(1, 5)
  // будет ошибка, так как обращаемся к приватно переменной вне класса
  // println(c1.state)

  // но можно получить доступ к приватным переменным в самом классе
  class Cubit2(private var state: Int, private var volume: Int) {
    def outCubic = {println(state)}
  }
  val cubic2=new Cubit2(2, 12)
  cubic2.outCubic

  // var -- getter available, setter also
  // val -- getter available, setter not
  // default getter not available, setter not available

}
