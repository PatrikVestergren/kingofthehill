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

  def trackRecordTodayFor(t: Long) = {
    DB.withConnection { implicit connection =>
      SQL(s"SELECT * FROM LAP WHERE lapTime = (SELECT min(lapTime) from LAP WHERE transponder=$t AND ts = (SELECT TIMESTAMP 'today')) AND transponder=$t").as(Lap.simple *)
    }
  }

  def lapsTodayFor(t: Long) = {
    DB.withConnection { implicit connection =>
      SQL(s"SELECT * FROM LAP WHERE transponder=$t AND ts = (SELECT TIMESTAMP 'today') ORDER BY lapNr").as(Lap.simple *)
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