package com.norm.http4sdemo

import cats.effect.Sync
import cats.implicits._
import com.norm.database.Queries
import doobie.hikari.HikariTransactor
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object Http4sdemoRoutes {

  def timeStampRoutes[F[_]: Sync](): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "date-time" =>
        for {
          dateTime <- TimeStamps.current().pure[F]
          resp <- Ok(dateTime)
        } yield resp
      }
  }

  import cats.effect.IO
  import doobie.util.transactor.Transactor
  def dbRoutes(transactor: Transactor[IO]): HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO]{}
    import dsl._
    import doobie.implicits._

    HttpRoutes.of[IO] {
      case GET -> Root / "write-date-time" =>
        val now = TimeStamps.now()
        for {
          dateTime <- TimeStamps.current(now).pure[IO]
          _ <- Queries.insert(now.getMillis()).run.transact(transactor)
          resp <- Ok(dateTime)
        } yield resp
      case GET -> Root / "latest-date-time" =>
        Queries.latest().option.transact(transactor).flatMap {
          case Some(time) => Ok(TimeStamps.fromLong(time))
          case None => NotFound("No time stamp found")
        }
    }
  }
}