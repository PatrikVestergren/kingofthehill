import java.util.Calendar

import com.google.gson.Gson
import models.Lap
import org.apache.http.client.methods.{HttpDelete, HttpPost}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient

import scala.util.Random


/**
 * Created by patrikv on 28/07/15.
 */
object Sender extends App {

  val drivers = Seq(("T.Tessman", pro()),
    ("P.Vestergren", pro()),
    ("John Doe", pro()),
    ("Speedy Gonzales", avg()),
    ("L. Skywalker", avg()),
    ("H. Solo", avg()),
    ("D.Vader", nov()),
    ("R2D2", nov()),
    ("J. Tebo", pro()),
    ("Ben Ten", nov()),
    ("C. America", nov())

  )
//deleteAll()
  val cal = Calendar.getInstance()

  for (i <- 0 until 10) {
    for ((driver, t) <- drivers) send(driver, t)
    Thread.sleep(1000)
  }

  //send("P.Vestergren", avg())
  //send("P.Vestergren", avg())

  def send(driver: String, lapTime: Long) = {
    val lap = new Lap(driver, 0l, 0l, lapTime, cal.getTimeInMillis)
   // println(lap)
    val lapAsJson = new Gson().toJson(lap)
    println(lapAsJson)
    //println(lapAsJson)
    // add name value pairs to a post object
   // val post = new HttpPost("http://hidden-tundra-1337.herokuapp.com/addLap")//
    val post = new HttpPost("http://localhost:9000/addLap")
    post.setHeader("Content-type", "application/json")
    post.setEntity(new StringEntity(lapAsJson))

    // send the post request
    val client = new DefaultHttpClient
    val response = client.execute(post)
   // println("--- HEADERS ---")

    response.getAllHeaders
  }

  def deleteAll() = {
    //println(lapAsJson)
    // add name value pairs to a post object
    // val post = new HttpPost("http://hidden-tundra-1337.herokuapp.com/addLap")//
    val post = new HttpDelete("http://localhost:9000/deleteAll")
    post.setHeader("Content-type", "application/json")

    // send the post request
    val client = new DefaultHttpClient
    val response = client.execute(post)
    // println("--- HEADERS ---")

    response.getAllHeaders
  }

  def generateLapTime(low: Long, high: Long): Long = {
    val r = new Random()
    //val range = low to high
    //range(r.nextDouble(range))
    (low + (high - low) * r.nextDouble()).toLong
  }

  def pro() = generateLapTime(31000, 28000)

  def avg() = generateLapTime(32500, 29500)

  def nov() = generateLapTime(40000, 31000)

  //def round(d: Double) = BigDecimal(d).setScale(3, BigDecimal.RoundingMode.HALF_UP).toDouble
}
