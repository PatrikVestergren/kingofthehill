//package models
//
//import anorm.SqlParser._
//import anorm._
//import play.api.db.DB
//
///**
// * Created by patrikv on 02/11/15.
// */
//case class Driver(name: String, lap: Lap)
//
//object Driver {
//  val simple = {
//    get[String]("name") ~
//      get[Double]("lapTime") map {
//      case name ~ lapTime => Lap(name, lapTime)
//    }
//  }
//
//  def findAll(): Seq[Lap] = {
//    DB.withConnection { implicit connection =>
//      SQL("select * from DRIVER").as(Lap.simple *)
//    }
//  }
//
//  def update(lap: Lap): Unit = {
//    val d = DB.withConnection { implicit connection =>
//      SQL("select * from DRIVER where name="+lap.name).as(Lap.simple *)
//    }
//    if (d.isEmpty) {
//      DB.withConnection { implicit connection =>
//        SQL("insert into DRIVER values ({name})").on(
//          'name -> lap.name
//        ).executeUpdate()
//      }
//    }
//
//    DB.withConnection { implicit connection =>
//      SQL("update DRIVER values ({lap}) where name=" + lap.name).on(
//        'lap -> lap.name
//      ).executeUpdate()
//    }
//  }
//}
//
//
