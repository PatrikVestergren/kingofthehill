package controllers

import java.util.{Calendar, Date}

import models.{CurrentRacer, BestMinutes, Lap}

/**
  * Created by patrikv on 05/11/15.
  */
case class Calculator() {

  val fiveMinutes = 300000
  val cal = Calendar.getInstance()

  def getBestNLapsTime(s: Seq[Lap], nrOfLaps: Int): Long = {
    if (s.length < nrOfLaps) return 0
    sumLapTimes(getBestNLaps(s, nrOfLaps))
  }

  def getBestNLaps(s: Seq[Lap], nrOfLaps: Int): Seq[Lap] = {
    if (s.isEmpty || nrOfLaps < 1) return Seq()

    def loop(laps: Seq[Lap], acc: Seq[Seq[Lap]]): Seq[Seq[Lap]] = {
      if (laps.length < nrOfLaps) acc
      else if (laps.length == nrOfLaps) acc ++ Seq(laps)
      else loop(laps.tail, acc ++ Seq(laps.slice(0, nrOfLaps)))
    }

    val bundled = loop(s, Seq())
    if (bundled.nonEmpty) return bundled.reduceLeft(bestTime)
    else Seq()
  }

  def getBestFiveMinutes(s: Seq[Lap]): (Seq[Lap], Long) = {
    if (s.isEmpty) return (Seq(), 0l)

    def loop(laps: Seq[Lap], acc: Seq[(Seq[Lap], Long)]): Seq[(Seq[Lap], Long)] = {
      if (laps.isEmpty) {
        return acc
      }
      return loop(laps.tail, acc ++ Seq(sumSeq(laps, (Seq(), 0l))))
    }
    val bundled = loop(s, Seq((Seq(), 0l)))
    if (bundled.isEmpty) return (Seq(), 0l)
    val best = bundled.reduceLeft(bestFive)
    best
  }

  def bestFive(a: (Seq[Lap], Long), b: (Seq[Lap], Long)): (Seq[Lap], Long) = {
    if (a._1.length > b._1.length) return a
    else if (a._1.length == b._1.length) {
      if (a._2 < b._2) return a
    }
    return b
  }

  def bestFiveSingle(a: (Int, Long), b: (Int, Long)): (Int, Long) = {
    if (a._1 > b._1) return a
    else if (a._1 == b._1) {
      if (a._2 < b._2) return a
    }
    return b
  }

  def sumSeq(s: Seq[Lap], acc: (Seq[Lap], Long)): (Seq[Lap], Long) = {
    if (s.isEmpty) {
      if (acc._2 >= fiveMinutes) return acc
      else return (Seq(), 0l)
    }
    if (acc._2 >= fiveMinutes) return acc
    sumSeq(s.tail, (acc._1 :+ s.head, acc._2 + s.head.lapTime))
  }

  //def sortBestTime(a: BestNLaps, b: BestNLaps) = a.time.toLong < b.time.toLong

  def sortNrOfLaps(a: CurrentRacer, b: CurrentRacer) = a.lapNr > b.lapNr
  def sortNrOfLaps(a: Lap, b: Lap) = a.lapNr > b.lapNr
  def sortNrOfLaps(a: DriverLap, b: DriverLap) = a.lapNr < b.lapNr


  def bestTime(a: Seq[Lap], b: Seq[Lap]): Seq[Lap] = {
    if (sumLapTimes(a) < sumLapTimes(b)) a else b
  }

  def sumLapTimes(laps: Seq[Lap]): Long = {
    laps.foldLeft(0l)((r, c) => r + c.lapTime)
  }

  def sortBestFive(l: List[BestMinutes]): List[BestMinutes] = {
    val b = l.sortWith(byLaps)
    b.sortWith(byTime)
  }

  def byLaps(a: BestMinutes, b: BestMinutes) = a.laps > b.laps

  def byTime(a: BestMinutes, b: BestMinutes) = {
    if (a.laps == b.laps) {
      a.totalTime < b.totalTime
    } else byLaps(a, b)
  }

//  def isToday(lap: CurrentLap): Boolean = {
////    val lapDate = Calendar.getInstance();
////    lapDate.setTimeInMillis(lap.ts.getTime)
////    lapDate.getTime.after(getStartOfDay())
//    true
//  }

  def isTodayForLap(lap: Lap): Boolean = {
    val lapDate = Calendar.getInstance();
    lapDate.setTimeInMillis(lap.ts.toEpochDay)
    lapDate.getTime.after(getStartOfDay())
  }

  def getStartOfDay(): Date = {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    calendar.getTime
  }

}
