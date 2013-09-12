package fr.xebia.scalaio.iteratee

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import concurrent.duration._
import fr.xebia.scalaio.akka.Compute
import fr.xebia.scalaio.implicits._
import play.api.libs.iteratee.Enumerator
import scala.concurrent.ExecutionContext
import org.joda.time.DateMidnight

case class Loan(
                 initial: Amount,
                 duration: Int,
                 rowIt: RowIt) {

  implicit lazy val timeout = Timeout(1 minute)

  def rows(implicit calculator: ActorRef, ctx: ExecutionContext): RowProducer = {
    Enumerator.flatten(
      ask(calculator, Compute(this)).mapTo[List[Row]]
        .map(Enumerator.enumerate(_))
    )
  }

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