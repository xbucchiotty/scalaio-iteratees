package fr.xebia.scalaio.iteratee

import AmountConsumer.sum
import RowTransformer.totalPaid
import akka.actor.SupervisorStrategy.{Escalate, Resume}
import akka.actor.{Props, OneForOneStrategy, ActorSystem}
import akka.cluster.Cluster
import akka.pattern.AskTimeoutException
import java.util.Currency
import org.joda.time.DateMidnight
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.cluster.routing.{ClusterRouterSettings, ClusterRouterConfig}
import fr.xebia.scalaio.akka.Backend
import akka.routing.RoundRobinRouter


object Master extends App {

  private val defaultTimeout = 1000 second

  val system = ActorSystem.create("ScalaIOSystem")

  Cluster(system).publishCurrentClusterState()

  Thread.sleep(5000)

  implicit val ctx = system.dispatcher

  val simpleStrategy =
    OneForOneStrategy() {
      case _: AskTimeoutException ⇒ Resume
      case _: RuntimeException => Escalate
    }

  val clusterSettings = ClusterRouterSettings(
    totalInstances = 100,
    maxInstancesPerNode = 2,
    allowLocalRoutees = false,
    None)

  val localRouter =
    RoundRobinRouter(nrOfInstances = 100)
      .withSupervisorStrategy(simpleStrategy)

  implicit val calculator = system.actorOf(Props[Backend]
    .withRouter(ClusterRouterConfig(
    local = localRouter,
    settings = clusterSettings))
    , "calculator")


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
