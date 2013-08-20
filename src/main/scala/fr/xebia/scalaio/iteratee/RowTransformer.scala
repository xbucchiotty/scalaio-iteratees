package fr.xebia.scalaio.iteratee

import play.api.libs.iteratee.Enumeratee
import scala.concurrent.ExecutionContext

object RowTransformer {

  def totalPaid(implicit ctx: ExecutionContext): RowTransformer[Amount] =
    Enumeratee.map[Row](row => row.interests + row.amortization)

}