package fr.xebia.scalaio.akka

import akka.actor.ActorSystem

object Seed1 extends App {
  System.setProperty("akka.remote.netty.tcp.port", "2551")
  val system = ActorSystem("ScalaIOSystem")
}

object Seed2 extends App {
  System.setProperty("akka.remote.netty.tcp.port", "2552")
  val system = ActorSystem("ScalaIOSystem")
}

object Slave extends App {
  val system = ActorSystem("ScalaIOSystem")
}