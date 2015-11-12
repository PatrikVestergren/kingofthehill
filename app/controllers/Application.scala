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
  val cal = Calendar.getInstance()
  val manager = new Manager(Lap.findAll())


  def index = Action {
    if (manager.empty()) manager.update(Lap.findAll())
    Ok(views.html.index(manager.getCurrentRacers(), manager.getBestNLaps(3), manager.getBestFiveMinutes(), format.format(cal.getTime())))
  }

  def addLap = Action(parse.json) {
    implicit request =>
      val res = request.body

      val name = (res \ "name").as[String]
      val lapTime = (res \ "lapTime").as[Double]
      if (name == null || lapTime <= 0.0) BadRequest
      else {
        val lap = new Lap(name, lapTime, cal.getTimeInMillis)
        Lap.create(lap)
        manager.update(lap)
        Redirect(routes.Application.index())
      }
  }

  def getLaps() = Action {

    val laps = Lap.findAll()

    val json = new Gson().toJson(laps.toArray)
    logger.info("As JSon: " + json)
    Ok(json).as("application/json")

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
