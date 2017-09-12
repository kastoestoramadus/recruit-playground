package walidus.finagle

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, Props}
import com.twitter.finagle.httpx
import com.twitter.finagle.httpx.{Response, Version}

import scala.concurrent.duration._

//Schedules to send the "foo"-message to the testActor after 50ms



class ProccessingActor extends Actor{
  import scala.concurrent.ExecutionContext.Implicits.global
  val second = new FiniteDuration(1000L, TimeUnit.MILLISECONDS)
  override def receive: Receive = endAfter(0 milliseconds)

  def endAfter(after: FiniteDuration): Receive = {
    case SecondDelay(v) =>
      val newTime = after.plus(second)
      context.system.scheduler.scheduleOnce(newTime, self, SendResponse(v, sender(), newTime))
      context become endAfter(newTime)
    case SendResponse(v, sender, time) =>
      context become endAfter(after.minus(second))
      sender ! Response(v, httpx.Status.Ok)
      println(s"sended after: $time and from name: ${self.toString()}")
  }

}

object ProccessingActor {
  def props() = Props(new ProccessingActor())
}

case class SecondDelay(v: Version)
case class SendResponse(v: Version, sender: ActorRef, delay: FiniteDuration)