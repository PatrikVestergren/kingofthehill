import java.time.LocalDate

import models.{BestMinutes, Lap}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by patrikv on 05/11/15.
  */
class CalculatorSpec extends FlatSpec with Matchers {
  val date = LocalDate.now()
  val calculator = new controllers.Calculator()
  "Calculator" should "return empty Seq() if input length < nrOfLaps" in {
    calculator.getBestNLaps(Seq(), 1) should be(Seq())
  }

  it should "return input seq if seq.length == nrOfLaps" in {
    val lap = Lap("name", 0, 0, 10000, date)
    calculator.getBestNLaps(Seq(lap, lap, lap), 3) should be(Seq(lap, lap, lap))
  }

  it should "return best three laps if given four and nrOfLaps = 3" in {
    val lap1 = Lap("name", 0, 0, 12000, date)
    val lap2 = Lap("name", 0, 0, 13000, date)
    val lap3 = Lap("name", 0, 0, 14000, date)
    val lap4 = Lap("name", 0, 0, 15000, date)
    calculator.getBestNLaps(Seq(lap1, lap2, lap3, lap4), 3) should be(Seq(lap1, lap2, lap3))
  }

  it should "handle if other order" in {
    val lap1 = Lap("name", 0, 0, 15000, date)
    val lap2 = Lap("name", 0, 0, 14000, date)
    val lap3 = Lap("name", 0, 0, 13000, date)
    val lap4 = Lap("name", 0, 0, 12000, date)
    calculator.getBestNLaps(Seq(lap1, lap2, lap3, lap4), 3) should be(Seq(lap2, lap3, lap4))
  }

  it should "handle many laps" in {
    val lap1 = Lap("name", 0, 0, 15000, date)
    val lap2 = Lap("name", 0, 0, 14000, date)
    val lap3 = Lap("name", 0, 0, 13000, date)
    val lap4 = Lap("name", 0, 0, 12000, date)
    val lap5 = Lap("name", 0, 0, 9234, date)
    val lap6 = Lap("name", 0, 0, 9235, date)
    val lap7 = Lap("name", 0, 0, 9236, date)
    val lap8 = Lap("name", 0, 0, 12000, date)
    val lap9 = Lap("name", 0, 0, 15000, date)
    val lap10 = Lap("name", 0, 0, 8000, date)
    val lap11 = Lap("name", 0, 0, 13000, date)
    val lap12 = Lap("name", 0, 0, 12000, date)
    calculator.getBestNLaps(Seq(lap1, lap2, lap3, lap4, lap5, lap6, lap7, lap8, lap9, lap10, lap11, lap12), 3) should be(Seq(lap5, lap6, lap7))
  }

  it should "find fastest lap" in {
    val lap1 = Lap("name", 0, 0, 15000, date)
    val lap2 = Lap("name", 0, 0, 14000, date)
    val lap3 = Lap("name", 0, 0, 13000, date)
    val lap4 = Lap("name", 0, 0, 12000, date)
    val lap5 = Lap("name", 0, 0, 9234, date)
    val lap6 = Lap("name", 0, 0, 9235, date)
    val lap7 = Lap("name", 0, 0, 9236, date)
    val lap8 = Lap("name", 0, 0, 12000, date)
    val lap9 = Lap("name", 0, 0, 15000, date)
    val lap10 = Lap("name", 0, 0, 8000, date)
    val lap11 = Lap("name", 0, 0, 13000, date)
    val lap12 = Lap("name", 0, 0, 12000, date)
    calculator.getBestNLaps(Seq(lap1, lap2, lap3, lap4, lap5, lap6, lap7, lap8, lap9, lap10, lap11, lap12), 1) should be(Seq(lap10))
  }

  it should "find best five minutes simple" in {
    val lap1 = Lap("name", 0, 0, 100000, date)
    val lap2 = Lap("name", 0, 0, 100000, date)
    val lap3 = Lap("name", 0, 0, 101000, date)
    calculator.getBestFiveMinutes(Seq(lap1, lap2, lap3)) should be((Seq(lap1, lap2, lap3), 301000))
  }

  it should "return empty result if not five minutes passed" in {
    val lap1 = Lap("name", 0, 0, 100000, date)
    calculator.getBestFiveMinutes(Seq(lap1)) should be((Seq(), 0l))
  }

  it should "return empty result if no laps runned" in {
    calculator.getBestFiveMinutes(Seq()) should be((Seq(), 0l))
  }

  it should "find exact five minutes passed" in {
    val lap1 = Lap("name", 0, 0, 100000, date)
    val lap2 = Lap("name", 0, 0, 100000, date)
    val lap3 = Lap("name", 0, 0, 100000, date)
    calculator.getBestFiveMinutes(Seq(lap1, lap2, lap3)) should be((Seq(lap1, lap2, lap3), 300000))
  }

  it should "return empty for not fully five minute" in {
    val lap1 = Lap("name", 0, 0, 100000, date)
    val lap2 = Lap("name", 0, 0, 100000, date)
    val lap3 = Lap("name", 0, 0, 99999, date)
    calculator.getBestFiveMinutes(Seq(lap1, lap2, lap3)) should be((Seq(), 0l))
  }

  it should "find five minutes when more laps" in {
    val lap1 = Lap("name", 0, 0, 100000, date)
    val lap2 = Lap("name", 0, 0, 100000, date)
    val lap3 = Lap("name", 0, 0, 100000, date)
    val lap4 = Lap("name", 0, 0, 100000, date)
    val lap5 = Lap("name", 0, 0, 100000, date)
    calculator.getBestFiveMinutes(Seq(lap1, lap2, lap3, lap4, lap5)) should be((Seq(lap1, lap2, lap3), 300000))
  }

  it should "find five minutes more" in {
    val lap1 = Lap("name", 0, 0, 100000, date)
    val lap2 = Lap("name", 0, 0, 100000, date)
    val lap3 = Lap("name", 0, 0, 90000, date)
    val lap4 = Lap("name", 0, 0, 90000, date)
    val lap5 = Lap("name", 0, 0, 20000, date)
    val lap6 = Lap("name", 0, 0, 250000, date)
    val lap7 = Lap("name", 0, 0, 200000, date)
    calculator.getBestFiveMinutes(Seq(lap1, lap2, lap3, lap4, lap5, lap6, lap7)) should be((Seq(lap2, lap3, lap4, lap5), 300000))
  }

  it should "calculate best five by number of laps" in {
    val l = List(BestMinutes("name1", 123, 2, 305000, "", "", null), BestMinutes("name2", 321, 5, 300000, "", "", null))
    calculator.sortBestFive(l) should be(l.reverse)
  }

  it should "calculate best five by time" in {
    val l = List(BestMinutes("name1", 123, 3, 302000, "", "", null), BestMinutes("name2", 321, 3, 301000, "", "", null))
    calculator.sortBestFive(l) should be(l.reverse)
  }

  it should "calculate best again" in {
    val l = List(BestMinutes("name1", 123, 30, 302000, "", "", null), BestMinutes("name2", 321, 3, 301000, "", "", null))
    calculator.sortBestFive(l) should be(l)
  }

}
