package fr.xebia.scalaio.iteratee

import AmountConsumer.sum
import RowTransformer.totalPaid
import akka.actor.{Props, ActorSystem}
import akka.routing.RoundRobinRouter
import fr.xebia.scalaio.akka.Backend
import java.util.Currency
import org.joda.time.DateMidnight
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}


object Master extends App {

  private val defaultTimeout = 1000 second

  val system = ActorSystem.create("ScalaIOSystem")

  implicit val ctx = ExecutionContext.Implicits.global

  implicit val calculator = system.actorOf(Props[Backend]
    .withRouter(RoundRobinRouter(nrOfInstances = 10)),
    "calculator")

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

  LogTime("simple ") {
    Await.result(simplePortfolio.rows &> totalPaid |>>> sum, atMost = defaultTimeout)
  }

  LogTime("complex") {
    Await.result(complexPortfolio.rows &> totalPaid |>>> sum, atMost = defaultTimeout)
  }

  system.shutdown()

}


object LogTime {
  def apply[T](name: String)(t: => T): T = {
    val start = System.currentTimeMillis()
    val result = t
    println(s"[$name] : ${System.currentTimeMillis() - start} ms")
    result
  }
}