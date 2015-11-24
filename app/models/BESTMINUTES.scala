package models

import java.time.LocalDate

import anorm.SqlParser._
import anorm._
import controllers.Calculator
import play.api.Play.current
import play.api.db.DB

/**
  * Created by patrikv on 23/11/15.
  */
case class BestMinutes(driver: String, transponder: Long, laps: Int, totalTime: Long, result: String, tsPres: String, ts: LocalDate)

object BestMinutes {

  val calculator = new Calculator()
  def isBetter(a: (Seq[Lap], Long), b: List[BestMinutes]): Boolean = {
    if (b.isEmpty) return true
    if (a._1.length == b.head.laps && a._2 == b.head.totalTime) return false
    val best = calculator.bestFiveSingle((a._1.length, a._2), (b.head.laps, b.head.totalTime))
    a._1.length == best._1 && a._2 == best._2
  }

  val simple = {
    get[String]("driver") ~
      get[Long]("transponder") ~
      get[Int]("laps") ~
      get[Long]("totalTime") ~
      get[String]("result") ~
      get[String]("tsPres") ~
      get[LocalDate]("ts") map {
      case driver ~ transponder ~ laps ~ totalTime ~ result ~ tsPres ~ ts => BestMinutes(driver, transponder, laps, totalTime, result, tsPres, ts)
    }
  }

  def bestFor(t: Long) = {
    DB.withConnection { implicit connection =>
      SQL(s"SELECT * FROM BESTMINUTES WHERE transponder=$t limit 1").as(BestMinutes.simple *)
    }
  }

  def update(record: BestMinutes) = {
    DB.withConnection { implicit connection =>
      SQL(s"UPDATE BESTMINUTES SET driver={driver}, laps={laps}, totalTime={totalTime},result={result},tsPres={tsPres} WHERE transponder={transponder}").on(
        'driver -> record.driver,
        'laps -> record.laps,
        'totalTime -> record.totalTime,
        'transponder -> record.transponder,
        'result -> record.result,
        'tsPres -> record.tsPres
      ).executeUpdate()
    }
  }

  def findAll(): Seq[BestMinutes] = {
    DB.withConnection { implicit connection =>
      SQL("select * from BESTMINUTES").as(BestMinutes.simple *)
    }
  }

  def deleteAll() = {
    DB.withConnection { implicit connection =>
      SQL("delete from BESTMINUTES").executeUpdate()
    }
  }

  def create(record: BestMinutes): Unit = {
    DB.withConnection { implicit connection =>
      SQL("insert into BESTMINUTES values ({driver},{transponder},{laps},{totalTime},{result},{tsPres})").on(
        'driver -> record.driver,
        'transponder -> record.transponder,
        'laps -> record.laps,
        'totalTime -> record.totalTime,
        'result -> record.result,
        'tsPres -> record.tsPres
      ).executeUpdate()
    }
  }
}

