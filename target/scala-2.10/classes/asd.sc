import org.joda.time.LocalTime

val yy = new stockPriceInfo("AAA", "2015-12-9@19:24:22", 111.12, 111.13,110.09, 123456789)

val sss = new stockDataFilter(yy).requirementsMet.get

val openingBell: LocalTime = new LocalTime(9, 30)
val closingBell: LocalTime = new LocalTime(16, 0)
val currentTime: LocalTime = new LocalTime(10, 11)

def isWithinTradingSession(l: LocalTime): Boolean = {
  val isAfterOpen: Boolean = currentTime.isAfter(openingBell)
  val isBeforeClose: Boolean = currentTime.isBefore(closingBell)
  isAfterOpen && isBeforeClose
}

isWithinTradingSession(currentTime)

/*
def requirementsMet: Option[stockPriceInfo] = isWithinTradingSession  match {
  case true => Some(s)
  case false => None
}*/
