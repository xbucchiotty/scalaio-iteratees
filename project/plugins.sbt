addSbtPlugin("fr.xebia.sbt.plugin" % "sbt-aws-plugin" % "0.0.1-SNAPSHOT")

initialize ~= {
  _ =>
    System.setProperty("com.amazonaws.sdk.disableCertChecking", "true")
}

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.1")
