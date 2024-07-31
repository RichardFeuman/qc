package com.example

import sun.jvm.hotspot.HelloWorld.fib

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object LazyEval extends App {

  class Strict {
    val e = {
      println("strict")
      9
    }
  }

  /*
  Когда перед val объявляется lazy, компилятор не будет немедленно вычислять выражение,
  а выполнит его при первом обращении. При последующих обращениях он просто вернет результат без каких-либо
  дополнительных вычислений или выполнения. Во время вычисления ленивого val весь экземпляр блокируется, что означает,
  что когда разные ленивые val запрашиваются из нескольких потоков, вычисление над ними будет происходить
  последовательно.
   */

  class LazyVal {
    lazy val l ={
      println("lazy")
      9
    }
  }

  val x=new Strict;

  val y=new LazyVal;

  x
  y

  // вычисление происходит при первом обращении,а затем используются уже вычисленные
  println(x.e)

  println(y.l)


  /*
  В приведенном случае в синглтоне ValStore есть два ленивых val. Если мы попытаемся получить к ним доступ из разных
  потоков, один из них должен будет ждать, пока не завершится первое выполнение.

В приведенном ниже примере, когда первая инициализация (4) выполняет вычисление для ленивого val fortyFive (1),
другая инициализация (5) не может выполнить вычисление для другого ленивого val fortySix (2), потому что весь экземпляр
ValStore заблокирован до завершения первой инициализации.
   */


  /*
  Пояснение
Lazy Val: В ValStore определены два ленивых значения (lazy val) fortyFive и fortySix, которые вычисляют Fibonacci числа для 45 и 46 соответственно, когда к ним впервые обращаются. Из-за ключевого слова lazy, каждое значение будет вычислено только один раз при первом доступе, а затем сохранится для последующего использования.
Модуль Scenario1:
Внутри объекта Scenario1 определен метод run, который запускает асинхронные вычисления.
Future.sequence(Seq(...)) создает последовательность будущих вычислений, которые выполняются параллельно. В данном случае, это два Future, которые будут выполнять печать сообщения и доступ к ленивым значениям fortyFive и fortySix.
Первый Future:
Первый Future выводит сообщение "starting (45)", затем обращается к ValStore.fortyFive, что приводит к вычислению fib(45) (если оно ранее не было вычислено), и после этого выводит "done (45)".
Второй Future:
Второй Future делает то же самое для fortySix: выводит "starting (46)", обращается к ValStore.fortySix, и после завершения вычисления вывода "done (46)".
Синхронизация
Теперь важно учесть, как работает синхронизация при доступе к ленивым val из нескольких потоков:

Когда один из потоков обращается к fortyFive, экземпляр ValStore блокируется, чтобы защитить критическую секцию, связанную с вычислением. Это означает, что пока одно из значений активно вычисляется, другое значение не может быть вычислено, если они относятся к одному объекту.
Если один поток вычисляет fortyFive, другой поток, пытаясь получить доступ к fortySix, должен будет ждать, пока первый поток завершит свою работу, потому что оба значения принадлежат одному экземпляру объекта (в данном случае ValStore).
Вывод
В результате, можно ожидать, что порядок печати "starting" и "done" будет следующим:

Один из потоков начнет вычисление первого числа, выведет "starting (45)", затем выполнит fib(45), после чего выведет "done (45)".
Второй поток, если он попытается выполнить fib(46), будет заблокирован до завершения первого вычисления, после чего сможет начать свое выполнение, выведя "starting (46)" и затем "done (46)".
Таким образом, код демонстрирует использование ленивых вычислений и асинхронного выполнения в Scala, при этом обеспечивая синхронизацию для работы с общими ресурсами.


   */

  object ValStore {
    lazy val fortyFive = fib(45)                   // (1)
    lazy val fortySix  = fib(46)                   // (2)
  }
  object Scenario1 {
    def run = {
      val result = Future.sequence(Seq(            // (3)
        Future {
          println("starting (45)")
          ValStore.fortyFive                       // (4)
          println("done (45)")
        },
        Future {
          println("starting (46)")
          ValStore.fortySix                         // (5)
          println("done (46)")
        }
      ))
      Await.result(result, 1.minute)
    }
  }

  /*
  Call-by-Name (CBN) is one of those Scala features that had more confused programmers than happy programmers.
  It’s deceptively powerful, often abused, most of the time misused,
  almost never employed to its full potential, and it has a terrible name.
   */

  def byValueFunction(x: Long): Unit = {
    println(x)
    println(x)
  }

  def byNameFunction(x: => Long): Unit = {
    println(x)
    println(x)
  }


  byNameFunction(System.nanoTime())

}
