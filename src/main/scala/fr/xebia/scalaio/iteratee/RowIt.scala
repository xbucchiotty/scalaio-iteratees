package fr.xebia.scalaio.iteratee

case class RowIt(first: Row, op: (Row => Row))