package fr.xebia.scalaio.iteratee

import play.api.libs.iteratee._

object RowConsumer {

  def list: RowConsumer[List[Row]] =
    Iteratee.getChunks[Row]


  val last: RowConsumer[Option[Row]] = {
    def step(last: Option[Row]): K[Row, Option[Row]] = {
      case Input.Empty => Cont(step(last))
      case Input.EOF => Done(last, Input.EOF)
      case Input.El(e) => Cont(step(Some(e)))
    }
    Cont(step(Option.empty[Row]))
  }

}
