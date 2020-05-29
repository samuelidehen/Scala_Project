package ca.mcit
import ca.mcit.schema.{Calendar, Route}

import scala.io.{BufferedSource, Source}

object Main extends App  {

  // Please Ignore file
  //I was testing my file reading code 
  //To demo the app please run EnrichedTrip
  
  val filePath = "/home/samuel/Desktop/routes.txt"
  val source: BufferedSource = Source.fromFile(filePath)

  source.getLines.drop(1)
    .map(line => line.split(","))
    .map(a => Route(a(0).toInt, a(1), a(2),a(3),a(4),a(5),a(6),a(7)))
    .foreach(route => println(route))

  source.close()
 /* class CalendarLookup(calendars: List[Calendar]){
    private val lookupTable:Map[String,Calendar] = calendars.map(calendar => calendar.service_id -> calendar).toMap
    def lookup(serviceId:String): Calendar = lookupTable.getOrElse(serviceId,null)
  }*/
}



