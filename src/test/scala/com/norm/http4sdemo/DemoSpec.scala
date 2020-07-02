package com.norm.http4sdemo

import cats.effect.IO
import org.http4s._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult

class DemoSpec extends org.specs2.mutable.Specification {

  "TimeStamp" >> {
    "return 200" >> {
      uriReturns200()
    }
    "return hello world" >> {
      uriReturnsDateTime()
    }
  }

  private[this] val retHelloWorld: Response[IO] = {
    val getTS = Request[IO](Method.GET, uri"/date-time")
    Http4sdemoRoutes.timeStampRoutes().orNotFound(getTS).unsafeRunSync()
  }

  private[this] def uriReturns200(): MatchResult[Status] =
    retHelloWorld.status must beEqualTo(Status.Ok)

  private[this] def uriReturnsDateTime(): MatchResult[String] = ???
    //retHelloWorld.as[String].unsafeRunSync() must beEqualTo("{\"message\":\"Hello, world\"}")
}