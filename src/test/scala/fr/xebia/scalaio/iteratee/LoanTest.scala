package fr.xebia.scalaio.iteratee

import fr.xebia.scalaio.Implicits
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import fr.xebia.scalaio.matchers.TableMatchers
import Assertions._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import RowTransformer.totalPaid
import AmountConsumer.sum

class LoanTest extends FunSuite with ShouldMatchers with Implicits with TableMatchers {

  private val defaultTimeout = 1 second

  import ExecutionContext.Implicits.global

  test("A simple loan test") {

    val rows = Loan.simple(
      start = "2012-01-01",
      initial = 1000 EUR,
      duration = 5,
      fixedRate = 0.05
    ).rows

    Await.result(rows run RowConsumer.list, atMost = defaultTimeout) should matchTable(
      (date, amortization, interests, outstanding),
      ("2013-01-01", 200 EUR, 50 EUR, 800 EUR),
      ("2014-01-01", 200 EUR, 40 EUR, 600 EUR),
      ("2015-01-01", 200 EUR, 30 EUR, 400 EUR),
      ("2016-01-01", 200 EUR, 20 EUR, 200 EUR),
      ("2017-01-01", 200 EUR, 10 EUR, 0 EUR)
    )
  }

  test("sum all amounts from a loan") {
    val rows = Loan.simple(
      start = "2012-01-01",
      initial = 1000 EUR,
      duration = 5,
      fixedRate = 0.05
    ).rows

    val totalPaidComputation: Future[Amount] = rows
      .through(totalPaid)
      .run(sum)

    Await.result(totalPaidComputation, atMost = defaultTimeout) should equal(1150 EUR)
  }

  test("sum all amounts of a portfolio") {
    println("----------")
    val portfolio = Portfolio(Seq
      (Loan.simple(
        start = "2012-01-01",
        initial = 1000 EUR,
        duration = 5,
        fixedRate = 0.05
      ), Loan.simple(
        start = "2013-01-02",
        initial = 2000 EUR,
        duration = 10,
        fixedRate = 0.03
      ))
    )

    val totalPaidComputation: Future[Amount] =
      portfolio.rows &> totalPaid |>>> sum

    Await.result(totalPaidComputation, atMost = defaultTimeout) should equal(3480 EUR)
  }
}