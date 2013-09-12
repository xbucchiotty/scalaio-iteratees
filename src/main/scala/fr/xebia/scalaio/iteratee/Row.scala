package fr.xebia.scalaio.iteratee

import org.joda.time.DateMidnight

trait Row extends Serializable{

  def date: DateMidnight

  def amortization: Amount

  def interests: Amount

  def outstanding: Amount

  override def toString = s"${date.toString("yyyy-MM-dd")}\t$amortization\t$interests\t$outstanding"
}
