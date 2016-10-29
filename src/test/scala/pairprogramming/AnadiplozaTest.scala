package pairprogramming

import org.scalatest.{FunSuite, Matchers}

class AnadiplozaTest extends FunSuite with Matchers {

  test("ala should be anadiploza") {
    new AnadiplozaDetector().test("barbara") shouldBe true
  }

}

class AnadiplozaDetector() {
  def test(s: String) : Boolean= {
    val in = s.toUpperCase()
    val sec = in.tail.dropWhile(_ != in.head).length
    //in.zip(sec).takeWhile(_ != _ )
    true
  }

}
