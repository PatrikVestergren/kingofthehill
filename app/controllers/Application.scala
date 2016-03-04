package controllers


import java.text.SimpleDateFormat
import java.time.LocalDate

import com.google.gson.Gson
import models.Lap
import org.slf4j.LoggerFactory
import play.api.libs.EventSource
import play.api.libs.iteratee.Concurrent
import play.api.mvc.{Action, Controller}

class Application extends Controller {

  val logger = LoggerFactory.getLogger(getClass)
  val NR_OF_LAPS = 3
  val manager = new Manager(NR_OF_LAPS)

  def index = Action {
    val laps = manager.getBestNLaps(NR_OF_LAPS)
    val laps_two = laps._1
    val laps_four = laps._2
    val laps_none = laps._3
    val minutes = manager.getBestFiveMinutes()
    val min_two = minutes._1
    val min_four = minutes._2
    val min_none = minutes._3
    val totalLaps = Lap.totalNrOfLaps()

    Ok(views.html.index(manager.getCurrentRacers(), laps_two, laps_four, laps_none, min_two, min_four, min_none, LocalDate.now().toString, totalLaps))
  }

  val (out, channel) = Concurrent.broadcast[String]

  val lapFormat = new SimpleDateFormat("mm:ss.SSS")
  def postUpdate(lapTime: Long) = Action {
    val formated = lapFormat.format(lapTime)
    val f = if (formated.startsWith("00:")) formated.drop(3) else ""
    channel.push(f.replaceAll("\\.", ","))
    Ok//Redirect(routes.Application.index())
  }

  def updates = Action {
    implicit request =>
      Ok.feed(out &> EventSource()).as("text/event-stream")
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
        Redirect(routes.Application.postUpdate(lapTime))
      }
  }

  def getLaps() = Action {
    val laps = Lap.findAll()
    val json = new Gson().toJson(laps.toArray)
    Ok(json).as("application/json")
  }

  def lapsFor(transponder: Long, day: String) = Action {
    if (transponder == 0) Redirect(routes.Application.index())
    else {
      val laps = manager.getTodaysLapsFor(transponder, day)
      val title = if (laps._1.size > 0) laps._1.head.name + " [" + transponder + "]" else "-"
      Ok(views.html.lapsFor(laps._1, laps._2, laps._3, title, day))
    }
  }

  def deleteAll() = Action {
    println("deleting all...")
    //Lap.deleteAll()
    //CurrentRacer.deleteAll()
    //BestNLaps.deleteAll()
    //BestMinutes.deleteAll()
    println("all deleted")
    Redirect(routes.Application.index())
  }

}
