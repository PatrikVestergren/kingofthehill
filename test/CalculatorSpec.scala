import java.text.SimpleDateFormat
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

  it should "calculate isBetterFive" in {
    calculator.isBetterFive((17, 350000), (16, 350000)) should be(true)
    calculator.isBetterFive((17, 350000), (16, 300000)) should be(true)
    calculator.isBetterFive((1, 350000), (0, 350000)) should be(true)
    calculator.isBetterFive((17, 350000), (18, 350000)) should be(false)
    calculator.isBetterFive((17, 300000), (18, 350000)) should be(false)
    calculator.isBetterFive((0, 350000), (1, 350000)) should be(false)
    calculator.isBetterFive((17, 350000), (17, 350001)) should be(true)
    calculator.isBetterFive((17, 350001), (17, 350000)) should be(false)
    calculator.isBetterFive((17, 350000), (17, 350000)) should be(false)
  }
  val lapFormat = new SimpleDateFormat("mm:ss.SSS")

  def formatTime(x: Long): String = lapFormat.format(x)
  it should "da da da" in {
    val s = List(19991,23047,19793,19682,19400,19351,19548,20160,20081,19350,19936,20989,20003,19357,19211,136255)
    println(s.length + " : " + s.sum)
      val l = Seq(Lap("driver", 123, 77,	26087, null),
      Lap("driver", 123, 78,	19991, null),
      Lap("driver", 123, 79,	23047, null),
      Lap("driver", 123, 80,	19793, null),
      Lap("driver", 123, 81,	19682, null),
      Lap("driver", 123, 82,	19400, null),
      Lap("driver", 123, 83,	19351, null),
      Lap("driver", 123, 84,	19548, null),
      Lap("driver", 123, 85,	20160, null),
      Lap("driver", 123, 86,	20081, null),
      Lap("driver", 123, 87,	19350, null),
      Lap("driver", 123, 88,	19936, null),
      Lap("driver", 123, 89,	20989, null),
      Lap("driver", 123, 90,	20003, null),
      Lap("driver", 123, 91,	19357, null),
      Lap("driver", 123, 92,	19211, null))
     /* Lap("driver", 123, 93, 136255, null))*/
val res = calculator.getBestFiveMinutes(l)
    println(res._1.length + "/" + formatTime(res._2))
  }

}

//Patrik 4wd Vestergren [7172582] - Best 3 laps: 00:57.195 Best five minutes: 16/06:36.154
// Bästa fem minuter rapporterat 78 till och med 93, bästa 3 min 124 -126, bästa tid 18.803

//val patriks = Seq(Lap("driver", 123, 1, 20668, null),
//  Lap("driver", 123, 2,	30013, null),
//  Lap("driver", 123, 3,	22110, null),
//  Lap("driver", 123, 4,	24122, null),
//  Lap("driver", 123, 5,	102890, null),
//  Lap("driver", 123, 6,	20006, null),
//  Lap("driver", 123, 7,	20382, null),
//  Lap("driver", 123, 8,	19516, null),
//  Lap("driver", 123, 9,	20641, null),
//  Lap("driver", 123, 10, 22708, null),
//  Lap("driver", 123, 11, 20020, null),
//  Lap("driver", 123, 12, 21161, null),
//  Lap("driver", 123, 13,	21228, null),
//  Lap("driver", 123, 14,	19626, null),
//  Lap("driver", 123, 15,	20329, null),
//  Lap("driver", 123, 16,	20403, null),
//  Lap("driver", 123, 17,	20988, null),
//  Lap("driver", 123, 18,	2703907, null),
//  Lap("driver", 123, 19,	22260, null),
//  Lap("driver", 123, 20,	20500, null),
//  Lap("driver", 123, 21,	20446, null),
//  Lap("driver", 123, 22,	21011, null),
//  Lap("driver", 123, 23,	21357, null),
//  Lap("driver", 123, 24,	22272, null),
//  Lap("driver", 123, 25,	20151, null),
//  Lap("driver", 123, 26,	19421, null),
//  Lap("driver", 123, 27,	19630, null),
//  Lap("driver", 123, 28,	19662, null),
//  Lap("driver", 123, 29,	19352, null),
//  Lap("driver", 123, 30,	19882, null),
//  Lap("driver", 123, 31,	19579, null),
//  Lap("driver", 123, 32,	22460, null),
//  Lap("driver", 123, 33,	22685, null),
//  Lap("driver", 123, 34,	19437, null),
//  Lap("driver", 123, 35,	21135, null),
//  Lap("driver", 123, 36,	111606, null),
//  Lap("driver", 123, 37,	19787, null),
//  Lap("driver", 123, 38,	20507, null),
//  Lap("driver", 123, 39,	20209, null),
//  Lap("driver", 123, 40,	20110, null),
//  Lap("driver", 123, 41,	20325, null),
//  Lap("driver", 123, 42,	20584, null),
//  Lap("driver", 123, 43,	19443, null),
//  Lap("driver", 123, 44,	101247, null),
//  Lap("driver", 123, 45,	121730, null),
//  Lap("driver", 123, 46,	1442305, null),
//  Lap("driver", 123, 47,	20644, null),
//  Lap("driver", 123, 48,	19675, null),
//  Lap("driver", 123, 49,	20032, null),
//  Lap("driver", 123, 50,	21582, null),
//  Lap("driver", 123, 51,	20017, null),
//  Lap("driver", 123, 52,	20157, null),
//  Lap("driver", 123, 53,	19728, null),
//  Lap("driver", 123, 54,	20423, null),
//  Lap("driver", 123, 55,	19964, null),
//  Lap("driver", 123, 56,	19888, null),
//  Lap("driver", 123, 57,	19999, null),
//  Lap("driver", 123, 58,	20389, null),
//  Lap("driver", 123, 59,	19182, null),
//  Lap("driver", 123, 60,	20279, null),
//  Lap("driver", 123, 61,	20687, null),
//  Lap("driver", 123, 62,	24977, null),
//  Lap("driver", 123, 63,	19286, null),
//  Lap("driver", 123, 64,	19640, null),
//  Lap("driver", 123, 65,	20116, null),
//  Lap("driver", 123, 66,	19726, null),
//  Lap("driver", 123, 67,	19170, null),
//  Lap("driver", 123, 68,	20159, null),
//  Lap("driver", 123, 69,	19223, null),
//  Lap("driver", 123, 70,	19079, null),
//  Lap("driver", 123, 71,	19476, null),
//  Lap("driver", 123, 72,	20183, null),
//  Lap("driver", 123, 73,	3817041, null),
//  Lap("driver", 123, 74,	19869, null),
//  Lap("driver", 123, 75,	53978, null),
//  Lap("driver", 123, 76,	21437, null),
//  Lap("driver", 123, 77,	26087, null),
//  Lap("driver", 123, 78,	19991, null),
//  Lap("driver", 123, 79,	23047, null),
//  Lap("driver", 123, 80,	19793, null),
//  Lap("driver", 123, 81,	19682, null),
//  Lap("driver", 123, 82,	19400, null),
//  Lap("driver", 123, 83,	19351, null),
//  Lap("driver", 123, 84,	19548, null),
//  Lap("driver", 123, 85,	20160, null),
//  Lap("driver", 123, 86,	20081, null),
//  Lap("driver", 123, 87,	19350, null),
//  Lap("driver", 123, 88,	19936, null),
//  Lap("driver", 123, 89,	20989, null),
//  Lap("driver", 123, 90,	20003, null),
//  Lap("driver", 123, 91,	19357, null),
//  Lap("driver", 123, 92,	19211, null),
//  Lap("driver", 123, 93, 136255, null),
//  Lap("driver", 123, 94, 19185, null),
//  Lap("driver", 123, 95, 19242, null),
//  Lap("driver", 123, 96, 19198, null),
//  Lap("driver", 123, 97, 20473, null),
//  Lap("driver", 123, 98, 19449, null),
//  Lap("driver", 123, 99, 19124, null),
//  Lap("driver", 123, 100,	19770, null),
//  Lap("driver", 123, 101,	20549, null),
//  Lap("driver", 123, 102,	19208, null),
//  Lap("driver", 123, 103,	21938, null),
//  Lap("driver", 123, 104,	19308, null),
//  Lap("driver", 123, 105,	19706, null),
//  Lap("driver", 123, 106,	34267, null),
//  Lap("driver", 123, 107,	20903, null),
//  Lap("driver", 123, 108,	1843957, null),
//  Lap("driver", 123, 109,	19783, null),
//  Lap("driver", 123, 110,	19962, null),
//  Lap("driver", 123, 111,	20271, null),
//  Lap("driver", 123, 112,	20483, null),
//  Lap("driver", 123, 113,	19487, null),
//  Lap("driver", 123, 114,	31397, null),
//  Lap("driver", 123, 115,	19585, null),
//  Lap("driver", 123, 116,	20879, null),
//  Lap("driver", 123, 117,	19979, null),
//  Lap("driver", 123, 118,	20301, null),
//  Lap("driver", 123, 119,	20694, null),
//  Lap("driver", 123, 120,	19194, null),
//  Lap("driver", 123, 121,	19660, null),
//  Lap("driver", 123, 122,	20061, null),
//  Lap("driver", 123, 123,	19718, null),
//  Lap("driver", 123, 124,	19321, null),
//  Lap("driver", 123, 125,	18803, null),
//  Lap("driver", 123, 126,	19071, null),
//  Lap("driver", 123, 127,	19771, null),
//  Lap("driver", 123, 128,	20624, null),
//  Lap("driver", 123, 129,	22950, null),
//  Lap("driver", 123, 130,	19528, null),
//  Lap("driver", 123, 131,	3021216, null),
//  Lap("driver", 123, 132,	55777, null),
//  Lap("driver", 123, 133,	20145, null),
//  Lap("driver", 123, 134,	19566, null),
//  Lap("driver", 123, 135,	22680, null),
//  Lap("driver", 123, 136,	21787, null),
//  Lap("driver", 123, 137,	19974, null),
//  Lap("driver", 123, 138,	19314, null),
//  Lap("driver", 123, 139,	21736, null))

