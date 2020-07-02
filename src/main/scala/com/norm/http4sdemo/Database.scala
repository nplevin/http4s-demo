package com.norm.database

import cats.effect.{Blocker, ContextShift, IO, Resource}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.util.transactor.Transactor
import doobie.util.ExecutionContexts
import io.circe.config.parser
import io.circe.generic.auto._
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContext



case class DbConfig(driver:String, url: String, username: String, password: String, poolSize: Int=10)

object DbConfig {
  def load(): IO[DbConfig] = {
    val path = sys.env.get("DB-TYPE").getOrElse("db")
    parser.decodePathF[IO, DbConfig](path)
  }
}

case class TimeStampRow(id: String, timestamp: Long)

object Queries {
  def createDb = sql"""CREATE DATABASE IF NOT EXISTS alltimes""".update
  def createTable = {
    sql"""CREATE TABLE IF NOT EXISTS timestamps (
         |  id SERIAL PRIMARY KEY,
         |  timestamp INT
         |);
       """.stripMargin
      .update
  }
  def insert(timeStamp: Long): doobie.Update0 =
    sql"""INSERT INTO timestamps (timestamp) VALUES (${timeStamp})"""
      .stripMargin
      .update

  def latest(): doobie.Query0[Long] =
    sql"""SELECT timestamp FROM timestamps ORDER BY timestamp DESC LIMIT 1;"""
      .stripMargin
      .query[Long]
}
object Database {
    def transactor()(implicit contextShift: ContextShift[IO]): Resource[IO, HikariTransactor[IO]] = {
      for{
        config <- Resource.liftF(DbConfig.load())
        ec <- ExecutionContexts.fixedThreadPool[IO](config.poolSize)
        blocker <- Blocker[IO]
        hikari <- HikariTransactor.newHikariTransactor[IO](
          config.driver,
          config.url,
          config.username,
          config.password,
          ec,
          blocker
        )
      } yield {
        hikari
      }
  }



  def bootstrap(xa: Transactor[IO]): IO[Int] = {
    import cats.implicits._
    //(Queries.createDb.run, Queries.createTable.run).mapN(_ + _).transact(xa)
    Queries.createTable.run.transact(xa)
  }
  def initialize(transactor:HikariTransactor[IO]) =
    transactor.configure { dataSource =>
      IO {
        val flyWay = Flyway.configure().dataSource(dataSource).load()
        println(dataSource)
        flyWay.migrate()
      }
  }

}