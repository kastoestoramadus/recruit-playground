package walidus

import com.twitter.util
import com.twitter.{util => twitter}
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

package object finagle {
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
