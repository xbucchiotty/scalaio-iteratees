package fr.xebia

import org.joda.time.{Period, ReadablePeriod, DateMidnight}

package object scalaio {

  object implicits {

    implicit class ExtendedDateMidnight(val date: DateMidnight) extends AnyVal {
      def +(period: ReadablePeriod): DateMidnight = date.plus(period)
    }

    implicit class IntForPeriod(val number: Int) {
      def year = Period.years(number)

      def years = Period.years(number)

      def month = Period.months(number)

      def months = Period.months(number)
    }

  }

}
