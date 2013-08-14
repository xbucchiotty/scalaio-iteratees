package fr.xebia.scalaio

import org.joda.time.DateMidnight
import fr.xebia.scalaio.iteratee.Amount
import java.util.Currency

trait Implicits {
  implicit def parseDate(dateAsString: String): DateMidnight = DateMidnight.parse(dateAsString)

  implicit def doubleToAmount(value: Double) = new Implicits.DoubleToAmount(value)
}

object Implicits {

  class DoubleToAmount(val value: Double) extends AnyVal {
    def EUR = Amount(value, Currency.getInstance("EUR"))
  }

}