package fr.xebia.scalaio.iteratee

import org.joda.time.DateMidnight
import fr.xebia.scalaio.implicits._

case class Loan(
                 initial: Amount,
                 duration: Int,
                 rowIt: RowIt) {

  def rows: Seq[Row] = Stream.iterate(rowIt.first)(rowIt.op) take duration

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

        def date = start + (1 year)

        def interests = initial * fixedRate

        def amortization = initial / duration

        def outstanding = initial - amortization

      },
        op = (last: Row) => new Row {

          def date = last.date + (1 year)

          def interests = last.outstanding * fixedRate

          def amortization = last amortization

          def outstanding = last.outstanding - amortization

        })
    )
  }

}