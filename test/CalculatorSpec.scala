import java.util.Calendar

import controllers.{BestFiveMinutes, CoreLap}
import models.Lap
import org.scalatest.{Matchers, FlatSpec}

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
    val lap = Lap("name", 10.0, cal.getTimeInMillis)
    calculator.getBestNLaps(Seq(lap, lap, lap), 3) should be (Seq(lap, lap, lap))
  }

  it should "return best three laps if given four and nrOfLaps = 3" in {
    val lap1 = Lap("name", 12.0, cal.getTimeInMillis)
    val lap2 = Lap("name", 13.0, cal.getTimeInMillis)
    val lap3 = Lap("name", 14.0, cal.getTimeInMillis)
    val lap4 = Lap("name", 15.0, cal.getTimeInMillis)
    calculator.getBestNLaps(Seq(lap1, lap2, lap3, lap4), 3) should be (Seq(lap1, lap2, lap3))
  }

  it should "handle if other order" in {
    val lap1 = Lap("name", 15.0, cal.getTimeInMillis)
    val lap2 = Lap("name", 14.0, cal.getTimeInMillis)
    val lap3 = Lap("name", 13.0, cal.getTimeInMillis)
    val lap4 = Lap("name", 12.0, cal.getTimeInMillis)
    calculator.getBestNLaps(Seq(lap1, lap2, lap3, lap4), 3) should be (Seq(lap2, lap3, lap4))
  }

  it should "handle many laps" in {
    val lap1 = Lap("name", 15.0, cal.getTimeInMillis)
    val lap2 = Lap("name", 14.0, cal.getTimeInMillis)
    val lap3 = Lap("name", 13.0, cal.getTimeInMillis)
    val lap4 = Lap("name", 12.0, cal.getTimeInMillis)
    val lap5 = Lap("name", 9.234, cal.getTimeInMillis)
    val lap6 = Lap("name", 9.235, cal.getTimeInMillis)
    val lap7 = Lap("name", 9.236, cal.getTimeInMillis)
    val lap8 = Lap("name", 12.0, cal.getTimeInMillis)
    val lap9 = Lap("name", 15.0, cal.getTimeInMillis)
    val lap10 = Lap("name", 8.0, cal.getTimeInMillis)
    val lap11 = Lap("name", 13.0, cal.getTimeInMillis)
    val lap12 = Lap("name", 12.0, cal.getTimeInMillis)
    calculator.getBestNLaps(Seq(lap1, lap2, lap3, lap4, lap5, lap6, lap7, lap8, lap9, lap10, lap11, lap12), 3) should be (Seq(lap5, lap6, lap7))
  }

  it should "find fastest lap" in {
    val lap1 = Lap("name", 15.0, cal.getTimeInMillis)
    val lap2 = Lap("name", 14.0, cal.getTimeInMillis)
    val lap3 = Lap("name", 13.0, cal.getTimeInMillis)
    val lap4 = Lap("name", 12.0, cal.getTimeInMillis)
    val lap5 = Lap("name", 9.234, cal.getTimeInMillis)
    val lap6 = Lap("name", 9.235, cal.getTimeInMillis)
    val lap7 = Lap("name", 9.236, cal.getTimeInMillis)
    val lap8 = Lap("name", 12.0, cal.getTimeInMillis)
    val lap9 = Lap("name", 15.0, cal.getTimeInMillis)
    val lap10 = Lap("name", 8.0, cal.getTimeInMillis)
    val lap11 = Lap("name", 13.0, cal.getTimeInMillis)
    val lap12 = Lap("name", 12.0, cal.getTimeInMillis)
    calculator.getBestNLaps(Seq(lap1, lap2, lap3, lap4, lap5, lap6, lap7, lap8, lap9, lap10, lap11, lap12), 1) should be (Seq(lap10))
  }

  it should "find best five minutes simple" in {
    val lap1 = Lap("name", 100.0, cal.getTimeInMillis)
    val lap2 = Lap("name", 100.0, cal.getTimeInMillis)
    val lap3 = Lap("name", 101.0, cal.getTimeInMillis)
    calculator.getBestFiveMinutes(Seq(lap1, lap2, lap3))  should be ((Seq(lap1, lap2, lap3), 301))
  }

  it should "return empty result if not five minutes passed" in {
    val lap1 = Lap("name", 100.0, cal.getTimeInMillis)
    calculator.getBestFiveMinutes(Seq(lap1)) should be ((Seq(), 0.0))
  }

  it should "return empty result if no laps runned" in {
    calculator.getBestFiveMinutes(Seq()) should be ((Seq(), 0.0))
  }

  it should "find exact five minutes passed" in {
    val lap1 = Lap("name", 100.0, cal.getTimeInMillis)
    val lap2 = Lap("name", 100.0, cal.getTimeInMillis)
    val lap3 = Lap("name", 100.0, cal.getTimeInMillis)
    calculator.getBestFiveMinutes(Seq(lap1, lap2, lap3))  should be ((Seq(lap1, lap2, lap3), 300))
  }

  it should "return empty for not fully five minute" in {
    val lap1 = Lap("name", 100.0, cal.getTimeInMillis)
    val lap2 = Lap("name", 100.0, cal.getTimeInMillis)
    val lap3 = Lap("name", 99.999, cal.getTimeInMillis)
    calculator.getBestFiveMinutes(Seq(lap1, lap2, lap3))  should be ((Seq(), 0.0))
  }

  it should "find five minutes when more laps" in {
    val lap1 = Lap("name", 100.0, cal.getTimeInMillis)
    val lap2 = Lap("name", 100.0, cal.getTimeInMillis)
    val lap3 = Lap("name", 100.0, cal.getTimeInMillis)
    val lap4 = Lap("name", 100.0, cal.getTimeInMillis)
    val lap5 = Lap("name", 100.0, cal.getTimeInMillis)
    calculator.getBestFiveMinutes(Seq(lap1, lap2, lap3, lap4, lap5))  should be ((Seq(lap1, lap2, lap3), 300))
  }

  it should "find five minutes more" in {
    val lap1 = Lap("name", 100.0, cal.getTimeInMillis)
    val lap2 = Lap("name", 100.0, cal.getTimeInMillis)
    val lap3 = Lap("name", 90.0, cal.getTimeInMillis)
    val lap4 = Lap("name", 90.0, cal.getTimeInMillis)
    val lap5 = Lap("name", 20.0, cal.getTimeInMillis)
    val lap6 = Lap("name", 250.0, cal.getTimeInMillis)
    val lap7 = Lap("name", 200.0, cal.getTimeInMillis)
    calculator.getBestFiveMinutes(Seq(lap1, lap2, lap3, lap4, lap5, lap6, lap7))  should be ((Seq(lap2, lap3, lap4, lap5), 300))
  }

  it should "calculate best five by number of laps" in {
    val lap1 = Lap("name1", 100.0, cal.getTimeInMillis)
    val lap2 = Lap("name2", 100.0, cal.getTimeInMillis)
    val lap3 = Lap("name3", 100.0, cal.getTimeInMillis)
    val lap4 = Lap("name4", 50.0, cal.getTimeInMillis)
    val lap5 = Lap("name5", 50.0, cal.getTimeInMillis)
    val lap6 = Lap("name6", 50.0, cal.getTimeInMillis)
    val lap7 = Lap("name7", 50.0, cal.getTimeInMillis)
    val lap8 = Lap("name8", 50.0, cal.getTimeInMillis)
    val lap9 = Lap("name9", 50.0, cal.getTimeInMillis)
    val l = List(BestFiveMinutes("name1", "", "", (Seq(lap1, lap2), 305.0)),BestFiveMinutes("name2", "", "", (Seq(lap4, lap5, lap6, lap7, lap8, lap8), 300.0)))
    calculator.sortBestFive(l) should be(l.reverse)
  }

  it should "calculate best five by time" in {
    val lap1 = Lap("name1", 100.0, cal.getTimeInMillis)
    val lap2 = Lap("name2", 100.0, cal.getTimeInMillis)
    val lap3 = Lap("name3", 102.0, cal.getTimeInMillis)
    val lap4 = Lap("name4", 100.0, cal.getTimeInMillis)
    val lap5 = Lap("name5", 100.0, cal.getTimeInMillis)
    val lap6 = Lap("name6", 101.0, cal.getTimeInMillis)
    val l = List(BestFiveMinutes("name1", "", "", (Seq(lap1, lap2, lap3), 302.0)),BestFiveMinutes("name2", "", "", (Seq(lap4, lap5, lap6), 301.0)))
    calculator.sortBestFive(l) should be(l.reverse)
  }

  it should "blslsls" in {
    val d = 34.1234567
    println("%1.3f".format(d))
  }

}
