package fr.xebia.scalaio.iteratee


object Master extends App {

  import java.util.Currency
  import org.joda.time.DateMidnight

  val simpleLoans =
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

  val complexLoans =
    for (i <- (0 to 5000).toSeq)
    yield {
      val rand = 1 + i % 31
      Loan.complex(
        start = DateMidnight.parse("2012-01-" + rand),
        initial = Amount(1000, Currency.getInstance("EUR")),
        duration = 50,
        fixedRate = rand / 100
      )
    }

  val simplePortfolio = Portfolio(simpleLoans)

  val complexPortfolio = Portfolio(complexLoans)

  val start = System.currentTimeMillis()

  LogTime("simple ") {
    simplePortfolio.rows.toStream
      .map(row => row.interests + row.amortization)
      .foldLeft(Amount(0, Currency.getInstance("EUR")))(_ + _)
  }

  LogTime("complex") {
    complexPortfolio.rows.toStream
      .map(row => row.interests + row.amortization)
      .foldLeft(Amount(0, Currency.getInstance("EUR")))(_ + _)
  }

}


object LogTime {
  def apply[T](name: String)(t: => T): T = {
    val start = System.currentTimeMillis()
    val result = t
    println(s"[$name] : ${System.currentTimeMillis() - start} ms")
    result
  }
}