package controllers

import models.Lap

/**
 * Created by patrikv on 05/11/15.
 */
case class Calculator() {

  val fiveMinutes = 300.00

  def getBestNLapsTime(s: Seq[Lap], nrOfLaps: Int): Double = {
    if (s.length < nrOfLaps) return 0.0
    val millis = sumLapTimes(getBestNLaps(s, nrOfLaps))
//    val f = String.format("%02d min, %02d sec",
//      TimeUnit.MILLISECONDS.toMinutes(millis),
//      TimeUnit.MILLISECONDS.toSeconds(millis) -
//        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
//    )
//
//    f
    millis
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

  def getBestFiveMinutes(s: Seq[Lap]): (Seq[Lap], Double) = {
    if (s.isEmpty) return (Seq(), 0.0)

    def loop(laps: Seq[Lap], acc: Seq[(Seq[Lap], Double)]): Seq[(Seq[Lap], Double)] = {
      if (laps.isEmpty) {
        return acc
      }
      return loop(laps.tail, acc ++ Seq(sumSeq(laps, (Seq(), 0.0))))
    }
    val bundled = loop(s, Seq((Seq(), 0.0)))
    if (bundled.isEmpty) return (Seq(), 0.0)
    val best = bundled.reduceLeft(bestFive)
     best
  }

  def bestFive(a: (Seq[Lap], Double), b: (Seq[Lap], Double)): (Seq[Lap], Double) = {
    if (a._1.length > b._1.length) return a
    else if (a._1.length == b._1.length) {
      if (a._2 < b._2) return a
    }
    return b
  }

  def sumSeq(s: Seq[Lap], acc: (Seq[Lap], Double)): (Seq[Lap], Double) = {
    if (s.isEmpty){
      if (acc._2 >= fiveMinutes) return acc
      else return (Seq(), 0.0)
    }
    if (acc._2 >= fiveMinutes) return acc
    sumSeq(s.tail, (acc._1 :+ s.head, acc._2 + s.head.lapTime))
  }

  def sortBestTime(a: BestNLaps, b: BestNLaps) = a.time.toDouble < b.time.toDouble


  def bestTime(a: Seq[Lap], b: Seq[Lap]): Seq[Lap] = {
    if (sumLapTimes(a) < sumLapTimes(b)) a else b
  }

  def sumLapTimes(laps: Seq[Lap]): Double = {
    laps.foldLeft(0.0)((r, c) => r + c.lapTime)
  }

  def sortBestFive(l: List[BestFiveMinutes]): List[BestFiveMinutes] = {
    val b = l.sortWith(byLaps)
    b.sortWith(byTime)
  }

  def byLaps(a: BestFiveMinutes, b: BestFiveMinutes) = a.best._1.length > b.best._1.length
  def byTime(a: BestFiveMinutes, b: BestFiveMinutes) = {
    if (a.best._1.length == b.best._1.length){
      a.best._2 < b.best._2
    } else byLaps(a, b)
  }

}
