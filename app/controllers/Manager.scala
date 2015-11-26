package controllers

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

import models.{BestMinutes, BestNLaps, CurrentRacer, Lap}

import scala.collection.mutable.ListBuffer

/**
  * Created by patrikv on 04/11/15.
  */
class Manager(nrOfLaps: Int) {

  val format = new SimpleDateFormat("yyyy-MM-dd")
  val format2 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  val calculator = new Calculator
  val lapFormat = new SimpleDateFormat("mm:ss.SSS")

  def formatTime(x: Long): String = lapFormat.format(x)

  def update(lap: Lap): Unit = {

    val driver = lap.driver
    val lapTime = lap.lapTime
    val lapNr = lap.lapNr
    val transponder = lap.transponder

    Lap.create(lap)

    val todays = Lap.lapsTodayFor(transponder)
    val bestThree = calculator.getBestNLapsTime(todays, nrOfLaps)
    if (bestThree > 0) {
      val bestThreeDb = BestNLaps.bestFor(transponder)
      if (bestThreeDb.isEmpty) {
        BestNLaps.create(BestNLaps(driver, transponder, bestThree, formatTime(bestThree), lap.ts.toString, lap.ts))
      } else if (BestNLaps.isBetter(bestThree, bestThreeDb.head.totalTime)) {
        BestNLaps.update(BestNLaps(driver, transponder, bestThree, formatTime(bestThree), lap.ts.toString, lap.ts))
      }
    }

    val bestMin = calculator.getBestFiveMinutes(todays)
    if (bestMin._2 > 0) {
      val bestFiveDb = BestMinutes.bestFor(transponder)
      if (bestFiveDb.isEmpty) {
        BestMinutes.create(BestMinutes(driver, transponder, bestMin._1.length, bestMin._2, bestMin._1.length + "/" + formatTime(bestMin._2), lap.ts.toString, lap.ts))
      } else if (calculator.isBetterFive((bestMin._1.length, bestMin._2), (bestFiveDb.head.laps, bestFiveDb.head.totalTime))) {
        BestMinutes.update(BestMinutes(driver, transponder, bestMin._1.length, bestMin._2, bestMin._1.length + "/" + formatTime(bestMin._2), lap.ts.toString, lap.ts))
      }
    }
    val record = Lap.trackRecordTodayFor(transponder).head.lapTime
    val c = CurrentRacer(driver, transponder, lapNr, formatTime(lapTime), formatTime(record), formatTime(bestThree), bestMin._1.length + "/" + formatTime(bestMin._2), lap.ts.toString, lap.ts)
    CurrentRacer.create(c)
  }

  def getTodaysLapsFor(t: Long): (Seq[DriverLap], String, String) = {

    val todays = Lap.lapsTodayFor(t)

    if (todays.isEmpty) {
      return (Seq(), "", "")
    }

    val transponder = todays.head.transponder
    val driver = todays.head.driver
    val bestLap = Lap.trackRecordTodayFor(t)

    val bestN = calculator.getBestNLaps(todays, nrOfLaps)
    val bestNTime = bestN.view.map(_.lapTime).sum
    val bestFive = calculator.getBestFiveMinutes(todays)

    val result = for (lap <- todays) yield DriverLap(driver, transponder, lap.lapNr, formatTime(lap.lapTime), calcClass(lap.lapNr, bestLap.head, bestN, bestFive))

    val b = result.sortWith(calculator.sortNrOfLaps)
    (b.distinct, formatTime(bestNTime), bestFive._1.length + "/" + formatTime(bestFive._2))
  }

  def calcClass(lapNr: Long, bestLap: Lap, bestN: Seq[Lap], bestFive: (Seq[Lap], Long)): String = {
    if (bestLap.lapNr == lapNr) return "fastLap"
    for (lap <- bestN) if (lap.lapNr == lapNr) return "bestN"
    for (lap <- bestFive._1) if (lap.lapNr == lapNr) return "bestFive"
    "regular"
  }

  def getCurrentRacers(): Seq[CurrentRacer] = {

    val racers = CurrentRacer.getLatest().sortWith(calculator.sortNrOfLaps)

    if (racers.size < 15) {
      val padding = ListBuffer[CurrentRacer]()
      for (i <- racers.size until 15) padding += CurrentRacer("-", 0, 0, "-", "-", "-", "-", "-", null)
      return racers ++ padding
    }
    return racers
  }

  def getBestNLaps(laps: Int): Seq[BestNLaps] = {

    val pres = BestNLaps.findAll()

    if (pres.length < 10) {
      val padding = ListBuffer[BestNLaps]()
      for (i <- pres.size until 10) padding += BestNLaps("-", 0, 0, "-", "-", null)
      return pres ++ padding
    }
    else if (pres.length > 10) return pres.slice(0, 10)
    else return pres
  }

  def getBestFiveMinutes(): Seq[BestMinutes] = {

    val best = BestMinutes.findAll()

    val sorted = calculator.sortBestFive(best.toList)

    if (sorted.length < 10) {
      val padding = ListBuffer[BestMinutes]()
      for (i <- sorted.size until 10) padding += BestMinutes("-", 0, 0, 0, "-", "-", null)
      return sorted ++ padding
    }
    else if (sorted.length > 10) return sorted.slice(0, 10)
    else return sorted

  }

  def getRecord(): (String, String, String) = {
    val lap = Lap.trackRecord()
    if (lap.nonEmpty) (formatTime(lap.head.lapTime), lap.head.driver, lap.head.ts.toString)
    else ("-", "-", "-")
  }

}

case class DriverLap(name: String, transponder: Long, lapNr: Long, time: String, cssClass: String)
