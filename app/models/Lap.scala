package models

/**
 * Created by patrikv on 01/08/15.
 */


import java.time.LocalDate

import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db._

case class Lap(driver: String, transponder: Long, lapNr: Long, lapTime: Long, ts: LocalDate)

object Lap {

  val simple = {
    get[String]("driver") ~
      get[Long]("transponder") ~
      get[Long]("lapNr") ~
      get[Long]("lapTime") ~
      get[LocalDate]("ts") map {
      case driver ~ transponder ~ lapNr ~ lapTime ~ ts => Lap(driver, transponder, lapNr, lapTime, ts)
    }
  }

  def totalNrOfLaps() = {
    DB.withConnection { implicit connection =>
      SQL("SELECT count(*) FROM LAP").as(scalar[Long].single)
    }
  }

  def trackRecord() = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM LAP WHERE lapTime = (SELECT min(lapTime) from LAP)").as(Lap.simple *)
    }
  }

  def trackRecordFor(t: Long) = {
    DB.withConnection { implicit connection =>
      SQL(s"SELECT * FROM LAP WHERE lapTime = (SELECT min(lapTime) from LAP WHERE transponder=$t)").as(Lap.simple *)
    }
  }

  def trackRecordForAtDay(t: Long, d: String) = {
    DB.withConnection { implicit connection =>
      SQL(s"SELECT * FROM LAP WHERE lapTime = (SELECT min(lapTime) from LAP WHERE transponder=$t AND ts=to_date('$d', 'YYYY MM DD')) AND transponder=$t").as(Lap.simple *)
    }
  }

  def trackRecordTodayFor(t: Long) = {
    DB.withConnection { implicit connection =>
      SQL(s"SELECT * FROM LAP WHERE lapTime = (SELECT min(lapTime) from LAP WHERE transponder=$t AND ts = (SELECT TIMESTAMP 'today')) AND transponder=$t").as(Lap.simple *)
    }
  }

  def lapsForDriverAtDate(t: Long, atDate: String) = {
    DB.withConnection { implicit connection =>
      SQL(s"SELECT * FROM LAP WHERE transponder=$t AND ts=to_date('$atDate', 'YYYY MM DD') ORDER BY lapNr").as(Lap.simple *)
    }
  }

  def getLatest(t: Long) = {
    DB.withConnection { implicit connection =>
      SQL(s"SELECT * FROM LAP WHERE lapNr = (SELECT max(lapNr) from LAP WHERE transponder=$t AND ts = (SELECT TIMESTAMP 'today')) AND transponder=$t").as(Lap.simple *)
    }
  }

  def findAll(): Seq[Lap] = {
    DB.withConnection { implicit connection =>
      SQL("select * from LAP").as(Lap.simple *)
    }
  }

  def deleteAll() = {
    DB.withConnection { implicit connection =>
      SQL("delete from LAP").executeUpdate()
    }
  }

  def create(lap: Lap): Unit = {
      DB.withConnection { implicit connection =>
      SQL("insert into LAP values ({driver},{transponder},{lapNr},{lapTime})").on(
        'driver -> lap.driver,
        'transponder -> lap.transponder,
        'lapNr -> lap.lapNr,
        'lapTime -> lap.lapTime
      ).executeUpdate()
    }
  }

}