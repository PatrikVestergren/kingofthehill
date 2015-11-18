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
  val drivers: Map[String, Seq[Lap]] = Map()
  val currentRacers: Map[String, CurrentLap] = Map()
  val bestNLaps: Map[String, BestNLaps] = Map()
  val bestFiveMinutes: Map[String, BestFiveMinutes] = Map()
  val calculator = new Calculator

  def empty(): Boolean = currentRacers.isEmpty && bestNLaps.isEmpty && bestFiveMinutes.isEmpty

  def update(s: Seq[Lap]): Unit = s.foreach(update)

  def fastestLap(a: Lap, b: Lap): Lap = if (a.lapTime < b.lapTime) a else b

  def fastest(n: Seq[Lap]): Lap = n.reduceLeft(fastestLap)

  def update(lap: Lap): Unit = {

    val driver = lap.driver
    val lapTime = lap.lapTime
    val date = format.format(new Date(lap.ts)) // else ""

    drivers.get(driver) match {
      case Some(l) => drivers.put(driver, l :+ lap)
      case None => drivers.put(driver, Seq(lap))
    }

    drivers.get(driver) match {
      case Some(laps) => {
        val bestThree = if (calculator.getBestNLapsTime(laps, 3) > 0) formatTime(calculator.getBestNLapsTime(laps, 3)) else "-"
        val calced = calculator.getBestFiveMinutes(laps)
        val bestFive = if (calced._2 > 0) calced._1.size.toString() + "/" + formatTime(calced._2) else "-"
        currentRacers.put(driver, CurrentLap(laps.length, driver, lapTime, formatTime(fastest(laps).lapTime), bestThree, bestFive, lap.ts))
      }
      case None => currentRacers.put(driver, CurrentLap(1, driver, lapTime, formatTime(lapTime), "-", "-", lap.ts))
    }

    for (driver <- drivers.keys) {
      drivers.get(driver) match {
        case Some(l) => {
          val bestN = calculator.getBestNLapsTime(l, 3)
          if (bestN > 0) bestNLaps.put(driver, BestNLaps(driver, bestN, date))
          val bestFive = calculator.getBestFiveMinutes(l)
          if (bestFive._2 > 0) bestFiveMinutes.put(driver, BestFiveMinutes(driver, date, bestFive))
        }
        case None =>
      }
    }
  }
  val f = new SimpleDateFormat("mm:ss.SSS")
  def formatTime(x: Long): String = f.format(x)

  def getCurrentRacers(): Seq[CurrentLapPres] = {

    val l = currentRacers.values.toList
    val laps = l.filter(x => calculator.isToday(x))

    val sorted = laps.sortWith(calculator.sortNrOfLaps)
    val pres = for (s <- sorted) yield CurrentLapPres(s.lapNr, s.name, formatTime(s.time), s.fastest, s.bestCons, s.bestMinutes)

    if (pres.size < 15) {
      val padding = ListBuffer[CurrentLapPres]()
      for (i <- pres.size until 15) padding += CurrentLapPres(0, "-", "-", "-", "-", "-")
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
    val pres = for(s <- sorted) yield BestPres(s.name, s.best._1.size.toString() + "/" + formatTime(s.best._2), s.date)
    if (pres.length < 10) {
      val padding = ListBuffer[BestPres]()
      for (i <- pres.size until 10) padding += BestPres("", "-", "-")
      return pres ++ padding
    }
    else if (pres.length > 10) return pres.slice(0, 10)
    else return pres

  }

}

case class CurrentLap(lapNr: Int, name: String, time: Long, fastest: String, bestCons: String, bestMinutes: String, ts: Long)
case class BestNLaps(name: String, time: Long, date: String)
case class BestFiveMinutes(name: String, date: String, best: (Seq[Lap], Long))
case class CurrentLapPres(lapNr: Int, name: String, time: String, fastest: String, bestCons: String, bestMinutes: String)
case class BestPres(name: String, time: String, date: String)
