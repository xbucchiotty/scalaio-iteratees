package fr.xebia.scalaio.iteratee


object Master extends App {

  import java.util.Currency
  import org.joda.time.DateMidnight

  val loans =
    for (i <- (0 to 5000).toSeq)
    yield {
      val rand = 1 + i % 31
      Loan.simple(
        start = DateMidnight.parse("2012-01-" + rand),
        initial = Amount(1000, Currency.getInstance("EUR")),
        duration = 50,
        fixedRate = rand / 100
      )
    }

  val portfolio = Portfolio(loans)

  val start = System.currentTimeMillis()

  LogTime {
    portfolio.rows.toStream
      .map(row => row.interests + row.amortization)
      .foldLeft(Amount(0, Currency.getInstance("EUR")))(_ + _)
  }

}


object LogTime {
  def apply[T](t: => T): T = {
    val start = System.currentTimeMillis()
    val result = t
    println(s"${System.currentTimeMillis() - start} ms")
    result
  }
}