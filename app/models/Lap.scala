package models

/**
 * Created by patrikv on 01/08/15.
 */


import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db._

case class Lap(driver: String, transponder: Long, lapNr: Long, lapTime: Long, ts: Long)

object Lap {

  val simple = {
    get[String]("driver") ~
      get[Long]("transponder") ~
      get[Long]("lapNr") ~
      get[Long]("lapTime") ~
      get[Long]("ts") map {
      case driver ~ transponder ~ lapNr ~ lapTime ~ ts => Lap(driver, transponder, lapNr, lapTime, ts)
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
      SQL("insert into LAP values ({driver},{transponder},{lapNr},{lapTime},{ts})").on(
        'driver -> lap.driver,
        'transponder -> lap.transponder,
        'lapNr -> lap.lapNr,
        'lapTime -> lap.lapTime,
        'ts -> lap.ts
      ).executeUpdate()
    }
  }

}