import java.net.InetSocketAddress

import akka.util.Timeout
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.httpx.path._
import com.twitter.finagle.httpx._
import com.twitter.finagle.{Httpx, Service}
import com.twitter.finagle.httpx
import com.twitter.{util => twitter}
import akka.actor._
import akka.pattern.ask
import scala.concurrent.Future

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

object ServerSetup extends App{
  val system = ActorSystem("HelloSystem")
  import scala.concurrent.ExecutionContext.Implicits.global

  val service = new Service[httpx.Request, httpx.Response] {
    var counter: Int = 0
    val workers: Array[ActorRef] = Array(
      system.actorOf(Props[ProccessingActor]),
      system.actorOf(Props[ProccessingActor])
    )

    def apply(req: httpx.Request): twitter.Future[Response] = {
      counter = counter + 1
      (req.method, Path(req.path)) match {
        case Method.Get -> Root =>
          twitter.Future.value {
            httpx.Response(req.version, httpx.Status.Ok)
          }
        case Method.Get -> Root / "foo" / "bar" =>
          (workers(counter%1) ? SecondDelay(req.version))(Timeout(10000000))
            .mapTo[Response]
      }
    }
    implicit def toTwitterFuture[T](f: Future[T]): twitter.Future[T] = {
      val promise = twitter.Promise[T]()
      f.onComplete(promise update _)
      promise
    }
    implicit def scalaToTwitterTry[T](t: Try[T]): twitter.Try[T] = t match {
      case Success(r) => twitter.Return(r)
      case Failure(ex) => twitter.Throw(ex)
    }
  }
  val socketAddress = new InetSocketAddress(8000)
  val server = ServerBuilder()
    .codec(Http())
    .bindTo(socketAddress)
    .name("HTTP endpoint")
    .build(service)
}
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