object PatternMatching extends App {

  val i: Any=25

  i match {
    case v: Int => println(s"Int $v")
    case v: String => println(s"String $v")
    case v: Boolean => println(s"Boolean $v")

  }

  // структурный паттерн матчинг

  /* Структурный паттерн матчинг в Scala - это мощный механизм, который позволяет
  обрабатывать данные на основе их структуры. Он широко используется для сопоставления шаблонов
  и выполнения соответствующих действий в зависимости от структуры данных.

Паттерн матчинг в Scala позволяет писать более краткий, выразительный и безопасный код.
Он особенно удобен для работы с алгебраическими типами данных, такими как case-классы и sealed-трейты.*/

  sealed trait Animal{
    def name: String
    def age: Int

    def whoIam: Unit = this match {
      case Dog(name, age) => println(s"Dog -$name")
      case Cat(name, age) => println(s"Cat -$name")
    }
  }

  case class Dog(name: String, age: Int) extends Animal

  case class Cat(name: String, age: Int) extends Animal


  /*Для того, чтобы использовать структурный паттерн матчинг в Scala, не обязательно создавать
  объект-компаньон с методом unapply. Однако, часто для удобства и прозрачности кода создание
  объекта-компаньона с методом unapply может быть полезным.
  Пример без использования объекта-компаньона с методом unapply:*/

  object Dog {
    def unapply(d: Dog): Option[(String, Int)] = {
      if(d.age<6) Some((d.name, d.age)) else None

    }
  }


  // матчинг на литерал
  /* Матчинг на литерал в Scala - это способ сопоставления значений константных литералов в
  выражениях матчинга (pattern matching). Например, при помощи конструкции матчинга в Scala
  можно проверить значение переменной и выполнить определенное действие в зависимости от того,
  какому литералу оно соответствует.*/
  //case
  case class Coffee(name: String, cheesePena: Boolean)

  val coffee1=new Coffee("latteX2", true)

  val coffee2=new Coffee("capuccinoX2", true)

  coffee1 match {
    case Coffee("latteX2", _) => println("this is latte")
    case Coffee("capuccinoX2", _) => println("this is capuccino")
  }

  // матчинг на константу
  // чтобы такой матчинг работал, константа должна быть с большой буквы

  case class IceCream(taste: String, price: Double)

  val iceCream1=IceCream("Strawberry", 4.5)

  val IceCream1Taste="Strawberry"

  iceCream1 match {
    case IceCream(IceCream1Taste, _) => println("i don't like this taste")
    case _ => "unkwown ice-cream"
  }

  /*матчинг с условием (гарды - это if) */

  iceCream1 match {
    case IceCream(taste, _) if taste.startsWith("S") => println("Looks like strawberry")
    case _ => println("strange taste")
  }

  /* as patten */

  def addDiscount(ic: IceCream): IceCream = ???

  val asPatExample=iceCream1 match {
    case ic @ IceCream(_, price) if price>5 => addDiscount(ic)
    case _ => IceCream
  }
  println(asPatExample)

  // паттерн матчинг со списками

  val l=List(5,8,13,15)

  val lMatch=l match {
    case List(5,_,_,_) => "5 first"
    case List(5,8,_,_) => "5 first, 8 -2nd"
    case List(5,8,_,_) => "5 first, 8 -2nd"
    case List(5,8,_*) => "5 first, 8 -2nd and other any elems"
  }
  println(lMatch)


  val lMatch2=l match {
    case head::next => head
    case li:+last => last
    // список, который начинается с единицы и дальше взять последний элемент
    case List(1,_*) :+last => last
  }

  println(lMatch2)


}
