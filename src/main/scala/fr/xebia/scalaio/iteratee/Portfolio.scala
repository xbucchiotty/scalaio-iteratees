package fr.xebia.scalaio.iteratee

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import concurrent.duration._
import fr.xebia.scalaio.akka.Compute
import play.api.libs.iteratee.Enumerator
import scala.concurrent.ExecutionContext


case class Portfolio(loans: Seq[Loan]) {

  implicit lazy val timeout = Timeout(1 minute)

  def rows(implicit calculator: ActorRef, ctx: ExecutionContext): RowProducer = {
    val split =
      loans
        .grouped(groupSize)
        .map(bulk => Compute(bulk))

    split
      .map(bulk => {
      Enumerator.flatten(
        ask(calculator, bulk)
          .mapTo[List[Row]]
          .map(Enumerator.enumerate(_))
      )
    }).foldLeft(Enumerator.empty[Row])(Enumerator.interleave(_, _))
  }

  private def groupSize:Int = if(loans.size>=4) loans.size / 4 else loans.size
}