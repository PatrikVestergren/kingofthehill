package controllers


import java.time.LocalDate

import com.google.gson.Gson
import models.{BestMinutes, BestNLaps, CurrentRacer, Lap}
import org.slf4j.LoggerFactory
import play.api.mvc.{Action, Controller}


class Application extends Controller {

  val logger = LoggerFactory.getLogger(getClass)
  val NR_OF_LAPS = 3
  val manager = new Manager(NR_OF_LAPS)


  def index = Action {
    val rec = manager.getRecord()
    Ok(views.html.index(manager.getCurrentRacers(), manager.getBestNLaps(NR_OF_LAPS), manager.getBestFiveMinutes(), LocalDate.now().toString, rec._1, rec._2, rec._3))
  }

  def addLap = Action(parse.json) {
    implicit request =>
      val res = request.body

      val driver = (res \ "driver").as[String]
      val transponder = (res \ "transponder").as[Long]
      val lapNr = (res \ "lapNr").as[Long]
      val lapTime = (res \ "lapTime").as[Long]
      if (driver.isEmpty || transponder < 0 || lapNr < 0 || lapTime <= 0) BadRequest
      else {
        val lap = new Lap(driver, transponder, lapNr, lapTime, LocalDate.now())
        manager.update(lap)
        Redirect(routes.Application.index())
      }
  }

  def getLaps() = Action {
    val laps = Lap.findAll()
    val json = new Gson().toJson(laps.toArray)
    Ok(json).as("application/json")
  }

  def lapsFor(transponder: Long) = Action {
    if (transponder == 0) Redirect(routes.Application.index())
    else {
      val laps = manager.getTodaysLapsFor(transponder.toLong)
      val title = if (laps._1.size > 0) laps._1.head.name + " [" + transponder + "]" else "-"
      Ok(views.html.lapsFor(laps._1, laps._2, laps._3, title))
    }
  }

  def deleteAll() = Action {
    println("deleting all...")
    Lap.deleteAll()
    CurrentRacer.deleteAll()
    BestNLaps.deleteAll()
    BestMinutes.deleteAll()
    println("all deleted")
    Redirect(routes.Application.index())
  }
}

//@base: #A65B00;
//@lighter1: #FFA940;
//@lighter2: #FFC073;
//@darker1: #BF7F30;
//@darker2: #FF8C00;
