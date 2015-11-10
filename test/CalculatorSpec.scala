import controllers.CoreLap
import models.Lap
import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by patrikv on 05/11/15.
 */
class CalculatorSpec extends FlatSpec with Matchers {
  val calculator = new controllers.Calculator()
  "Calculator" should "return empty Seq() if input length < nrOfLaps" in {
    calculator.getBestNLaps(Seq(), 1) should be(Seq())
  }

  it should "return input seq if seq.length == nrOfLaps" in {
    val lap = Lap("name", 10.0, null)
    calculator.getBestNLaps(Seq(lap, lap, lap), 3) should be (Seq(lap, lap, lap))
  }

  it should "return best three laps if given four and nrOfLaps = 3" in {
    val lap1 = Lap("name", 12.0, null)
    val lap2 = Lap("name", 13.0, null)
    val lap3 = Lap("name", 14.0, null)
    val lap4 = Lap("name", 15.0, null)
    calculator.getBestNLaps(Seq(lap1, lap2, lap3, lap4), 3) should be (Seq(lap1, lap2, lap3))
  }

  it should "handle if other order" in {
    val lap1 = Lap("name", 15.0, null)
    val lap2 = Lap("name", 14.0, null)
    val lap3 = Lap("name", 13.0, null)
    val lap4 = Lap("name", 12.0, null)
    calculator.getBestNLaps(Seq(lap1, lap2, lap3, lap4), 3) should be (Seq(lap2, lap3, lap4))
  }

  it should "handle many laps" in {
    val lap1 = Lap("name", 15.0, null)
    val lap2 = Lap("name", 14.0, null)
    val lap3 = Lap("name", 13.0, null)
    val lap4 = Lap("name", 12.0, null)
    val lap5 = Lap("name", 9.234, null)
    val lap6 = Lap("name", 9.235, null)
    val lap7 = Lap("name", 9.236, null)
    val lap8 = Lap("name", 12.0, null)
    val lap9 = Lap("name", 15.0, null)
    val lap10 = Lap("name", 8.0, null)
    val lap11 = Lap("name", 13.0, null)
    val lap12 = Lap("name", 12.0, null)
    calculator.getBestNLaps(Seq(lap1, lap2, lap3, lap4, lap5, lap6, lap7, lap8, lap9, lap10, lap11, lap12), 3) should be (Seq(lap5, lap6, lap7))
  }

  it should "find fastest lap" in {
    val lap1 = Lap("name", 15.0, null)
    val lap2 = Lap("name", 14.0, null)
    val lap3 = Lap("name", 13.0, null)
    val lap4 = Lap("name", 12.0, null)
    val lap5 = Lap("name", 9.234, null)
    val lap6 = Lap("name", 9.235, null)
    val lap7 = Lap("name", 9.236, null)
    val lap8 = Lap("name", 12.0, null)
    val lap9 = Lap("name", 15.0, null)
    val lap10 = Lap("name", 8.0, null)
    val lap11 = Lap("name", 13.0, null)
    val lap12 = Lap("name", 12.0, null)
    calculator.getBestNLaps(Seq(lap1, lap2, lap3, lap4, lap5, lap6, lap7, lap8, lap9, lap10, lap11, lap12), 1) should be (Seq(lap10))
  }

  it should "find best five minutes simple" in {
    val lap1 = Lap("name", 100.0, null)
    val lap2 = Lap("name", 100.0, null)
    val lap3 = Lap("name", 101.0, null)
    calculator.getBestFiveMinutes(Seq(lap1, lap2, lap3))  should be ((Seq(lap1, lap2, lap3), 301))
  }

  it should "return empty result if not five minutes passed" in {
    val lap1 = Lap("name", 100.0, null)
    calculator.getBestFiveMinutes(Seq(lap1)) should be ((Seq(), 0.0))
  }

}
