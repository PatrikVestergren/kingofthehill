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

    val name = lap.name
    val lapTime = lap.lapTime
    val date = format.format(new Date(lap.ts))// else ""

    drivers.get(name) match {
      case Some(l) => drivers.put(name, l :+ lap)
      case None => drivers.put(name, Seq(lap))
    }

    drivers.get(name) match {
      case Some(laps) => {
        val bestThree = if (calculator.getBestNLapsTime(laps, 3) > 0.0) formatTime(calculator.getBestNLapsTime(laps, 3)) else "-"
        val calced = calculator.getBestFiveMinutes(laps)
        val bestFive = if (calced._2 > 0.0) calced._1.size.toString() + "/" + formatTime(calced._2) else "-"
        currentRacers.put(name, CurrentLap(laps.length, name, formatTime(lapTime), formatTime(fastest(laps).lapTime), bestThree, bestFive))
      }
      case None => currentRacers.put(name, CurrentLap(1, name, formatTime(lapTime), formatTime(lapTime), "-", "-"))
    }

    for (driver <- drivers.keys) {
      drivers.get(driver) match {
        case Some(l) => {
          val bestN = calculator.getBestNLapsTime(l, 3)
          if (bestN > 0.0) bestNLaps.put(driver, BestNLaps(driver, formatTime(bestN), date))
          val bestFive = calculator.getBestFiveMinutes(l)
          if (bestFive._2 > 0.0) bestFiveMinutes.put(driver, BestFiveMinutes(driver, bestFive._1.size.toString() + "/" + formatTime(bestFive._2), date, bestFive))
        }
        case None =>
      }
    }
  }

  def formatTime(x: Double): String = "%1.3f".format(x)

  def getCurrentRacers(): Seq[CurrentLap] = {

    val laps = currentRacers.values.toList

    val sorted = laps.sortWith(sortNrOfLaps)

    if (sorted.size < 15) {
      val padding = ListBuffer[CurrentLap]()
      for (i <- sorted.size until 15) padding += CurrentLap(0, "-", "-", "-", "-", "-")
      return sorted ++ padding
    }
    return sorted
  }

  def getBestNLaps(laps: Int): Seq[BestNLaps] = {

    val b = bestNLaps.values.toList
    val sorted = b.sortWith(calculator.sortBestTime)
    if (sorted.length < 10) {
      val padding = ListBuffer[BestNLaps]()
      for (i <- sorted.size until 10) padding += BestNLaps("", "-", "-")
      return sorted ++ padding
    }
    else if (sorted.length > 10) return sorted.slice(0, 10)
    else return sorted
  }

  def getBestFiveMinutes(): Seq[BestFiveMinutes] = {

    val best = bestFiveMinutes.values.toList


    val sorted = calculator.sortBestFive(best)
    if (sorted.length < 10) {
      val padding = ListBuffer[BestFiveMinutes]()
      for (i <- sorted.size until 10) padding += BestFiveMinutes("", "-", "-", (Seq(), 0.0))
      return sorted ++ padding
    }
    else if (sorted.length > 10) return sorted.slice(0, 10)
    else return sorted

  }


  def sortNrOfLaps(a: CurrentLap, b: CurrentLap) = {
    a.laps > b.laps
  }


}

case class CurrentLap(laps: Int, name: String, time: String, fastest: String, bestCons: String, bestMinutes: String)

case class BestNLaps(name: String, time: String, date: String)

case class BestFiveMinutes(name: String, time: String, date: String, best: (Seq[Lap], Double))
