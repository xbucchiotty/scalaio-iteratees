package fr.xebia.scalaio.akka

import akka.actor.Actor
import fr.xebia.scalaio.iteratee.Loan

class Backend extends Actor {

  def receive = {
    case Compute(loans) =>
      sender.tell(
        msg = loans.toList.flatMap(_.stream).toList,
        sender = self
      )
  }
}

case class Compute(loans: Seq[Loan])

object Compute {
  def apply(loan: Loan): Compute = Compute(List(loan))
}