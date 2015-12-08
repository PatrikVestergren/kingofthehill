package controllers

import java.text.SimpleDateFormat
import java.util.regex.Pattern

import models.{BestMinutes, BestNLaps, CurrentRacer, Lap}

import scala.collection.mutable.ListBuffer

/**
  * Created by patrikv on 04/11/15.
  */
class Manager(nrOfLaps: Int) {

  val format = new SimpleDateFormat("yyyy-MM-dd")
  val calculator = new Calculator
  val lapFormat = new SimpleDateFormat("mm:ss.SSS")
  val twoWD = Pattern.compile("2wd", Pattern.CASE_INSENSITIVE)
  val fourWD = Pattern.compile("4wd", Pattern.CASE_INSENSITIVE)

  def formatTime(x: Long): String = lapFormat.format(x)

  def update(lap: Lap): Unit = {

    val lapNr = lap.lapNr
    val transponder = lap.transponder

    // handle restart of myrcm, i.e. lap number restarted
    val latests = Lap.getLatest(transponder)
    if (latests.isEmpty) {
      updateAll(lap)
    } else {
      val latest = latests.head
      if (latest.lapNr == lapNr) return
      if (lapNr < latest.lapNr) updateAll(Lap(lap.driver, lap.transponder, latest.lapNr + 1, lap.lapTime, lap.ts))
      else updateAll(lap)
    }

  }

  def updateAll(lap: Lap): Unit = {
    Lap.create(lap)
    val driver: String = lap.driver
    val lapTime: Long = lap.lapTime
    val transponder: Long = lap.transponder
    val lapNr = lap.lapNr
    val todays = Lap.lapsForDriverAtDate(transponder, lap.ts.toString)
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

  def getTodaysLapsFor(t: Long, day: String): (Seq[DriverLap], String, String) = {

    val laps = Lap.lapsForDriverAtDate(t, day)

    if (laps.isEmpty) {
      return (Seq(), "", "")
    }

    val transponder = laps.head.transponder
    val driver = laps.head.driver
    val bestLap = Lap.trackRecordForAtDay(t, day)

    val bestN = calculator.getBestNLaps(laps, nrOfLaps)
    val bestNTime = bestN.view.map(_.lapTime).sum
    val bestFive = calculator.getBestFiveMinutes(laps)

    val result = for (lap <- laps) yield DriverLap(driver, transponder, lap.lapNr, formatTime(lap.lapTime), calcClass(lap.lapNr, bestLap.head, bestN, bestFive))

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

    if (racers.size < 35) {
      val padding = ListBuffer[CurrentRacer]()
      for (i <- racers.size until 35) padding += CurrentRacer("-", 0, 0, "-", "-", "-", "-", "-", null)
      return racers ++ padding
    }
    return racers
  }

  def getBestNLaps(laps: Int): (Seq[BestNLaps],Seq[BestNLaps],Seq[BestNLaps]) = {

    val all = BestNLaps.findAll()
    val two = all.filter(x => twoWD.matcher(x.driver).find())
    val four = all.filter(x => fourWD.matcher(x.driver).find())
    val none = all.filterNot(x => twoWD.matcher(x.driver).find() || fourWD.matcher(x.driver).find())

    return (fixList(two), fixList(four), fixList(none))
  }

  def fixList(laps: Seq[BestNLaps]): Seq[BestNLaps] = {
    if (laps.length < 10) {
      val padding = ListBuffer[BestNLaps]()
      for (i <- laps.size until 10) padding += BestNLaps("-", 0, 0, "-", "-", null)
      return laps ++ padding
    }
    else if (laps.length > 10) return laps.slice(0, 10)
    else return laps
  }

  def getBestFiveMinutes(): (Seq[BestMinutes],Seq[BestMinutes],Seq[BestMinutes]) = {

    val best = BestMinutes.findAll()

    val sorted = calculator.sortBestFive(best.toList)
    val two = sorted.filter(x => twoWD.matcher(x.driver).find())
    val four = sorted.filter(x => fourWD.matcher(x.driver).find())
    val none = sorted.filterNot(x => twoWD.matcher(x.driver).find() || fourWD.matcher(x.driver).find())

    return (fixMinutes(two), fixMinutes(four), fixMinutes(none))

  }

  def fixMinutes(sorted: List[BestMinutes]): Seq[BestMinutes] = {
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
