// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.2")

// Build + Deployments
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.6")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.1")

// sbt-web
addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.3")
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.7")
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.0")
addSbtPlugin("org.scalastyle" % "scalastyle-sbt-plugin" % "0.8.0")

// This plugin automatically refreshes Chrome when you make changes to your app
//addSbtPlugin("com.jamesward" %% "play-auto-refresh" % "0.0.11")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.5")

// SBT Eclipse Plugin
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "4.0.0")
