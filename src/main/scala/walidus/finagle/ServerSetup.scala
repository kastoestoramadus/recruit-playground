package walidus.finagle

import java.net.InetSocketAddress

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.{Service, httpx}
import com.twitter.finagle.httpx._
import com.twitter.finagle.httpx.path._
import com.twitter.{util => twitter}

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
  }
  val socketAddress = new InetSocketAddress(8000)
  val server = ServerBuilder()
    .codec(Http())
    .bindTo(socketAddress)
    .name("HTTP endpoint")
    .build(service)
}
