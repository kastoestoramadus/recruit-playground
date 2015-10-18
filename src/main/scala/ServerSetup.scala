import java.net.InetSocketAddress

import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.httpx.{Http, Request}
import com.twitter.finagle.{Httpx, Service}
import com.twitter.finagle.httpx
import com.twitter.util.{Await, Future}

class ServerSetup {
  val service = new Service[httpx.Request, httpx.Response] {
    def apply(req: httpx.Request): Future[httpx.Response] =
      Future.value(
        httpx.Response(req.version, httpx.Status.Ok)
      )
  }
  val socketAddress = new InetSocketAddress(8000)
  val server = ServerBuilder()
    .codec(Http())
    .bindTo(socketAddress)
    .name("HTTP endpoint")
    .build(service)
}
