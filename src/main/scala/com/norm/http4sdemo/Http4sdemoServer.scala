package com.norm.http4sdemo

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cats.implicits._
import com.norm.database.DbConfig
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import com.norm.database._

import scala.concurrent.ExecutionContext.global
import cats.effect.IO
import doobie.hikari.HikariTransactor
object Http4sdemoServer {

  def stream[F[_]: ConcurrentEffect](transactor:HikariTransactor[IO])(implicit T: Timer[IO], C: ContextShift[IO]): Stream[IO, Nothing] = {
    for {
      // client <- BlazeClientBuilder[IO](global).stream
      _ <-Stream.eval(Database.initialize(transactor))
      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = (
        Http4sdemoRoutes.timeStampRoutes[IO]() <+>
        Http4sdemoRoutes.dbRoutes(transactor)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)


      exitCode <- BlazeServerBuilder[IO](global)
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
