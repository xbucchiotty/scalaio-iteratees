package fr.xebia.scalaio.iteratee

import fr.xebia.scalaio.implicits._
import org.joda.time.DateMidnight
import play.api.libs.iteratee.Enumerator
import scala.concurrent.ExecutionContext

case class Loan(
                 initial: Amount,
                 duration: Int,
                 rowIt: RowIt) {

  def rows(implicit ctx: ExecutionContext): RowProducer =
    Enumerator.enumerate(stream)

  def stream =
    Stream.iterate(rowIt.first)(rowIt.op) take duration
}

object Loan {
  def simple(
              start: DateMidnight,
              initial: Amount,
              duration: Int,
              fixedRate: Double) = {

    Loan(
      initial,
      duration,
      RowIt(first = new Row {

        val date = start + (1 year)

        val interests = initial * fixedRate

        val amortization = initial / duration

        val outstanding = initial - amortization

      },
        op = (last: Row) => new Row {

          def date = last.date + (1 year)

          val interests = last.outstanding * fixedRate

          val amortization = last amortization

          val outstanding = last.outstanding - amortization

        })
    )
  }

  def complex(
               start: DateMidnight,
               initial: Amount,
               duration: Int,
               fixedRate: Double) = {

    val sleepTime = ((Math.random() * 1000) % 2) toLong

    Loan(
      initial,
      duration,
      RowIt(first = new Row {

        val date = start + (1 year)

        val interests = initial * fixedRate

        val amortization = initial / duration

        val outstanding = initial - amortization

      },
        op = (last: Row) => new Row {

          val date = last.date + (1 year)

          val interests = {
            Thread.sleep(sleepTime)
            last.outstanding * fixedRate
          }

          val amortization = last amortization

          val outstanding = last.outstanding - amortization

        })
    )
  }

}