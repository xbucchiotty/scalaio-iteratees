package fr.xebia.scalaio.iteratee

import play.api.libs.iteratee.Enumerator
import scala.concurrent.{Future, ExecutionContext}

case class Portfolio(loans: Seq[Loan]) {

  def rows(implicit ctx: ExecutionContext): RowProducer = {
    val split =
      loans
        .map(_.stream)
        .grouped(groupSize)

    split
      .map(rowIt => Enumerator.flatten(Future(rowIt.flatten).map(Enumerator.enumerate(_))))
      .foldLeft(Enumerator.empty[Row])(Enumerator.interleave(_, _))
  }

 private def groupSize:Int = if(loans.size>=4) loans.size / 4 else loans.size
}
