package com.norm.http4sdemo

import java.util.TimeZone

import cats.Applicative
import cats.implicits._
// import io.circe.{Encoder, Json}
import io.circe._
import io.circe.generic.semiauto._
import org.http4s.EntityEncoder
import org.http4s.circe._
import org.joda.time.{DateTime, DateTimeZone}


object TimeStamps {
  final case class TimeStamp(time: String, date: String)

  implicit val TimeStampDecoder: Decoder[TimeStamp] = deriveDecoder[TimeStamp]
  implicit val TimeStampEncoder: Encoder[TimeStamp] = deriveEncoder[TimeStamp]

  implicit def timeStampEntityEncoder[F[_]: Applicative]: EntityEncoder[F, TimeStamp] =
    jsonEncoderOf[F, TimeStamp]

  def now(): DateTime = DateTime.now(DateTimeZone.UTC)
  def fromLong(time:Long): TimeStamp = {
    val dateTime = new DateTime(time,DateTimeZone.UTC)
    current(dateTime)
  }
  def current(now: DateTime): TimeStamp = {
    TimeStamp(now.toString("HH:mm:ss.SSS"), now.toString("yyyy-MM-dd"))
  }
  def current(): TimeStamp = {
    val now = DateTime.now(DateTimeZone.UTC)
    // TimeStamp(now.toString("HH:mm:ss.SSS"), now.toString("yyyy-MM-dd"))
    current(now)
  }
}