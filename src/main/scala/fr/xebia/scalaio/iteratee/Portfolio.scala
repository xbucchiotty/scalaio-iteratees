package fr.xebia.scalaio.iteratee

import play.api.libs.iteratee.Enumerator
import scala.concurrent.ExecutionContext

case class Portfolio(loans: Seq[Loan]) {

  def rows(implicit ctx: ExecutionContext): RowProducer = {
    //val split = loans.toStream.map(_.rows).grouped(loans.size / 4)

    //split.map(_.foldLeft(Enumerator.empty[Row])(_ andThen _)).foldLeft(Enumerator.empty[Row])(Enumerator.interleave(_, _))

    loans.map(_.rows).foldLeft(Enumerator.empty[Row])(_ >>> _)

  }
}