package fr.xebia.scalaio.iteratee

import play.api.libs.iteratee.Enumeratee
import scala.concurrent.ExecutionContext
import org.joda.time.DateMidnight


object RowTransformer {

  def totalPaid(implicit ctx: ExecutionContext): RowTransformer[Amount] =
    Enumeratee.map[Row](row => row.interests + row.amortization)

  def interests(implicit ctx:ExecutionContext): RowTransformer[Amount] =
    Enumeratee.map[Row](_.interests)

  def until(date: DateMidnight)(implicit ctx:ExecutionContext): RowTransformer[Row] =
    Enumeratee.filter[Row](row => !row.date.isAfter(date))

}