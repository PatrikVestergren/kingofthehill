package models

/**
 * Created by patrikv on 01/08/15.
 */


import java.util.Date

import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db._

case class Lap(name: String, lapTime: Double, ts: Date = null)

object Lap {

  val simple = {
    get[String]("name") ~
      get[Double]("lapTime") map {
      case name ~ lapTime => Lap(name, lapTime)
    }
  }

  def findAll(): Seq[Lap] = {
    DB.withConnection { implicit connection =>
      SQL("select * from LAP").as(Lap.simple *)
    }
  }

  def create(lap: Lap): Unit = {
    DB.withConnection { implicit connection =>
      SQL("insert into LAP values ({name},{lapTime},CURRENT_DATE)").on(
        'name -> lap.name,
        'lapTime -> lap.lapTime
      ).executeUpdate()
    }
  }

}