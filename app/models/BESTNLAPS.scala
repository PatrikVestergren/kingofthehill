package models

import java.time.LocalDate
import play.api.Play.current
import anorm.SqlParser._
import anorm._
import play.api.db.DB

/**
  * Created by patrikv on 23/11/15.
  */
case class BestNLaps(driver: String, transponder: Long, totalTime: Long, totalTimePres: String, tsPres: String, ts: LocalDate)

object BestNLaps {

  def isBetter(bestThree: Long, totalTime: Long): Boolean = bestThree < totalTime

  val simple = {
    get[String]("driver") ~
      get[Long]("transponder") ~
      get[Long]("totalTime") ~
      get[LocalDate]("ts") ~
      get[String]("totalTimePres") ~
      get[String]("tsPres") map {
      case driver ~ transponder ~ totalTime ~ ts ~ totalTimePres ~ tsPres => BestNLaps(driver, transponder, totalTime, totalTimePres, tsPres, ts)
    }
  }

  def bestForToday(t: Long) = {
    DB.withConnection { implicit connection =>
      SQL(s"SELECT * FROM BESTNLAPS WHERE transponder=$t AND ts = (SELECT TIMESTAMP 'today') limit 1").as(BestNLaps.simple *)
    }
  }

  def bestFor(t: Long) = {
    DB.withConnection { implicit connection =>
      SQL(s"SELECT * FROM BESTNLAPS WHERE transponder=$t limit 1").as(BestNLaps.simple *)
    }
  }

  def update(record: BestNLaps) = {
    DB.withConnection { implicit connection =>
      SQL(s"UPDATE BESTNLAPS SET driver={driver}, totalTime={totalTime},totalTimePres={totalTimePres},tsPres={tsPres} WHERE transponder={transponder}").on(
        'driver -> record.driver,
        'totalTime -> record.totalTime,
        'transponder -> record.transponder,
        'totalTime -> record.totalTime,
        'totalTimePres -> record.totalTimePres,
        'tsPres -> record.tsPres
      ).executeUpdate()
    }
  }

  def findAll(): Seq[BestNLaps] = {
    DB.withConnection { implicit connection =>
      SQL("select * from BESTNLAPS order by totalTime").as(BestNLaps.simple *)
    }
  }

  def create(record: BestNLaps): Unit = {
    DB.withConnection { implicit connection =>
      SQL("insert into BESTNLAPS values ({driver},{transponder},{totalTime},{totalTimePres},{tsPres})").on(
        'driver -> record.driver,
        'transponder -> record.transponder,
        'totalTime -> record.totalTime,
        'totalTimePres -> record.totalTimePres,
        'tsPres -> record.tsPres
      ).executeUpdate()
    }
  }
}


