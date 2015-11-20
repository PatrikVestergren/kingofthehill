package controllers

import java.text.SimpleDateFormat
import java.util.Date

import models.Lap

import scala.collection.mutable.{ListBuffer, Map}

/**
  * Created by patrikv on 04/11/15.
  */
class Manager(initLaps: Seq[Lap]) {

  val format = new SimpleDateFormat("yyyy-MM-dd")
  val drivers: Map[Long, Seq[Lap]] = Map()
  val currentRacers: Map[Long, CurrentLap] = Map()
  val bestNLaps: Map[Long, BestNLaps] = Map()
  val bestFiveMinutes: Map[Long, BestFiveMinutes] = Map()
  val calculator = new Calculator

  def empty(): Boolean = currentRacers.isEmpty && bestNLaps.isEmpty && bestFiveMinutes.isEmpty

  def update(s: Seq[Lap]): Unit = s.foreach(update)

  def fastestLap(a: Lap, b: Lap): Lap = if (a.lapTime < b.lapTime) a else b

  def fastest(n: Seq[Lap]): Lap = n.reduceLeft(fastestLap)

  def update(lap: Lap): Unit = {

    val driver = lap.driver
    val lapTime = lap.lapTime
    val lapNr = lap.lapNr
    val transponder = lap.transponder
    val date = format.format(lap.ts)

    drivers.get(transponder) match {
      case Some(l) => {
        drivers.put(transponder, l :+ lap)
      }
      case None => drivers.put(transponder, Seq(lap))
    }

    drivers.get(transponder) match {
      case Some(laps) => {
        val bestThree = if (calculator.getBestNLapsTime(laps, 3) > 0) formatTime(calculator.getBestNLapsTime(laps, 3)) else "-"
        val calced = calculator.getBestFiveMinutes(laps)
        val bestFive = if (calced._2 > 0) calced._1.size.toString() + "/" + formatTime(calced._2) else "-"
        currentRacers.put(transponder, CurrentLap(lapNr, driver, transponder.toString, lapTime, formatTime(fastest(laps).lapTime), bestThree, bestFive, lap.ts))
      }
      case None => currentRacers.put(transponder, CurrentLap(lapNr, driver, transponder.toString, lapTime, formatTime(lapTime), "-", "-", lap.ts))
    }

    for (tran <- drivers.keys) {
      drivers.get(tran) match {
        case Some(l) => {
          val bestN = calculator.getBestNLapsTime(l, 3)
          if (bestN > 0) bestNLaps.put(tran, BestNLaps(driver, bestN, date))
          val bestFive = calculator.getBestFiveMinutes(l)
          if (bestFive._2 > 0) bestFiveMinutes.put(tran, BestFiveMinutes(driver, date, bestFive))
        }
        case None =>
      }
    }
  }

  val f = new SimpleDateFormat("mm:ss.SSS")

  def formatTime(x: Long): String = f.format(x)

  def getTodaysLaps(): Seq[CurrentLap] = {
    val l = currentRacers.values.toList
    l.filter(x => calculator.isToday(x))
  }

  def getTodaysLapsFor(t: Long): Seq[DriverLap] = {
    val todays = drivers.get(t) match {
      case Some(l) => l.filter(x => calculator.isTodayForLap(x))
      case None => Seq()
    }

    if (todays.isEmpty) {
      return Seq()
    }
    val transponder = todays(0).transponder
    val bestLap = calculator.getBestNLaps(todays, 1)
    val bestN = calculator.getBestNLaps(todays, 3)
    val bestFive = calculator.getBestFiveMinutes(todays)

    val result = for (lap <- todays) yield DriverLap(lap.driver, transponder, lap.lapNr, formatTime(lap.lapTime), calcClass(lap.lapNr, bestLap(0), bestN, bestFive))

    result
  }

  def calcClass(lapNr: Long, bestLap: Lap, bestN: Seq[Lap], bestFive: (Seq[Lap], Long)): String = {
    if (bestLap.lapNr == lapNr) return "fastLap"
    for (lap <- bestN) if (lap.lapNr == lapNr) return "bestN"
    for (lap <- bestFive._1) if (lap.lapNr == lapNr) return "bestFive"
    "regular"
  }

  def getCurrentRacers(): Seq[CurrentLapPres] = {

    val laps = getTodaysLaps()

    val sorted = laps.sortWith(calculator.sortNrOfLaps)
    val pres = for (s <- sorted) yield CurrentLapPres(s.lapNr, s.name, s.transponder, formatTime(s.time), s.fastest, s.bestCons, s.bestMinutes)

    if (pres.size < 15) {
      val padding = ListBuffer[CurrentLapPres]()
      for (i <- pres.size until 15) padding += CurrentLapPres(0, "-", "-", "-", "-", "-", "-")
      return pres ++ padding
    }
    return pres
  }

  def getBestNLaps(laps: Int): Seq[BestPres] = {

    val b = bestNLaps.values.toList
    val sorted = b.sortWith(calculator.sortBestTime)
    val pres = for (s <- sorted) yield BestPres(s.name, formatTime(s.time), s.date)

    if (pres.length < 10) {
      val padding = ListBuffer[BestPres]()
      for (i <- pres.size until 10) padding += BestPres("", "-", "-")
      return pres ++ padding
    }
    else if (pres.length > 10) return pres.slice(0, 10)
    else return pres
  }

  def getBestFiveMinutes(): Seq[BestPres] = {

    val best = bestFiveMinutes.values.toList

    val sorted = calculator.sortBestFive(best)
    val pres = for (s <- sorted) yield BestPres(s.name, s.best._1.size.toString() + "/" + formatTime(s.best._2), s.date)
    if (pres.length < 10) {
      val padding = ListBuffer[BestPres]()
      for (i <- pres.size until 10) padding += BestPres("", "-", "-")
      return pres ++ padding
    }
    else if (pres.length > 10) return pres.slice(0, 10)
    else return pres

  }

  def contains(lap: Lap): Boolean = {
    drivers.get(lap.transponder) match {
      case Some(laps) => for (l <- laps) if (l.driver == lap.driver && l.lapNr == lap.lapNr && l.lapTime == lap.lapTime && (calculator.isTodayForLap(lap) && calculator.isTodayForLap(l))) return true
      case None => return false
    }
    return false
  }

}

case class CurrentLap(lapNr: Long, name: String, transponder: String, time: Long, fastest: String, bestCons: String, bestMinutes: String, ts: Long)

case class BestNLaps(name: String, time: Long, date: String)

case class BestFiveMinutes(name: String, date: String, best: (Seq[Lap], Long))

case class CurrentLapPres(lapNr: Long, name: String, transponder: String, time: String, fastest: String, bestCons: String, bestMinutes: String)

case class BestPres(name: String, time: String, date: String)

case class DriverLap(name: String, transponder: Long, lapNr: Long, time: String, cssClass: String)
