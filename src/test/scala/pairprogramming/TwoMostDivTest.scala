package pairprogramming

import org.scalatest.{FlatSpec, Matchers}

class TwoMostDivTest extends FlatSpec with Matchers{
  "TwoMostDiv" should "return two different numbers with most divisors" in {
    //TwoMostDiv.mostTwoDivs(Seq(12, 15, 5)) shouldEqual( (12, 15))
  }

}

object TwoMostDiv {
  def give(): Unit = {

  }

  def mostTwoDivs(numbs: Seq[Int]): (Int, Int) = {
    val seq = numbs.distinct.map{ el =>
      val divs = (0 to 5000).count(el%_==0)
      (divs, el)
    }.sortWith((f,s)=>f._1 > s._1).take(2).map(_._2)
    (seq.head, seq(1))
  }

}


/*
const words = ["aaaa", "Baaa","Caaa","aaaa","Gaaa","aaaa","Daaa","aaaa","aaaa"];

function isUpper(word) {
	const code = word.charCodeAt(0)
	return code >= 65 && code <= 90;
}

const result = words.map(word => isUpper(word) ? 'Who? Sarah' : 'What? singing');

console.log(result);
 */