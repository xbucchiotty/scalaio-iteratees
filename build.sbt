

version := "1.0.0"

scalaVersion := "2.10.3"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "1.9.1" % "test",
  "joda-time" % "joda-time" % "2.2",
  "org.joda" % "joda-convert" % "1.2",
  "joda-time" % "joda-time" % "2.2",
  "org.joda" % "joda-money" % "0.9",
  "com.typesafe.play" %% "play-iteratees" % "2.2.0-RC2",
  "com.typesafe.akka" %% "akka-actor" % "2.2.1"
)

fork in run := true

fork in test := true

javaOptions in run += "-Xmx2G"
