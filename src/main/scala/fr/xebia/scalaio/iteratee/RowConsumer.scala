package fr.xebia.scalaio.iteratee

import play.api.libs.iteratee.Iteratee

object RowConsumer {

  def list: RowConsumer[List[Row]] = Iteratee.getChunks[Row]

}
