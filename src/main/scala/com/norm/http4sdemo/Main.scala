package com.norm.http4sdemo

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.norm.database.Database

object Main extends IOApp {
  def run(args: List[String]) = {
    val server= for{
      transactor <- Database.transactor()
    } yield {
      Http4sdemoServer.stream[IO](transactor).compile.drain.as(ExitCode.Success)
    }
    server.use(x=>x)
  }
}