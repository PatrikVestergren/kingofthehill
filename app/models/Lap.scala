package models

/**
 * Created by patrikv on 01/08/15.
 */


import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db._

case class Lap(name: String, lapTime: Double, ts: Long)

object Lap {

  val simple = {
    get[String]("name") ~
      get[Double]("lapTime") ~
      get[Long]("ts") map {
      case name ~ lapTime ~ ts => Lap(name, lapTime, ts)
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
      SQL("insert into LAP values ({name},{lapTime},{ts})").on(
        'name -> lap.name,
        'lapTime -> lap.lapTime,
        'ts -> lap.ts
      ).executeUpdate()
    }
  }

}