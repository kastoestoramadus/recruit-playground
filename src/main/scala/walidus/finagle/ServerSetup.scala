package walidus.finagle

import java.net.InetSocketAddress
import java.util.concurrent.{LinkedBlockingQueue, TimeUnit, ThreadPoolExecutor}

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.{Service, httpx}
import com.twitter.finagle.httpx._
import com.twitter.finagle.httpx.path._
import com.twitter.{util => twitter}

import scala.concurrent.ExecutionContext

object ServerSetup extends App{
  val system = ActorSystem("HelloSystem")

  val service = new Service[httpx.Request, httpx.Response] {
    var counter: Int = 0
    val workers: Array[ActorRef] = Array(
      system.actorOf(ProccessingActor.props(), "first"),
      system.actorOf(ProccessingActor.props(), "second")
    )

    def apply(req: httpx.Request): twitter.Future[Response] = {
      (req.method, Path(req.path)) match {
        case Method.Get -> Root =>
          twitter.Future.value {
            httpx.Response(req.version, httpx.Status.Ok)
          }
        case Method.Get -> Root / "foo" / "bar" =>
          counter = counter + 1
          (workers(counter%2) ? SecondDelay(req.version))(Timeout(10000000))
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
