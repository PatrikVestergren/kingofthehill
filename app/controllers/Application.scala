package controllers


import java.text.SimpleDateFormat
import java.util.Calendar

import com.google.gson.Gson
import models.Lap
import org.slf4j.LoggerFactory
import play.api.mvc.{Action, Controller}


class Application extends Controller {

  val logger = LoggerFactory.getLogger(getClass)
  val format = new SimpleDateFormat("yyyy-MM-dd")

  val manager = new Manager(Lap.findAll())

  val NR_OF_LAPS = 3


  def index = Action {
    val cal = Calendar.getInstance()
    if (manager.empty()) manager.update(Lap.findAll())
    Ok(views.html.index(manager.getCurrentRacers(), manager.getBestNLaps(NR_OF_LAPS), manager.getBestFiveMinutes(), format.format(cal.getTime())))
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
        val lap = new Lap(driver, transponder, lapNr, lapTime, Calendar.getInstance().getTimeInMillis)
        if (!manager.contains(lap)) {
          Lap.create(lap)
          manager.update(lap)
        } else {
          println("Found dublett")
        }
        Redirect(routes.Application.index())
      }
  }

  def getLaps() = Action {
    val laps = Lap.findAll()
    val json = new Gson().toJson(laps.toArray)
    Ok(json).as("application/json")
  }

  def lapsFor(transponder: String) = Action {
    if (transponder == "-") Redirect(routes.Application.index())
    else {
      val laps = manager.getTodaysLapsFor(transponder.toLong)
      val title = if (laps.size > 0) laps.head.name + " [" + transponder + "]" else "-"
      Ok(views.html.lapsFor(laps, title))
    }
  }

  def deleteAll() = Action {
    println("deleting all...")
    Lap.deleteAll()
    println("all deleted remaining: " + Lap.findAll().length)
    Redirect(routes.Application.index())
  }
}

//@base: #A65B00;
//@lighter1: #FFA940;
//@lighter2: #FFC073;
//@darker1: #BF7F30;
//@darker2: #FF8C00;
