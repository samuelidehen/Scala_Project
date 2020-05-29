package ca.mcit.Enriched

import ca.mcit.schema.{Route, RouteTrip, Trip}



case class JoinOutput(left: Any, right: Option[Any])

class RouteLookUpTable[Trip, Route](val joinCond: (Trip, Route) => Boolean)  {
  def join(a: List[Trip], b: List[Route]): List[JoinOutput] = for {
    i <- a
    j <- b
    if joinCond(i,j)
  } yield JoinOutput(i, Some(j))
}



class CalendarLookUpTable[Calendar, RouteTrip](val joinCond: (Calendar, RouteTrip) => Boolean) {
 def join(a: List[Calendar], b: List[RouteTrip]): List[JoinOutput] = for {
    l <- a
    r <- b
    if joinCond(l, r)
  } yield JoinOutput(l, Some(r))

}

