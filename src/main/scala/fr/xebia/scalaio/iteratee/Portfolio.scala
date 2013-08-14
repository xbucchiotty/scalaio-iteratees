package fr.xebia.scalaio.iteratee

case class Portfolio(loans: Iterable[Loan]) {

  def rows: Iterable[Row] = loans flatMap (_.rows)
}

object Portfolio {
  def apply(loans: Loan*): Portfolio = new Portfolio(loans)
}