import java.util.Calendar

import com.google.gson.Gson
import models.Lap
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient

import scala.util.Random

//import play.api.libs.ws.WSResponse

//import play.api.libs.ws.WS


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

  val cal = Calendar.getInstance()

  for (a <- 1 until 2000)
    for ((driver, t) <- drivers) send(driver, t)

  send("P.Vestergren", avg())
  send("P.Vestergren", avg())

  def send(driver: String, lapTime: Double) = {
    val lap = new Lap(driver, lapTime)
   // println(lap)
    val lapAsJson = new Gson().toJson(lap)
    //println(lapAsJson)
    // add name value pairs to a post object
    val post = new HttpPost("http://localhost:9000/addLap")
    post.setHeader("Content-type", "application/json")
    post.setEntity(new StringEntity(lapAsJson))

    // send the post request
    val client = new DefaultHttpClient
    val response = client.execute(post)
   // println("--- HEADERS ---")
    response.getAllHeaders
  }

  def generateLapTime(low: Double, high: Double): Double = {
    val r = new Random()
    //val range = low to high
    //range(r.nextDouble(range))
    low + (high - low) * r.nextDouble();
  }

  def pro() = round(generateLapTime(31.000, 28.000))

  def avg() = round(generateLapTime(32.500, 29.500))

  def nov() = round(generateLapTime(40.000, 31.000))

  def round(d: Double) = BigDecimal(d).setScale(3, BigDecimal.RoundingMode.HALF_UP).toDouble
}
