package controllers


import java.text.SimpleDateFormat
import java.util.Calendar

import com.google.gson.Gson
import models.Lap
import org.slf4j.LoggerFactory
import play.api.data.Form
import play.api.data.Forms.{nonEmptyText, number, tuple}
import play.api.mvc.{Action, Controller}


class Application extends Controller {

  val logger = LoggerFactory.getLogger(getClass)
  val format = new SimpleDateFormat("yyyy-MM-dd")
  val cal = Calendar.getInstance()
  val manager = new Manager(Lap.findAll())

  val lapForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "lapTime" -> number)
  )

  def index = Action {
    Ok(views.html.index(manager.getCurrentRacers(), manager.getBestNLaps(3), manager.getBestFiveMinutes()))
  }

  def addLap = Action(parse.json) {
    implicit request =>
      val res = request.body

      val name = (res \ "name").as[String]
      val lapTime = (res \ "lapTime").as[Double]
      if (name == null || lapTime == null) BadRequest
      else {
        val lap = new Lap(name, lapTime, cal.getTime)
        Lap.create(lap)
        manager.update(lap)
        Redirect(routes.Application.index())
      }
     // Redirect(routes.Application.index())
  }

  def getLaps() = Action {

    val laps = Lap.findAll()

    val json = new Gson().toJson(allLaps().toArray)
    logger.info("As JSon: " + json)
    Ok(json).as("application/json")

  }

  def allLaps(): Seq[Lap] = {
    val laps = Lap.findAll()

    if (laps.length > 9) return laps
    for (l <- laps.length until 10) yield Lap("", 0l, Calendar.getInstance().getTime())
  }
}
//@base: #A65B00;
//@lighter1: #FFA940;
//@lighter2: #FFC073;
//@darker1: #BF7F30;
//@darker2: #FF8C00;
