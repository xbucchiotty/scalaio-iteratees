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

        lazy val date = start + (1 year)

        lazy val interests = initial * fixedRate

        lazy val amortization = initial / duration

        lazy val outstanding = initial - amortization

      },
        op = (last: Row) => new Row {

          lazy val date = last.date + (1 year)

          lazy val interests = {
            Thread.sleep(sleepTime)
            last.outstanding * fixedRate
          }

          lazy val amortization = last amortization

          lazy val outstanding = last.outstanding - amortization

        })
    )
  }

}