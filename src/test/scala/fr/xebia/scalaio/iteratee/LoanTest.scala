package fr.xebia.scalaio.iteratee

import AmountConsumer.sum
import Assertions._
import RowConsumer.{last, list}
import RowTransformer.{interests, until, totalPaid}
import akka.actor.{OneForOneStrategy, ActorRef, Props, ActorSystem}
import akka.routing.RoundRobinRouter
import fr.xebia.scalaio.Implicits
import fr.xebia.scalaio.akka.Backend
import fr.xebia.scalaio.matchers.TableMatchers
import org.joda.time.DateMidnight
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.pattern.AskTimeoutException
import akka.cluster.routing.{ClusterRouterConfig, ClusterRouterSettings}
import akka.cluster.Cluster

class LoanTest extends FunSuite with ShouldMatchers with Implicits with TableMatchers with BeforeAndAfterAll {

  private val defaultTimeout = 1 second

  var system: ActorSystem = _
  implicit var calculator: ActorRef = _
  implicit var ctx: ExecutionContext = _

  override protected def beforeAll() {
    system = ActorSystem.create("ScalaIOSystem")

    Cluster(system).publishCurrentClusterState()

    ctx = ExecutionContext.Implicits.global

    val simpleStrategy =
      OneForOneStrategy() {
        case _: AskTimeoutException ⇒ Resume
        case _: RuntimeException => Escalate
      }

    val clusterSettings = ClusterRouterSettings(
      totalInstances = 100,
      maxInstancesPerNode = 2,
      allowLocalRoutees = true,
      None)

    val localRouter =
      RoundRobinRouter(nrOfInstances = 100)
        .withSupervisorStrategy(simpleStrategy)

    calculator = system.actorOf(Props[Backend]
      .withRouter(ClusterRouterConfig(
      local = localRouter,
      settings = clusterSettings))
      , "calculator")
  }

  override protected def afterAll() {
    system.shutdown()
  }

  test("A simple loan test") {

    val rows = Loan.simple(
      start = "2012-01-01",
      initial = 1000 EUR,
      duration = 5,
      fixedRate = 0.05
    ).rows

    Await.result(rows run list, atMost = defaultTimeout) should matchTable(
      (date, amortization, Assertions.interests, outstanding),
      ("2013-01-01", 200 EUR, 50 EUR, 800 EUR),
      ("2014-01-01", 200 EUR, 40 EUR, 600 EUR),
      ("2015-01-01", 200 EUR, 30 EUR, 400 EUR),
      ("2016-01-01", 200 EUR, 20 EUR, 200 EUR),
      ("2017-01-01", 200 EUR, 10 EUR, 0 EUR)
    )
  }

  test("sum all amounts from a loan") {
    val rows = Loan.simple(
      start = "2012-01-01",
      initial = 1000 EUR,
      duration = 5,
      fixedRate = 0.05
    ).rows

    val totalPaidComputation: Future[Amount] = rows
      .through(totalPaid)
      .run(sum)

    Await.result(totalPaidComputation, atMost = defaultTimeout) should equal(1150 EUR)
  }

  test("sum all amounts of a portfolio") {
    println("----------")
    val portfolio = Portfolio(Seq
      (Loan.simple(
        start = "2012-01-01",
        initial = 1000 EUR,
        duration = 5,
        fixedRate = 0.05
      ), Loan.simple(
        start = "2013-01-02",
        initial = 2000 EUR,
        duration = 10,
        fixedRate = 0.03
      ))
    )

    val totalPaidComputation: Future[Amount] =
      portfolio.rows &> totalPaid |>>> sum

    Await.result(totalPaidComputation, atMost = defaultTimeout) should equal(3480 EUR)
  }

  test("sum all interests until 2015-03-01 of a portfolio") {
    println("----------")
    val portfolio = Portfolio(Seq
      (Loan.simple(
        start = "2012-01-01",
        initial = 1000 EUR,
        duration = 5,
        fixedRate = 0.05
      ), Loan.simple(
        start = "2013-01-02",
        initial = 2000 EUR,
        duration = 10,
        fixedRate = 0.03
      ))
    )

    val totalPaidComputation: Future[Amount] =
      portfolio.rows &> until("2015-03-01") &> interests |>>> sum

    Await.result(totalPaidComputation, atMost = defaultTimeout) should equal(234 EUR)
  }

  test("get last payment generated") {
    println("----------")
    val loan = Loan.simple(
      start = "2012-01-01",
      initial = 1000 EUR,
      duration = 5,
      fixedRate = 0.05
    )

    val lastRowComputation = loan.rows |>>> last

    val lastRow = Await.result(lastRowComputation, atMost = defaultTimeout)

    lastRow should be('defined)
    lastRow map (_.date should equal(DateMidnight.parse("2017-01-01")))
  }

}