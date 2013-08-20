package fr.xebia.scalaio

import play.api.libs.iteratee.{Iteratee, Enumeratee, Enumerator}

package object iteratee {

  type RowOp = (Row => Row)

  type RowProducer = Enumerator[Row]

  type RowTransformer[T] = Enumeratee[Row, T]

  type AmountConsumer[T] = Iteratee[Amount, T]

  type RowConsumer[T] = Iteratee[Row, T]

}
