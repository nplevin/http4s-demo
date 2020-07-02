package timestamptest
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
//import assertions._

sealed abstract class ShortSimulation(startWith: Int, endWith: Int, dur:Int) extends Simulation {

  val url = sys.env.get("GATLING_URL").map(u=>"""http://"""+u).getOrElse("""http://localhost:8080""")
  

  val httpConf = http
    .baseUrl(url)
    .check(responseTimeInMillis.lte(400), substring("date"))


  val scn = scenario("timestamps") // A scenario is a chain of requests and pauses
    .exec(http("date-time").get("/date-time"))

   setUp(
    scn.inject(
      rampConcurrentUsers(startWith) to (endWith) during (dur minutes)
      )
    ).throttle(reachRps(1000) in (2 minutes), holdFor(dur minutes))
     .protocols(httpConf)
     
}

sealed abstract class SustainedSimulation(endWith: Int, sustain:Int) extends Simulation {

  val url = sys.env.get("GATLING_URL").map(u=>"""http://"""+u).getOrElse("""http://localhost:8080""")

  val httpConf = http
    .baseUrl(url)
    .check(responseTimeInMillis.lte(400), substring("date"))


  val scn = scenario("timestamps") // A scenario is a chain of requests and pauses
    .exec(http("date-time").get("/date-time"))

   setUp(
      //scn.inject(rampConcurrentUsers(startWith) to (endWith) during (dur minutes)),
      scn.inject(constantUsersPerSec(endWith) during (sustain minutes))
    ).throttle(reachRps(1000) in (2 minutes), holdFor(sustain minutes))
     .protocols(httpConf)
     
}

class TimeStampSimulationRamp10 extends ShortSimulation(50,500,10)
class TimeStampSimulation1000 extends SustainedSimulation(1000,5)
class TimeStampSimulationEasyShort extends ShortSimulation(10,200,5)
class TimeStampSimulation200 extends SustainedSimulation(200,5) 
class TimeStampSimulationEasiestShort extends ShortSimulation(5,50,5)
class TimeStampSimulation50 extends SustainedSimulation(50,5) 
class TimeStampSimulation100 extends SustainedSimulation(100,5) 
class TimeStampSimulation500 extends SustainedSimulation(500,5)
class TimeStampSimulation300 extends SustainedSimulation(300,5)

