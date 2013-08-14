package fr.xebia.scalaio.iteratee

import java.util.Currency

case class Amount(value: Double, currency: Currency) {
  assert(currency != null)

  def *(coef: Double) = copy(value = coef * value)

  def -(other: Amount) = copy(value = value - other.value)

  def +(other: Amount) = copy(value = value + other.value)

  def /(divisor: Double) = copy(value = value / divisor)

  override def toString = s"$value, $currency"
}
