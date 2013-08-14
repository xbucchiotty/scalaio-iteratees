package fr.xebia.scalaio.iteratee

import fr.xebia.scalaio.Implicits
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import fr.xebia.scalaio.matchers.TableMatchers
import Assertions._

class LoanTest extends FunSuite with ShouldMatchers with Implicits with TableMatchers {

  test("A simple loan test") {

    val rows = Loan.simple(
      start = "2012-01-01",
      initial = 1000 EUR,
      duration = 5,
      fixedRate = 0.05
    ).rows

    println(rows.mkString("\n"))

    rows should matchTable(
      (date, amortization, interests, outstanding),
      ("2013-01-01", 200 EUR, 50 EUR, 800 EUR),
      ("2014-01-01", 200 EUR, 40 EUR, 600 EUR),
      ("2015-01-01", 200 EUR, 30 EUR, 400 EUR),
      ("2016-01-01", 200 EUR, 20 EUR, 200 EUR),
      ("2017-01-01", 200 EUR, 10 EUR, 0 EUR)
    )
  }
}