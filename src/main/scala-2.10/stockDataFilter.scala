import java.text.SimpleDateFormat
import java.util.Date

import org.joda.time.{LocalDate, LocalTime}


/**
 * Created by Emeka Ume(emeka.ume@accenture.com) on 9/6/2015.
 * This class was created to filter out NYSE Market Close due
 * the fact that it would miss skew moving averages.
 * Get Info here: https://www.nyse.com/markets/hours-calendars
 * and
 *
 */
class stockDataFilter(val s: stockPriceInfo) extends Serializable {

  val dateDelim = "-"
  val timeDelim = ":"
  val dateAndTime =  s.dateTime
  val splitDateTime  = dateAndTime.split("@")
  val dateStamp = splitDateTime(0)
  val time =  splitDateTime(1)
  val splitDate = dateStamp.split(dateDelim)
  val splitTime = time.split(timeDelim)
  val year =   splitDate(0)
  val month = splitDate(1)
  val day =  splitDate(2)
  val hour = splitTime(0)
  val minute = splitTime(1)
  val second = splitTime(2)

  val openingBell: LocalTime = new LocalTime(9, 30)
  val closingBell: LocalTime = new LocalTime(16, 0)
  val currentTime: LocalTime = new LocalTime(hour.toInt, minute.toInt)

  //NonTradingSessions
  val newYearsDay: Date = new Date(year.toInt - 1900, 0, 1)
  val weekends: List[String] = List("Saturday", "Sunday")

  val date = new Date(year.toInt - 1900, month.toInt - 1, day.toInt)
  val currentDate = new LocalDate(year.toInt - 1990, month.toInt, day.toInt)

  //New York Stock Exchange operates from 9:30 a.m. to 4:00 p.m
  def isWithinTradingSession: Boolean = {
    val isAfterOpen: Boolean = currentTime.isAfter(openingBell)
    val isBeforeClose: Boolean = currentTime.isBefore(closingBell)
    isAfterOpen && isBeforeClose
  } //return True if it is withing trading time

  // Mon-Fri, BUT NOT Sat & Sun
  def isWeekDay: Boolean = {
    val date = new Date(year.toInt - 1900, month.toInt - 1, day.toInt)
    val dayOfWeek = new SimpleDateFormat("EEEE").format(date)
    if (!weekends.contains(dayOfWeek)) true else false
  } //return True if weekday

  //NYSE is closed on the following days. No trading occures
  def isNotHoliday: Boolean = {
    def isNotNewYears: Boolean = if (date.equals(newYearsDay) == false) true else false
    //returns true if it is new years

    // MLK is observed on the third Monday of January each year
    def isNotMLK_Day: Boolean = {
      val isJan = if (month.toInt - 1 == 0) true else false
      val date = new Date(year.toInt - 1900, month.toInt - 1, day.toInt)
      val isMonday = if ((new SimpleDateFormat("EEEE").format(date)).equals("Monday")) true else false
      //val isMonday = if (jodaDate.getDayOfWeek != DateTimeConstants.MONDAY) true else false
      val whatWeek = Math.ceil(day.toInt / 7)
      val is3rdMonday = if (whatWeek == 3) true else false

      if (isJan && isMonday && is3rdMonday == true) false else true
    } // return True if it is not MLK Day

    //GW's day or Presidents day is always on the 3rd Monday of Febuary
    def isNotWasgingtonBirthDay(): Boolean = {
      val isFeb = if (month.toInt - 1 == 1) true else false
      val date = new Date(year.toInt - 1900, month.toInt - 1, day.toInt)
      val isMonday = if ((new SimpleDateFormat("EEEE").format(date)).equals("Monday")) true else false
      val whatWeek = Math.ceil(day.toInt / 7)
      val is3rdDay = if (whatWeek == 3) true else false

      if (isFeb && isMonday && is3rdDay == true) false else true
    } //Return True if it is not GW Day

    //the Friday immediately preceding Easter Sunday
    def isGoodFriday: Boolean = {
      //first get Easter Sunday Month and Day
      val easterJavaClass = new Easter(year.toInt)
      val easterMonthDay = easterJavaClass.getEaster().split(" ")
      val goodFridayMonth = easterMonthDay(0) //April, March
      val goodFridayDay: Int = easterMonthDay(1).toInt //25, 4, 7
      val monthIndexList = List("January", "Febuary", "March", "April", "May", "June", "July", "August", "September",
          "October", "November", "December")
      val isGoodFridayMonth = if (this.month.toInt - 1 == monthIndexList.indexOf(goodFridayMonth)) true else false
      val isGoodFridayDay = if (day.toInt == goodFridayDay) true else false

      if (isGoodFridayMonth && isGoodFridayDay == true) false else true
    } //Returns true if not GoodFriday session

    //Last Monday in May
    def isNotMemorialDay: Boolean = {
      val isMay = if (month.toInt - 1 == 0) true else false
      val date = new Date(year.toInt - 1900, month.toInt - 1, day.toInt)
      val isMonday = if ((new SimpleDateFormat("EEEE").format(date)).equals("Monday")) true else false
      val whatWeek = Math.ceil(day.toInt / 7)
      val isLastMondayOfMonth = if (whatWeek == 4 || whatWeek == 5) true else false

      if (isMay && isMonday && isLastMondayOfMonth == true) false else true
    } //Returns True if not in Memorial Day Session

    /**
    4th Of July on a weekday if the 4th of July falls on a Sunday,
    the federal observed holiday is the following Monday. If July 4
    falls on a Saturday Friday, July 3
      */
    def isNotForthOfJulyWeekendDay: Boolean = {
      val isJuly = if (month.toInt - 1 == 6) true else false
      val weekEndIndex = List("Saturday", "Sunday")
      val isFriday = if ((new SimpleDateFormat("EEEE").format(date)).equals("Friday")) true else false
      val isMonday = if ((new SimpleDateFormat("EEEE").format(date)).equals("Monday")) true else false
      val isThe3rd = if (day.toInt == 3) true else false
      val isThe5th = if (day.toInt == 5) true else false
      val isFridayOff = if (isThe3rd && isFriday) true else false
      val isMondayOff = if (isThe5th && isMonday) true else false
      val is4thWeekEnd = if (isFridayOff || isMondayOff) true else false

      if (isJuly || is4thWeekEnd == true) false else true
    }

    //The first Monday of September
    def isNotLaborDay: Boolean = {
      val isSeptember = if (month.toInt - 1 == 8) true else false
      val date = new Date(year.toInt - 1900, month.toInt - 1, day.toInt)
      val isMonday = if ((new SimpleDateFormat("EEEE").format(date)).equals("Monday")) true else false
      val whatWeek = Math.ceil(day.toInt / 7)
      val isFirstMondayOfMonth = if (whatWeek == 1) true else false

      if (isSeptember && isMonday && isFirstMondayOfMonth == true) false else true
    }

    //closed on thursdays and only open till 1PM on friday
    def isNotThanksGivingSession(): Boolean = {
      val isNovember = if (month.toInt - 1 == 10) true else false
      val date = new Date(year.toInt - 1900, month.toInt - 1, day.toInt)
      val isThursday = if ((new SimpleDateFormat("EEEE").format(date)).equals("Thursday")) true else false
      val whatWeek = Math.ceil(day.toInt / 7)
      val is4thThursdayOfMonth = if (whatWeek == 4) true else false
      val isFridayAfter1PM_JodaDate = new LocalTime(year.toInt - 1900, month.toInt - 1, day.toInt + 1)
      val thanksGivingClosing_JodaTime = new LocalTime(12, 59, 59)
      val isPastFridayAt1PM =
        if (currentDate.equals(isFridayAfter1PM_JodaDate) &&
          currentTime.isAfter(thanksGivingClosing_JodaTime)) true
        else false

      if ((isNovember && isThursday && is4thThursdayOfMonth) || isPastFridayAt1PM == true) false else true
    }

    def isNotChristmas: Boolean = {
      val christmasDay = new LocalDate(year.toInt - 1900, 12, 25)
      val the24th = new LocalDate(year.toInt - 1900, 12, 24)
      val isPast1PM_JodaDate = new LocalTime(12, 59, 59)

      if (currentDate.equals(christmasDay) ||
        (currentDate.equals(the24th) && currentTime.equals(isPast1PM_JodaDate)) == true) true
      else false
    }

    ((isNotNewYears || isNotMLK_Day || isNotWasgingtonBirthDay || isGoodFriday || isNotMemorialDay
      || isNotForthOfJulyWeekendDay || isNotLaborDay || isNotThanksGivingSession || isNotChristmas))
  }

  def requirementsMet: Option[stockPriceInfo] = isWithinTradingSession && isWeekDay && isNotHoliday  match {
    case true => Some(s)
    case false => None
  }
}

