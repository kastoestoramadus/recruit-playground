package walidus.finagle

import akka.actor.Actor
import com.twitter.finagle.httpx
import com.twitter.finagle.httpx.{Response, Version}

object ServiceOfWork {
  def proccess() = Thread.sleep(1000)
}

class ProccessingActor extends Actor{

  override def receive: Receive = {
    case SecondDelay(v) =>
      ServiceOfWork.proccess() // working for 1000 milis
      sender ! Response(v, httpx.Status.Ok)
  }
}

case class SecondDelay(v: Version)