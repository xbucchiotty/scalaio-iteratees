package fr.xebia.scalaio.iteratee

import org.joda.time.DateMidnight
import org.scalatest.matchers.{HavePropertyMatchResult, HavePropertyMatcher}
import fr.xebia.scalaio.matchers.TableMatchers.CheckColumn


object Assertions {

  def date: CheckColumn[Row, String] = expectedValue => new HavePropertyMatcher[Row, String] {
    def apply(actual: Row) = HavePropertyMatchResult(actual.date == DateMidnight.parse(expectedValue), "date", expectedValue, actual.date.toString("yyyy-MM-dd"))
  }

  def interests: CheckColumn[Row, Amount] = expectedValue => new HavePropertyMatcher[Row, Amount] {
    def apply(actual: Row) = HavePropertyMatchResult(actual.interests == expectedValue, "interets", expectedValue, actual.interests)
  }

  def amortization: CheckColumn[Row, Amount] = expectedValue => new HavePropertyMatcher[Row, Amount] {
    def apply(actual: Row) = HavePropertyMatchResult(actual.amortization == expectedValue, "amortization", expectedValue, actual.amortization)
  }

  def outstanding: CheckColumn[Row, Amount] = expectedValue => new HavePropertyMatcher[Row, Amount] {
    def apply(actual: Row) = HavePropertyMatchResult(actual.outstanding == expectedValue, "amortization", expectedValue, actual.outstanding)
  }
}
