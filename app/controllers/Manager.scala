package controllers

import java.text.SimpleDateFormat
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer
import models.Lap

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

  def fastestLap(a: Lap, b: Lap): Lap = if (a.lapTime < b.lapTime) a else b

  def fastest(n: Seq[Lap]): Lap = n.reduceLeft(fastestLap)

  def update(lap: Lap): Unit = {

    val name = lap.name
    val lapTime = lap.lapTime
    val date = format.format(lap.ts)

    drivers.get(name) match {
      case Some(l) => drivers.put(name, l :+ lap)
      case None => drivers.put(name, Seq(lap))
    }

    drivers.get(name) match {
      case Some(laps) => currentRacers.put(name, CurrentLap(laps.length, name, lapTime, fastest(laps).lapTime, calculator.getBestNLapsTime(laps, 3).toString, "-"))
      case None => currentRacers.put(name, CurrentLap(1, name, lapTime, lapTime, "-", "-"))
    }

    for (driver <- drivers.keys) {
      drivers.get(driver) match {
        case Some(l) => {
          val laps = calculator.getBestNLapsTime (l, 3)
          if (laps > 0.0) bestNLaps.put(driver, BestNLaps(driver, laps.toString, date))
        }
      }
    }
  }


  def getCurrentRacers(): Seq[CurrentLap] = {

    val laps = currentRacers.values.toList

    val sorted = laps.sortWith(sortNrOfLaps)

    if (sorted.size < 20) {
      val padding = ListBuffer[CurrentLap]()
      for (i <- sorted.size until 20) padding += CurrentLap(0, "", 0, 0.0, "-", "-")
      return sorted ++ padding
    }
    return sorted
  }

  def getBestNLaps(laps: Int): Seq[BestNLaps] = {
    val b = bestNLaps.values.toList
    val sorted = b.sortWith(sortBestTime)
    if (sorted.length < 10) {
      val padding = ListBuffer[BestNLaps]()
      for (i <- sorted.size until 10) padding += BestNLaps("", "-", "-")
      return sorted ++ padding
    }
    else if (sorted.length > 10) return sorted.slice(0, 10)
    else return sorted
  }

  def getBestFiveMinutes(): Seq[BestFiveMinutes] = {
    val sorted = bestFiveMinutes.values.toList
    if (sorted.length < 10) {
      val padding = ListBuffer[BestFiveMinutes]()
      for (i <- sorted.size until 10) padding += BestFiveMinutes("", "-", "-")
      return sorted ++ padding
    }
    else if (sorted.length > 10) return sorted.slice(0, 10)
    else return sorted

  }

  def sortNrOfLaps(a: CurrentLap, b: CurrentLap) = {
    a.laps > b.laps
  }

  def sortBestTime(a: BestNLaps, b: BestNLaps) = a.time.toDouble < b.time.toDouble

}

case class CurrentLap(laps: Int, name: String, time: Double, fastest: Double, bestCons: String, bestMinutes: String)
case class BestNLaps(name: String, time: String, date: String)
case class BestFiveMinutes(name: String, time: String, date: String)
