package ca.mcit.enriched

import ca.mcit.schema.{Calendar,  Route, Trip}
import java.io._

import scala.io.Source

object EnrichedTrip extends App {
  // File write instantiation
  val writeEnrichedFile= new FileWriter("Data/enriched-trip.txt")
  val bw = new BufferedWriter(writeEnrichedFile)
  
  //Step 1
  //File Read for Trips and Route
  val tripSource = Source.fromFile("Data/trips.txt")
  val tripList: List[Trip] = tripSource
    .getLines()
    .toList
    .tail
    .map(_.split(",", -1))
    .map(p => Trip(p(0).toInt, p(1), p(2), p(3), p(4).toInt, p(5).toInt, p(6).toInt,
      if (p(7).isEmpty) None else Some(p(7)),
      if (p(8).isEmpty) None else Some(p(8))))
  tripSource.close()

  val routeSource =  Source.fromFile("Data/routes.txt")
  val routeList: List[Route] = routeSource
    .getLines()
    .toList
    .tail
    .map(_.split(",", -1))
    .map(p => Route(p(0).toInt, p(1), p(2), p(3), p(4), p(5), p(6),p(7)))
  routeSource.close()
  
  //Trip Route Enrichement (Intermediate)
  val routeTrips: List[JoinOutput] = new RouteLookUpTable[Trip, Route]((i, j) => i.route_id == j.route_id)
    .join(tripList, routeList)
  
  //Step 2
  //File Read for Calendar
  val calendarSource =  Source.fromFile("Data/calendar.txt")
  val calendarList: List[Calendar] = calendarSource
    .getLines()
    .toList
    .tail
    .map(_.split(",", -1))
    .map(p => Calendar(p(0), p(1).toInt, p(2).toInt, p(3).toInt, p(4).toInt, p(5).toInt, p(6).toInt,p(7).toInt,p(8).toInt,p(9).toInt))
  calendarSource.close()
 
  //Enriched Trip Join (Final)
  val enrichedTripJoin = new CalendarLookUpTable[Calendar, JoinOutput]((m, n) => m.service_id == n.left.asInstanceOf[Trip].service_id).join(calendarList,routeTrips)

  val enrichedTrip = enrichedTripJoin.map(joinOutput => {

    val trips = Trip.toCsv(joinOutput.right.getOrElse("0" ).asInstanceOf[JoinOutput].left.asInstanceOf[Trip])

    val routes = Route.toCsv(joinOutput.right.getOrElse("0").asInstanceOf[JoinOutput].right.getOrElse(" ").asInstanceOf[Route])

    val calendars = Calendar.toCsv(joinOutput.left.asInstanceOf[Calendar])

    trips + "," + routes + "," + calendars
  })
  
   //Csv file write
   val header:String ="route_id,service_id,trip_id,trip_headsign,direction_id,shape_id,wheelchair_accessible,note_fr,note_en,agency_id,route_short_name,route_long_name,route_type,route_url,route_color,route_text_color,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date"
  bw.write(header)
  for (i <- enrichedTrip) {
    bw.newLine()
    bw.write(i)
  }
  bw.close()
}
