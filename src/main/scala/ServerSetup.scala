import java.net.InetSocketAddress

import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.httpx.path._
import com.twitter.finagle.httpx.{Method, HttpMuxer, Http, Request}
import com.twitter.finagle.{Httpx, Service}
import com.twitter.finagle.httpx
import com.twitter.util.{Await, Future}

object ServerSetup extends App{
  val service = new Service[httpx.Request, httpx.Response] {
    def apply(req: httpx.Request): Future[httpx.Response] = {
      Future.value {
        (req.method, Path(req.path)) match {
          case Method.Get -> Root =>
            httpx.Response(req.version, httpx.Status.Ok)
          case Method.Get -> Root / "foo" / "bar" =>
            Thread.sleep(1000)
            httpx.Response(req.version, httpx.Status.Ok)
        }
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
