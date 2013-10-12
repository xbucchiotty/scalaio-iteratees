

version := "1.0.0"

scalaVersion := "2.10.0"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "1.9.1" % "test",
  "joda-time" % "joda-time" % "2.2",
  "org.joda" % "joda-convert" % "1.2",
  "joda-time" % "joda-time" % "2.2",
  "org.joda" % "joda-money" % "0.9"
)

fork in run := true

javaOptions in run += "-Xmx2G"
