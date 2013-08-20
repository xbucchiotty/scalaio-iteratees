package fr.xebia.scalaio.iteratee

import java.util.Currency
import play.api.libs.iteratee.Iteratee
import scala.concurrent.ExecutionContext

object AmountConsumer {

  val EUR = Currency.getInstance("EUR")

  def sum(implicit ctx:ExecutionContext): AmountConsumer[Amount] =
    Iteratee.fold[Amount, Amount](Amount(0, EUR))(_ + _)
}
