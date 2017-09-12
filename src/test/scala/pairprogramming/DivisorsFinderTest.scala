package pairprogramming

import org.scalatest.{FlatSpec, Matchers}

class DivisorsFinderTest extends FlatSpec with Matchers {
    "Finder" should " find with most divisors" in {
        DivisorsFinder.find(Seq(12,5)) shouldEqual(12)
    }
}

object DivisorsFinder {
    def find(numbers: Seq[Int]): Int = {
        numbers.map(el => (countDivisors(el), el))
              .maxBy(el => el._1)._2
    }
    def countDivisors(n : Int): Int = {
        if(n==1) 1
        else {
            (2 to n/2).count {el =>
                if(n%el == 0) true else false
            }
        }
    }
}