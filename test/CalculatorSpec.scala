import java.util.Calendar

import controllers.BestFiveMinutes
import models.Lap
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by patrikv on 05/11/15.
  */
class CalculatorSpec extends FlatSpec with Matchers {
  val cal = Calendar.getInstance()
  val calculator = new controllers.Calculator()
  "Calculator" should "return empty Seq() if input length < nrOfLaps" in {
    calculator.getBestNLaps(Seq(), 1) should be(Seq())
  }

  it should "return input seq if seq.length == nrOfLaps" in {
    val lap = Lap("name", 0, 0, 10000, cal.getTimeInMillis)
    calculator.getBestNLaps(Seq(lap, lap, lap), 3) should be(Seq(lap, lap, lap))
  }

  it should "return best three laps if given four and nrOfLaps = 3" in {
    val lap1 = Lap("name", 0, 0, 12000, cal.getTimeInMillis)
    val lap2 = Lap("name", 0, 0, 13000, cal.getTimeInMillis)
    val lap3 = Lap("name", 0, 0, 14000, cal.getTimeInMillis)
    val lap4 = Lap("name", 0, 0, 15000, cal.getTimeInMillis)
    calculator.getBestNLaps(Seq(lap1, lap2, lap3, lap4), 3) should be(Seq(lap1, lap2, lap3))
  }

  it should "handle if other order" in {
    val lap1 = Lap("name", 0, 0, 15000, cal.getTimeInMillis)
    val lap2 = Lap("name", 0, 0, 14000, cal.getTimeInMillis)
    val lap3 = Lap("name", 0, 0, 13000, cal.getTimeInMillis)
    val lap4 = Lap("name", 0, 0, 12000, cal.getTimeInMillis)
    calculator.getBestNLaps(Seq(lap1, lap2, lap3, lap4), 3) should be(Seq(lap2, lap3, lap4))
  }

  it should "handle many laps" in {
    val lap1 = Lap("name", 0, 0, 15000, cal.getTimeInMillis)
    val lap2 = Lap("name", 0, 0, 14000, cal.getTimeInMillis)
    val lap3 = Lap("name", 0, 0, 13000, cal.getTimeInMillis)
    val lap4 = Lap("name", 0, 0, 12000, cal.getTimeInMillis)
    val lap5 = Lap("name", 0, 0, 9234, cal.getTimeInMillis)
    val lap6 = Lap("name", 0, 0, 9235, cal.getTimeInMillis)
    val lap7 = Lap("name", 0, 0, 9236, cal.getTimeInMillis)
    val lap8 = Lap("name", 0, 0, 12000, cal.getTimeInMillis)
    val lap9 = Lap("name", 0, 0, 15000, cal.getTimeInMillis)
    val lap10 = Lap("name", 0, 0, 8000, cal.getTimeInMillis)
    val lap11 = Lap("name", 0, 0, 13000, cal.getTimeInMillis)
    val lap12 = Lap("name", 0, 0, 12000, cal.getTimeInMillis)
    calculator.getBestNLaps(Seq(lap1, lap2, lap3, lap4, lap5, lap6, lap7, lap8, lap9, lap10, lap11, lap12), 3) should be(Seq(lap5, lap6, lap7))
  }

  it should "find fastest lap" in {
    val lap1 = Lap("name", 0, 0, 15000, cal.getTimeInMillis)
    val lap2 = Lap("name", 0, 0, 14000, cal.getTimeInMillis)
    val lap3 = Lap("name", 0, 0, 13000, cal.getTimeInMillis)
    val lap4 = Lap("name", 0, 0, 12000, cal.getTimeInMillis)
    val lap5 = Lap("name", 0, 0, 9234, cal.getTimeInMillis)
    val lap6 = Lap("name", 0, 0, 9235, cal.getTimeInMillis)
    val lap7 = Lap("name", 0, 0, 9236, cal.getTimeInMillis)
    val lap8 = Lap("name", 0, 0, 12000, cal.getTimeInMillis)
    val lap9 = Lap("name", 0, 0, 15000, cal.getTimeInMillis)
    val lap10 = Lap("name", 0, 0, 8000, cal.getTimeInMillis)
    val lap11 = Lap("name", 0, 0, 13000, cal.getTimeInMillis)
    val lap12 = Lap("name", 0, 0, 12000, cal.getTimeInMillis)
    calculator.getBestNLaps(Seq(lap1, lap2, lap3, lap4, lap5, lap6, lap7, lap8, lap9, lap10, lap11, lap12), 1) should be(Seq(lap10))
  }

  it should "find best five minutes simple" in {
    val lap1 = Lap("name", 0, 0, 100000, cal.getTimeInMillis)
    val lap2 = Lap("name", 0, 0, 100000, cal.getTimeInMillis)
    val lap3 = Lap("name", 0, 0, 101000, cal.getTimeInMillis)
    calculator.getBestFiveMinutes(Seq(lap1, lap2, lap3)) should be((Seq(lap1, lap2, lap3), 301))
  }

  it should "return empty result if not five minutes passed" in {
    val lap1 = Lap("name", 0, 0, 100000, cal.getTimeInMillis)
    calculator.getBestFiveMinutes(Seq(lap1)) should be((Seq(), 0l))
  }

  it should "return empty result if no laps runned" in {
    calculator.getBestFiveMinutes(Seq()) should be((Seq(), 0l))
  }

  it should "find exact five minutes passed" in {
    val lap1 = Lap("name", 0, 0, 100000, cal.getTimeInMillis)
    val lap2 = Lap("name", 0, 0, 100000, cal.getTimeInMillis)
    val lap3 = Lap("name", 0, 0, 100000, cal.getTimeInMillis)
    calculator.getBestFiveMinutes(Seq(lap1, lap2, lap3)) should be((Seq(lap1, lap2, lap3), 300))
  }

  it should "return empty for not fully five minute" in {
    val lap1 = Lap("name", 0, 0, 100000, cal.getTimeInMillis)
    val lap2 = Lap("name", 0, 0, 100000, cal.getTimeInMillis)
    val lap3 = Lap("name", 0, 0, 99999, cal.getTimeInMillis)
    calculator.getBestFiveMinutes(Seq(lap1, lap2, lap3)) should be((Seq(), 0l))
  }

  it should "find five minutes when more laps" in {
    val lap1 = Lap("name", 0, 0, 100000, cal.getTimeInMillis)
    val lap2 = Lap("name", 0, 0, 100000, cal.getTimeInMillis)
    val lap3 = Lap("name", 0, 0, 100000, cal.getTimeInMillis)
    val lap4 = Lap("name", 0, 0, 100000, cal.getTimeInMillis)
    val lap5 = Lap("name", 0, 0, 100000, cal.getTimeInMillis)
    calculator.getBestFiveMinutes(Seq(lap1, lap2, lap3, lap4, lap5)) should be((Seq(lap1, lap2, lap3), 300))
  }

  it should "find five minutes more" in {
    val lap1 = Lap("name", 0, 0, 100000, cal.getTimeInMillis)
    val lap2 = Lap("name", 0, 0, 100000, cal.getTimeInMillis)
    val lap3 = Lap("name", 0, 0, 90000, cal.getTimeInMillis)
    val lap4 = Lap("name", 0, 0, 90000, cal.getTimeInMillis)
    val lap5 = Lap("name", 0, 0, 20000, cal.getTimeInMillis)
    val lap6 = Lap("name", 0, 0, 250000, cal.getTimeInMillis)
    val lap7 = Lap("name", 0, 0, 200000, cal.getTimeInMillis)
    calculator.getBestFiveMinutes(Seq(lap1, lap2, lap3, lap4, lap5, lap6, lap7)) should be((Seq(lap2, lap3, lap4, lap5), 300))
  }

  it should "calculate best five by number of laps" in {
    val lap1 = Lap("name1", 0, 0, 100, cal.getTimeInMillis)
    val lap2 = Lap("name2", 0, 0, 100, cal.getTimeInMillis)
    val lap3 = Lap("name3", 0, 0, 100, cal.getTimeInMillis)
    val lap4 = Lap("name4", 0, 0, 50, cal.getTimeInMillis)
    val lap5 = Lap("name5", 0, 0, 50, cal.getTimeInMillis)
    val lap6 = Lap("name6", 0, 0, 50, cal.getTimeInMillis)
    val lap7 = Lap("name7", 0, 0, 50, cal.getTimeInMillis)
    val lap8 = Lap("name8", 0, 0, 50, cal.getTimeInMillis)
    val lap9 = Lap("name9", 0, 0, 50, cal.getTimeInMillis)
    val l = List(BestFiveMinutes("name1", "", (Seq(lap1, lap2), 305000)), BestFiveMinutes("name2", "", (Seq(lap4, lap5, lap6, lap7, lap8, lap8), 300000)))
    calculator.sortBestFive(l) should be(l.reverse)
  }

  it should "calculate best five by time" in {
    val lap1 = Lap("name1", 0, 0, 100, cal.getTimeInMillis)
    val lap2 = Lap("name2", 0, 0, 100, cal.getTimeInMillis)
    val lap3 = Lap("name3", 0, 0, 102, cal.getTimeInMillis)
    val lap4 = Lap("name4", 0, 0, 100, cal.getTimeInMillis)
    val lap5 = Lap("name5", 0, 0, 100, cal.getTimeInMillis)
    val lap6 = Lap("name6", 0, 0, 101, cal.getTimeInMillis)
    val l = List(BestFiveMinutes("name1", "", (Seq(lap1, lap2, lap3), 302000)), BestFiveMinutes("name2", "", (Seq(lap4, lap5, lap6), 301000)))
    calculator.sortBestFive(l) should be(l.reverse)
  }

  it should "blslsls" in {
    val d = 34.1234567
    println("%1.3f".format(d))
  }

}
