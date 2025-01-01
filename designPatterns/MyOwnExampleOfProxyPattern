package com.example

object MyOwnExampleOfProxyPattern extends App {
  
  /*
  Proxy Pattern:
  The Proxy pattern provides a surrogate or placeholder for another object. It acts as an intermediary or control 
  point to manage access to the real object, allowing additional functionality to be added without 
  modifying the original object’s code. The proxy pattern is useful in lazy loading scenarios where the proxy objects 
  instantiates the real object only when required.
  
  It is also useful in situations where you want to control access to an object or add some extra behavior around the 
  object’s operations. This can be achieved by having a Proxy class that mimics the interface of the real object 
  and delegates calls to the real object when needed.
   */

  // subject: Page interface
  trait Page {

    def logVisit( userId: String): Unit

    def open() : Unit

  }

  // ReadSubject: Real Page

  class RealPage(url: String) extends Page {

    override def logVisit(userId: String): Unit = println(s"INFO:user [$userId] visits [$url]")

    override def open() : Unit = println(s"opening [$url]")

  }

  // proxy

  class PageProxy(url: String) extends Page  {

    private var realPage: Option[RealPage] = None

    def logVisit( userId: String): Unit = {

      if (realPage.isEmpty) {

        realPage = Option(new RealPage(url))

      }
      realPage.get.logVisit(userId)
    }

    def open(): Unit = {
      println(s"opening [$url]")
    }

  }

  // example

  val page1 = new PageProxy("https://github.com/")
  page1.logVisit("585")

  page1.open()



}
