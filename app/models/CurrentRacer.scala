package models

import java.time.LocalDate

import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db.DB

/**
  * Created by patrikv on 23/11/15.
  */
case class CurrentRacer(driver: String, transponder: Long, lapNr: Long, lapTime: String, fastest: String, bestN: String, bestFive: String, tsPres: String, ts: LocalDate)

object CurrentRacer {

  val simple = {
    get[String]("driver") ~
      get[Long]("transponder") ~
      get[Long]("lapNr") ~
      get[String]("lapTime") ~
      get[String]("fastest") ~
      get[String]("bestN") ~
      get[String]("bestFive") ~
      get[String]("tsPres") ~
      get[LocalDate]("ts") map {
      case driver ~ transponder ~ lapNr ~ lapTime ~ fastest ~ bestN ~ bestFive ~ tsPres ~ ts => CurrentRacer(driver, transponder, lapNr, lapTime, fastest, bestN, bestFive, tsPres, ts)
    }
  }

  def findAll(): Seq[CurrentRacer] = {
    DB.withConnection { implicit connection =>
      SQL("select * from CURRENTRACER").as(CurrentRacer.simple *)
    }
  }

  def getLatest(): Seq[CurrentRacer] = {
    DB.withConnection { implicit connection =>
      SQL("select distinct on (transponder) * from CURRENTRACER order by transponder, lapNr DESC").as(CurrentRacer.simple *)
    }
  }

  def deleteAll() = {
    DB.withConnection { implicit connection =>
      SQL("delete from CURRENTRACER").executeUpdate()
    }
  }

  def create(record: CurrentRacer): Unit = {
    DB.withConnection { implicit connection =>
      SQL("insert into CURRENTRACER values ({driver},{transponder},{lapNr},{lapTime},{fastest},{bestN},{bestFive},{tsPres})").on(
        'driver -> record.driver,
        'transponder -> record.transponder,
        'lapNr -> record.lapNr,
        'lapTime -> record.lapTime,
        'fastest -> record.fastest,
        'bestN -> record.bestN,
        'bestFive -> record.bestFive,
        'tsPres -> record.tsPres
      ).executeUpdate()
    }
  }


}